package com.lingyuan.simplesql.server;

import com.lingyuan.simplesql.domain.dto.SqlGeneratorParam;

public interface SqlGenerator {

    /**
     * 生成SQL语句
     *
     * @param param  参数
     * @return 输出路径或SQL语句
     */
    Object generate(SqlGeneratorParam param);


    /**
     * 获取SQL生成器类型
     * @return 类型
     */
    String getType();
}
