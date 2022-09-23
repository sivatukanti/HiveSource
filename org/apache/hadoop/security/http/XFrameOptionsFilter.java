// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.http;

import javax.servlet.http.HttpServletResponseWrapper;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import javax.servlet.Filter;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class XFrameOptionsFilter implements Filter
{
    public static final String X_FRAME_OPTIONS = "X-Frame-Options";
    public static final String CUSTOM_HEADER_PARAM = "xframe-options";
    private String option;
    
    public XFrameOptionsFilter() {
        this.option = "DENY";
    }
    
    @Override
    public void destroy() {
    }
    
    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
        ((HttpServletResponse)res).setHeader("X-Frame-Options", this.option);
        chain.doFilter(req, new XFrameOptionsResponseWrapper((HttpServletResponse)res));
    }
    
    @Override
    public void init(final FilterConfig config) throws ServletException {
        final String customOption = config.getInitParameter("xframe-options");
        if (customOption != null) {
            this.option = customOption;
        }
    }
    
    public static Map<String, String> getFilterParams(final Configuration conf, final String confPrefix) {
        return conf.getPropsWithPrefix(confPrefix);
    }
    
    public class XFrameOptionsResponseWrapper extends HttpServletResponseWrapper
    {
        public XFrameOptionsResponseWrapper(final HttpServletResponse response) {
            super(response);
        }
        
        @Override
        public void addHeader(final String name, final String value) {
            if (!name.equals("X-Frame-Options")) {
                super.addHeader(name, value);
            }
        }
        
        @Override
        public void setHeader(final String name, final String value) {
            if (!name.equals("X-Frame-Options")) {
                super.setHeader(name, value);
            }
        }
        
        @Override
        public void setDateHeader(final String name, final long date) {
            if (!name.equals("X-Frame-Options")) {
                super.setDateHeader(name, date);
            }
        }
        
        @Override
        public void addDateHeader(final String name, final long date) {
            if (!name.equals("X-Frame-Options")) {
                super.addDateHeader(name, date);
            }
        }
        
        @Override
        public void setIntHeader(final String name, final int value) {
            if (!name.equals("X-Frame-Options")) {
                super.setIntHeader(name, value);
            }
        }
        
        @Override
        public void addIntHeader(final String name, final int value) {
            if (!name.equals("X-Frame-Options")) {
                super.addIntHeader(name, value);
            }
        }
        
        @Override
        public boolean containsHeader(final String name) {
            final boolean contains = false;
            if (name.equals("X-Frame-Options")) {
                return XFrameOptionsFilter.this.option != null;
            }
            super.containsHeader(name);
            return contains;
        }
    }
}
