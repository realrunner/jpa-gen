package com.aziosoft.jpagen.config;

import java.util.List;

/**
 * Created by mnelson on 3/5/14.
 *
 */
public class Relations {
    String defaultFk;
    String defaultRefCol;
    CollectionType defaultCollectionType;
    List<Relation> related;

    public String getDefaultFk() {
        return defaultFk;
    }

    public void setDefaultFk(String defaultFk) {
        this.defaultFk = defaultFk;
    }

    public String getDefaultRefCol() {
        return defaultRefCol;
    }

    public void setDefaultRefCol(String defaultRefCol) {
        this.defaultRefCol = defaultRefCol;
    }

    public CollectionType getDefaultCollectionType() {
        return defaultCollectionType;
    }

    public void setDefaultCollectionType(CollectionType defaultCollectionType) {
        this.defaultCollectionType = defaultCollectionType;
    }

    public List<Relation> getRelated() {
        return related;
    }

    public void setRelated(List<Relation> related) {
        this.related = related;
    }
}
