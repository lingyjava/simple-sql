package com.lingyuan.simplesql.server.impl;

import com.lingyuan.simplesql.common.exception.BusinessException;
import com.lingyuan.simplesql.common.util.ExcelParseUtil;
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

import java.util.List;

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
        List<List<String>> data = ExcelParseUtil.readExcel(param.getExcelPath());
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
        String sql;
        switch (param.getSqlType().toLowerCase()) {
            case "insert" -> sql = GenerateInsertHandler.getSQL(rows, header, param.getTableName());
            case "update" -> sql = GenerateUpdateHandler.getSQL(rows, header, param.getWhereColumnCount() == null ? 1 : param.getWhereColumnCount(), param.getTableName());
            case "delete" -> sql = GenerateDeleteHandler.getSQL(rows, header, param.getWhereColumnCount() == null ? header.size() : param.getWhereColumnCount(), param.getTableName());
            case "select" -> sql = GenerateSelectHandler.getSQL(rows, header, param.getTableName());
            default -> throw new BusinessException("不支持的SQL类型: " + param.getSqlType());
        }
        // 写入SQL到文件
        String outputFilePath = FileUtil.getDefaultOutputFilePath(param.getSqlType() + "-" + param.getTableName() + "-");
        FileUtil.writeStringToFile(sql, outputFilePath);
        return outputFilePath;
    }

    @Override
    public String getType() {
        return "EXCEL";
    }

}
