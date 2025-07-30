package com.lingyuan.simplesql.server.impl;

import com.lingyuan.simplesql.common.exception.BusinessException;
import com.lingyuan.simplesql.server.SqlGenerator;

import java.util.Arrays;
import java.util.List;

public class SqlGeneratorFactory {

    private SqlGeneratorFactory() {
        // 私有构造函数，防止实例化
    }

    private static final List<SqlGenerator> GENERATORS = Arrays.asList(
            new SqlGeneratorExcelImpl()
            // 可以继续添加其他实现类
    );

    public static SqlGenerator getGenerator(String type) {
        if (type == null || type.isEmpty()) {
            throw new BusinessException("生成类型不能为空");
        }
        for (SqlGenerator generator : GENERATORS) {
            if (generator.getType().equalsIgnoreCase(type)) {
                return generator;
            }
        }
        throw new BusinessException("未找到对应类型的SqlGenerator: " + type);
    }
}
