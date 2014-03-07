package com.aziosoft.jpagen.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mnelson on 3/5/14.
 *
 */
public class Relation {
    String table;
    String name;
    RelationType type;
    Boolean includeInverse = true;
    FetchType fetchType;
    FetchMode fetchMode;
    CascadeType cascade;
    CollectionType collectionType;
    Boolean orphanRemoval = true;
    String orderBy;
    List<JoinColumn> joinColumns = new ArrayList<>();
    JoinTable joinTable;
    Integer batchSize;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RelationType getType() {
        return type;
    }

    public void setType(RelationType type) {
        this.type = type;
    }

    public Boolean getIncludeInverse() {
        return includeInverse;
    }

    public void setIncludeInverse(Boolean includeInverse) {
        this.includeInverse = includeInverse;
    }

    public FetchType getFetchType() {
        return fetchType;
    }

    public void setFetchType(FetchType fetchType) {
        this.fetchType = fetchType;
    }

    public FetchMode getFetchMode() {
        return fetchMode;
    }

    public void setFetchMode(FetchMode fetchMode) {
        this.fetchMode = fetchMode;
    }

    public CascadeType getCascade() {
        return cascade;
    }

    public void setCascade(CascadeType cascade) {
        this.cascade = cascade;
    }

    public CollectionType getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(CollectionType collectionType) {
        this.collectionType = collectionType;
    }

    public Boolean getOrphanRemoval() {
        return orphanRemoval;
    }

    public void setOrphanRemoval(Boolean orphanRemoval) {
        this.orphanRemoval = orphanRemoval;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public List<JoinColumn> getJoinColumns() {
        return joinColumns;
    }

    public void setJoinColumns(List<JoinColumn> joinColumns) {
        this.joinColumns = joinColumns;
    }

    public JoinTable getJoinTable() {
        return joinTable;
    }

    public void setJoinTable(JoinTable joinTable) {
        this.joinTable = joinTable;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }
}
