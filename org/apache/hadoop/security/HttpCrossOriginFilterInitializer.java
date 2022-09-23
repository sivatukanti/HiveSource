// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.security.http.CrossOriginFilter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.FilterContainer;
import org.slf4j.Logger;
import org.apache.hadoop.http.FilterInitializer;

public class HttpCrossOriginFilterInitializer extends FilterInitializer
{
    public static final String PREFIX = "hadoop.http.cross-origin.";
    public static final String ENABLED_SUFFIX = "enabled";
    private static final Logger LOG;
    
    @Override
    public void initFilter(final FilterContainer container, final Configuration conf) {
        final String key = this.getEnabledConfigKey();
        final boolean enabled = conf.getBoolean(key, false);
        if (enabled) {
            container.addGlobalFilter("Cross Origin Filter", CrossOriginFilter.class.getName(), getFilterParameters(conf, this.getPrefix()));
        }
        else {
            HttpCrossOriginFilterInitializer.LOG.info("CORS filter not enabled. Please set " + key + " to 'true' to enable it");
        }
    }
    
    protected static Map<String, String> getFilterParameters(final Configuration conf, final String prefix) {
        final Map<String, String> filterParams = new HashMap<String, String>();
        for (final Map.Entry<String, String> entry : conf.getValByRegex(prefix).entrySet()) {
            String name = entry.getKey();
            final String value = entry.getValue();
            name = name.substring(prefix.length());
            filterParams.put(name, value);
        }
        return filterParams;
    }
    
    protected String getPrefix() {
        return "hadoop.http.cross-origin.";
    }
    
    protected String getEnabledConfigKey() {
        return this.getPrefix() + "enabled";
    }
    
    static {
        LOG = LoggerFactory.getLogger(HttpCrossOriginFilterInitializer.class);
    }
}
