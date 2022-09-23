// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.http.lib;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.Filter;
import java.security.Principal;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.HashMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.FilterContainer;
import org.slf4j.Logger;
import org.apache.hadoop.http.FilterInitializer;

public class StaticUserWebFilter extends FilterInitializer
{
    static final String DEPRECATED_UGI_KEY = "dfs.web.ugi";
    private static final Logger LOG;
    
    @Override
    public void initFilter(final FilterContainer container, final Configuration conf) {
        final HashMap<String, String> options = new HashMap<String, String>();
        final String username = getUsernameFromConf(conf);
        options.put("hadoop.http.staticuser.user", username);
        container.addFilter("static_user_filter", StaticUserFilter.class.getName(), options);
    }
    
    static String getUsernameFromConf(final Configuration conf) {
        final String oldStyleUgi = conf.get("dfs.web.ugi");
        if (oldStyleUgi != null) {
            StaticUserWebFilter.LOG.warn("dfs.web.ugi should not be used. Instead, use hadoop.http.staticuser.user.");
            final String[] parts = oldStyleUgi.split(",");
            return parts[0];
        }
        return conf.get("hadoop.http.staticuser.user", "dr.who");
    }
    
    static {
        LOG = LoggerFactory.getLogger(StaticUserWebFilter.class);
    }
    
    static class User implements Principal
    {
        private final String name;
        
        public User(final String name) {
            this.name = name;
        }
        
        @Override
        public String getName() {
            return this.name;
        }
        
        @Override
        public int hashCode() {
            return this.name.hashCode();
        }
        
        @Override
        public boolean equals(final Object other) {
            return other == this || (other != null && other.getClass() == this.getClass() && ((User)other).name.equals(this.name));
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
    
    public static class StaticUserFilter implements Filter
    {
        private User user;
        private String username;
        
        @Override
        public void destroy() {
        }
        
        @Override
        public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
            final HttpServletRequest httpRequest = (HttpServletRequest)request;
            if (httpRequest.getRemoteUser() != null) {
                chain.doFilter(request, response);
            }
            else {
                final HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(httpRequest) {
                    @Override
                    public Principal getUserPrincipal() {
                        return StaticUserFilter.this.user;
                    }
                    
                    @Override
                    public String getRemoteUser() {
                        return StaticUserFilter.this.username;
                    }
                };
                chain.doFilter(wrapper, response);
            }
        }
        
        @Override
        public void init(final FilterConfig conf) throws ServletException {
            this.username = conf.getInitParameter("hadoop.http.staticuser.user");
            this.user = new User(this.username);
        }
    }
}
