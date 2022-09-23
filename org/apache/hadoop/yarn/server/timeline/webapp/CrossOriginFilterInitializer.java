// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.timeline.webapp;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.FilterContainer;
import org.apache.hadoop.http.FilterInitializer;

public class CrossOriginFilterInitializer extends FilterInitializer
{
    public static final String PREFIX = "yarn.timeline-service.http-cross-origin.";
    
    @Override
    public void initFilter(final FilterContainer container, final Configuration conf) {
        container.addGlobalFilter("Cross Origin Filter", CrossOriginFilter.class.getName(), getFilterParameters(conf));
    }
    
    static Map<String, String> getFilterParameters(final Configuration conf) {
        final Map<String, String> filterParams = new HashMap<String, String>();
        for (final Map.Entry<String, String> entry : conf.getValByRegex("yarn.timeline-service.http-cross-origin.").entrySet()) {
            String name = entry.getKey();
            final String value = entry.getValue();
            name = name.substring("yarn.timeline-service.http-cross-origin.".length());
            filterParams.put(name, value);
        }
        return filterParams;
    }
}
