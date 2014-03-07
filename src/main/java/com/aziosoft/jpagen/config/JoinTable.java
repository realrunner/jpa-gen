package com.aziosoft.jpagen.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mnelson on 3/5/14.
 *
 */
public class JoinTable {
    String table;
    List<JoinColumn> joinColumns = new ArrayList<>();
    List<JoinColumn> inverseJoinColumns = new ArrayList<>();

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<JoinColumn> getJoinColumns() {
        return joinColumns;
    }

    public void setJoinColumns(List<JoinColumn> joinColumns) {
        this.joinColumns = joinColumns;
    }

    public List<JoinColumn> getInverseJoinColumns() {
        return inverseJoinColumns;
    }

    public void setInverseJoinColumns(List<JoinColumn> inverseJoinColumns) {
        this.inverseJoinColumns = inverseJoinColumns;
    }
}
