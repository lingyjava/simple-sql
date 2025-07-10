package com.lingyuan.simplesql.server.impl;

import com.lingyuan.simplesql.common.util.ValidationUtil;
import com.lingyuan.simplesql.domain.model.SelectParams;
import jakarta.validation.constraints.NotNull;

import java.util.stream.Collectors;

public class SelectBuild {

    public String buildSelectQuery(@NotNull SelectParams params) {
        ValidationUtil.validate(params);
        StringBuilder query = new StringBuilder("SELECT ");

        if (params.isSelectAllFields()) {
            query.append("*");
        } else {
            query.append(String.join(", ", params.getSelectFields()));
        }

        // database name maybe is null
        query.append(" FROM ");
        if (params.getDatabaseName() != null) {
            query.append(params.getDatabaseName()).append(".");
        }
        query.append(params.getTableName());

        if (params.getWhereClause() != null && !params.getWhereClause().isEmpty()) {
            query.append(" WHERE ");
            query.append(params.getWhereClause().entrySet().stream()
                .map(entry -> entry.getKey() + " = '" + entry.getValue() + "'")
                .collect(Collectors.joining(" AND ")));
        }

        if (params.getOrderBy() != null && params.getOrderBy().length > 0) {
            query.append(" ORDER BY ").append(String.join(", ", params.getOrderBy()));
        }

        if (params.getLimit() > 0) {
            query.append(" LIMIT ").append(params.getLimit());
        }

        return query.toString();
    }
}
