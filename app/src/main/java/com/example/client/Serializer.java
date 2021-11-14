package com.example.client;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Serializer {

    public static <T> T deserialize(String value, Class<T> returnType) {
        return (new Gson()).fromJson(value, returnType);
    }

    public static String serialize (Object value) {
        return (new GsonBuilder().setPrettyPrinting().create()).toJson(value);
    }
}
