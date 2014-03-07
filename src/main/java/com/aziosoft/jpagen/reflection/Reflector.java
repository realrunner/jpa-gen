package com.aziosoft.jpagen.reflection;

import com.aziosoft.jpagen.config.Configuration;
import com.aziosoft.jpagen.db.Column;
import com.aziosoft.jpagen.db.ForeignKey;
import com.aziosoft.jpagen.db.Table;
import com.aziosoft.jpagen.util.F;
import org.skife.jdbi.v2.Handle;

import java.util.List;
import java.util.Map;

/**
 * Created by mnelson on 3/5/14.
 *
 */
public abstract class Reflector {
    final protected Configuration config;
    final protected Handle handle;

    public Reflector(Configuration config, Handle handle) {
        this.config = config;
        this.handle = handle;
    }

    public List<Table> reflect() {
        List<Table> tables = getTableList();
        Map<String,Table> tableMap = F.makeMap(tables, new F.F2<String, Table>() {
            @Override public String apply(Table p) {
                return p.getName();
            }
        });
        List<Column> columns = getColumnsForTables(tables);
        List<ForeignKey> fks = getForeignKeysForTables(tables);
        correlateColumns(tableMap, columns);
        correlateForeignKeys(tableMap, fks);
        return tables;
    }

    private void correlateForeignKeys(Map<String,Table> tables, List<ForeignKey> fks) {
        for(ForeignKey fk : fks) {
            if(tables.containsKey(fk.getChildTableName())) {
                tables.get(fk.getChildTableName()).getForeignKeys().add(fk);
            }
        }
    }

    private void correlateColumns(Map<String,Table> tables, List<Column> columns) {
        for(Column col : columns) {
            if(tables.containsKey(col.getTableName())) {
                tables.get(col.getTableName()).getColumns().add(col);
            }
        }
    }

    abstract List<Table> getTableList();
    abstract List<Column> getColumnsForTables(List<Table> tables);
    abstract List<ForeignKey> getForeignKeysForTables(List<Table> tables);

}
