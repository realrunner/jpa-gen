package com.aziosoft.jpagen.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mnelson on 3/5/14.
 *
 */
public class F {
    public static <K,V> Map<K,V> makeMap(Iterable<V> list, F2<K,V> propGetter){
        Map<K,V> map = new LinkedHashMap<K, V>();
        for(V v : list) {
            map.put(propGetter.apply(v), v);
        }
        return map;
    }

    public static <K,R> List<R> map(Iterable<K> list, F2<R,K> extractor) {
        List<R> l = new ArrayList<R>();
        for(K k : list) {
            l.add(extractor.apply(k));
        }
        return l;
    }

    public static interface F2<R1,P1> {
        public R1 apply(P1 p);
    }
}
