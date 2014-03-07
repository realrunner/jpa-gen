package com.aziosoft.jpagen.generation;

import com.aziosoft.jpagen.config.Configuration;
import com.aziosoft.jpagen.db.Table;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by mnelson on 3/6/14.
 *
 */
public abstract class Generator {
    final protected File destination;
    final protected Configuration config;

    protected Generator(File destination, Configuration config) {
        this.destination = destination;
        this.config = config;
    }

    public void generate(List<Table> tables) {
        for(Table table: tables) {
            File dest = new File(destination, getFileName(table));
            try(OutputStream os = new FileOutputStream(dest, false)) {
                writeClass(os, table);
            }
            catch(Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    abstract protected String getFileName(Table table);
    abstract protected void writeClass(OutputStream os, Table table) throws IOException;
}
