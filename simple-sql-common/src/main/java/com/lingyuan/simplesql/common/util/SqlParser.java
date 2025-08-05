package com.lingyuan.simplesql.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lingyuan.simplesql.domain.enums.SqlStatementType;
import com.lingyuan.simplesql.domain.model.SqlStatement;

/**
 * SQL解析器
 * 用于解析SQL文件中的语句
 */
public class SqlParser {

    /**
     * 解析SQL文件内容，提取可回退的语句
     *
     * @param sqlContent SQL文件内容
     * @return 可回退的SQL语句列表
     */
    public static List<SqlStatement> parseReversibleStatements(String sqlContent) {
        List<SqlStatement> statements = new ArrayList<>();
        
        if (sqlContent == null || sqlContent.trim().isEmpty()) {
            return statements;
        }

        // 按分号分割SQL语句
        String[] sqlStatements = sqlContent.split(";");
        
        for (String statement : sqlStatements) {
            statement = statement.trim();
            if (statement.isEmpty()) {
                continue;
            }

            SqlStatement sqlStatement = parseStatement(statement);
            if (sqlStatement != null && sqlStatement.isReversible()) {
                statements.add(sqlStatement);
            }
        }

        return statements;
    }

    /**
     * 解析单个SQL语句
     * 仅支持INSERT语句
     *
     * @param statement SQL语句
     * @return SQL语句对象
     */
    private static SqlStatement parseStatement(String statement) {
        // 移除注释行，只保留实际的SQL语句
        String cleanStatement = removeComments(statement);
        String upperStatement = cleanStatement.toUpperCase().trim();
        
        if (upperStatement.startsWith("INSERT INTO")) {
            return parseInsertStatement(cleanStatement);
        }
        
        // 其他语句类型暂不支持
        return null;
    }
    
    /**
     * 移除SQL语句中的注释行
     */
    private static String removeComments(String statement) {
        StringBuilder result = new StringBuilder();
        String[] lines = statement.split("\n");
        
        for (String line : lines) {
            String trimmedLine = line.trim();
            // 跳过注释行（以--开头）和空行
            if (!trimmedLine.startsWith("--") && !trimmedLine.isEmpty()) {
                result.append(line).append("\n");
            }
        }
        
        return result.toString().trim();
    }

    /**
     * 解析INSERT语句
     * 支持批量插入和多行语句
     */
    private static SqlStatement parseInsertStatement(String statement) {
        // 匹配 INSERT INTO table_name (columns) VALUES (values), (values), ...
        // 支持多行和批量插入
        Pattern pattern = Pattern.compile(
            "INSERT\\s+INTO\\s+`?([^`\\s]+)`?\\s*\\(([^)]+)\\)\\s*VALUES\\s*([^;]+)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        );
        
        Matcher matcher = pattern.matcher(statement);
        if (matcher.find()) {
            String tableName = matcher.group(1);
            String columns = matcher.group(2);
            String valuesPart = matcher.group(3);
            
            // 提取所有VALUES子句用于生成DELETE语句
            // 对于批量插入，我们需要处理所有的值来生成完整的DELETE语句
            Pattern valuePattern = Pattern.compile("\\(([^)]+)\\)");
            Matcher valueMatcher = valuePattern.matcher(valuesPart);
            
            StringBuilder allValues = new StringBuilder();
            boolean firstValue = true;
            
            while (valueMatcher.find()) {
                if (!firstValue) {
                    allValues.append(";");
                }
                allValues.append(valueMatcher.group(1));
                firstValue = false;
            }
            
            if (allValues.length() > 0) {
                return new SqlStatement(
                    SqlStatementType.INSERT,
                    tableName,
                    statement,
                    columns,
                    allValues.toString()
                );
            }
        }
        
        return null;
    }


} 