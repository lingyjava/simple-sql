package com.lingyuan.server;

import com.lingyuan.simplesql.domain.model.UpdateParams;
import com.lingyuan.simplesql.server.impl.UpdateBuild;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class UpdateBuildTest {

    @Test
    void buildsUpdateSQLWithAllFields() {
        UpdateParams params = new UpdateParams();
        params.setDatabaseName("test_db");
        params.setTableName("test_table");
        params.setUpdateFields(Map.of("field1", "value1", "field2", "value2"));
        params.setWhereClause(Map.of("id", "123"));
        params.setLimit(1);

        String sql = new UpdateBuild().buildUpdateSQL(params);
        System.out.println(sql);
        Assertions.assertEquals("UPDATE test_db.test_table SET field1 = 'value1', field2 = 'value2' WHERE id = '123' LIMIT 1", sql);
    }

    @Test
    void buildsUpdateSQLWithoutDatabaseName() {
        UpdateParams params = new UpdateParams();
        params.setTableName("test_table");
        params.setUpdateFields(Map.of("field1", "value1"));
        params.setWhereClause(Map.of("id", "123"));

        String sql = new UpdateBuild().buildUpdateSQL(params);
        System.out.println(sql);
        Assertions.assertEquals("UPDATE test_table SET field1 = 'value1' WHERE id = '123'", sql);
    }

    @Test
    void buildsUpdateSQLWithoutWhereClause() {
        UpdateParams params = new UpdateParams();
        params.setDatabaseName("test_db");
        params.setTableName("test_table");
        params.setUpdateFields(Map.of("field1", "value1"));

        String sql = new UpdateBuild().buildUpdateSQL(params);
        System.out.println(sql);
        Assertions.assertEquals("UPDATE test_db.test_table SET field1 = 'value1'", sql);
    }

    @Test
    void buildsUpdateSQLWithEmptyUpdateFields() {
        UpdateParams params = new UpdateParams();
        params.setDatabaseName("test_db");
        params.setTableName("test_table");
        params.setUpdateFields(Map.of());

        Assertions.assertThrows(IllegalArgumentException.class, () -> new UpdateBuild().buildUpdateSQL(params));
    }

    @Test
    void throwsExceptionForNullParams() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new UpdateBuild().buildUpdateSQL(null));
    }

    @Test
    void throwsExceptionForMissingTableName() {
        UpdateParams params = new UpdateParams();
        params.setDatabaseName("test_db");
        params.setUpdateFields(Map.of("field1", "value1"));

        Assertions.assertThrows(IllegalArgumentException.class, () -> new UpdateBuild().buildUpdateSQL(params));
    }
}
