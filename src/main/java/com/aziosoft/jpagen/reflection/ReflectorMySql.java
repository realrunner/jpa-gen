package com.aziosoft.jpagen.reflection;

import com.aziosoft.jpagen.config.Configuration;
import com.aziosoft.jpagen.db.Column;
import com.aziosoft.jpagen.db.ColumnType;
import com.aziosoft.jpagen.db.ForeignKey;
import com.aziosoft.jpagen.db.Table;
import com.aziosoft.jpagen.util.F;
import org.apache.commons.lang3.StringUtils;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mnelson on 3/5/14.
 *
 */
public class ReflectorMySql extends Reflector {

    static Pattern colTypePattern = Pattern.compile("(.*)\\(([^,]*),?([^\\)]*)\\)");

    final static Map<String,ColumnType> TypeMap;
    static {
        TypeMap = new HashMap<>();
        TypeMap.put("int", ColumnType.Int);
        TypeMap.put("tinyint", ColumnType.Int);
        TypeMap.put("smallint", ColumnType.Int);
        TypeMap.put("mediumint", ColumnType.Int);
        TypeMap.put("bigint", ColumnType.BigInt);
        TypeMap.put("float", ColumnType.Decimal);
        TypeMap.put("double", ColumnType.Decimal);
        TypeMap.put("decimal", ColumnType.Decimal);

        TypeMap.put("date", ColumnType.Date);
        TypeMap.put("datetime", ColumnType.Date);
        TypeMap.put("timestamp", ColumnType.Date);
        TypeMap.put("time", ColumnType.Time);
        TypeMap.put("year", ColumnType.Int);

        TypeMap.put("varchar", ColumnType.String);
        TypeMap.put("char", ColumnType.String);
        TypeMap.put("text", ColumnType.String);
        TypeMap.put("tinytext", ColumnType.String);
        TypeMap.put("mediumtext", ColumnType.String);
        TypeMap.put("longtext", ColumnType.String);
        TypeMap.put("enum", ColumnType.String);

        TypeMap.put("blob", ColumnType.Blob);
        TypeMap.put("tinyblob", ColumnType.Blob);
        TypeMap.put("mediumblob", ColumnType.Blob);
        TypeMap.put("longblob", ColumnType.Blob);
    }

    public ReflectorMySql(Configuration config, Handle handle) {
        super(config, handle);
    }

    @Override
    protected List<Table> getTableList() {
        String sql = "select table_name, table_schema from information_schema.tables";
        List<String> ands = new LinkedList<String>();
        List<String> params = new ArrayList<String>();
        if(StringUtils.isNotBlank(config.getSchema())) {
            ands.add("table_schema = ?");
            params.add(config.getSchema());
        }
        if(config.getExcludeTables() != null) {
            List<String> exclusions = F.map(config.getExcludeTables(), new F.F2<String, String>() {
                @Override public String apply(String p) {
                    return "table_name not like '" + p.replace("*","%") + "'";
                }
            });
            ands.add("(" + StringUtils.join(exclusions, " AND ") + ")");
        }
        if(config.getIncludeTables() != null) {
            List<String> exclusions = F.map(config.getIncludeTables(), new F.F2<String, String>() {
                @Override public String apply(String p) {
                    return "table_name like '" + p.replace("*","%") + "'";
                }
            });
            ands.add("(" + StringUtils.join(exclusions, " OR ") + ")");
        }
        if(ands.size() > 0) {
            sql = sql + " WHERE " + StringUtils.join(ands, " AND ");
        }
        Query<Map<String,Object>> query = handle.createQuery(sql);
        int paramCount = 0;
        for(String p : params) {
            query = query.bind(paramCount, p);
            paramCount++;
        }
        return query.map(new TableMapper()).list();
    }

    @Override
    protected List<Column> getColumnsForTables(List<Table> tables) {
        String sql =
                String.format(
                "select \n" +
                    "table_name,\n" +
                    "column_name,\n" +
                    "column_default,\n" +
                    "is_nullable,\n" +
                    "data_type,\n" +
                    "character_maximum_length,\n" +
                    "numeric_precision,\n" +
                    "numeric_scale,\n" +
                    "column_type,\n" +
                    "column_key,\n" +
                    "extra\n" +
                "from information_schema.columns\n" +
                "where table_schema=? and table_name in (%s)\n" +
                "order by ordinal_position", StringUtils.join( F.map(tables, new F.F2<Object, Table>() {
                    @Override public Object apply(Table p) {
                        return "'" + p.getName() + "'";
                    }
                }), ", ") );
        return handle.createQuery(sql)
                .bind(0, config.getSchema())
                .map(new ColumnMapper())
                .list();
    }

    @Override
    protected List<ForeignKey> getForeignKeysForTables(List<Table> tables) {
        return new LinkedList<>();
    }

    //JDBI mappers

    class TableMapper implements ResultSetMapper<Table> {
        @Override
        public Table map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
            Table table = new Table();
            table.setName(resultSet.getString(1));
            table.setSchema(resultSet.getString(2));
            return table;
        }
    }

    class ColumnMapper implements ResultSetMapper<Column> {
        @Override
        public Column map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
            Column column = new Column();
            column.setTableName(resultSet.getString(1));
            column.setName(resultSet.getString(2));
            column.setDefaultValue(resultSet.getString(3));
            column.setNullable("YES".equalsIgnoreCase(resultSet.getString(4)));
            column.setTypeName(resultSet.getString(5));
            column.setLength(resultSet.getInt(6));
            column.setPrecision(resultSet.getInt(7));
            column.setIsPk("PRI".equalsIgnoreCase(resultSet.getString(10)));

            column.setType(getType(column, resultSet.getString(9)));

            return column;
        }



        private ColumnType getType(Column column, String columnType) {
            Matcher m = colTypePattern.matcher(columnType);
            if(m.matches()) {
                if(!m.group(1).equals(column.getTypeName())) {
                    column.setTypeName(m.group(1));
                }
                if(StringUtils.isNotBlank(m.group(2))) {
                    column.setLength(Integer.parseInt(m.group(2)));
                }
                if(StringUtils.isNotBlank(m.group(3))) {
                    column.setPrecision(Integer.parseInt(m.group(3)));
                }
            }

            if(TypeMap.containsKey(column.getTypeName().toLowerCase())) {
                return TypeMap.get(column.getTypeName().toLowerCase());
            }
            return ColumnType.String;
        }
    }

    class ForeignKeyMapper implements ResultSetMapper<ForeignKey> {
        @Override
        public ForeignKey map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
            return null;
        }
    }

//
//    //Queries
//    @RegisterMapper({TableMapper.class, ColumnMapper.class, ForeignKeyMapper.class})
//    interface Queries {
//
//        public List<Table> getTables();
//        public List<Table> getTables(@Bind("schema") String schema);
//        public List<Table> getColumns();
//    }
}
