package com.lingyuan.simplesql.server.impl;

import com.lingyuan.simplesql.common.util.FileUtil;
import com.lingyuan.simplesql.common.util.SqlParser;
import com.lingyuan.simplesql.domain.dto.SqlGeneratorParam;
import com.lingyuan.simplesql.domain.model.SqlStatement;
import com.lingyuan.simplesql.server.SqlGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * SQL 生成器 - 回退脚本实现
 */
public class SqlGeneratorRollbackImpl implements SqlGenerator {

    @Override
    public Object generate(SqlGeneratorParam param) {
        try {
            // 读取SQL文件内容
            String sqlContent = readSqlFile(param.getFilePath());
            
            // 解析SQL语句
            List<SqlStatement> statements = SqlParser.parseReversibleStatements(sqlContent);
            
            // 生成回退脚本
            String rollbackScript = generateRollbackScript(statements, param);
            
            // 保存回退脚本文件
            String outputPath = saveRollbackScript(rollbackScript, param);
            
            return outputPath;
            
        } catch (Exception e) {
            throw new RuntimeException("生成回退脚本失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String getType() {
        return "ROLLBACK";
    }

    /**
     * 读取SQL文件内容
     */
    private String readSqlFile(String sqlFilePath) throws IOException {
        if (sqlFilePath == null || sqlFilePath.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL文件路径不能为空");
        }
        
        File sqlFile = new File(sqlFilePath);
        if (!sqlFile.exists()) {
            throw new IllegalArgumentException("SQL文件不存在: " + sqlFilePath);
        }
        
        return new String(Files.readAllBytes(Paths.get(sqlFilePath)));
    }

    /**
     * 生成回退脚本
     */
    private String generateRollbackScript(List<SqlStatement> statements, SqlGeneratorParam param) {
        StringBuilder script = new StringBuilder();
        
        // 添加头部注释
        script.append("-- SQL回退脚本\n");
        script.append("-- 生成时间: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        script.append("-- 原始SQL文件: ").append(param.getFilePath()).append("\n");
        script.append("-- 可回退语句数量: ").append(statements.size()).append("\n");
        script.append("-- 注意: 仅支持INSERT语句，其他语句类型会被忽略\n\n");

        if (statements.isEmpty()) {
            script.append("-- 未找到可回退的SQL语句\n");
            script.append("-- 支持的语句类型: INSERT\n");
            script.append("-- 忽略的语句类型: SELECT, DELETE, DROP, UPDATE, ALTER TABLE, CREATE TABLE\n");
        } else {
            // 按语句类型分组
            script.append("-- ==========================================\n");
            script.append("-- 可回退的SQL语句\n");
            script.append("-- ==========================================\n\n");
            
            for (int i = 0; i < statements.size(); i++) {
                SqlStatement statement = statements.get(i);
                script.append("-- 原始语句 ").append(i + 1).append(": ").append(statement.getType().getKeyword()).append("\n");
                
                // 修复多行注释问题：将原始SQL按行分割并每行添加注释
                String[] originalSqlLines = statement.getOriginalSql().split("\n");
                for (String line : originalSqlLines) {
                    if (!line.trim().isEmpty()) {
                        script.append("-- ").append(line.trim()).append("\n");
                    }
                }
                
                script.append("-- 回退语句:\n");
                
                String rollbackSql = statement.generateRollbackSql();
                if (rollbackSql != null) {
                    script.append(rollbackSql).append("\n");
                } else {
                    script.append("-- 无法自动生成回退语句\n");
                }
                script.append("\n");
            }
        }
        
        // 添加使用说明
        script.append("-- ==========================================\n");
        script.append("-- 使用说明\n");
        script.append("-- ==========================================\n");
        script.append("-- 1. 执行前请备份数据库\n");
        script.append("-- 2. 仔细检查每个回退语句的正确性\n");
        script.append("-- 3. 建议在测试环境中先验证回退脚本\n");
        
        return script.toString();
    }

    /**
     * 保存回退脚本文件
     */
    private String saveRollbackScript(String rollbackScript, SqlGeneratorParam param) {
        try {
            // 生成输出文件名
            String originalFileName = new File(param.getFilePath()).getName();
            String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
            String outputPath = FileUtil.getDefaultOutputFilePath(String.format("ROLLBACK-%s", baseName));
            FileUtil.writeStringToFile(rollbackScript, outputPath);
            
            return outputPath;
            
        } catch (Exception e) {
            throw new RuntimeException("保存回退脚本失败: " + e.getMessage(), e);
        }
    }
} 