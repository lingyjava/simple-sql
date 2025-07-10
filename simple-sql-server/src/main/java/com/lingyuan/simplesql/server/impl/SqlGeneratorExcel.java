package com.lingyuan.simplesql.server.impl;

import com.alibaba.excel.EasyExcel;
import com.lingyuan.simplesql.common.exception.BusinessException;
import com.lingyuan.simplesql.server.SqlGenerator;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class SqlGeneratorExcel implements SqlGenerator {

    public static void main(String[] args) {
        SqlGenerator sqlGenerator = new SqlGeneratorExcel();
        sqlGenerator.generateSql(null);
    }

    @Override
    public Object generateSql(Map<String, Object> params) {
        String inputFilePath = getDefaultInputFilePath();
        String outputFilePath = getDefaultOutputFilePath();
        // 如果params不为空，尝试从中获取输入文件路径
        if (params != null && params.containsKey("inputFilePath")) {
            inputFilePath = params.get("inputFilePath").toString();
        } else {
            log.info("未提供输入文件路径，使用默认路径: {}", inputFilePath);
        }
        List<List<String>> data = readExcel(inputFilePath);

        if (data == null || data.size() < 6) {
            throw new BusinessException("Excel文件格式错误或数据不足，至少需要6行数据");
        }
        // 读取配置项（第3行）
        int configRowLine = 2; // 第3行索引为2
        String configTableNames = data.get(configRowLine).get(0);
        String configOutputFile = data.get(configRowLine).get(1);
        String configSqlType = data.get(configRowLine).get(2);
        int configWhereCount = data.get(configRowLine).get(3) != null ? Integer.parseInt(data.get(configRowLine).get(3)) : -1;
        if (configTableNames == null || configOutputFile == null || configSqlType == null || configWhereCount < 0) {
            throw new BusinessException("配置项不完整，请检查Excel文件的前四行");
        }
        if (!configOutputFile.isEmpty() && !"default".equalsIgnoreCase(configOutputFile)) {
            outputFilePath = configOutputFile;
            // 如果是路径，则确保目录存在
            File outputFile = new File(outputFilePath);
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                createDirectory(parentDir.getAbsolutePath());
            }
        }

        // 读取表头
        List<String> header = data.get(4);
        // 读取数据
        List<List<String>> rows = data.subList(5, data.size());

        if (header.isEmpty() || rows.isEmpty()) {
            throw new BusinessException("Excel数据格式错误，表头或数据行不能为空");
        }

        // 生成SQL语句
        String sql;
        switch (configSqlType.toLowerCase()) {
            // case "insert" -> sql = generateInsertSql(rows, header);
            case "update" -> sql = generateUpdateSql(rows, header, configWhereCount, configTableNames);
            case "delete" -> sql = generateDeleteSql(rows, header, configWhereCount, configTableNames);
            default -> throw new BusinessException("不支持的SQL类型: " + configSqlType);
        }
        if (sql == null) {
            throw new BusinessException("SQL生成失败，请检查输入数据和配置项");
        }
        // 写入SQL到文件
        // writeSqlToExcel(sql);
        writeSqlToFile(sql, outputFilePath);
        return outputFilePath;
    }

    private String generateUpdateSql(List<List<String>> rows, List<String> header, int whereCount, String tableNames) {
        if (rows.isEmpty() || header.isEmpty()) {
            throw new BusinessException("行数据或表头不完整，无法生成UPDATE语句");
        }

        if (whereCount < 1 || whereCount >= header.size()) {
            throw new BusinessException("WHERE条件数量不正确，必须大于0且小于表头列数");
        }

        List<String> setColumns = new ArrayList<>();
        for (int i = whereCount; i < header.size(); i++) {
            setColumns.add(header.get(i));
        }
        if (setColumns.isEmpty()) {
            throw new BusinessException("没有可更新的列，请检查表头和数据");
        }

        // 检查set字段值是否都相同
        boolean allSetEqual = true;
        List<String> firstSet = new ArrayList<>();
        for (int i = whereCount; i < header.size(); i++) {
            firstSet.add(rows.get(0).get(i));
        }
        for (List<String> row : rows) {
            for (int i = 0, j = whereCount; j < header.size(); j++) {
                if (!firstSet.get(i).equals(row.get(j))) {
                    allSetEqual = false;
                    break;
                }
                i++;
            }
            if (!allSetEqual) break;
        }

        List<String> sqlList = new ArrayList<>();
        // 限制条件仅为单列时触发
        if (allSetEqual && whereCount == 1) {
            StringBuilder sql = new StringBuilder("UPDATE {tableName}");
            StringBuilder setClause = new StringBuilder(" SET ");
            for (int i = 0; i < setColumns.size(); i++) {
                if (i > 0) setClause.append(", ");
                setClause.append(setColumns.get(i)).append(" = '").append(firstSet.get(i)).append("'");
            }
            StringBuilder whereIn = new StringBuilder();
            whereIn.append(" WHERE ");
            for (int i = 0; i < whereCount; i++) {
                if (i > 0) whereIn.append(" AND ");
                whereIn.append(header.get(i)).append(" IN (");
                for (int j = 0; j < rows.size(); j++) {
                    if (j > 0) whereIn.append(",");
                    whereIn.append("'").append(rows.get(j).get(i)).append("'");
                }
                whereIn.append(")");
            }
            sql.append(setClause).append(whereIn).append(";");
            sqlList.add(sql.toString());
        } else {
            for (List<String> row : rows) {
                StringBuilder sql = new StringBuilder("UPDATE {tableName}");
                StringBuilder setClause = new StringBuilder( " SET " );
                int setIdx = 0;
                for (int i = whereCount; i < header.size(); i++) {
                    if (setIdx > 0) setClause.append(", ");
                    setClause.append(header.get(i)).append(" = '").append(row.get(i)).append("'");
                    setIdx++;
                }
                StringBuilder whereClause = new StringBuilder(" WHERE ");
                for (int i = 0; i < whereCount; i++) {
                    if (i > 0) whereClause.append(" AND ");
                    whereClause.append(header.get(i)).append(" = '").append(row.get(i)).append("'");
                }
                sql.append(setClause).append(whereClause).append(";");
                sqlList.add(sql.toString());
            }
        }
        return String.join("\n", replaceTableName(sqlList, tableNames));
    }

    private List<String> replaceTableName(List<String> sqlList, String tableNames) {
        List<String> result = new ArrayList<>();
        String[] tabNames = tableNames.split(",");
        for (String tabName : tabNames) {
            for (String sql : sqlList) {
                String formattedSql = sql.replace("{tableName}", tabName.trim());
                result.add(formattedSql);
            }
        }
        return result;
    }

    private String generateInsertSql(List<List<String>> rows, List<String> header) {
        StringBuilder sql = new StringBuilder("INSERT INTO " + rows.get(1) + " (");
        for (int i = 2; i < header.size(); i++) {
            if (i > 2) sql.append(", ");
            sql.append(header.get(i));
        }
        sql.append(") VALUES (");
        for (int i = 2; i < rows.size(); i++) {
            if (i > 2) sql.append(", ");
            sql.append("'").append(rows.get(i)).append("'");
        }
        sql.append(");");
        return sql.toString();
    }
    private String generateDeleteSql(List<List<String>> rows, List<String> header, int whereCount, String tableNames) {
        if (rows.isEmpty() || header.isEmpty()) {
            throw new BusinessException("行数据或表头不完整，无法生成DELETE语句");
        }

        if (whereCount < 1 || whereCount >= header.size()) {
            throw new BusinessException("WHERE条件数量不正确，必须大于0且小于表头列数");
        }

        List<String> sqlList = new ArrayList<>();
        if (whereCount == 1) {
            // 生成 in 批量 delete
            StringBuilder sql = new StringBuilder("DELETE FROM {tableName} WHERE ");
            sql.append(header.get(0)).append(" IN (");
            for (int i = 0; i < rows.size(); i++) {
                if (i > 0) sql.append(", ");
                sql.append("'").append(rows.get(i).get(0)).append("'");
            }
            sql.append(");");
            sqlList.add(sql.toString());
        } else {
            // 生成单条 delete
            for (List<String> row : rows) {
                StringBuilder sql = new StringBuilder("DELETE FROM {tableName} WHERE ");
                for (int i = 0; i < whereCount; i++) {
                    if (i > 0) sql.append(" AND ");
                    sql.append(header.get(i)).append(" = '").append(row.get(i)).append("'");
                }
                sql.append(";");
                sqlList.add(sql.toString());
            }
        }
        return String.join("\n", replaceTableName(sqlList, tableNames));
    }

    @Override
    public String getType() {
        return "excel";
    }

    private String getDefaultInputFilePath() {
        String dir = System.getProperty("user.dir");
        File xls = new File(dir, "input.xls");
        File xlsx = new File(dir, "input.xlsx");
        if (xlsx.exists()) return xlsx.getAbsolutePath();
        if (xls.exists()) return xls.getAbsolutePath();
        // 默认返回xlsx
        return xlsx.getAbsolutePath();
    }

    /**
     * 获取默认输出文件路径（当前目录下output-年月日时分秒-6位随机数.sql）
     */
    private String getDefaultOutputFilePath() {
        String dir = System.getProperty("user.dir") + File.separator + "simplesql-output";
        createDirectory(dir);
        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int rand = 100000 + new Random().nextInt(900000);
        return dir + File.separator + "output-" + time + "-" + rand + ".sql";
    }

    /**
     * 创建目录
     */
    public void createDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                log.info("Directory created: {}", dirPath);
            } else {
                throw new BusinessException("Failed to create directory: " + dirPath);
            }
        } else {
            log.info("Directory already exists: {}", dirPath);
        }
    }

    /**
     * 读取Excel文件
     */
    public List<List<String>> readExcel(String filePath) {
        return convertMapListToListList(EasyExcel.read(filePath)
                .sheet()
                .headRowNumber(0)
                .doReadSync());
    }

    /**
     * 写入SQL到文件
     */
    public void writeSqlToExcel(String sql, String inputFilePath) {
        try {
            // 读取现有Excel
            List<List<String>> data = readExcel(inputFilePath);
            if (data.isEmpty()) {
                throw new BusinessException("Excel文件为空，无法写入SQL");
            }
            // 确保第一行有至少5列
            List<String> firstRow = data.get(0);
            while (firstRow.size() < 5) {
                firstRow.add("");
            }
            firstRow.set(4, sql);

            // 写回Excel（覆盖原文件）
            EasyExcel.write(inputFilePath)
                    .sheet()
                    .doWrite(data);
        } catch (Exception e) {
            throw new BusinessException("写入SQL到Excel失败: " + e.getMessage(), e);
        }
    }

    /**
     * 写入SQL到SQL文件，默认输出到当前目录下的output-年月日时分秒-6位随机数.sql
     */
    public void writeSqlToFile(String sql, String outputFilePath) {
        try (PrintWriter out = new PrintWriter(outputFilePath)) {
            out.println(sql);
            log.info("SQL has been written to: {}", outputFilePath);
        } catch (Exception e) {
            throw new BusinessException("Failed to write SQL to file: " + outputFilePath, e);
        }
    }

    public List<List<String>> convertMapListToListList(List<Map<Integer, String>> mapList) {
        List<List<String>> result = new ArrayList<>();

        for (Map<Integer, String> map : mapList) {
            int maxIndex = map.keySet().stream().max(Integer::compareTo).orElse(0);
            List<String> row = new ArrayList<>(Collections.nCopies(maxIndex + 1, ""));
            for (Map.Entry<Integer, String> entry : map.entrySet()) {
                row.set(entry.getKey(), entry.getValue());
            }
            result.add(row);
        }

        return result;
    }

}
