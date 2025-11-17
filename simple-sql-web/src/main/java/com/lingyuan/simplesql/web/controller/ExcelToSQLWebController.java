package com.lingyuan.simplesql.web.controller;

import com.lingyuan.simplesql.common.util.FileUtil;
import com.lingyuan.simplesql.domain.dto.SqlGeneratorParam;
import com.lingyuan.simplesql.server.impl.SqlGeneratorFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Controller
public class ExcelToSQLWebController {

	@GetMapping("/excel")
	public String excelPage(Model model) {
		List<String> sqlTypes = Arrays.asList("SELECT", "INSERT", "UPDATE", "DELETE");
		model.addAttribute("sqlTypes", sqlTypes);
		return "excel_to_sql";
	}

	@PostMapping(value = "/excel/generate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String generateSQL(
			@RequestParam(value = "databaseName", required = false) String databaseName,
			@RequestParam("tableName") String tableName,
			@RequestParam("sqlType") String sqlType,
			@RequestParam(value = "whereColumnCount", required = false) Integer whereColumnCount,
			@RequestParam("file") MultipartFile file,
			Model model
	) {
		model.addAttribute("sqlTypes", Arrays.asList("SELECT", "INSERT", "UPDATE", "DELETE"));
		model.addAttribute("databaseName", StringUtils.hasText(databaseName) ? databaseName : "");
		model.addAttribute("tableName", tableName);
		model.addAttribute("sqlTypeSelected", sqlType);
		model.addAttribute("whereColumnCount", whereColumnCount);

		// 基本校验
		if (file == null || file.isEmpty()) {
			model.addAttribute("error", "请上传Excel文件（.xls 或 .xlsx）");
			return "excel_to_sql";
		}
		if (!StringUtils.hasText(tableName)) {
			model.addAttribute("error", "请填写表名");
			return "excel_to_sql";
		}
		if (!StringUtils.hasText(sqlType)) {
			model.addAttribute("error", "请选择SQL类型");
			return "excel_to_sql";
		}

		// 保存上传文件到临时目录
		Path tempFilePath;
		try {
			String original = file.getOriginalFilename();
			String suffix = (original != null && original.toLowerCase().endsWith(".xlsx")) ? ".xlsx" : ".xls";
			tempFilePath = Files.createTempFile("excel_upload_", suffix);
			file.transferTo(tempFilePath.toFile());
		} catch (IOException e) {
			model.addAttribute("error", "保存上传文件失败: " + e.getMessage());
			return "excel_to_sql";
		}

		// 组装参数并调用生成器
		try {
			SqlGeneratorParam param = new SqlGeneratorParam();
			param.setFilePath(tempFilePath.toAbsolutePath().toString());
			param.setTableName(tableName.trim());
			param.setDatabaseName(StringUtils.hasText(databaseName) ? databaseName.trim() : null);
			param.setSqlType(sqlType);
			param.setType("EXCEL_TO_SQL");
			if (whereColumnCount != null) {
				param.setWhereColumnCount(whereColumnCount);
			}

			Object result = SqlGeneratorFactory.getGenerator(param.getType()).generate(param);
			String outputPath = String.valueOf(result);

			model.addAttribute("success", "SQL 文件已生成");
			model.addAttribute("outputPath", outputPath);

			// 小文件直接在页面中预览内容，避免用户来回切换
			String previewContent = null;
			boolean previewTooLarge = false;
			String previewError = null;
			File generatedFile = new File(outputPath);
			// 约 100KB 以内的文件直接预览
			final long maxPreviewBytes = 100 * 1024;
			if (generatedFile.exists() && generatedFile.isFile()) {
				long size = generatedFile.length();
				if (size <= maxPreviewBytes) {
					try {
						previewContent = Files.readString(generatedFile.toPath(), StandardCharsets.UTF_8);
					} catch (IOException e) {
						// 预览失败时记录错误信息，但不影响下载功能
						previewError = "预览加载失败: " + e.getMessage();
					}
				} else {
					previewTooLarge = true;
				}
			} else {
				previewError = "生成的文件不存在或无法访问: " + outputPath;
			}
			model.addAttribute("previewContent", previewContent);
			model.addAttribute("previewTooLarge", previewTooLarge);
			model.addAttribute("previewError", previewError);

			// 对路径进行 URL 编码，避免特殊字符（如 \ 和空格）导致 400 错误
			String encodedPath = URLEncoder.encode(outputPath, StandardCharsets.UTF_8);
			model.addAttribute("downloadUrl", "/excel/download?path=" + encodedPath);
		} catch (Exception ex) {
			model.addAttribute("error", "生成失败: " + ex.getMessage());
		} finally {
			try {
				Files.deleteIfExists(tempFilePath);
			} catch (IOException ignored) {
			}
		}
		return "excel_to_sql";
	}

	@GetMapping("/excel/template")
	public ResponseEntity<Resource> downloadTemplate() {
		try {
			// 从 classpath 的 resources 根目录加载模板文件
			ClassPathResource resource = new ClassPathResource("StandardTemplate.xlsx");
			if (!resource.exists()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"StandardTemplate.xlsx\"")
					.contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
					.body(resource);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/excel/download")
	public ResponseEntity<Resource> downloadGenerated(@RequestParam("path") String path) {
		if (!StringUtils.hasText(path)) {
			return ResponseEntity.badRequest().build();
		}
		// path 在生成时经过了 URL 编码，这里需要先解码
		String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
		File target = new File(decodedPath);
		// 仅允许下载应用数据目录下的文件
		String outputDir = FileUtil.getAppDataDir();
		try {
			String canonicalTarget = target.getCanonicalPath();
			String canonicalOutput = new File(outputDir).getCanonicalPath();
			if (!canonicalTarget.startsWith(canonicalOutput)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		if (!target.exists() || !target.isFile()) {
			return ResponseEntity.notFound().build();
		}
		Resource resource = new FileSystemResource(target);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + target.getName() + "\"")
				.contentType(MediaType.TEXT_PLAIN)
				.body(resource);
	}
}


