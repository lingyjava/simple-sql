package com.lingyuan.simplesql.common.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

/**
 * ValidationUtil 是一个工具类，用于验证对象的约束条件。
 * 它使用 Jakarta Bean Validation API 来检查对象是否符合定义的约束条件。
 * 如果对象违反了约束条件，则抛出 IllegalArgumentException 异常。
 * * 使用示例：
 * * <pre>
 *     ValidationUtil.validate(myObject);
 * * </pre>
 *
 * 通过以下注解可以定义对象的约束条件：
 * * <pre>
 * *     @NotNull
 * *     @Size(min = 1, message = "长度必须大于0")
 * * </pre>
 */
public class ValidationUtil {
    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    /**
     * 验证对象的约束条件，如果有违反约束条件的情况，则抛出异常。
     *
     * @param object 需要验证的对象
     * @param <T>    对象类型
     * @throws IllegalArgumentException 如果验证失败，抛出此异常
     */
    public static <T> void validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                sb.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException(sb.toString());
        }
    }
}
