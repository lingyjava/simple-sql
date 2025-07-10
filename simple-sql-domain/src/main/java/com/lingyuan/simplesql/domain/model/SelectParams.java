package com.lingyuan.simplesql.domain.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * 用户生成 select 语句的参数类
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SelectParams implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据库名称
     */
    private String databaseName;

    /**
     * 表名
     */
    @NotNull
    private String tableName;

    /**
     * 查询字段
     */
    private String[] selectFields;

    /**
     * 使用 * 号查询所有字段
     */
    private boolean selectAllFields;

    /**
     * 查询条件
     */
    private Map<String, Object> whereClause;

    /**
     * 排序字段
     */
    private String[] orderBy;

    /**
     * 分页参数
     */
    private int limit;

}
