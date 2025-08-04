package com.lingyuan.server.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.lingyuan.simplesql.server.handler.GenerateInsertHandler;
import com.lingyuan.simplesql.server.handler.GenerateDeleteHandler;
import com.lingyuan.simplesql.server.handler.GenerateUpdateHandler;
import com.lingyuan.simplesql.server.handler.GenerateSelectHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateSQLTest {

    private List<List<String>> getRows() {
        List<List<String>> rows = new ArrayList<>();
        rows.add(Arrays.asList("1", "Lingyuan", "22"));
        rows.add(Arrays.asList("4", "Tom", "20"));
        rows.add(Arrays.asList("7", "Jerry", "25"));
        return rows;
    }

    private List<String> getHeader() {
        return Arrays.asList("id", "name", "age");
    }

    private List<List<String>> getRowsWithNull() {
        List<List<String>> rows = new ArrayList<>();
        rows.add(Arrays.asList("1", "Lingyuan", "22"));
        rows.add(Arrays.asList("4", "Tom", "20"));
        rows.add(Arrays.asList("7", "Jerry", ""));
        rows.add(Arrays.asList("10", "", "28"));
        return rows;
    }

    private String getTableName() {
        return "user";
    }

    @Test
    public void testGenerateSQLWithNull() {
        Assertions.assertEquals(GenerateSelectHandler.getSQL(getRowsWithNull(), getHeader(), getTableName()), 
        "SELECT * FROM `user` WHERE `id` IN ('1', '4', '7', '10') AND `name` IN ('', 'Tom', 'Jerry', 'Lingyuan') AND `age` IN ('22', '', '28', '20');");
        Assertions.assertEquals(GenerateInsertHandler.getSQL(getRowsWithNull(), getHeader(), getTableName()), 
        "INSERT INTO `user` (`id`, `name`, `age`) VALUES \n" +
        "('1', 'Lingyuan', '22'), \n" + 
        "('4', 'Tom', '20'), \n" +
        "('7', 'Jerry', ''), \n" +
        "('10', '', '28');");
        Assertions.assertEquals(GenerateDeleteHandler.getSQL(getRowsWithNull(), getHeader(), 3, getTableName()), 
        "DELETE FROM user WHERE `id` = '1' AND `name` = 'Lingyuan' AND `age` = '22';\n" + 
        "DELETE FROM user WHERE `id` = '4' AND `name` = 'Tom' AND `age` = '20';\n" + 
        "DELETE FROM user WHERE `id` = '7' AND `name` = 'Jerry' AND `age` = '';\n" + 
        "DELETE FROM user WHERE `id` = '10' AND `name` = '' AND `age` = '28';");
        Assertions.assertEquals(GenerateUpdateHandler.getSQL(getRowsWithNull(), getHeader(), 1, getTableName()), 
        "UPDATE `user` SET `name` = 'Lingyuan', `age` = '22' WHERE `id` = '1';\n" + 
        "UPDATE `user` SET `name` = 'Tom', `age` = '20' WHERE `id` = '4';\n" + 
        "UPDATE `user` SET `name` = 'Jerry', `age` = '' WHERE `id` = '7';\n" + 
        "UPDATE `user` SET `name` = '', `age` = '28' WHERE `id` = '10';");
    }

    @Test
    public void testGenerateSQL() {
        Assertions.assertEquals(GenerateSelectHandler.getSQL(getRows(), getHeader(), getTableName()), 
        "SELECT * FROM `user` WHERE `id` IN ('1', '4', '7') AND `name` IN ('Lingyuan', 'Tom', 'Jerry') AND `age` IN ('22', '20', '25');");
        Assertions.assertEquals(GenerateInsertHandler.getSQL(getRows(), getHeader(), getTableName()), 
        "INSERT INTO `user` (`id`, `name`, `age`) VALUES \n" + 
        "('1', 'Lingyuan', '22'), \n" + 
        "('4', 'Tom', '20'), \n" + 
        "('7', 'Jerry', '25');");   
        Assertions.assertEquals(GenerateDeleteHandler.getSQL(getRows(), getHeader(), 3, getTableName()), 
        "DELETE FROM user WHERE `id` = '1' AND `name` = 'Lingyuan' AND `age` = '22';\n" + 
        "DELETE FROM user WHERE `id` = '4' AND `name` = 'Tom' AND `age` = '20';\n" + 
        "DELETE FROM user WHERE `id` = '7' AND `name` = 'Jerry' AND `age` = '25';");
        Assertions.assertEquals(GenerateUpdateHandler.getSQL(getRows(), getHeader(), 1, getTableName()), 
        "UPDATE `user` SET `name` = 'Lingyuan', `age` = '22' WHERE `id` = '1';\n" + 
        "UPDATE `user` SET `name` = 'Tom', `age` = '20' WHERE `id` = '4';\n" + 
        "UPDATE `user` SET `name` = 'Jerry', `age` = '25' WHERE `id` = '7';");
    }
    
}
