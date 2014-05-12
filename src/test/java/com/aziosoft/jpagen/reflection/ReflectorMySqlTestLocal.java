package com.aziosoft.jpagen.reflection;

import com.aziosoft.jpagen.config.Configuration;
import com.aziosoft.jpagen.db.Column;
import com.aziosoft.jpagen.db.ColumnType;
import com.aziosoft.jpagen.db.Table;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mnelson on 3/6/14.
 *
 */
public class ReflectorMySqlTestLocal {

    Reflector reflector;

    @Before
    public void before() throws IOException {
        Configuration config = Configuration.fromStream(getClass().getResourceAsStream("/test-2.json"));

        com.mysql.jdbc.jdbc2.optional.MysqlDataSource ds
                = new MysqlDataSource();
        ds.setUrl("jdbc:mysql://localhost/devopsplus");
        ds.setUser("devopsplus");
        ds.setPassword("devopsplus");
        DBI dbi = new DBI(ds);
        Handle handle = dbi.open();

        reflector = new ReflectorMySql(config, handle);
    }

    @Test
    public void testGetTables() {
        List<Table> tables = reflector.getTableList();
        Assert.assertTrue(tables.size() > 10);
        for(Table table: tables) {
            Assert.assertNotNull(table.getName());
            Assert.assertNotNull(table.getSchema());
        }
    }

    @Test
    public void testGetColumns() {
        List<Table> tables = new LinkedList<>();
        Table t = new Table();
        t.setName("do_group");
        tables.add(t);
        List<Column> columns = reflector.getColumnsForTables(tables);
        Assert.assertEquals(6, columns.size());
        Column c = columns.get(0);
        Assert.assertEquals("id", c.getName());
        Assert.assertEquals(false, (boolean)c.getNullable());
        Assert.assertEquals(ColumnType.BigInt, c.getType());
        Assert.assertEquals(true, (boolean)c.getIsPk());
        //Assert.assertEquals();
    }
}
