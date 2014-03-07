package com.aziosoft.jpagen.db;

/**
 * Created by mnelson on 3/5/14.
 *
 */
public class ForeignKey {
    String childTableName;
    String parentTableName;
    String fromColumnName;
    String toColumnName;

    public String getChildTableName() {
        return childTableName;
    }

    public void setChildTableName(String childTableName) {
        this.childTableName = childTableName;
    }

    public String getParentTableName() {
        return parentTableName;
    }

    public void setParentTableName(String parentTableName) {
        this.parentTableName = parentTableName;
    }

    public String getFromColumnName() {
        return fromColumnName;
    }

    public void setFromColumnName(String fromColumnName) {
        this.fromColumnName = fromColumnName;
    }

    public String getToColumnName() {
        return toColumnName;
    }

    public void setToColumnName(String toColumnName) {
        this.toColumnName = toColumnName;
    }
}
