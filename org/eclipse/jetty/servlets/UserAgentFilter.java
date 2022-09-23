// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlets;

import java.util.regex.Matcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.servlet.Filter;

public class UserAgentFilter implements Filter
{
    private Pattern _pattern;
    private Map _agentCache;
    private int _agentCacheSize;
    private String _attribute;
    
    public UserAgentFilter() {
        this._agentCache = new ConcurrentHashMap();
        this._agentCacheSize = 1024;
    }
    
    public void destroy() {
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        if (this._attribute != null && this._pattern != null) {
            final String ua = this.getUserAgent(request);
            request.setAttribute(this._attribute, ua);
        }
        chain.doFilter(request, response);
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        this._attribute = filterConfig.getInitParameter("attribute");
        final String p = filterConfig.getInitParameter("userAgent");
        if (p != null) {
            this._pattern = Pattern.compile(p);
        }
        final String size = filterConfig.getInitParameter("cacheSize");
        if (size != null) {
            this._agentCacheSize = Integer.parseInt(size);
        }
    }
    
    public String getUserAgent(final ServletRequest request) {
        final String ua = ((HttpServletRequest)request).getHeader("User-Agent");
        return this.getUserAgent(ua);
    }
    
    public String getUserAgent(final String ua) {
        if (ua == null) {
            return null;
        }
        String tag = this._agentCache.get(ua);
        if (tag == null) {
            final Matcher matcher = this._pattern.matcher(ua);
            if (matcher.matches()) {
                if (matcher.groupCount() > 0) {
                    for (int g = 1; g <= matcher.groupCount(); ++g) {
                        final String group = matcher.group(g);
                        if (group != null) {
                            tag = ((tag == null) ? group : (tag + group));
                        }
                    }
                }
                else {
                    tag = matcher.group();
                }
            }
            else {
                tag = ua;
            }
            if (this._agentCache.size() >= this._agentCacheSize) {
                this._agentCache.clear();
            }
            this._agentCache.put(ua, tag);
        }
        return tag;
    }
}
