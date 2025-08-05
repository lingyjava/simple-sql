package com.lingyuan.simplesql.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.alibaba.excel.EasyExcel;

/**
 * 解析Excel工具类
 */
public class ExcelParse {

    /**
     * 获取某行第i列，若越界则返回""
     * @param row 行数据
     * @param i 列索引
     * @return 某行第i列，若越界则返回""
     */
    public static String getCell(List<String> row, int i) {
        return i < row.size() ? row.get(i) : "";
    }

    /**
     * 读取Excel文件
     * @param filePath 文件路径
     * @return List<List<String>>列表
     */
    public static List<List<String>> readExcel(String filePath) {
        return convertMapListToListList(EasyExcel.read(filePath)
                .sheet()
                .headRowNumber(0)
                .doReadSync());
    }

    /**
     * 将Map<Integer, String>列表转换为List<List<String>>列表
     * @param mapList Map<Integer, String>列表
     * @return List<List<String>>列表
     */
    public static List<List<String>> convertMapListToListList(List<Map<Integer, String>> mapList) {
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
