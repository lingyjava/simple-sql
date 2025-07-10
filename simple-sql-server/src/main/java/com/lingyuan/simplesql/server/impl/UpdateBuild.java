package com.lingyuan.simplesql.server.impl;

import com.lingyuan.simplesql.common.util.ValidationUtil;
import com.lingyuan.simplesql.domain.model.UpdateParams;

import java.util.Map;

public class UpdateBuild {

    public String buildUpdateSQL(UpdateParams params) {
        ValidationUtil.validate(params);

        StringBuilder sql = new StringBuilder("UPDATE ");

        if (params.getDatabaseName() != null) {
            sql.append(params.getDatabaseName()).append(".");
        }
        sql.append(params.getTableName()).append(" SET ");

        // 构建更新字段部分
        for (Map.Entry<String, Object> entry : params.getUpdateFields().entrySet()) {
            sql.append(entry.getKey()).append(" = '").append(entry.getValue()).append("', ");
        }
        sql.setLength(sql.length() - 2); // 移除最后的逗号和空格

        // 构建条件部分
        if (params.getWhereClause() != null && !params.getWhereClause().isEmpty()) {
            sql.append(" WHERE ");
            for (Map.Entry<String, Object> entry : params.getWhereClause().entrySet()) {
                sql.append(entry.getKey()).append(" = '").append(entry.getValue()).append("' AND ");
            }
            sql.setLength(sql.length() - 5); // 移除最后的 AND
        }

        // 添加限制
        if (params.getLimit() > 0) {
            sql.append(" LIMIT ").append(params.getLimit());
        }

        return sql.toString();
    }
}
