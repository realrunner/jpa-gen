package com.aziosoft.jpagen.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by mnelson on 3/5/14.
 *
 */
public class SerializationUtils {
    static ObjectMapper mapper;
    public static ObjectMapper getMapper() {
        if(mapper == null) {
            mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        }
        return mapper;
    }

    public static <T> T read(InputStream s, Class<T> clazz) throws IOException {
        return (T)getMapper().readValue(s, clazz);
    }

    public static <T> void write(OutputStream s, T obj) throws IOException {
        getMapper().writeValue(s, obj);
    }

}
