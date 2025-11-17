package com.lingyuan.simplesql.common.db;

import com.lingyuan.simplesql.common.util.FileUtil;

import java.io.File;
import java.util.List;

/**
 * 表名和库名管理数据库助手
 */
public class TableDictionaryHelper {
    
    private final SQLiteHelper dbHelper;
    private static final String DB_FILE_NAME = "table_dictionary.db";
    
    public TableDictionaryHelper() {
        String dbPath = FileUtil.getAppDataDir() + File.separator + DB_FILE_NAME;
        this.dbHelper = new SQLiteHelper(dbPath);
        initDatabase();
    }
    
    /**
     * 初始化数据库表
     */
    private void initDatabase() {
        String createTableSql = """
            CREATE TABLE IF NOT EXISTS table_dictionary_info (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                table_name TEXT NOT NULL,
                database_name TEXT,
                remark TEXT,
                create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        dbHelper.executeDML(createTableSql);
    }
    
    /**
     * 添加表名和库名
     */
    public void addTableDictionary(String tableName, String databaseName, String remark) {
        String sql = "INSERT INTO table_dictionary_info (table_name, database_name, remark) VALUES (?, ?, ?)";
        dbHelper.executeDML(sql, tableName, databaseName, remark);
    }
    
    /**
     * 删除表名和库名
     */
    public void deleteTableDictionary(int id) {
        String sql = "DELETE FROM table_dictionary_info WHERE id = ?";
        dbHelper.executeDML(sql, id);
    }
    
    /**
     * 获取所有表名和库名
     */
    public List<TableDictionaryInfo> getAllTableDictionary() {
        String sql = "SELECT id, table_name, database_name, remark, create_time, update_time FROM table_dictionary_info ORDER BY create_time DESC";
        return dbHelper.query(sql, rs -> {
            try {
                TableDictionaryInfo info = new TableDictionaryInfo();
                info.setId(rs.getInt("id"));
                info.setTableName(rs.getString("table_name"));
                info.setDatabaseName(rs.getString("database_name"));
                info.setRemark(rs.getString("remark"));
                info.setCreateTime(rs.getTimestamp("create_time"));
                info.setUpdateTime(rs.getTimestamp("update_time"));
                return info;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    /**
     * 根据表名搜索
     */
    public List<TableDictionaryInfo> searchByTableName(String tableName) {
        String sql = "SELECT id, table_name, database_name, remark, create_time, update_time FROM table_dictionary_info WHERE table_name LIKE ? ORDER BY create_time DESC";
        return dbHelper.query(sql, rs -> {
            try {
                TableDictionaryInfo info = new TableDictionaryInfo();
                info.setId(rs.getInt("id"));
                info.setTableName(rs.getString("table_name"));
                info.setDatabaseName(rs.getString("database_name"));
                info.setRemark(rs.getString("remark"));
                info.setCreateTime(rs.getTimestamp("create_time"));
                info.setUpdateTime(rs.getTimestamp("update_time"));
                return info;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, "%" + tableName + "%");
    }
    
    /**
     * 根据库名搜索
     */
    public List<TableDictionaryInfo> searchByDatabaseName(String databaseName) {
        String sql = "SELECT id, table_name, database_name, remark, create_time, update_time FROM table_dictionary_info WHERE database_name LIKE ? ORDER BY create_time DESC";
        return dbHelper.query(sql, rs -> {
            try {
                TableDictionaryInfo info = new TableDictionaryInfo();
                info.setId(rs.getInt("id"));
                info.setTableName(rs.getString("table_name"));
                info.setDatabaseName(rs.getString("database_name"));
                info.setRemark(rs.getString("remark"));
                info.setCreateTime(rs.getTimestamp("create_time"));
                info.setUpdateTime(rs.getTimestamp("update_time"));
                return info;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, "%" + databaseName + "%");
    }
    
    /**
     * 表名和库名信息实体类
     */
    public static class TableDictionaryInfo {
        private int id;
        private String tableName;
        private String databaseName;
        private String remark;
        private java.sql.Timestamp createTime;
        private java.sql.Timestamp updateTime;
        
        // Getters and Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        
        public String getTableName() { return tableName; }
        public void setTableName(String tableName) { this.tableName = tableName; }
        
        public String getDatabaseName() { return databaseName; }
        public void setDatabaseName(String databaseName) { this.databaseName = databaseName; }
        
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
        
        public java.sql.Timestamp getCreateTime() { return createTime; }
        public void setCreateTime(java.sql.Timestamp createTime) { this.createTime = createTime; }
        
        public java.sql.Timestamp getUpdateTime() { return updateTime; }
        public void setUpdateTime(java.sql.Timestamp updateTime) { this.updateTime = updateTime; }
    }
}