package com.aziosoft.jpagen;

import com.aziosoft.jpagen.config.Configuration;
import com.aziosoft.jpagen.db.Table;
import com.aziosoft.jpagen.generation.Generator;
import com.aziosoft.jpagen.generation.java.JpaClassGenerator;
import com.aziosoft.jpagen.reflection.Reflector;
import com.aziosoft.jpagen.reflection.ReflectorMySql;
import com.jolbox.bonecp.BoneCPDataSource;
import org.apache.commons.cli.*;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by mnelson on 3/5/14.
 *
 */
public class Driver {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("c", "config", true, "path to the configuration file");
        options.addOption("dburi", true, "Database connection URI. Format: username/password@jdbc_standard_url. (i.e scott/tiger@jdbc:mysql://localhost/emp)");
        options.addOption("d", "dest", true, "path to write the generated files");
        options.addOption("h", "help", false, "prints usage");

        CommandLineParser parser = new PosixParser();

        Handle handle = null;
        try {
            CommandLine cmd = parser.parse(options, args);
            if(cmd.hasOption("help")) {
                printHelp(options);
                return;
            }
            if(!cmd.hasOption("config") || !cmd.hasOption("dburi") || !cmd.hasOption("dest")) {
                throw new ParseException("Missing required options");
            }

            FullJdbcUrl url = FullJdbcUrl.parse(cmd.getOptionValue("dburi"));

            Configuration config;
            try (FileInputStream fis = new FileInputStream(cmd.getOptionValue("c"))) {
                config = Configuration.fromStream(fis);
            }

            BoneCPDataSource ds = new BoneCPDataSource();
            ds.setJdbcUrl(url.getUrl());
            ds.setUser(url.getUsername());
            ds.setPassword(url.getPassword());


            DBI dbi = new DBI(ds);
            handle = dbi.open();

            System.out.println("Reading database metadata");
            Reflector ref = new ReflectorMySql(config, handle);
            List<Table> tables = ref.reflect();

            System.out.println(String.format("Read metadata for %d tables", tables.size()));

            File dest = new File(cmd.getOptionValue("d"));
            if(!dest.exists()) {
                if(!dest.mkdir()) {
                    System.out.println( String.format("Destination directory %s doesn't exist and cannot be created. ", dest.getAbsolutePath()));
                    printHelp(options);
                    return;
                }
            }

            System.out.println(String.format("Generating files into %s", dest.getAbsolutePath()));

            Generator generator = new JpaClassGenerator(dest, config);
            generator.generate(tables);

            System.out.println("Complete");

        } catch (ParseException e) {
            System.out.println("Error parsing options: " + e.getMessage());
            printHelp(options);
        } catch (java.text.ParseException e) {
            System.out.println(e.getMessage());
            printHelp(options);
        } catch (IOException e) {
            System.out.println();
        }
        finally {
            if(handle != null) {
                handle.close();
            }
        }
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("jpa-gen", options);
    }


}
