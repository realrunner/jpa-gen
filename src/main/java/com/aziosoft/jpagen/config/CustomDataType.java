package com.aziosoft.jpagen.config;

import com.aziosoft.jpagen.db.ColumnType;

/**
 * Created by mnelson on 3/6/14.
 */
public class CustomDataType {
    ColumnType type;
    Integer length;
    String dataType;

    public ColumnType getType() {
        return type;
    }

    public void setType(ColumnType type) {
        this.type = type;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
