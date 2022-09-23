// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.core;

import org.apache.htrace.shaded.commons.logging.LogFactory;
import java.util.HashMap;
import java.util.Map;
import org.apache.htrace.shaded.commons.logging.Log;

public abstract class HTraceConfiguration
{
    private static final Log LOG;
    private static final Map<String, String> EMPTY_MAP;
    public static final HTraceConfiguration EMPTY;
    
    public static HTraceConfiguration fromMap(final Map<String, String> conf) {
        return new MapConf(conf);
    }
    
    public static HTraceConfiguration fromKeyValuePairs(final String... pairs) {
        if (pairs.length % 2 != 0) {
            throw new RuntimeException("You must specify an equal number of keys and values.");
        }
        final Map<String, String> conf = new HashMap<String, String>();
        for (int i = 0; i < pairs.length; i += 2) {
            conf.put(pairs[i], pairs[i + 1]);
        }
        return new MapConf(conf);
    }
    
    public abstract String get(final String p0);
    
    public abstract String get(final String p0, final String p1);
    
    public boolean getBoolean(final String key, final boolean defaultValue) {
        final String value = this.get(key, String.valueOf(defaultValue)).trim().toLowerCase();
        if ("true".equals(value)) {
            return true;
        }
        if ("false".equals(value)) {
            return false;
        }
        HTraceConfiguration.LOG.warn("Expected boolean for key [" + key + "] instead got [" + value + "].");
        return defaultValue;
    }
    
    public int getInt(final String key, final int defaultVal) {
        final String val = this.get(key);
        if (val == null || val.trim().isEmpty()) {
            return defaultVal;
        }
        try {
            return Integer.parseInt(val);
        }
        catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Bad value for '" + key + "': should be int");
        }
    }
    
    static {
        LOG = LogFactory.getLog(HTraceConfiguration.class);
        EMPTY_MAP = new HashMap<String, String>(1);
        EMPTY = fromMap(HTraceConfiguration.EMPTY_MAP);
    }
    
    private static class MapConf extends HTraceConfiguration
    {
        private final Map<String, String> conf;
        
        public MapConf(final Map<String, String> conf) {
            this.conf = new HashMap<String, String>(conf);
        }
        
        @Override
        public String get(final String key) {
            return this.conf.get(key);
        }
        
        @Override
        public String get(final String key, final String defaultValue) {
            final String value = this.get(key);
            return (value == null) ? defaultValue : value;
        }
    }
}
