package com.lingyuan.simplesql.server.handler;

import com.lingyuan.simplesql.common.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;

public class GenerateDeleteHandler {

    public static String getSQL(List<List<String>> rows, List<String> header, int whereCount, String tableName) {
        if (rows.isEmpty() || header.isEmpty()) {
            throw new BusinessException("行数据或表头不完整，无法生成DELETE语句");
        }
        if (whereCount < 1 || whereCount >= header.size()) {
            throw new BusinessException("WHERE条件数量不正确，必须大于0且小于表头列数");
        }
        if (tableName == null || tableName.isEmpty()) {
            throw new BusinessException("表名不能为空，无法生成DELETE语句");
        }

        List<String> sqlList = new ArrayList<>();
        if (whereCount == 1) {
            // 生成 in 批量 delete
            StringBuilder sql = new StringBuilder("DELETE FROM ").append(tableName).append(" WHERE ");
            sql.append("`").append(header.get(0)).append("`").append(" IN (");
            for (int i = 0; i < rows.size(); i++) {
                if (i > 0) sql.append(", ");
                sql.append("'").append(rows.get(i).get(0)).append("'");
            }
            sql.append(");");
            sqlList.add(sql.toString());
        } else {
            // 生成单条 delete
            for (List<String> row : rows) {
                StringBuilder sql = new StringBuilder("DELETE FROM ").append(tableName).append(" WHERE ");
                for (int i = 0; i < whereCount; i++) {
                    if (i > 0) sql.append(" AND ");
                    sql.append("`").append(header.get(i)).append("`").append(" = '").append(row.get(i)).append("'");
                }
                sql.append(";");
                sqlList.add(sql.toString());
            }
        }
        return String.join("\n", sqlList);
    }
}
