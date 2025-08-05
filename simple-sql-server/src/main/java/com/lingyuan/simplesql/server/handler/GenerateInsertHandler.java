package com.lingyuan.simplesql.server.handler;

import com.lingyuan.simplesql.common.exception.BusinessException;
import com.lingyuan.simplesql.common.util.ExcelParse;

import java.util.List;

public class GenerateInsertHandler {

    /**
     * 生成INSERT语句
     * @param rows 行数据
     * @param header 表头/列名
     * @param tableName 表名
     * @param databaseName 数据库名（可选）
     * @return 生成的SQL语句
     */
    public static String getSQL(List<List<String>> rows, List<String> header, String tableName, String databaseName) {
        if (rows.isEmpty() || header.isEmpty()) {
            throw new BusinessException("行数据或表头不完整，无法生成INSERT语句");
        }
        if (tableName == null || tableName.isEmpty()) {
            throw new BusinessException("表名不能为空，无法生成INSERT语句");
        }

        StringBuilder sql = new StringBuilder("INSERT INTO ");
        
        // 如果有数据库名，则添加数据库名前缀
        if (databaseName != null && !databaseName.trim().isEmpty()) {
            sql.append("`").append(databaseName.trim()).append("`.");
        }
        
        sql.append("`").append(tableName).append("` (");

        // 添加列名
        for (int i = 0; i < header.size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append("`").append(header.get(i)).append("`");
        }
        sql.append(") VALUES ");

        // 添加行数据
        for (int i = 0; i < rows.size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append("(");
            for (int j = 0; j < rows.get(i).size(); j++) {
                if (j > 0) sql.append(", ");
                sql.append("'").append(ExcelParse.getCell(rows.get(i), j)).append("'");
            }
            sql.append(")");
        }
        sql.append(";");
        return sql.toString();
    }

    /**
     * 生成INSERT语句（兼容旧版本）
     * @param rows 行数据
     * @param header 表头/列名
     * @param tableName 表名
     * @return 生成的SQL语句
     */
    public static String getSQL(List<List<String>> rows, List<String> header, String tableName) {
        return getSQL(rows, header, tableName, null);
    }
}
