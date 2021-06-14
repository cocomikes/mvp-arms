package io.ganguo.library.mvp.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import java.io.Reader;
import java.lang.reflect.Type;


/**
 * Gson utilities.
 *
 * Created by tony on 8/21/14.
 */
public abstract class GsonUtils {
    private static final String TAG = GsonUtils.class.getName();

    private static final Gson GSON = createGson(true);
    private static final Gson GSON_NO_NULLS = createGson(false);
    private static final Gson GSON_EXPOSE = createExposedGson();

    /**
     * Create the standard {Gson} configuration
     *
     * @return created gson, never null
     */
    public static final Gson createGson() {
        return createGson(true);
    }

    /**
     * Create the standard {Gson} configurationØ
     *
     * @param serializeNulls whether nulls should be serialized
     * @return created gson, never null
     */
    public static final Gson createGson(final boolean serializeNulls) {
        final GsonBuilder builder = new GsonBuilder();

        // 是否序列号带空的参数到gson中
        // { token:null }
        if (serializeNulls) {
            builder.serializeNulls();
        }
        return builder.create();
    }

    public static final Gson createExposedGson() {
        final GsonBuilder builder = new GsonBuilder();

        builder.serializeNulls();
        builder.excludeFieldsWithoutExposeAnnotation();

        return builder.create();
    }

    /**
     * Get reusable pre-configured {Gson} instance
     *
     * @return Gson instance
     */
    public static final Gson getGson() {
        return GSON;
    }

    /**
     * Get reusable pre-configured {Gson} instance
     *
     * @return Gson instance
     */
    public static final Gson getGson(final boolean serializeNulls) {
        return serializeNulls ? GSON : GSON_NO_NULLS;
    }

    public static final Gson getExposedGson() {
        return GSON_EXPOSE;
    }

    /**
     * Convert object to json
     *
     * @return json string
     */
    public static final String toJson(final Object object) {
        return toJson(object, true);
    }

    /**
     * Convert object to json
     *
     * @return json string
     */
    public static final String toJson(final Object object, final boolean includeNulls) {
        return includeNulls ? GSON.toJson(object) : GSON_NO_NULLS.toJson(object);
    }

    /**
     * Convert string to given type
     *
     * @return instance of type
     */
    public static final <V> V fromJson(String json, Class<V> type) {
        if(TextUtils.isEmpty(json)){
            return null;
        }

        try{
            return GSON.fromJson(json, type);
        } catch (JsonSyntaxException e){
            android.util.Log.e(TAG,"",e);
        }
        return null;
    }

    /**
     * Convert string to given type
     *
     * @return instance of type
     */
    public static final <V> V fromJson(String json, Type type) {
        if(TextUtils.isEmpty(json)){
            return null;
        }

        try{
            return GSON.fromJson(json, type);
        } catch (JsonSyntaxException e){
            android.util.Log.e(TAG,"",e);
        }
        return null;
    }

    /**
     * Convert content of reader to given type
     *
     * @return instance of type
     */
    public static final <V> V fromJson(Reader reader, Class<V> type) {
        if(reader == null){
            return null;
        }

        try{
            return GSON.fromJson(reader, type);
        } catch (JsonSyntaxException e){
            android.util.Log.e(TAG,"",e);
        }
        return null;
    }

    /**
     * Convert content of reader to given type
     *
     * @return instance of type
     */
    public static final <V> V fromJson(Reader reader, Type type) {
        if(reader == null){
            return null;
        }

        try{
            return GSON.fromJson(reader, type);
        } catch (JsonSyntaxException e){
            android.util.Log.e(TAG,"",e);
        }
        return null;
    }

    /**
     * Convert content of element to given type
     *
     * @return instance of type
     */
    public static final <V> V fromJson(JsonElement element, Class<V> type) {
        if(element == null){
            return null;
        }

        try{
            return GSON.fromJson(element, type);
        } catch (JsonSyntaxException e){
            android.util.Log.e(TAG,"",e);
        }
        return null;
    }

    public static final <V> V fromJson(JsonElement element, Type type) {
        if(element == null){
            return null;
        }

        try{
            return GSON.fromJson(element, type);
        } catch (JsonSyntaxException e){
            android.util.Log.e(TAG,"",e);
        }
        return null;
    }

}