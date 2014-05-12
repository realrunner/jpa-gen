package com.aziosoft.jpagen.generation.java;

import com.aziosoft.jpagen.config.*;
import com.aziosoft.jpagen.db.Column;
import com.aziosoft.jpagen.db.ColumnType;
import com.aziosoft.jpagen.db.Table;
import com.aziosoft.jpagen.generation.Generator;
import com.aziosoft.jpagen.util.F;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

/**
 * Created by mnelson on 3/6/14.
 *
 */
public class JpaClassGenerator extends Generator {

    final static String BaseClassDefaultSuffix = "Base";
    final static RelationType DefaultRelationType = RelationType.OneToMany;
    final static CollectionType DefaultCollectionType = CollectionType.List;


    final static Map<ColumnType, String> TypeMap;
    static {
        TypeMap = new HashMap<>();
        TypeMap.put(ColumnType.Int, "Integer");
        TypeMap.put(ColumnType.String, "String");
        TypeMap.put(ColumnType.BigInt, "Long");
        TypeMap.put(ColumnType.Decimal, "java.math.BigDecimal");
        TypeMap.put(ColumnType.Date, "java.util.Date");
        TypeMap.put(ColumnType.Text, "String");
        TypeMap.put(ColumnType.Blob, "byte[]");
        TypeMap.put(ColumnType.Time, "java.sql.Time");
        TypeMap.put(ColumnType.Boolean, "Boolean");
        TypeMap.put(ColumnType.UUID, "java.util.UUID");
    }

    public JpaClassGenerator(File destination,  Configuration config) {
        super(destination, config);
    }

    @Override
    protected String getFileName(Table table) {
        return className(table.getName(), true) + ".java";
    }

    @Override
    protected void writeClass(OutputStream os, Table table) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(os);
        writeClass(writer, table);
        writer.flush();
    }


    protected String className(String tableName, Boolean includeBaseSuffixForMappedSuperclasses) {
        if(config.getClassNames() != null && config.getClassNames().containsKey(tableName)) {
            return config.getClassNames().get(tableName);
        }
        String name = tableName;
        if(config.getExcludePrefixInClassNames() && StringUtils.isNotBlank(config.getTablePrefix()) && name.startsWith(config.getTablePrefix())) {
            name = name.substring(config.getTablePrefix().length());
        }
        String[] pcs = name.split("_");
        List<String> pcsList = Arrays.asList(pcs);
        name = StringUtils.join(F.map(pcsList, new F.F2<String, String>() {
            @Override public String apply(String p) {
                return StringUtils.capitalize(p);
            }
        }), "");

        if(includeBaseSuffixForMappedSuperclasses && config.getMappedSuperClasses() != null && config.getMappedSuperClasses().contains(tableName)) {
            name = name + (StringUtils.isNotBlank(config.getMappedSuperClassSuffix()) ? config.getMappedSuperClassSuffix() : BaseClassDefaultSuffix);
        }

        return name;
    }

    protected String fieldName(String colName) {
        String name = colName;
        String[] pcs = name.split("_");
        List<String> pcsList = Arrays.asList(pcs);
        name = StringUtils.join(F.map(pcsList, new F.F2<String, String>() {
            @Override public String apply(String p) {
                return StringUtils.capitalize(p);
            }
        }), "");
        return name.substring(0,1).toLowerCase() + name.substring(1);
    }

    protected void writeClass(OutputStreamWriter writer, Table table) throws IOException {
        writer.write(header());
        if(config.getCustomAnnotations() != null && config.getCustomAnnotations().containsKey(table.getName())) {
            for(String ann : config.getCustomAnnotations().get(table.getName())) {
                writer.write(ann + "\n");
            }
        }

        if(config.getMappedSuperClasses() != null && config.getMappedSuperClasses().contains(table.getName())) {
            writer.write(String.format("@javax.persistence.MappedSuperclass // for %s\n", table.getName()));
        }
        else {
            writer.write("@javax.persistence.Entity\n");
            writer.write(String.format("@javax.persistence.Table(name = \"%s\")\n", table.getName()));
        }
        if(config.getCachedClasses().contains(table.getName())) {
            writer.write("@javax.persistence.Cacheable\n");
        }


        String baseClass = "";
        if(config.getTableBaseClasses() != null && config.getTableBaseClasses().containsKey(table.getName())) {
            baseClass = config.getTableBaseClasses().get(table.getName());
        }
        else if (StringUtils.isNotBlank(config.getCommonBaseClass())) {
            baseClass = config.getCommonBaseClass();
        }

        if(StringUtils.isNotBlank(baseClass)) {
            baseClass = " extends " + baseClass;
        }

        writer.write(String.format("public class %s%s implements java.io.Serializable {\n", className(table.getName(), true), baseClass));

        writeFields(writer, table);
        writeRelationsAndAccessors(writer, table);
        writeFieldAccessors(writer, table);

        writer.write("}");

    }

    private void writeFieldAccessors(OutputStreamWriter writer, Table table) throws IOException {
        for(Column col : table.getColumns()) {
            if(ignoreColumn(col.getName())) continue;

            String fieldName = fieldName(col.getName());
            String fieldNameCap = StringUtils.capitalize(fieldName);
            String javaType = getJavaType(col);
            writer.write(String.format("\tpublic %s get%s() { return this.%s; }\n", javaType, fieldNameCap, fieldName));
            writer.write(String.format("\tpublic void set%s(%s %s) { this.%s = %s; }\n\n", fieldNameCap, javaType, fieldName, fieldName, fieldName));
        }
        writer.write("\n");
    }


    private void writeRelationsAndAccessors(OutputStreamWriter writer, Table table) throws IOException {
        StringBuilder accessors = new StringBuilder();

        if(config.getRelationships() != null && config.getRelationships().containsKey(table.getName())) {
            Relations rels = config.getRelationships().get(table.getName());
            for(Relation rel : rels.getRelated()) {
                writer.write(generateRelationField(table, rels, rel));
                accessors.append(generateRelationAccessors(rels, rel));
            }
        }

        writer.write("\n\n");
        writer.write(accessors.toString());
    }

    private String generateRelationAccessors(Relations rels, Relation rel) {
        StringBuilder sb = new StringBuilder();

        CollectionType collectionType = getOr(rel.getCollectionType(), rels.getDefaultCollectionType(), DefaultCollectionType);
        String relClassName = className(rel.getTable(), false);

        RelationType type = getOr(rel.getType(), DefaultRelationType);
        String fieldType = getRelationFieldDataType(type, rels, rel);
        String newInstType = collectionType.toJavaConcreteType(relClassName);
        if(type == RelationType.ManyToOne) {
            newInstType = relClassName;
        }

        String fieldName = rel.getName();
        String fieldNameCap = StringUtils.capitalize(fieldName);

        sb.append(String.format("\tpublic %s get%s() { \n" +
                "\t\tif(this.%s == null) {\n" +
                "\t\t\tthis.%s = new %s(); \n" +
                "\t\t}\n" +
                "\t\treturn this.%s; \n" +
                "\t}\n", fieldType, fieldNameCap, fieldName, fieldName, newInstType, fieldName));
        sb.append(String.format("\tpublic void set%s(%s %s) { this.%s = %s; }\n\n", fieldNameCap, fieldType, fieldName, fieldName, fieldName));

        return sb.toString();
    }

    private String generateRelationField(Table table, Relations rels, Relation rel) {
        StringBuilder sb = new StringBuilder();

        //relation type annotation
        RelationType type = getOr(rel.getType(), DefaultRelationType);
        String typeAnn = "@javax.persistence."+ type;
        List<String> typeAnnParams = new LinkedList<>();
        if(rel.getCascade() != null) {
            typeAnnParams.add("cascade="+rel.getCascade().javaTypeString());
        }
        if(rel.getFetchType() != null) {
            typeAnnParams.add("fetch="+rel.getFetchType().javaTypeString());
        }
        if(rel.getOrphanRemoval() != null && type == RelationType.OneToMany) {
            typeAnnParams.add("orphanRemoval=" + rel.getOrphanRemoval());
        }
        if(typeAnnParams.size() > 0) {
            typeAnn = String.format("%s(%s)", typeAnn, StringUtils.join(typeAnnParams, ", "));
        }
        sb.append("\t").append(typeAnn).append("\n");

        //FetchMode
        if(rel.getFetchMode() != null) {
            sb.append("\t@org.hibernate.annotations.Fetch(org.hibernate.annotations.FetchMode.")
                    .append(rel.getFetchMode().toString().toUpperCase())
                    .append(")\n");
        }

        //BatchSize
        if(rel.getBatchSize() != null) {
            sb.append("\t@org.hibernate.annotations.BatchSize(size = ").append(rel.getBatchSize()).append(")\n");
        }

        //Order by
        if(StringUtils.isNotBlank(rel.getOrderBy())) {
            sb.append("\t@javax.persistence.OrderBy(\"").append(rel.getOrderBy()).append("\")\n");
        }

        //Join columns
        List<JoinColumn> joinColumns = getRelJoinColumns(table, rels, rel, rel.getJoinColumns());
        if(joinColumns.size() > 0) {
            List<String> joinCols = F.map(rel.getJoinColumns(), new MakeJoinColumn());
            if(rel.getJoinColumns().size() > 1) {
                sb.append("\t@javax.persistence.JoinColumns({").append(StringUtils.join(joinCols, ", ")).append("})\n");
            }
            else {
                sb.append("\t").append(joinCols.get(0)).append("\n");
            }
        }

        //Join table
        if(rel.getJoinTable() != null) {
            JoinTable jt = rel.getJoinTable();
            sb.append("\t@javax.persistence.JoinTable(name = \"").append(jt.getTable()).append("\",\n");
            List<JoinColumn> jcs = getRelJoinColumns(table, rels, rel, jt.getJoinColumns());
            List<JoinColumn> ijcs = getRelJoinColumns(table, rels, rel, jt.getInverseJoinColumns());
            sb.append("\t\tjoinColumns = {").append(StringUtils.join(F.map(jcs, new MakeJoinColumn()),  ", ")).append("},\n");
            sb.append("\t\tinverseJoinColumns = {").append(StringUtils.join(F.map(ijcs, new MakeJoinColumn()),  ", ")).append("}");
            sb.append(")\n");
        }

        //Caching
        if(config.getCachedClasses().contains(table.getName())) {
            sb.append("\t@org.hibernate.annotations.Cache(usage=org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE)\n");
        }

        //Field
        String fieldType = getRelationFieldDataType(type, rels, rel);
        sb.append(String.format("\tprivate %s %s;\n\n", fieldType, rel.getName()));


        return sb.toString();
    }

    private String getRelationFieldDataType(RelationType type, Relations rels, Relation rel) {
        if(type == RelationType.ManyToOne) {
            return className(rel.getTable(), false);
        }
        CollectionType collectionType = getOr(rel.getCollectionType(), rels.getDefaultCollectionType(), DefaultCollectionType);
        return collectionType.toJavaType(className(rel.getTable(), false));
    }

    private List<JoinColumn> getRelJoinColumns(Table table, Relations rels, Relation rel, List<JoinColumn> joinColumns) {
        if(joinColumns == null) {
            joinColumns = new ArrayList<>();
        }
        if(joinColumns.size() == 0) {
            JoinColumn jc = new JoinColumn();
            jc.setFk(rels.getDefaultFk());

            jc.setRefCol(getOrS(rels.getDefaultRefCol(), config.getDefaultRefCol()));

            joinColumns.add(jc);
        }

        for(JoinColumn jc : joinColumns) {
            jc.setFk(getOrS(jc.getFk(), rels.getDefaultFk()));
            jc.setRefCol( getOrS(jc.getRefCol(),rels.getDefaultRefCol(), config.getDefaultRefCol()) );
            if(StringUtils.isBlank(jc.getFk())) {
                throw new RuntimeException(String.format("Failed generating code fore relation: %s on table %s. Join columns didn't specify and  no defaultFk specified.", rel.getName(), table.getName()));
            }
            if(StringUtils.isBlank(jc.getFk())) {
                throw new RuntimeException(String.format("Failed generating code fore relation: %s on table %s. Join columns didn't specify and no defaultRefCol specified on relationship nor the global config.", rel.getName(), table.getName()));
            }
        }

        return joinColumns;
    }

    class MakeJoinColumn implements F.F2<String, JoinColumn> {
        @Override public String apply(JoinColumn p) {
            String typeAnn = "@javax.persistence.JoinColumn";
            List<String> typeAnnParams = new LinkedList<>();
            if(StringUtils.isNotBlank(p.getFk())) {
                typeAnnParams.add("name=\""+p.getFk()+"\"");
            }
            if(StringUtils.isNotBlank(p.getRefCol())) {
                typeAnnParams.add("referencedColumnName=\""+p.getRefCol()+"\"");
            }
            if(!p.getInsertable()) {
                typeAnnParams.add("insertable = false");
            }
            if(!p.getUpdatable()) {
                typeAnnParams.add("updatable = false");
            }
            if(!p.getNullable()) {
                typeAnnParams.add("nullable = false");
            }
            return typeAnn + "(" + StringUtils.join(typeAnnParams, ", ") + ")";
        }
    }

    private void writeFields(OutputStreamWriter writer, Table table) throws IOException {
        for(Column col : table.getColumns()) {
            if(ignoreColumn(col.getName())) continue;

            if(col.getIsPk()) {
                writer.write("\t@javax.persistence.Id\n");
                if(config.getDefaultStrategies() != null) {
                    for(Map.Entry<String,String> entry:config.getDefaultStrategies().entrySet()) {
                        String rx = entry.getKey().replace("*", ".*");
                        if(table.getName().matches(rx)) {
                            writer.write("\t" + entry.getValue());
                            break;
                        }
                    }
                }
            }

            if(getEnumDataType(col) != null) {
                writer.write("\t@javax.persistence.Enumerated(javax.persistence.EnumType.STRING");
            }

            List<String> colAnnParams = new LinkedList<>();
            colAnnParams.add(String.format("name=\"%s\"", col.getName()));
            if(isColumnUsedInManagedRelation(table, col)) {
                colAnnParams.add("updatable = false");
                colAnnParams.add("insertable = false");
            }
            String colAnn = String.format("@javax.persistence.Column(%s)", StringUtils.join(colAnnParams, ", "));


            writer.write(String.format("\t%s\n", colAnn));
            writer.write(String.format("\tprivate %s %s;\n\n", getJavaType(col), fieldName(col.getName())));
        }
        writer.write("\n");
    }

    private boolean isColumnUsedInManagedRelation(Table table, Column col) {

        if(config.getRelationships() != null && config.getRelationships().containsKey(table.getName())) {
            Relations rels = config.getRelationships().get(table.getName());
            for(Relation rel : rels.getRelated()) {
                if(rel.getType() == RelationType.ManyToOne) {
                    List<JoinColumn> jcs = getRelJoinColumns(table, rels, rel, rel.getJoinColumns());
                    for(JoinColumn jc : jcs) {
                        if(jc.getFk().equals(col.getName())) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private String getJavaType(Column col) {
        if(config.getCustomDataTypes() != null) {
            for(CustomDataType cust : config.getCustomDataTypes()) {
                if(cust.getType().equals(col.getType()) &&
                    (cust.getLength() == null || cust.getLength().equals(col.getLength()))) {
                    return cust.getDataType();
                }
            }
        }
        if(config.getFieldTypes() != null) {
            for(FieldType ft : config.getFieldTypes()) {
                if(ft.getTable().equals(col.getTableName()) && ft.getColumn().equals(col.getName())) {
                    return ft.getType();
                }
            }
        }

        String enumType = getEnumDataType(col);
        if(enumType != null) {
            return enumType;
        }

        return TypeMap.containsKey(col.getType()) ? TypeMap.get(col.getType()) : "String";
    }


    String getEnumDataType(Column col) {
        if(config.getEnumFields() != null) {
            for(FieldType ft : config.getEnumFields()) {
                if(ft.getTable().equals(col.getTableName()) && ft.getColumn().equals(col.getName())) {
                    return ft.getType();
                }
            }
        }
        return null;
    }

    protected boolean ignoreColumn(String name) {
        return config.getBaseClassColumns() != null && config.getBaseClassColumns().contains(name);
    }

    protected String header() {
        return String.format(
                "package %s;\n\n" +
                "/**\n" +
                " * Generated by jpa-gen on %s\n" +
                " */\n", config.getPackageName(), new DateTime());
    }

    @SafeVarargs
    final <T> T getOr(T... val) {
        for(T t:val) {
            if(t != null) return t;
        }
        return null;
    }

    final String getOrS(String... val) {
        for(String t:val) {
            if(StringUtils.isNotBlank(t)) return t;
        }
        return null;
    }


}
