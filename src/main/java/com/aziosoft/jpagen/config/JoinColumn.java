package com.aziosoft.jpagen.config;

/**
 * Created by mnelson on 3/5/14.
 *
 */
public class JoinColumn {
    String fk;
    String refCol;
    Boolean nullable = true;
    Boolean updatable = true;
    Boolean insertable = true;

    public String getFk() {
        return fk;
    }

    public void setFk(String fk) {
        this.fk = fk;
    }

    public String getRefCol() {
        return refCol;
    }

    public void setRefCol(String refCol) {
        this.refCol = refCol;
    }

    public Boolean getNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public Boolean getUpdatable() {
        return updatable;
    }

    public void setUpdatable(Boolean updatable) {
        this.updatable = updatable;
    }

    public Boolean getInsertable() {
        return insertable;
    }

    public void setInsertable(Boolean insertable) {
        this.insertable = insertable;
    }
}
