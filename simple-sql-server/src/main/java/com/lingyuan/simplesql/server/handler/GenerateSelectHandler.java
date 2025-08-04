package com.lingyuan.simplesql.server.handler;

import com.lingyuan.simplesql.common.exception.BusinessException;
import com.lingyuan.simplesql.common.util.ExcelParseUtil;

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
     * @return 生成的SQL语句
     */
    public static String getSQL(List<List<String>> rows, List<String> header, String tableName) {
        if (rows.isEmpty() || header.isEmpty()) {
            throw new BusinessException("行数据或表头不完整，无法生成SELECT语句");
        }
        if (tableName == null || tableName.isEmpty()) {
            throw new BusinessException("表名不能为空，无法生成SELECT语句");
        }

        StringBuilder sql = new StringBuilder("SELECT * ");
        sql.append("FROM `").append(tableName).append("` WHERE ");

        for (int i = 0; i < header.size(); i++) {
            Set<String> colValues = new LinkedHashSet<>(); 
            for (List<String> row : rows) {
                colValues.add(ExcelParseUtil.getCell(row, i));
            }
            if (i > 0) sql.append(" AND ");
            sql.append("`").append(header.get(i)).append("`");
            if (colValues.size() == 1) {
                sql.append(" = ").append("'").append(ExcelParseUtil.getCell(rows.get(0), i)).append("'");
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
}
