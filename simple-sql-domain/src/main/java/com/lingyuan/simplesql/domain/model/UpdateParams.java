package com.lingyuan.simplesql.domain.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateParams implements Serializable {

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
     * 更新字段及其新值
     */
    @NotNull
    @Size(min = 1, message = "更新字段不能为空")
    private Map<String, Object> updateFields;

    /**
     * 更新条件
     */
    private Map<String, Object> whereClause;

    /**
     * 限制更新的记录数
     */
    private int limit;
}
