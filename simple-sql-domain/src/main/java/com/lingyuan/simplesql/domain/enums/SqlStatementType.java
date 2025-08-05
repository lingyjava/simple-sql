package com.lingyuan.simplesql.domain.enums;

/**
 * SQL语句类型枚举
 */
public enum SqlStatementType {
    INSERT("INSERT"),
    UPDATE("UPDATE"),
    CREATE_TABLE("CREATE TABLE"),
    ALTER_TABLE("ALTER TABLE"),
    DELETE("DELETE"),
    DROP("DROP"),
    SELECT("SELECT");

    private final String keyword;

    SqlStatementType(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    /**
     * 判断是否为可回退的语句类型
     * UPDATE和DELETE需要原数据进行还原，INSERT可以直接删除
     * DROP TABLE和ALTER TABLE忽略不予处理
     */
    public boolean isReversible() {
        return this == INSERT;
    }
} 