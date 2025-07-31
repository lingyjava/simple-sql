package com.lingyuan.simplesql.server.impl;

import com.alibaba.excel.EasyExcel;
import com.lingyuan.simplesql.common.exception.BusinessException;
import com.lingyuan.simplesql.common.util.ValidationUtil;
import com.lingyuan.simplesql.domain.dto.SqlGeneratorParam;
import com.lingyuan.simplesql.domain.enums.SQLTypeEnum;
import com.lingyuan.simplesql.server.SqlGenerator;
import com.lingyuan.simplesql.server.handler.GenerateDeleteHandler;
import com.lingyuan.simplesql.server.handler.GenerateInsertHandler;
import com.lingyuan.simplesql.server.handler.GenerateSelectHandler;
import com.lingyuan.simplesql.server.handler.GenerateUpdateHandler;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class SqlGeneratorExcelImpl implements SqlGenerator {
    
    private void checkParam(@NotNull SqlGeneratorParam param) {
        ValidationUtil.validate(param);
        if (param.getTableName() == null || param.getTableName().isEmpty()) {
            throw new BusinessException("表名不能为空");
        }
        if (param.getExcelPath() == null || param.getExcelPath().isEmpty()) {
            throw new BusinessException("Excel文件路径不能为空");
        }
        if (param.getSqlType() == null || param.getSqlType().isEmpty()) {
            throw new BusinessException("SQL类型不能为空");
        }
        if (!SQLTypeEnum.contains(param.getSqlType())) {
            throw new BusinessException("不支持的SQL类型: " + param.getSqlType());
        }
    }

    @Override
    public Object generate(SqlGeneratorParam param) {
        // 校验参数
        checkParam(param);

        // 读取excel
        List<List<String>> data = readExcel(param.getExcelPath());
        if (data == null || data.size() < 6) {
            throw new BusinessException("Excel文件格式错误或数据不足，至少需要6行数据");
        }
        // 读取表头
        List<String> header = data.get(0);
        // 读取数据
        List<List<String>> rows = data.subList(1, data.size());

        if (header.isEmpty() || rows.isEmpty()) {
            throw new BusinessException("Excel数据格式错误，表头或数据行不能为空");
        }

        for (List<String> row : rows) {
            if (row.size() != header.size()) {
                throw new BusinessException("数据行与表头列数不一致，行长度：" + row.size() + "，表头长度：" + header.size());
            }
        }

        // 生成SQL语句
        String sql;
        switch (param.getSqlType().toLowerCase()) {
            case "insert" -> sql = GenerateInsertHandler.getSQL(rows, header, param.getTableName());
            case "update" -> sql = GenerateUpdateHandler.getSQL(rows, header, param.getWhereColumnCount(), param.getTableName());
            case "delete" -> sql = GenerateDeleteHandler.getSQL(rows, header, param.getWhereColumnCount(), param.getTableName());
            case "select" -> sql = GenerateSelectHandler.getSQL(rows, header, param.getTableName());
            default -> throw new BusinessException("不支持的SQL类型: " + param.getSqlType());
        }
        // 写入SQL到文件
        String outputFilePath = getDefaultOutputFilePath(param.getSqlType() + "-" + param.getTableName() + "-");
        writeSqlToFile(sql, outputFilePath);
        return outputFilePath;
    }

    @Override
    public String getType() {
        return "EXCEL";
    }

    /**
     * 获取默认输出文件路径（当前目录下output-年月日时分秒-6位随机数.sql）
     */
    private String getDefaultOutputFilePath(String prefix) {
        String dir = System.getProperty("user.dir") + File.separator + "output";
        createDirectory(dir);
        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int rand = 100000 + new Random().nextInt(900000);
        return dir + File.separator + prefix + time + "-" + rand + ".sql";
    }

    /**
     * 创建目录
     */
    public void createDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                log.info("Directory created: {}", dirPath);
            } else {
                throw new BusinessException("Failed to create directory: " + dirPath);
            }
        } else {
            log.info("Directory already exists: {}", dirPath);
        }
    }

    /**
     * 读取Excel文件
     */
    public List<List<String>> readExcel(String filePath) {
        return convertMapListToListList(EasyExcel.read(filePath)
                .sheet()
                .headRowNumber(0)
                .doReadSync());
    }

    /**
     * 写入SQL到SQL文件
     */
    public void writeSqlToFile(String sql, String outputFilePath) {
        try (PrintWriter out = new PrintWriter(outputFilePath)) {
            out.println(sql);
            log.info("SQL has been written to: {}", outputFilePath);
        } catch (Exception e) {
            throw new BusinessException("Failed to write SQL to file: " + outputFilePath, e);
        }
    }

    public List<List<String>> convertMapListToListList(List<Map<Integer, String>> mapList) {
        List<List<String>> result = new ArrayList<>();

        for (Map<Integer, String> map : mapList) {
            int maxIndex = map.keySet().stream().max(Integer::compareTo).orElse(0);
            List<String> row = new ArrayList<>(Collections.nCopies(maxIndex + 1, ""));
            for (Map.Entry<Integer, String> entry : map.entrySet()) {
                row.set(entry.getKey(), entry.getValue());
            }
            result.add(row);
        }

        return result;
    }

}
