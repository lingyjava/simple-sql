package com.lingyuan.simplesql.domain.model;

import com.lingyuan.simplesql.domain.enums.SqlStatementType;

/**
 * SQL语句实体类
 */
public class SqlStatement {
    private final SqlStatementType type;
    private final String tableName;
    private final String originalSql;
    private final String detail1; // 用于存储列名、SET子句等
    private final String detail2; // 用于存储值、WHERE子句等

    public SqlStatement(SqlStatementType type, String tableName, String originalSql, String detail1, String detail2) {
        this.type = type;
        this.tableName = tableName;
        this.originalSql = originalSql;
        this.detail1 = detail1;
        this.detail2 = detail2;
    }

    public SqlStatementType getType() {
        return type;
    }

    public String getTableName() {
        return tableName;
    }

    public String getOriginalSql() {
        return originalSql;
    }

    public String getDetail1() {
        return detail1;
    }

    public String getDetail2() {
        return detail2;
    }

    /**
     * 判断是否为可回退的语句
     */
    public boolean isReversible() {
        return type.isReversible();
    }

    /**
     * 生成回退SQL语句
     * 仅支持INSERT语句
     */
    public String generateRollbackSql() {
        switch (type) {
            case INSERT:
                return generateDeleteFromInsert();
            default:
                return null;
        }
    }

    /**
     * 为INSERT语句生成DELETE回退语句
     */
    private String generateDeleteFromInsert() {
        if (detail1 == null || detail2 == null) {
            return null;
        }
        
        // 解析列名
        String[] columns = detail1.split(",");
        
        // 解析所有值（可能包含多个VALUES子句，用分号分隔）
        String[] valueGroups = detail2.split(";");
        
        StringBuilder deleteStatements = new StringBuilder();
        
        for (int groupIndex = 0; groupIndex < valueGroups.length; groupIndex++) {
            String valueGroup = valueGroups[groupIndex].trim();
            if (valueGroup.isEmpty()) {
                continue;
            }
            
            // 解析当前VALUES子句中的值
            String[] values = valueGroup.split(",");
            
            if (columns.length != values.length) {
                continue; // 跳过不匹配的组
            }
            
            StringBuilder whereClause = new StringBuilder();
            for (int i = 0; i < columns.length; i++) {
                if (i > 0) {
                    whereClause.append(" AND ");
                }
                
                String column = columns[i].trim();
                String value = values[i].trim();
                
                // 修复列名重复反引号问题
                // 如果列名已经包含反引号，则不再添加
                if (column.startsWith("`") && column.endsWith("`")) {
                    whereClause.append(column);
                } else {
                    whereClause.append("`").append(column).append("`");
                }
                
                whereClause.append(" = ").append(value);
            }
            
            // 生成DELETE语句
            String deleteStatement = String.format("DELETE FROM `%s` WHERE %s;", tableName, whereClause.toString());
            
            if (groupIndex > 0) {
                deleteStatements.append("\n");
            }
            deleteStatements.append(deleteStatement);
        }
        
        return deleteStatements.toString();
    }



    @Override
    public String toString() {
        return String.format("SqlStatement{type=%s, tableName='%s', originalSql='%s'}", 
            type, tableName, originalSql);
    }
} 