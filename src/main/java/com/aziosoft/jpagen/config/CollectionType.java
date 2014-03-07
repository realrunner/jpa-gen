package com.aziosoft.jpagen.config;

import java.util.HashMap;

/**
 * Created by mnelson on 3/6/14.
 */
public enum CollectionType {
    List, Map, Set;

    final static java.util.Map<CollectionType, String> ConcreteMap;
    static {
        ConcreteMap = new HashMap<>();
        ConcreteMap.put(List, "ArrayList");
        ConcreteMap.put(Map, "HashMap");
        ConcreteMap.put(Set, "HashSet");
    }


    public String toJavaType(String subType) {
        return "java.util."+this+"<" + subType + ">";
    }

    public String toJavaConcreteType(String subType) {
        return "java.util." + ConcreteMap.get(this) + "<" + subType + ">";
    }
}
