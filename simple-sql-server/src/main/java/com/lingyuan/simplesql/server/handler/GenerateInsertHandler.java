package com.lingyuan.simplesql.server.handler;

import com.lingyuan.simplesql.common.exception.BusinessException;

import java.util.List;

public class GenerateInsertHandler {

    /**
     * 生成INSERT语句
     * @param rows 行数据
     * @param header 表头/列名
     * @param tableName 表名
     * @return 生成的SQL语句
     */
    public static String getSQL(List<List<String>> rows, List<String> header, String tableName) {
        if (rows.isEmpty() || header.isEmpty()) {
            throw new BusinessException("行数据或表头不完整，无法生成INSERT语句");
        }
        if (tableName == null || tableName.isEmpty()) {
            throw new BusinessException("表名不能为空，无法生成INSERT语句");
        }

        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append("`").append(tableName).append("` (");

        // 添加列名
        for (int i = 0; i < header.size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append("`").append(header.get(i)).append("`");
        }
        sql.append(") VALUES \n");

        // 添加行数据
        for (int i = 0; i < rows.size(); i++) {
            if (i > 0) sql.append(", \n");
            sql.append("(");
            for (int j = 0; j < rows.get(i).size(); j++) {
                if (j > 0) sql.append(", ");
                String data = rows.get(i).get(j);
                if (data == null || data.isEmpty()) {
                    sql.append("NULL");
                } else {
                    sql.append("'").append(data).append("'");
                }
            }
            sql.append(")");
        }
        sql.append(";");
        return sql.toString();
    }
}
