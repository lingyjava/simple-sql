package com.lingyuan.simplesql.server.handler;

import com.lingyuan.simplesql.common.exception.BusinessException;
import com.lingyuan.simplesql.common.util.ExcelParse;

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

        // 检查WHERE条件列是否有空值，并收集去重后的值
        boolean hasNullInWhere = false;
        Set<String> uniqueWhereValues = new LinkedHashSet<>();
        
        for (List<String> row : rows) {
            for (int i = 0; i < whereCount; i++) {
                String value = ExcelParse.getCell(row, i);
                if (value == null || value.isEmpty()) {
                    hasNullInWhere = true;
                } else {
                    // 只收集第一列的值用于批量delete判断
                    if (i == 0) {
                        uniqueWhereValues.add(value);
                    }
                }
            }
        }

        Set<String> sqlSet = new LinkedHashSet<>(); // 使用Set去重
        // 批量delete：单列where条件、WHERE条件无空值、去重后的值数量大于1时使用
        if (whereCount == 1 && !hasNullInWhere && uniqueWhereValues.size() > 1) {
            // 生成 in 批量 delete
            StringBuilder sql = new StringBuilder("DELETE FROM ");
            
            // 如果有数据库名，则添加数据库名前缀
            if (databaseName != null && !databaseName.trim().isEmpty()) {
                sql.append("`").append(databaseName.trim()).append("`.");
            }
            
            sql.append("`").append(tableName).append("`").append(" WHERE ");
            sql.append("`").append(header.get(0)).append("`").append(" IN (");
            int index = 0;
            for (String value : uniqueWhereValues) {
                if (index > 0) sql.append(", ");
                sql.append("'").append(value).append("'");
                index++;
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
