package com.lingyuan.simplesql.server.handler;

import com.lingyuan.simplesql.common.exception.BusinessException;
import com.lingyuan.simplesql.common.util.ExcelParse;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GenerateDeleteHandler {

    /**
     * 生成DELETE语句
     * @param rows 行数据
     * @param header 表头/列名
     * @param whereCount 条件列数
     * @param tableName 表名
     * @param databaseName 数据库名（可选）
     * @return 生成的SQL语句
     */
    public static String getSQL(List<List<String>> rows, List<String> header, int whereCount, String tableName, String databaseName) {
        if (rows.isEmpty() || header.isEmpty()) {
            throw new BusinessException("行数据或表头不完整，无法生成DELETE语句");
        }
        if (whereCount < 1 || whereCount >= header.size()) {
            whereCount = header.size();
        }
        if (tableName == null || tableName.isEmpty()) {
            throw new BusinessException("表名不能为空，无法生成DELETE语句");
        }

        // 检查WHERE条件列是否有空值，以及是否所有行的WHERE条件都相同
        boolean canUseBatchDelete = true;
        boolean hasNullInWhere = false;
        
        // 检查第一行的WHERE条件值
        List<String> firstWhereValues = new ArrayList<>();
        for (int i = 0; i < whereCount; i++) {
            String value = ExcelParse.getCell(rows.get(0), i);
            firstWhereValues.add(value);
            if (value == null || value.isEmpty()) {
                hasNullInWhere = true;
            }
        }
        
        // 检查所有行的WHERE条件是否相同
        for (List<String> row : rows) {
            for (int i = 0; i < whereCount; i++) {
                String firstValue = firstWhereValues.get(i);
                String currentValue = ExcelParse.getCell(row, i);
                // 修复空指针异常：使用Objects.equals或手动判断null
                if ((firstValue == null && currentValue != null) || 
                    (firstValue != null && !firstValue.equals(currentValue))) {
                    canUseBatchDelete = false;
                    break;
                }
            }
            if (!canUseBatchDelete) break;
        }

        Set<String> sqlSet = new LinkedHashSet<>(); // 使用Set去重
        // 批量delete：单列where条件、WHERE条件相同且无空值时使用
        if (whereCount == 1 && canUseBatchDelete && !hasNullInWhere) {
            // 生成 in 批量 delete
            StringBuilder sql = new StringBuilder("DELETE FROM ");
            
            // 如果有数据库名，则添加数据库名前缀
            if (databaseName != null && !databaseName.trim().isEmpty()) {
                sql.append("`").append(databaseName.trim()).append("`.");
            }
            
            sql.append("`").append(tableName).append("`").append(" WHERE ");
            sql.append("`").append(header.get(0)).append("`").append(" IN (");
            for (int i = 0; i < rows.size(); i++) {
                if (i > 0) sql.append(", ");
                sql.append("'").append(ExcelParse.getCell(rows.get(i), 0)).append("'");
            }
            sql.append(");");
            sqlSet.add(sql.toString());
        } else {
            // 生成单条 delete
            for (List<String> row : rows) {
                StringBuilder sql = new StringBuilder("DELETE FROM ");
                
                // 如果有数据库名，则添加数据库名前缀
                if (databaseName != null && !databaseName.trim().isEmpty()) {
                    sql.append("`").append(databaseName.trim()).append("`.");
                }
                
                sql.append("`").append(tableName).append("`").append(" WHERE ");
                for (int i = 0; i < whereCount; i++) {
                    if (i > 0) sql.append(" AND ");
                    sql.append("`").append(header.get(i)).append("`");
                    String cellValue = ExcelParse.getCell(row, i);
                    if (cellValue != null && !cellValue.isEmpty()) {
                        sql.append(" = ").append("'").append(cellValue).append("'");
                    } else {
                        sql.append(" IS NULL");
                    }
                }
                sql.append(";");
                sqlSet.add(sql.toString());
            }
        }
        return String.join("\n", sqlSet);
    }

    /**
     * 生成DELETE语句（兼容旧版本）
     * @param rows 行数据
     * @param header 表头/列名
     * @param whereCount 条件列数
     * @param tableName 表名
     * @return 生成的SQL语句
     */
    public static String getSQL(List<List<String>> rows, List<String> header, int whereCount, String tableName) {
        return getSQL(rows, header, whereCount, tableName, null);
    }
}
