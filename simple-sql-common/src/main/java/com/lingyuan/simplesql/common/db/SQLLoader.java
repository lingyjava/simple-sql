package com.lingyuan.simplesql.common.db;

import com.lingyuan.simplesql.common.exception.BusinessException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SQLLoader {

    private static final Map<String, Map<String, String>> sqlCache = new HashMap<>();

    public static String getSQL(String fileName, String sqlName) {
        if (!sqlCache.containsKey(fileName)) {
            loadSQLFile(fileName);
        }
        return sqlCache.get(fileName).get(sqlName);
    }

    private static void loadSQLFile(String fileName) {
        Map<String, String> sqlMap = new HashMap<>();
        // 加载 sql 目录下的 SQL 文件
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(SQLLoader.class.getClassLoader().getResourceAsStream("sql/" + fileName))))) {
            String line;
            String currentName = null;
            StringBuilder sqlBuilder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // 解析 SQL 名称
                if (line.startsWith("-- name:")) {
                    if (currentName != null) {
                        sqlMap.put(currentName, sqlBuilder.toString().trim());
                        sqlBuilder.setLength(0);
                    }
                    currentName = line.substring(8).trim();
                } else if (!line.isEmpty() && !line.startsWith("--")) {
                    sqlBuilder.append(line).append(" ");
                }
            }

            if (currentName != null) {
                sqlMap.put(currentName, sqlBuilder.toString().trim());
            }
            sqlCache.put(fileName, sqlMap);
        } catch (Exception e) {
            throw new BusinessException("Failed to load SQL file: " + fileName, e);
        }
    }

    public static void main(String[] args) {
        System.out.println(SQLLoader.getSQL("ss_table_demo.sql", "CreateTable"));
        System.out.println(SQLLoader.getSQL("ss_table_demo.sql", "SelectById"));
        System.out.println(SQLLoader.getSQL("ss_table_demo.sql", "SelectAll"));
        System.out.println(SQLLoader.getSQL("ss_table_demo.sql", "InsertTable"));
        System.out.println(SQLLoader.getSQL("ss_table_demo.sql", "UpdateTable"));
        System.out.println(SQLLoader.getSQL("ss_table_demo.sql", "DeleteById"));
    }
}
