// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.factory;

import java.util.Iterator;
import java.util.Collections;
import com.sun.jersey.core.util.KeyComparator;
import com.sun.jersey.core.util.KeyComparatorHashMap;
import com.sun.jersey.core.util.StringIgnoreCaseKeyComparator;
import java.util.Map;

public final class ResponseBuilderHeaders
{
    public static final int CACHE_CONTROL = 0;
    public static final int CONTENT_LANGUAGE = 1;
    public static final int CONTENT_LOCATION = 2;
    public static final int CONTENT_TYPE = 3;
    public static final int ETAG = 4;
    public static final int LAST_MODIFIED = 5;
    public static final int LOCATION = 6;
    private static final Map<String, Integer> HEADER_MAP;
    private static final String[] HEADER_ARRAY;
    
    private static Map<String, Integer> createHeaderMap() {
        final Map<String, Integer> m = new KeyComparatorHashMap<String, Integer>(StringIgnoreCaseKeyComparator.SINGLETON);
        m.put("Cache-Control", 0);
        m.put("Content-Language", 1);
        m.put("Content-Location", 2);
        m.put("Content-Type", 3);
        m.put("ETag", 4);
        m.put("Last-Modified", 5);
        m.put("Location", 6);
        return Collections.unmodifiableMap((Map<? extends String, ? extends Integer>)m);
    }
    
    private static String[] createHeaderArray() {
        final Map<String, Integer> m = createHeaderMap();
        final String[] a = new String[m.size()];
        for (final Map.Entry<String, Integer> e : m.entrySet()) {
            a[e.getValue()] = e.getKey();
        }
        return a;
    }
    
    public static int getSize() {
        return ResponseBuilderHeaders.HEADER_MAP.size();
    }
    
    public static String getNameFromId(final int id) {
        return ResponseBuilderHeaders.HEADER_ARRAY[id];
    }
    
    public static Integer getIdFromName(final String name) {
        return ResponseBuilderHeaders.HEADER_MAP.get(name);
    }
    
    static {
        HEADER_MAP = createHeaderMap();
        HEADER_ARRAY = createHeaderArray();
    }
}
