package com.lingyuan.simplesql.common.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * SQLite 数据库操作助手类
 * 提供通用的 DML 和查询方法，支持自定义行映射
 */
public class SQLiteHelper {

    private final String dbUrl;

    public SQLiteHelper(String dbFilePath) {
        // 如设置 jdbc:sqlite::memory:，则创建的是临时内存数据库，程序关闭即丢失。
        this.dbUrl = "jdbc:sqlite:" + dbFilePath;
    }

    /**
     * 获取数据库连接
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    /**
     * 通用更新（INSERT, UPDATE, DELETE）
     */
    public int executeDML(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setParams(stmt, params);
            return stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("SQLite update failed: " + e.getMessage(), e);
        }
    }

    /**
     * 通用查询（支持自定义行映射）
     */
    public <T> List<T> query(String sql, Function<ResultSet, T> mapper, Object... params) {
        List<T> result = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setParams(stmt, params);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapper.apply(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("SQLite query failed: " + e.getMessage(), e);
        }

        return result;
    }

    /**
     * 设置 SQL 参数
     */
    private void setParams(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }

    /**
     * 主方法示例
     * @param args
     */
    public static void main(String[] args) {
        SQLiteHelper db = new SQLiteHelper("data.db");

        // 创建表
        String createTableSql = "CREATE TABLE IF NOT EXISTS ss_table_demo (id TEXT PRIMARY KEY, name TEXT)";
        db.executeDML(createTableSql);

        // 插入数据
        String insertSql = "INSERT INTO ss_table_demo (id, name) VALUES (?, ?)";
        db.executeDML(insertSql, "1", "LingYuan");

        // 查询数据
        db.query("select name from ss_table_demo where id = ?", rs -> {
            try {
                return rs.getString("name");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, 1).forEach(System.out::println);

        // 更新数据
        String updateSql = "update ss_table_demo set name = ? where id = ?";
        db.executeDML(updateSql, "LingYuan666", "1");

        // 再次查询数据
        String querySQL = "select name from ss_table_demo";
        db.query(querySQL, rs -> {
            try {
                return rs.getString("name");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).forEach(System.out::println);

    }
}

