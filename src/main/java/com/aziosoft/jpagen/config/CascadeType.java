package com.aziosoft.jpagen.config;

/**
 * Created by mnelson on 3/6/14.
 */
public enum CascadeType {
    /** Cascade all operations */
    ALL,

    /** Cascade persist operation */
    PERSIST,

    /** Cascade merge operation */
    MERGE,

    /** Cascade remove operation */
    REMOVE,

    /** Cascade refresh operation */
    REFRESH,

    /**
     * Cascade detach operation
     *
     * @since Java Persistence 2.0
     *
     */
    DETACH;

    public String javaTypeString() {
        return "javax.persistence.CascadeType."+this;
    }
}
