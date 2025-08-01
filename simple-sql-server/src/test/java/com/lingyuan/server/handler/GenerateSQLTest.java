package com.lingyuan.server.handler;

import org.junit.jupiter.api.Test;

import com.lingyuan.simplesql.server.handler.GenerateInsertHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateSQLTest {

    private List<List<String>> getRows() {
        List<List<String>> rows = new ArrayList<>();
        rows.add(Arrays.asList("1", "2", "3"));
        rows.add(Arrays.asList("4", "5", "6"));
        rows.add(Arrays.asList("7", "8", "9"));
        return rows;
    }

    private List<String> getHeader() {
        return Arrays.asList("id", "name", "age");
    }

    private List<List<String>> getRowsWithNull() {
        List<List<String>> rows = new ArrayList<>();
        rows.add(Arrays.asList("1", "2", "3"));
        rows.add(Arrays.asList("4", "5", "6"));
        rows.add(Arrays.asList("7", "8", ""));
        rows.add(Arrays.asList("10", "", "12"));
        return rows;
    }

    private String getTableName() {
        return "user";
    }

    @Test
    public void testGenerateInsertSQL() {
        String sql = GenerateInsertHandler.getSQL(getRows(), getHeader(), getTableName());
        System.out.println(sql);
    }
    
}
