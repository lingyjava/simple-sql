package com.lingyuan.simplesql.server;

import java.util.Map;

public interface SqlGenerator {

    /**
     * 生成SQL语句
     *
     * @param params  参数
     * @return outputPath 输出路径或SQL语句
     */
    Object generateSql(Map<String, Object> params);

    String getType();
}
