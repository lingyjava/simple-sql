package com.lingyuan.simplesql.server.handler;

import com.lingyuan.simplesql.common.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;

public class GenerateUpdateHandler {

    /**
     * 生成UPDATE语句
     * @param rows 行数据
     * @param header 表头/列名
     * @param whereCount WHERE条件列数
     * @param tableName 表名
     * @return 生成的SQL语句
     */
    public static String getSQL(List<List<String>> rows, List<String> header, int whereCount, String tableName) {
        if (rows.isEmpty() || header.isEmpty()) {
            throw new BusinessException("行数据或表头不完整，无法生成UPDATE语句");
        }
        if (whereCount < 1 || whereCount >= header.size()) {
            throw new BusinessException("WHERE条件数量不正确，必须大于0且小于表头列数");
        }
        if (tableName == null || tableName.isEmpty()) {
            throw new BusinessException("表名不能为空，无法生成UPDATE语句");
        }

        // 检查whereCount是否超过表头长度
        List<String> setColumns = new ArrayList<>();
        for (int i = whereCount; i < header.size(); i++) {
            setColumns.add(header.get(i));
        }
        if (setColumns.isEmpty()) {
            throw new BusinessException("没有可更新的列，请检查表头和数据");
        }

        // 检查set字段值是否都相同
        boolean allSetEqual = true;
        List<String> firstSet = new ArrayList<>();
        for (int i = whereCount; i < header.size(); i++) {
            firstSet.add(rows.get(0).get(i));
        }
        for (List<String> row : rows) {
            for (int i = 0, j = whereCount; j < header.size(); j++) {
                // 不比较空值
                if (firstSet.get(i) == null || row.get(j) == null) {
                    continue;
                }
                if (!firstSet.get(i).equals(row.get(j))) {
                    allSetEqual = false;
                    break;
                }
                i++;
            }
            if (!allSetEqual) break;
        }

        List<String> sqlList = new ArrayList<>();
        // 批量update （仅为单列where条件且值相同时触发）：因为批量时WHERE用AND拼接，避免多列where条件时可能导致不是想要的结果
        if (allSetEqual && whereCount == 1) {
            StringBuilder sql = new StringBuilder("UPDATE ");
            sql.append("`").append(tableName).append("`");
            StringBuilder setClause = new StringBuilder(" SET ");
            for (int i = 0; i < setColumns.size(); i++) {
                if (i > 0) setClause.append(", ");
                setClause.append("`").append(setColumns.get(i)).append("`").append(" = '").append(firstSet.get(i)).append("'");
            }
            StringBuilder whereIn = new StringBuilder();
            whereIn.append(" WHERE ");
            for (int i = 0; i < whereCount; i++) {
                if (i > 0) whereIn.append(" AND ");
                whereIn.append("`").append(header.get(i)).append("`").append(" IN (");
                for (int j = 0; j < rows.size(); j++) {
                    if (j > 0) whereIn.append(",");
                    whereIn.append("'").append(rows.get(j).get(i)).append("'");
                }
                whereIn.append(")");
            }
            sql.append(setClause).append(whereIn).append(";");
            sqlList.add(sql.toString());
        } else {
            // 单行 update
            for (List<String> row : rows) {
                StringBuilder sql = new StringBuilder("UPDATE ");
                sql.append("`").append(tableName).append("`");
                StringBuilder setClause = new StringBuilder( " SET " );
                int setIdx = 0;
                for (int i = whereCount; i < header.size(); i++) {
                    if (setIdx > 0) setClause.append(", ");
                    setClause.append("`").append(header.get(i)).append("`").append(" = '").append(row.get(i)).append("'");
                    setIdx++;
                }
                StringBuilder whereClause = new StringBuilder(" WHERE ");
                for (int i = 0; i < whereCount; i++) {
                    if (i > 0) whereClause.append(" AND ");
                    whereClause.append("`").append(header.get(i)).append("`").append(" = '").append(row.get(i)).append("'");
                }
                sql.append(setClause).append(whereClause).append(";");
                sqlList.add(sql.toString());
            }
        }
        return String.join("\n", sqlList);
    }
}
