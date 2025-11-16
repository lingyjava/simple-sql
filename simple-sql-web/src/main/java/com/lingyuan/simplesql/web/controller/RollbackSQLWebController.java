package com.lingyuan.simplesql.web.controller;

import com.lingyuan.simplesql.domain.dto.SqlGeneratorParam;
import com.lingyuan.simplesql.server.impl.SqlGeneratorFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
public class RollbackSQLWebController {

	@GetMapping("/rollback")
	public String rollbackSQLPage() {
		return "rollback_sql";
	}

	@PostMapping(value = "/rollback/generate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String generateRollbackSQL(
			@RequestParam("file") MultipartFile file,
			Model model
	) {
		if (file == null || file.isEmpty()) {
			model.addAttribute("error", "请上传 SQL 文件（.sql）");
			return "rollback_sql";
		}
		// 保存临时文件
		Path tempFilePath;
		try {
			String suffix = ".sql";
			tempFilePath = Files.createTempFile("sql_upload_", suffix);
			file.transferTo(tempFilePath.toFile());
		} catch (Exception e) {
			model.addAttribute("error", "保存上传文件失败: " + e.getMessage());
			return "rollback_sql";
		}

		// 调用生成器
		try {
			SqlGeneratorParam param = new SqlGeneratorParam();
			param.setType("ROLLBACK");
			param.setFilePath(tempFilePath.toAbsolutePath().toString());

			Object result = SqlGeneratorFactory.getGenerator(param.getType()).generate(param);
			String outputPath = String.valueOf(result);

			model.addAttribute("success", "回退脚本已生成");
			model.addAttribute("outputPath", outputPath);

			// 小文件直接在页面中预览内容
			String previewContent = null;
			boolean previewTooLarge = false;
			File generatedFile = new File(outputPath);
			final long maxPreviewBytes = 100 * 1024;
			if (generatedFile.exists() && generatedFile.isFile()) {
				long size = generatedFile.length();
				if (size <= maxPreviewBytes) {
					try {
						previewContent = Files.readString(generatedFile.toPath(), StandardCharsets.UTF_8);
					} catch (Exception ignored) {
						// 预览失败不影响下载
					}
				} else {
					previewTooLarge = true;
				}
			}
			model.addAttribute("previewContent", previewContent);
			model.addAttribute("previewTooLarge", previewTooLarge);

			// 与 Excel 页面保持一致，对路径进行 URL 编码，并复用下载接口
			String encodedPath = URLEncoder.encode(outputPath, StandardCharsets.UTF_8);
			model.addAttribute("downloadUrl", "/excel/download?path=" + encodedPath);
		} catch (Exception ex) {
			model.addAttribute("error", "生成失败: " + ex.getMessage());
		} finally {
			try { Files.deleteIfExists(tempFilePath); } catch (Exception ignored) {}
		}
		return "rollback_sql";
	}
}


