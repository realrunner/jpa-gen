package com.aziosoft.jpagen.generation;

import com.aziosoft.jpagen.config.Configuration;
import com.aziosoft.jpagen.db.Table;
import com.aziosoft.jpagen.generation.java.JpaClassGenerator;
import com.aziosoft.jpagen.reflection.Reflector;
import com.aziosoft.jpagen.reflection.ReflectorMySql;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.apache.commons.io.FileDeleteStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Created by mnelson on 3/6/14.
 *
 */
public class GeneratorTestLocal {

    File tempLocation;
    Reflector reflector;
    Configuration config;

    @Before
    public void before() throws IOException {
        tempLocation = Files.createTempDirectory("jpagen").toFile();
        System.out.println(tempLocation.getAbsolutePath());


        com.mysql.jdbc.jdbc2.optional.MysqlDataSource ds
                = new MysqlDataSource();
        ds.setUrl("jdbc:mysql://localhost/devopsplus");
        ds.setUser("devopsplus");
        ds.setPassword("devopsplus");
        DBI dbi = new DBI(ds);
        Handle handle = dbi.open();

        config = Configuration.fromStream(getClass().getResourceAsStream("/test-2.json"));
        reflector = new ReflectorMySql(config, handle);
    }

    @Test
    public void testFullGeneration() throws IOException {

        List<Table> tables = reflector.reflect();

        Generator g = new JpaClassGenerator(tempLocation, config);

        g.generate(tables);
    }

    @After
    public void after() throws IOException {
        FileDeleteStrategy.FORCE.delete(tempLocation);
    }
}
