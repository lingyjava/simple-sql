package com.lingyuan.simplesql.server.handler;

import com.lingyuan.simplesql.common.exception.BusinessException;
import com.lingyuan.simplesql.common.util.ExcelParse;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GenerateUpdateHandler {

    /**
     * 生成UPDATE语句
     * @param rows 行数据
     * @param header 表头/列名
     * @param whereCount WHERE条件列数
     * @param tableName 表名
     * @param databaseName 数据库名（可选）
     * @return 生成的SQL语句
     */
    public static String getSQL(List<List<String>> rows, List<String> header, int whereCount, String tableName, String databaseName) {
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
        List<String> firstSetValue = new ArrayList<>();
        for (int i = whereCount; i < header.size(); i++) {
            firstSetValue.add(ExcelParse.getCell(rows.get(0), i));
        }
        for (List<String> row : rows) {
            for (int i = 0, j = whereCount; j < header.size(); j++) {
                String firstValue = firstSetValue.get(i);
                String currentValue = ExcelParse.getCell(row, j);
                // 修复空指针异常：使用Objects.equals或手动判断null
                if ((firstValue == null && currentValue != null) || 
                    (firstValue != null && !firstValue.equals(currentValue))) {
                    allSetEqual = false;
                    break;
                }
                i++;
            }
            if (!allSetEqual) break;
        }

        // 检查WHERE条件列是否有空值，以及是否所有行的WHERE条件都相同
        boolean canUseBatchUpdate = true;
        boolean hasNullInWhere = false;
        
        // 检查第一行的WHERE条件值
        List<String> firstWhereValues = new ArrayList<>();
        for (int i = 0; i < whereCount; i++) {
            String value = ExcelParse.getCell(rows.get(0), i);
            firstWhereValues.add(value);
            if (value == null || value.isEmpty()) {
                hasNullInWhere = true;
            }
        }
        
        // 检查所有行的WHERE条件是否相同
        for (List<String> row : rows) {
            for (int i = 0; i < whereCount; i++) {
                String firstValue = firstWhereValues.get(i);
                String currentValue = ExcelParse.getCell(row, i);
                // 修复空指针异常：使用Objects.equals或手动判断null
                if ((firstValue == null && currentValue != null) || 
                    (firstValue != null && !firstValue.equals(currentValue))) {
                    canUseBatchUpdate = false;
                    break;
                }
            }
            if (!canUseBatchUpdate) break;
        }

        Set<String> sqlSet = new LinkedHashSet<>(); // 使用Set去重
        // 批量update：单列where条件、set值相同、WHERE条件相同且无空值时使用
        if (allSetEqual && whereCount == 1 && canUseBatchUpdate && !hasNullInWhere) {
            StringBuilder sql = new StringBuilder("UPDATE ");
            
            // 如果有数据库名，则添加数据库名前缀
            if (databaseName != null && !databaseName.trim().isEmpty()) {
                sql.append("`").append(databaseName.trim()).append("`.");
            }
            
            sql.append("`").append(tableName).append("`");
            // 拼接 set 子句
            StringBuilder setClause = new StringBuilder(" SET ");
            for (int i = 0; i < setColumns.size(); i++) {
                if (i > 0) setClause.append(", ");
                setClause.append("`").append(setColumns.get(i)).append("`");
                if (firstSetValue.get(i) != null && !firstSetValue.get(i).isEmpty()) {
                    setClause.append(" = ").append("'").append(firstSetValue.get(i)).append("'");
                } else {
                    setClause.append(" = NULL");
                }
            }
            // 拼接 where 子句
            StringBuilder whereIn = new StringBuilder();
            whereIn.append(" WHERE ");
            for (int i = 0; i < whereCount; i++) {
                if (i > 0) whereIn.append(" AND ");
                whereIn.append("`").append(header.get(i)).append("`").append(" IN (");
                for (int j = 0; j < rows.size(); j++) {
                    if (j > 0) whereIn.append(",");
                    whereIn.append("'").append(ExcelParse.getCell(rows.get(j), i)).append("'");
                }
                whereIn.append(")");
            }
            sql.append(setClause).append(whereIn).append(";");
            sqlSet.add(sql.toString());
        } else {
            // 单行 update
            for (List<String> row : rows) {
                StringBuilder sql = new StringBuilder("UPDATE ");
                
                // 如果有数据库名，则添加数据库名前缀
                if (databaseName != null && !databaseName.trim().isEmpty()) {
                    sql.append("`").append(databaseName.trim()).append("`.");
                }
                
                sql.append("`").append(tableName).append("`");
                // 拼接 set 子句
                StringBuilder setClause = new StringBuilder( " SET " );
                int setIdx = 0;
                for (int i = whereCount; i < header.size(); i++) {
                    if (setIdx > 0) setClause.append(", ");
                    setClause.append("`").append(header.get(i)).append("`").append(" = ");
                    String cellValue = ExcelParse.getCell(row, i);
                    if (cellValue != null && !cellValue.isEmpty()) {
                        setClause.append("'").append(cellValue).append("'");
                    } else {
                        setClause.append("NULL");
                    }
                    setIdx++;
                }
                // 拼接 where 子句
                StringBuilder whereClause = new StringBuilder(" WHERE ");
                for (int i = 0; i < whereCount; i++) {
                    if (i > 0) whereClause.append(" AND ");
                    whereClause.append("`").append(header.get(i)).append("`");
                    String cellValue = ExcelParse.getCell(row, i);
                    if (cellValue != null && !cellValue.isEmpty()) {
                        whereClause.append(" = ").append("'").append(cellValue).append("'");
                    } else {
                        whereClause.append(" IS NULL");
                    }
                }
                sql.append(setClause).append(whereClause).append(";");
                sqlSet.add(sql.toString());
            }
        }
        return String.join("\n", sqlSet);
    }

    /**
     * 生成UPDATE语句（兼容旧版本）
     * @param rows 行数据
     * @param header 表头/列名
     * @param whereCount WHERE条件列数
     * @param tableName 表名
     * @return 生成的SQL语句
     */
    public static String getSQL(List<List<String>> rows, List<String> header, int whereCount, String tableName) {
        return getSQL(rows, header, whereCount, tableName, null);
    }
}
