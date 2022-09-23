// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.util;

import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import net.minidev.json.JSONArray;
import java.net.URISyntaxException;
import java.net.URI;
import java.text.ParseException;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.JSONObject;

public class JSONObjectUtils
{
    public static JSONObject parse(final String s) throws ParseException {
        Object o;
        try {
            o = new JSONParser(640).parse(s);
        }
        catch (net.minidev.json.parser.ParseException e) {
            throw new ParseException("Invalid JSON: " + e.getMessage(), 0);
        }
        if (o instanceof JSONObject) {
            return (JSONObject)o;
        }
        throw new ParseException("JSON entity is not an object", 0);
    }
    
    @Deprecated
    public static JSONObject parseJSONObject(final String s) throws ParseException {
        return parse(s);
    }
    
    private static <T> T getGeneric(final JSONObject o, final String key, final Class<T> clazz) throws ParseException {
        if (!o.containsKey(key)) {
            throw new ParseException("Missing JSON object member with key \"" + key + "\"", 0);
        }
        if (o.get(key) == null) {
            throw new ParseException("JSON object member with key \"" + key + "\" has null value", 0);
        }
        final Object value = ((HashMap<K, Object>)o).get(key);
        if (!clazz.isAssignableFrom(value.getClass())) {
            throw new ParseException("Unexpected type of JSON object member with key \"" + key + "\"", 0);
        }
        return (T)value;
    }
    
    public static boolean getBoolean(final JSONObject o, final String key) throws ParseException {
        return getGeneric(o, key, Boolean.class);
    }
    
    public static int getInt(final JSONObject o, final String key) throws ParseException {
        return getGeneric(o, key, Number.class).intValue();
    }
    
    public static long getLong(final JSONObject o, final String key) throws ParseException {
        return getGeneric(o, key, Number.class).longValue();
    }
    
    public static float getFloat(final JSONObject o, final String key) throws ParseException {
        return getGeneric(o, key, Number.class).floatValue();
    }
    
    public static double getDouble(final JSONObject o, final String key) throws ParseException {
        return getGeneric(o, key, Number.class).doubleValue();
    }
    
    public static String getString(final JSONObject o, final String key) throws ParseException {
        return getGeneric(o, key, String.class);
    }
    
    public static URI getURI(final JSONObject o, final String key) throws ParseException {
        try {
            return new URI(getGeneric(o, key, String.class));
        }
        catch (URISyntaxException e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }
    
    public static JSONArray getJSONArray(final JSONObject o, final String key) throws ParseException {
        return getGeneric(o, key, JSONArray.class);
    }
    
    public static String[] getStringArray(final JSONObject o, final String key) throws ParseException {
        final JSONArray jsonArray = getJSONArray(o, key);
        try {
            return jsonArray.toArray(new String[0]);
        }
        catch (ArrayStoreException ex) {
            throw new ParseException("JSON object member with key \"" + key + "\" is not an array of strings", 0);
        }
    }
    
    public static List<String> getStringList(final JSONObject o, final String key) throws ParseException {
        final String[] array = getStringArray(o, key);
        return Arrays.asList(array);
    }
    
    public static JSONObject getJSONObject(final JSONObject o, final String key) throws ParseException {
        return getGeneric(o, key, JSONObject.class);
    }
    
    private JSONObjectUtils() {
    }
}
