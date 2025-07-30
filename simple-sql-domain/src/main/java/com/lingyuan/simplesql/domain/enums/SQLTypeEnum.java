package com.lingyuan.simplesql.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SQLTypeEnum {
    SELECT("SELECT", "查询"),
    INSERT("INSERT", "新增"),
    UPDATE("UPDATE", "修改"),
    DELETE("DELETE", "删除"),
    ;
    private final String name;
    private final String desc;

    public static boolean contains(String name) {
        for (SQLTypeEnum type : SQLTypeEnum.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}
