package com.sm.lt.infrastructure;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
public class JsonUtils {

    private static final Gson gson = new GsonBuilder().serializeNulls().create();

    public static String toJson(Object o) {
        return gson.toJson(o);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return gson.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            log.error("Error while parsing " + json + " to " + clazz, e);
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(InputStream json, Class<T> clazz) {
        try {
            return gson.fromJson(new InputStreamReader(json), clazz);
        } catch (JsonSyntaxException | JsonIOException e) {
            log.error("Error while parsing " + json + " to " + clazz, e);
            throw new RuntimeException(e);
        }
    }

    public static Jsonizer jsonizer(Object object) {
        return new Jsonizer(object);
    }

    @RequiredArgsConstructor
    public static class Jsonizer {
        private final Object object;

        @Override
        public String toString() {
            return toJson(object);
        }
    }
}