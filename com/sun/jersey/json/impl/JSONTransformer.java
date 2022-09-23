// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl;

import org.codehaus.jettison.json.JSONArray;
import java.util.LinkedList;
import java.util.Collection;
import org.codehaus.jettison.json.JSONException;
import java.util.Iterator;
import org.codehaus.jettison.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

final class JSONTransformer
{
    static <T> Map<String, T> asMap(final String jsonObjectVal) throws JSONException {
        if (null == jsonObjectVal) {
            return null;
        }
        final Map<String, T> result = new HashMap<String, T>();
        final JSONObject sourceMap = new JSONObject(jsonObjectVal);
        final Iterator<String> keyIterator = (Iterator<String>)sourceMap.keys();
        while (keyIterator.hasNext()) {
            final String key = keyIterator.next();
            result.put(key, (T)sourceMap.get(key));
        }
        return result;
    }
    
    static <T> Collection<T> asCollection(final String jsonArrayVal) throws JSONException {
        if (null == jsonArrayVal) {
            return null;
        }
        final Collection<T> result = new LinkedList<T>();
        final JSONArray arrayVal = new JSONArray(jsonArrayVal);
        for (int i = 0; i < arrayVal.length(); ++i) {
            result.add((T)arrayVal.get(i));
        }
        return result;
    }
    
    static String asJsonArray(final Collection<?> collection) {
        return (null == collection) ? "[]" : new JSONArray(collection).toString();
    }
    
    static String asJsonObject(final Map map) {
        return (null == map) ? "{}" : new JSONObject(map).toString();
    }
}
