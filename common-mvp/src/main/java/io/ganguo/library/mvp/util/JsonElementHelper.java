package io.ganguo.library.mvp.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonElementHelper {
    public static boolean checkJsonElementNonNull(JsonElement jsonElement){
        if(jsonElement == null || (jsonElement == new JsonParser().parse("[]"))){
            return false;
        }
        return jsonElement != JsonNull.INSTANCE;
    }

    public static JsonObject jsonObjectValue(JsonElement jsonElement){
        if(jsonElement instanceof JsonObject && checkJsonElementNonNull(jsonElement)){
            return jsonElement.getAsJsonObject();
        }
        return null;
    }

    public static JsonArray jsonArrayValue(JsonElement jsonElement){
        if(jsonElement instanceof JsonArray && checkJsonElementNonNull(jsonElement)){
            return jsonElement.getAsJsonArray();
        }
        return null;
    }

    public static Boolean booleanValue(JsonElement jsonElement){
        if(checkJsonElementNonNull(jsonElement)){
            return jsonElement.getAsBoolean();
        }
        return null;
    }

    public static String stringValue(JsonElement jsonElement){
        if(checkJsonElementNonNull(jsonElement)){
            return jsonElement.getAsString();
        }
        return null;
    }

    public static Long longValue(JsonElement jsonElement){
        if(checkJsonElementNonNull(jsonElement)){
            return jsonElement.getAsLong();
        }
        return null;
    }

    public static Double doubleValue(JsonElement jsonElement){
        if(checkJsonElementNonNull(jsonElement)){
            return jsonElement.getAsDouble();
        }
        return null;
    }

    public static Integer intValue(JsonElement jsonElement){
        if(checkJsonElementNonNull(jsonElement)){
            return jsonElement.getAsInt();
        }
        return null;
    }
}
