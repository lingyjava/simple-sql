package com.lingyuan.simplesql.server.impl;

import com.lingyuan.simplesql.common.exception.BusinessException;
import com.lingyuan.simplesql.common.util.ExcelParse;
import com.lingyuan.simplesql.common.util.FileUtil;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * SQL 生成器 - Excel To SQL 实现
 */
@Slf4j
public class SqlGeneratorExcelImpl implements SqlGenerator {
    
    private void checkParam(@NotNull SqlGeneratorParam param) {
        ValidationUtil.validate(param);
        if (param.getTableName() == null || param.getTableName().isEmpty()) {
            throw new BusinessException("表名不能为空");
        }
        if (param.getFilePath() == null || param.getFilePath().isEmpty()) {
            throw new BusinessException("文件路径不能为空");
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
        List<List<String>> data = ExcelParse.readExcel(param.getFilePath());
        if (data == null || data.size() < 2) {
            throw new BusinessException("Excel文件格式错误或数据不足，至少需要2行数据");
        }
        // 读取表头
        List<String> header = data.get(0);
        // 读取数据
        List<List<String>> rows = data.subList(1, data.size());

        if (header.isEmpty() || rows.isEmpty()) {
            throw new BusinessException("Excel数据格式错误，表头或数据行不能为空");
        }

        // 生成SQL语句
        StringBuilder sql = new StringBuilder();
        // 添加头部注释
        sql.append("-- ==========================================\n");
        sql.append("-- Excel 转 SQL 工具\n");
        sql.append("-- ==========================================\n");
        sql.append("-- 生成时间: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sql.append("-- 表名: ").append(param.getTableName()).append("\n");
        sql.append("-- 数据行数: ").append(rows.size()).append("\n");
        sql.append("-- 数据列数: ").append(header.size()).append("\n");
        sql.append("-- 条件列数(仅UPDATE/DELETE生效): ").append(param.getWhereColumnCount() == null ? header.size() : param.getWhereColumnCount()).append("\n");
        sql.append("-- 数据类型: ").append(param.getSqlType()).append("\n");
        // 使用说明
        sql.append("-- 使用说明\n");
        sql.append("-- 1. 执行前请备份数据库\n");
        sql.append("-- 2. 仔细检查每个SQL语句的正确性\n");
        sql.append("-- 3. 建议在测试环境中先验证SQL语句\n");
        sql.append("-- ==========================================\n");
        sql.append("\n");
        
        switch (param.getSqlType().toLowerCase()) {
            case "insert" -> sql.append(GenerateInsertHandler.getSQL(rows, header, param.getTableName(), param.getDatabaseName()));
            case "update" -> sql.append(GenerateUpdateHandler.getSQL(rows, header, param.getWhereColumnCount() == null ? 1 : param.getWhereColumnCount(), param.getTableName(), param.getDatabaseName()));
            case "delete" -> sql.append(GenerateDeleteHandler.getSQL(rows, header, param.getWhereColumnCount() == null ? header.size() : param.getWhereColumnCount(), param.getTableName(), param.getDatabaseName()));
            case "select" -> sql.append(GenerateSelectHandler.getSQL(rows, header, param.getTableName(), param.getDatabaseName()));
            default -> throw new BusinessException("不支持的SQL类型: " + param.getSqlType());
        }
        // 写入SQL到文件
        String outputFilePath = FileUtil.getDefaultOutputFilePath(param.getSqlType() + ";" + param.getTableName());
        FileUtil.writeStringToFile(sql.toString(), outputFilePath);
        return outputFilePath;
    }

    @Override
    public String getType() {
        return "EXCEL_TO_SQL";
    }

}
