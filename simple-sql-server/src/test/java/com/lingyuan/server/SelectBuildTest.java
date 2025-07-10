package com.lingyuan.server;

import com.lingyuan.simplesql.domain.model.SelectParams;
import com.lingyuan.simplesql.server.impl.SelectBuild;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SelectBuildTest {

    private SelectBuild selectBuild;
    private SelectParams params;

    @BeforeEach
    void setup() {
        selectBuild = new SelectBuild();
        params = new SelectParams();
    }

    @Test
    void buildsQueryWithAllFields() {
        params.setSelectAllFields(true);
        params.setDatabaseName("test_db");
        params.setTableName("test_table");

        String query = selectBuild.buildSelectQuery(params);
        System.out.println(query);
        Assertions.assertEquals("SELECT * FROM test_db.test_table", query);
    }

    @Test
    void buildsQueryWithSpecificFields() {
        params.setSelectFields(List.of("field1", "field2").toArray(new String[0]));
        params.setDatabaseName("test_db");
        params.setTableName("test_table");

        String query = selectBuild.buildSelectQuery(params);
        System.out.println(query);
        Assertions.assertEquals("SELECT field1, field2 FROM test_db.test_table", query);
    }

    @Test
    void buildsQueryWithWhereClause() {
        params.setSelectAllFields(true);
        params.setDatabaseName("test_db");
        params.setTableName("test_table");
        Map<String, Object> whereClause = new HashMap<>();
        whereClause.put("field1", "value1");
        whereClause.put("field2", "value2");
        params.setWhereClause(whereClause);

        String query = selectBuild.buildSelectQuery(params);
        System.out.println(query);
        Assertions.assertEquals("SELECT * FROM test_db.test_table WHERE field1 = 'value1' AND field2 = 'value2'", query);
    }

    @Test
    void buildsQueryWithOrderBy() {
        params.setSelectAllFields(true);
        params.setDatabaseName("test_db");
        params.setTableName("test_table");
        params.setOrderBy(new String[]{"field1", "field2"});

        String query = selectBuild.buildSelectQuery(params);
        System.out.println(query);
        Assertions.assertEquals("SELECT * FROM test_db.test_table ORDER BY field1, field2", query);
    }

    @Test
    void buildsQueryWithLimit() {
        params.setSelectAllFields(true);
        params.setDatabaseName("test_db");
        params.setTableName("test_table");
        params.setLimit(10);

        String query = selectBuild.buildSelectQuery(params);
        System.out.println(query);
        Assertions.assertEquals("SELECT * FROM test_db.test_table LIMIT 10", query);
    }

    @Test
    void buildsQueryWithAllClauses() {
        params.setSelectFields(List.of("field1", "field2").toArray(new String[0]));
        params.setDatabaseName("test_db");
        params.setTableName("test_table");
        Map<String, Object> whereClause = new HashMap<>();
        whereClause.put("field1", "value1");
        whereClause.put("field2", "value2");
        params.setWhereClause(whereClause);
        params.setOrderBy(new String[]{"field1", "field2"});
        params.setLimit(10);

        String query = selectBuild.buildSelectQuery(params);
        System.out.println(query);
        Assertions.assertEquals("SELECT field1, field2 FROM test_db.test_table WHERE field1 = 'value1' AND field2 = 'value2' ORDER BY field1, field2 LIMIT 10", query);
    }

    @Test
    void throwsExceptionForNullParams() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> selectBuild.buildSelectQuery(null));
    }

    @Test
    void buildsQueryWithoutDatabaseName() {
        params.setSelectAllFields(true);
        params.setTableName("test_table");
        String query = selectBuild.buildSelectQuery(params);
        System.out.println(query);
        Assertions.assertEquals("SELECT * FROM test_table", query);
    }

    @Test
    void throwsExceptionForMissingTableName() {
        params.setSelectAllFields(true);
        params.setDatabaseName("test_db");

        Assertions.assertThrows(IllegalArgumentException.class, () -> selectBuild.buildSelectQuery(params));
    }
}