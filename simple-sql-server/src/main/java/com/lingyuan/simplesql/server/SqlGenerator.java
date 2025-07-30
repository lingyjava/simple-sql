package com.lingyuan.simplesql.server;

import com.lingyuan.simplesql.domain.dto.SqlGeneratorParam;

import java.util.Map;

public interface SqlGenerator {

    /**
     * 生成SQL语句
     *
     * @param param  参数
     * @return 输出路径或SQL语句
     */
    Object generate(SqlGeneratorParam param);

    String getType();
}
