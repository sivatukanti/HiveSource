// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import java.util.Iterator;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.security.authentication.server.AuthenticationFilter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.FilterContainer;
import org.apache.hadoop.http.FilterInitializer;

public class AuthenticationFilterInitializer extends FilterInitializer
{
    static final String PREFIX = "hadoop.http.authentication.";
    
    @Override
    public void initFilter(final FilterContainer container, final Configuration conf) {
        final Map<String, String> filterConfig = getFilterConfigMap(conf, "hadoop.http.authentication.");
        container.addFilter("authentication", AuthenticationFilter.class.getName(), filterConfig);
    }
    
    public static Map<String, String> getFilterConfigMap(final Configuration conf, final String prefix) {
        final Map<String, String> filterConfig = new HashMap<String, String>();
        filterConfig.put("cookie.path", "/");
        final Map<String, String> propsWithPrefix = conf.getPropsWithPrefix(prefix);
        for (final Map.Entry<String, String> entry : propsWithPrefix.entrySet()) {
            filterConfig.put(entry.getKey(), entry.getValue());
        }
        final String bindAddress = conf.get("bind.address");
        String principal = filterConfig.get("kerberos.principal");
        if (principal != null) {
            try {
                principal = SecurityUtil.getServerPrincipal(principal, bindAddress);
            }
            catch (IOException ex) {
                throw new RuntimeException("Could not resolve Kerberos principal name: " + ex.toString(), ex);
            }
            filterConfig.put("kerberos.principal", principal);
        }
        return filterConfig;
    }
}
