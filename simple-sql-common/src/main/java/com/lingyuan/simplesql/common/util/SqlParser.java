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
     * 支持批量插入和多行语句，支持数据库名.表名格式，支持反引号包裹
     */
    private static SqlStatement parseInsertStatement(String statement) {
        // 匹配 INSERT INTO `database`.`table` (columns) VALUES (values), (values), ...
        // 或者 INSERT INTO database.table (columns) VALUES (values), (values), ...
        // 或者 INSERT INTO `table` (columns) VALUES (values), (values), ...
        // 支持多行和批量插入
        Pattern pattern = Pattern.compile(
            "INSERT\\s+INTO\\s+((?:`[^`]+`\\.)?`[^`]+`|[^`\\s]+(?:\\.[^`\\s]+)?)\\s*\\(([^)]+)\\)\\s*VALUES\\s*([^;]+)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        );
        
        Matcher matcher = pattern.matcher(statement);
        if (matcher.find()) {
            String fullTableName = matcher.group(1);
            String columns = matcher.group(2);
            String valuesPart = matcher.group(3);
            
            // 解析数据库名和表名
            String databaseName = null;
            String tableName = fullTableName;
            
            // 处理反引号包裹的情况
            if (fullTableName.contains("`.")) {
                // 格式：`database`.`table`
                Pattern dbTablePattern = Pattern.compile("`([^`]+)`\\.`([^`]+)`");
                Matcher dbTableMatcher = dbTablePattern.matcher(fullTableName);
                if (dbTableMatcher.find()) {
                    databaseName = dbTableMatcher.group(1).trim();
                    tableName = dbTableMatcher.group(2).trim();
                }
            } else if (fullTableName.contains("`") && fullTableName.contains(".")) {
                // 格式：database.`table` 或 `database`.table
                Pattern mixedPattern = Pattern.compile("([^`\\s]+)\\.`([^`]+)`");
                Matcher mixedMatcher = mixedPattern.matcher(fullTableName);
                if (mixedMatcher.find()) {
                    databaseName = mixedMatcher.group(1).trim();
                    tableName = mixedMatcher.group(2).trim();
                } else {
                    // 尝试 `database`.table 格式
                    Pattern mixedPattern2 = Pattern.compile("`([^`]+)`\\.([^`\\s]+)");
                    Matcher mixedMatcher2 = mixedPattern2.matcher(fullTableName);
                    if (mixedMatcher2.find()) {
                        databaseName = mixedMatcher2.group(1).trim();
                        tableName = mixedMatcher2.group(2).trim();
                    }
                }
            } else if (fullTableName.contains(".")) {
                // 格式：database.table
                String[] parts = fullTableName.split("\\.");
                if (parts.length == 2) {
                    databaseName = parts[0].trim();
                    tableName = parts[1].trim();
                }
            } else if (fullTableName.startsWith("`") && fullTableName.endsWith("`")) {
                // 格式：`table`
                tableName = fullTableName.substring(1, fullTableName.length() - 1).trim();
            }
            
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
                // 如果有数据库名，将其添加到表名中，用特殊分隔符分隔
                String finalTableName = databaseName != null ? databaseName + "|" + tableName : tableName;
                
                return new SqlStatement(
                    SqlStatementType.INSERT,
                    finalTableName,
                    statement,
                    columns,
                    allValues.toString()
                );
            }
        }
        
        return null;
    }


} 