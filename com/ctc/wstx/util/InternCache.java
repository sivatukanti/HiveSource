// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.util;

import java.util.Map;
import java.util.LinkedHashMap;

public final class InternCache extends LinkedHashMap<String, String>
{
    private static final int DEFAULT_SIZE = 64;
    private static final int MAX_SIZE = 660;
    private static final InternCache sInstance;
    
    private InternCache() {
        super(64, 0.6666f, false);
    }
    
    public static InternCache getInstance() {
        return InternCache.sInstance;
    }
    
    public String intern(final String input) {
        String result;
        synchronized (this) {
            result = ((LinkedHashMap<K, String>)this).get(input);
        }
        if (result == null) {
            result = input.intern();
            synchronized (this) {
                this.put(result, result);
            }
        }
        return result;
    }
    
    @Override
    protected boolean removeEldestEntry(final Map.Entry<String, String> eldest) {
        return this.size() > 660;
    }
    
    static {
        sInstance = new InternCache();
    }
}
