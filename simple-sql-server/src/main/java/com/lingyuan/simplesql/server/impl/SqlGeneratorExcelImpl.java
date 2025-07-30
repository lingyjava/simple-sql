package com.lingyuan.simplesql.server.impl;

import com.alibaba.excel.EasyExcel;
import com.lingyuan.simplesql.common.exception.BusinessException;
import com.lingyuan.simplesql.common.util.ValidationUtil;
import com.lingyuan.simplesql.domain.dto.SqlGeneratorParam;
import com.lingyuan.simplesql.domain.enums.SQLTypeEnum;
import com.lingyuan.simplesql.server.SqlGenerator;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class SqlGeneratorExcelImpl implements SqlGenerator {
    
    private void checkParam(@NotNull SqlGeneratorParam param) {
        ValidationUtil.validate(param);
        if (param.getTableName() == null || param.getTableName().isEmpty()) {
            throw new BusinessException("表名不能为空");
        }
        if (param.getExcelPath() == null || param.getExcelPath().isEmpty()) {
            throw new BusinessException("Excel文件路径不能为空");
        }
        if (param.getSqlType() == null || param.getSqlType().isEmpty()) {
            throw new BusinessException("SQL类型不能为空");
        }
        if (!SQLTypeEnum.contains(param.getSqlType())) {
            throw new BusinessException("不支持的SQL类型: " + param.getSqlType());
        }
    }

    @Override
    public Object generate(SqlGeneratorParam param) {
        // 校验参数
        checkParam(param);

        // 读取excel
        List<List<String>> data = readExcel(param.getExcelPath());
        if (data == null || data.size() < 6) {
            throw new BusinessException("Excel文件格式错误或数据不足，至少需要6行数据");
        }
        // 读取表头
        List<String> header = data.get(0);
        // 读取数据
        List<List<String>> rows = data.subList(1, data.size());

        if (header.isEmpty() || rows.isEmpty()) {
            throw new BusinessException("Excel数据格式错误，表头或数据行不能为空");
        }

        for (List<String> row : rows) {
            if (row.size() != header.size()) {
                throw new BusinessException("数据行与表头列数不一致，行长度：" + row.size() + "，表头长度：" + header.size());
            }
        }

        // 生成SQL语句
        String sql;
        switch (param.getSqlType().toLowerCase()) {
            case "insert" -> sql = generateInsertSql(rows, header, param.getTableName());
            case "update" -> sql = generateUpdateSql(rows, header, param.getWhereColumnCount(), param.getTableName());
            case "delete" -> sql = generateDeleteSql(rows, header, param.getWhereColumnCount(), param.getTableName());
            case "select" -> sql = generateSelectSql(rows, header, param.getTableName());
            default -> throw new BusinessException("不支持的SQL类型: " + param.getSqlType());
        }
        // 写入SQL到文件
        String outputFilePath = getDefaultOutputFilePath(param.getSqlType() + "-" + param.getTableName() + "-");
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

    private String generateInsertSql(List<List<String>> rows, List<String> header, String tableNames) {
        if (rows.isEmpty() || header.isEmpty()) {
            throw new BusinessException("行数据或表头不完整，无法生成INSERT语句");
        }
        StringBuilder sql = new StringBuilder("INSERT INTO " + "{tableName}" + " (");
        for (int i = 0; i < header.size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append("`").append(header.get(i)).append("`");
        }
        sql.append(") VALUES \n");
        for (int i = 0; i < rows.size(); i++) {
            if (i > 0) sql.append(", \n");
            sql.append("(");
            for (int j = 0; j < rows.get(i).size(); j++) {
                if (j > 0) sql.append(", ");
                sql.append("'").append(rows.get(i).get(j)).append("'");
            }
            sql.append(")");

        }
        sql.append(";");

        List<String> sqlList = new ArrayList<>();
        sqlList.add(sql.toString());
        return String.join("\n", replaceTableName(sqlList, tableNames));
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

    private String generateSelectSql(List<List<String>> rows, List<String> header, String tableNames) {
        if (rows.isEmpty() || header.isEmpty()) {
            throw new BusinessException("行数据或表头不完整，无法生成SELECT语句");
        }
        StringBuilder sql = new StringBuilder("SELECT * FROM {tableName} WHERE ");
        for (int i = 0; i < header.size(); i++) {
            Set<String> colValues = new HashSet<>();
            for (List<String> row : rows) {
                colValues.add(row.get(i));
            }
            if (i > 0) sql.append(" AND ");
            sql.append("`").append(header.get(i)).append("`");
            if (colValues.size() == 1) {
                sql.append(" = '").append(rows.get(0).get(i)).append("'");
            } else {
                sql.append(" IN (");
                int idx = 0;
                for (String val : colValues) {
                    if (idx++ > 0) sql.append(", ");
                    sql.append("'").append(val).append("'");
                }
                sql.append(")");
            }
        }
        sql.append(";");
        List<String> sqlList = new ArrayList<>();
        sqlList.add(sql.toString());
        return String.join("\n", replaceTableName(sqlList, tableNames));
    }

    @Override
    public String getType() {
        return "EXCEL";
    }

    /**
     * 获取默认输出文件路径（当前目录下output-年月日时分秒-6位随机数.sql）
     */
    private String getDefaultOutputFilePath(String prefix) {
        String dir = System.getProperty("user.dir") + File.separator + "output";
        createDirectory(dir);
        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int rand = 100000 + new Random().nextInt(900000);
        return dir + File.separator + prefix + time + "-" + rand + ".sql";
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
     * 写入SQL到SQL文件
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
