package com.lingyuan.simplesql.server.handler;

import com.lingyuan.simplesql.common.exception.BusinessException;
import com.lingyuan.simplesql.common.util.ExcelParse;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GenerateSelectHandler {

    /**
     * 生成SELECT语句
     *
     * @param rows      行数据
     * @param header    表头/列名
     * @param tableName 表名
     * @param databaseName 数据库名（可选）
     * @return 生成的SQL语句
     */
    public static String getSQL(List<List<String>> rows, List<String> header, String tableName, String databaseName) {
        if (rows.isEmpty() || header.isEmpty()) {
            throw new BusinessException("行数据或表头不完整，无法生成SELECT语句");
        }
        if (tableName == null || tableName.isEmpty()) {
            throw new BusinessException("表名不能为空，无法生成SELECT语句");
        }

        StringBuilder sql = new StringBuilder("SELECT * ");
        sql.append("FROM ");
        
        // 如果有数据库名，则添加数据库名前缀
        if (databaseName != null && !databaseName.trim().isEmpty()) {
            sql.append("`").append(databaseName.trim()).append("`.");
        }
        
        sql.append("`").append(tableName).append("` WHERE ");

        for (int i = 0; i < header.size(); i++) {
            Set<String> colValues = new LinkedHashSet<>(); 
            for (List<String> row : rows) {
                colValues.add(ExcelParse.getCell(row, i));
            }
            if (i > 0) sql.append(" AND ");
            sql.append("`").append(header.get(i)).append("`");
            if (colValues.size() == 1) {
                sql.append(" = ").append("'").append(ExcelParse.getCell(rows.get(0), i)).append("'");
            } else {
                sql.append(" IN (");
                int idx = 0;
                for (String val : colValues) {
                    if (idx++ > 0) sql.append(", ");
                    sql.append("'").append(val).append("'");
                }
                sql.append(")");
            }
        }
        sql.append(";");
        return sql.toString();
    }

    /**
     * 生成SELECT语句（兼容旧版本）
     *
     * @param rows      行数据
     * @param header    表头/列名
     * @param tableName 表名
     * @return 生成的SQL语句
     */
    public static String getSQL(List<List<String>> rows, List<String> header, String tableName) {
        return getSQL(rows, header, tableName, null);
    }
}
