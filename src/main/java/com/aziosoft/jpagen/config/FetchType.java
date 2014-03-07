package com.aziosoft.jpagen.config;

/**
 * Created by mnelson on 3/6/14.
 */
public enum FetchType {
    Eager, Lazy;

    public String javaTypeString() {
        return "javax.persistence.FetchType."+this.toString().toUpperCase();
    }
}
