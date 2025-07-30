package com.lingyuan.simplesql.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SqlGeneratorParam implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "功能类型不能为空")
    private String type; // 功能类型
    /**
     * 数据库名称
     */
    private String databaseName;
    /**
     * 表名
     */
    private String tableName;
    /**
     * Excel文件路径
     */
    private String excelPath;

    /**
     * SQL语句类型
     */
    private String sqlType;

    /** 条件列数 */
    private Integer whereColumnCount;

    private String outputPath; // 输出路径
}
