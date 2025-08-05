package com.lingyuan.simplesql.server.handler;

import com.lingyuan.simplesql.common.exception.BusinessException;
import com.lingyuan.simplesql.common.util.ExcelParse;

import java.util.ArrayList;
import java.util.List;

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

        List<String> sqlList = new ArrayList<>();
        if (whereCount == 1) {
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
            sqlList.add(sql.toString());
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
                    sql.append("`").append(header.get(i)).append("`").append(" = ");
                    sql.append("'").append(ExcelParse.getCell(row, i)).append("'");
                }
                sql.append(";");
                sqlList.add(sql.toString());
            }
        }
        return String.join("\n", sqlList);
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
