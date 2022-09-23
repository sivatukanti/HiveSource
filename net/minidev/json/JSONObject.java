// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json;

import java.util.Collection;
import java.util.Iterator;
import net.minidev.json.reader.JsonWriter;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class JSONObject extends HashMap<String, Object> implements JSONAware, JSONAwareEx, JSONStreamAwareEx
{
    private static final long serialVersionUID = -503443796854799292L;
    
    public JSONObject() {
    }
    
    public static String escape(final String s) {
        return JSONValue.escape(s);
    }
    
    public static String toJSONString(final Map<String, ?> map) {
        return toJSONString(map, JSONValue.COMPRESSION);
    }
    
    public static String toJSONString(final Map<String, ?> map, final JSONStyle compression) {
        final StringBuilder sb = new StringBuilder();
        try {
            writeJSON(map, sb, compression);
        }
        catch (IOException ex) {}
        return sb.toString();
    }
    
    public static void writeJSONKV(final String key, final Object value, final Appendable out, final JSONStyle compression) throws IOException {
        if (key == null) {
            out.append("null");
        }
        else if (!compression.mustProtectKey(key)) {
            out.append(key);
        }
        else {
            out.append('\"');
            JSONValue.escape(key, out, compression);
            out.append('\"');
        }
        out.append(':');
        if (value instanceof String) {
            compression.writeString(out, (String)value);
        }
        else {
            JSONValue.writeJSONString(value, out, compression);
        }
    }
    
    public JSONObject appendField(final String fieldName, final Object fieldValue) {
        this.put(fieldName, fieldValue);
        return this;
    }
    
    public String getAsString(final String key) {
        final Object obj = ((HashMap<K, Object>)this).get(key);
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }
    
    public Number getAsNumber(final String key) {
        final Object obj = ((HashMap<K, Object>)this).get(key);
        if (obj == null) {
            return null;
        }
        if (obj instanceof Number) {
            return (Number)obj;
        }
        return Long.valueOf(obj.toString());
    }
    
    public JSONObject(final Map<String, ?> map) {
        super(map);
    }
    
    public static void writeJSON(final Map<String, ?> map, final Appendable out) throws IOException {
        writeJSON(map, out, JSONValue.COMPRESSION);
    }
    
    public static void writeJSON(final Map<String, ?> map, final Appendable out, final JSONStyle compression) throws IOException {
        if (map == null) {
            out.append("null");
            return;
        }
        JsonWriter.JSONMapWriter.writeJSONString(map, out, compression);
    }
    
    @Override
    public void writeJSONString(final Appendable out) throws IOException {
        writeJSON(this, out, JSONValue.COMPRESSION);
    }
    
    @Override
    public void writeJSONString(final Appendable out, final JSONStyle compression) throws IOException {
        writeJSON(this, out, compression);
    }
    
    public void merge(final Object o2) {
        merge(this, o2);
    }
    
    protected static JSONObject merge(final JSONObject o1, final Object o2) {
        if (o2 == null) {
            return o1;
        }
        if (o2 instanceof JSONObject) {
            return merge(o1, (JSONObject)o2);
        }
        throw new RuntimeException("JSON megre can not merge JSONObject with " + o2.getClass());
    }
    
    private static JSONObject merge(final JSONObject o1, final JSONObject o2) {
        if (o2 == null) {
            return o1;
        }
        for (final String key : ((HashMap<String, V>)o1).keySet()) {
            final Object value1 = ((HashMap<K, Object>)o1).get(key);
            final Object value2 = ((HashMap<K, Object>)o2).get(key);
            if (value2 == null) {
                continue;
            }
            if (value1 instanceof JSONArray) {
                ((HashMap<String, JSONArray>)o1).put(key, merge((JSONArray)value1, value2));
            }
            else if (value1 instanceof JSONObject) {
                ((HashMap<String, JSONObject>)o1).put(key, merge((JSONObject)value1, value2));
            }
            else {
                if (value1.equals(value2)) {
                    continue;
                }
                if (value1.getClass().equals(value2.getClass())) {
                    throw new RuntimeException("JSON merge can not merge two " + value1.getClass().getName() + " Object together");
                }
                throw new RuntimeException("JSON merge can not merge " + value1.getClass().getName() + " with " + value2.getClass().getName());
            }
        }
        for (final String key : ((HashMap<String, V>)o2).keySet()) {
            if (o1.containsKey(key)) {
                continue;
            }
            o1.put(key, ((HashMap<K, Object>)o2).get(key));
        }
        return o1;
    }
    
    protected static JSONArray merge(final JSONArray o1, final Object o2) {
        if (o2 == null) {
            return o1;
        }
        if (o1 instanceof JSONArray) {
            return merge(o1, (JSONArray)o2);
        }
        o1.add(o2);
        return o1;
    }
    
    private static JSONArray merge(final JSONArray o1, final JSONArray o2) {
        o1.addAll(o2);
        return o1;
    }
    
    @Override
    public String toJSONString() {
        return toJSONString(this, JSONValue.COMPRESSION);
    }
    
    @Override
    public String toJSONString(final JSONStyle compression) {
        return toJSONString(this, compression);
    }
    
    public String toString(final JSONStyle compression) {
        return toJSONString(this, compression);
    }
    
    @Override
    public String toString() {
        return toJSONString(this, JSONValue.COMPRESSION);
    }
}
