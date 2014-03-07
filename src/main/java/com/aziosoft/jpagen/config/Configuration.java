package com.aziosoft.jpagen.config;

import com.aziosoft.jpagen.util.SerializationUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by mnelson on 3/5/14.
 *
 */
public class Configuration {
    String packageName;
    String tablePrefix;
    String commonBaseClass;
    String schema;
    List<String> baseClassColumns;
    Boolean excludePrefixInClassNames = true;
    String defaultRefCol;
    List<String> excludeTables;
    List<String> includeTables;
    List<CustomDataType> customDataTypes;
    List<FieldType> fieldTypes;
    List<FieldType> enumFields;
    List<String> mappedSuperClasses;
    Map<String, List<String>> classListeners;
    Map<String, String> tableBaseClasses;
    Map<String, List<String>> customAnnotations;
    Map<String, String> defaultStrategies;
    Map<String, Relations> relationships;
    String mappedSuperClassSuffix;


    public static Configuration fromStream(InputStream stream) throws IOException {
        return SerializationUtils.read(stream, Configuration.class);
    }


    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public String getCommonBaseClass() {
        return commonBaseClass;
    }

    public void setCommonBaseClass(String commonBaseClass) {
        this.commonBaseClass = commonBaseClass;
    }

    public List<String> getBaseClassColumns() {
        return baseClassColumns;
    }

    public void setBaseClassColumns(List<String> baseClassColumns) {
        this.baseClassColumns = baseClassColumns;
    }

    public Boolean getExcludePrefixInClassNames() {
        return excludePrefixInClassNames;
    }

    public void setExcludePrefixInClassNames(Boolean excludePrefixInClassNames) {
        this.excludePrefixInClassNames = excludePrefixInClassNames;
    }

    public String getDefaultRefCol() {
        return defaultRefCol;
    }

    public void setDefaultRefCol(String defaultRefCol) {
        this.defaultRefCol = defaultRefCol;
    }

    public List<String> getExcludeTables() {
        return excludeTables;
    }

    public void setExcludeTables(List<String> excludeTables) {
        this.excludeTables = excludeTables;
    }

    public List<String> getIncludeTables() {
        return includeTables;
    }

    public void setIncludeTables(List<String> includeTables) {
        this.includeTables = includeTables;
    }

    public List<CustomDataType> getCustomDataTypes() {
        return customDataTypes;
    }

    public void setCustomDataTypes(List<CustomDataType> customDataTypes) {
        this.customDataTypes = customDataTypes;
    }

    public List<FieldType> getFieldTypes() {
        return fieldTypes;
    }

    public void setFieldTypes(List<FieldType> fieldTypes) {
        this.fieldTypes = fieldTypes;
    }

    public List<FieldType> getEnumFields() {
        return enumFields;
    }

    public void setEnumFields(List<FieldType> enumFields) {
        this.enumFields = enumFields;
    }

    public List<String> getMappedSuperClasses() {
        return mappedSuperClasses;
    }

    public void setMappedSuperClasses(List<String> mappedSuperClasses) {
        this.mappedSuperClasses = mappedSuperClasses;
    }

    public Map<String, List<String>> getClassListeners() {
        return classListeners;
    }

    public void setClassListeners(Map<String, List<String>> classListeners) {
        this.classListeners = classListeners;
    }

    public Map<String, String> getTableBaseClasses() {
        return tableBaseClasses;
    }

    public void setTableBaseClasses(Map<String, String> tableBaseClasses) {
        this.tableBaseClasses = tableBaseClasses;
    }

    public Map<String, List<String>> getCustomAnnotations() {
        return customAnnotations;
    }

    public void setCustomAnnotations(Map<String, List<String>> customAnnotations) {
        this.customAnnotations = customAnnotations;
    }

    public Map<String, String> getDefaultStrategies() {
        return defaultStrategies;
    }

    public void setDefaultStrategies(Map<String, String> defaultStrategies) {
        this.defaultStrategies = defaultStrategies;
    }

    public Map<String, Relations> getRelationships() {
        return relationships;
    }

    public void setRelationships(Map<String, Relations> relationships) {
        this.relationships = relationships;
    }

    public String getMappedSuperClassSuffix() {
        return mappedSuperClassSuffix;
    }

    public void setMappedSuperClassSuffix(String mappedSuperClassSuffix) {
        this.mappedSuperClassSuffix = mappedSuperClassSuffix;
    }
}
