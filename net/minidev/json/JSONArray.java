// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json;

import net.minidev.json.reader.JsonWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class JSONArray extends ArrayList<Object> implements List<Object>, JSONAwareEx, JSONStreamAwareEx
{
    private static final long serialVersionUID = 9106884089231309568L;
    
    public static String toJSONString(final List<?> list) {
        return toJSONString(list, JSONValue.COMPRESSION);
    }
    
    public static String toJSONString(final List<?> list, final JSONStyle compression) {
        final StringBuilder sb = new StringBuilder();
        try {
            writeJSONString(list, sb, compression);
        }
        catch (IOException ex) {}
        return sb.toString();
    }
    
    public static void writeJSONString(final Iterable<?> list, final Appendable out, final JSONStyle compression) throws IOException {
        if (list == null) {
            out.append("null");
            return;
        }
        JsonWriter.JSONIterableWriter.writeJSONString(list, out, compression);
    }
    
    public static void writeJSONString(final List<?> list, final Appendable out) throws IOException {
        writeJSONString(list, out, JSONValue.COMPRESSION);
    }
    
    public JSONArray appendElement(final Object element) {
        this.add(element);
        return this;
    }
    
    public void merge(final Object o2) {
        JSONObject.merge(this, o2);
    }
    
    @Override
    public String toJSONString() {
        return toJSONString(this, JSONValue.COMPRESSION);
    }
    
    @Override
    public String toJSONString(final JSONStyle compression) {
        return toJSONString(this, compression);
    }
    
    @Override
    public String toString() {
        return this.toJSONString();
    }
    
    public String toString(final JSONStyle compression) {
        return this.toJSONString(compression);
    }
    
    @Override
    public void writeJSONString(final Appendable out) throws IOException {
        writeJSONString(this, out, JSONValue.COMPRESSION);
    }
    
    @Override
    public void writeJSONString(final Appendable out, final JSONStyle compression) throws IOException {
        writeJSONString(this, out, compression);
    }
}
