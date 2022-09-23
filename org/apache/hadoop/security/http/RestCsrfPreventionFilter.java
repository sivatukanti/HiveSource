// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.http;

import org.slf4j.LoggerFactory;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.Iterator;
import java.util.HashSet;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import java.util.regex.Pattern;
import java.util.Set;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import javax.servlet.Filter;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class RestCsrfPreventionFilter implements Filter
{
    private static final Logger LOG;
    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String BROWSER_USER_AGENT_PARAM = "browser-useragents-regex";
    public static final String CUSTOM_HEADER_PARAM = "custom-header";
    public static final String CUSTOM_METHODS_TO_IGNORE_PARAM = "methods-to-ignore";
    static final String BROWSER_USER_AGENTS_DEFAULT = "^Mozilla.*,^Opera.*";
    public static final String HEADER_DEFAULT = "X-XSRF-HEADER";
    static final String METHODS_TO_IGNORE_DEFAULT = "GET,OPTIONS,HEAD,TRACE";
    private String headerName;
    private Set<String> methodsToIgnore;
    private Set<Pattern> browserUserAgents;
    
    public RestCsrfPreventionFilter() {
        this.headerName = "X-XSRF-HEADER";
        this.methodsToIgnore = null;
    }
    
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        final String customHeader = filterConfig.getInitParameter("custom-header");
        if (customHeader != null) {
            this.headerName = customHeader;
        }
        final String customMethodsToIgnore = filterConfig.getInitParameter("methods-to-ignore");
        if (customMethodsToIgnore != null) {
            this.parseMethodsToIgnore(customMethodsToIgnore);
        }
        else {
            this.parseMethodsToIgnore("GET,OPTIONS,HEAD,TRACE");
        }
        String agents = filterConfig.getInitParameter("browser-useragents-regex");
        if (agents == null) {
            agents = "^Mozilla.*,^Opera.*";
        }
        this.parseBrowserUserAgents(agents);
        RestCsrfPreventionFilter.LOG.info("Adding cross-site request forgery (CSRF) protection, headerName = {}, methodsToIgnore = {}, browserUserAgents = {}", this.headerName, this.methodsToIgnore, this.browserUserAgents);
    }
    
    void parseBrowserUserAgents(final String userAgents) {
        final String[] agentsArray = userAgents.split(",");
        this.browserUserAgents = new HashSet<Pattern>();
        for (final String patternString : agentsArray) {
            this.browserUserAgents.add(Pattern.compile(patternString));
        }
    }
    
    void parseMethodsToIgnore(final String mti) {
        final String[] methods = mti.split(",");
        this.methodsToIgnore = new HashSet<String>();
        for (int i = 0; i < methods.length; ++i) {
            this.methodsToIgnore.add(methods[i]);
        }
    }
    
    protected boolean isBrowser(final String userAgent) {
        if (userAgent == null) {
            return false;
        }
        for (final Pattern pattern : this.browserUserAgents) {
            final Matcher matcher = pattern.matcher(userAgent);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }
    
    public void handleHttpInteraction(final HttpInteraction httpInteraction) throws IOException, ServletException {
        if (!this.isBrowser(httpInteraction.getHeader("User-Agent")) || this.methodsToIgnore.contains(httpInteraction.getMethod()) || httpInteraction.getHeader(this.headerName) != null) {
            httpInteraction.proceed();
        }
        else {
            httpInteraction.sendError(400, "Missing Required Header for CSRF Vulnerability Protection");
        }
    }
    
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest)request;
        final HttpServletResponse httpResponse = (HttpServletResponse)response;
        this.handleHttpInteraction(new ServletFilterHttpInteraction(httpRequest, httpResponse, chain));
    }
    
    @Override
    public void destroy() {
    }
    
    public static Map<String, String> getFilterParams(final Configuration conf, final String confPrefix) {
        return conf.getPropsWithPrefix(confPrefix);
    }
    
    static {
        LOG = LoggerFactory.getLogger(RestCsrfPreventionFilter.class);
    }
    
    private static final class ServletFilterHttpInteraction implements HttpInteraction
    {
        private final FilterChain chain;
        private final HttpServletRequest httpRequest;
        private final HttpServletResponse httpResponse;
        
        public ServletFilterHttpInteraction(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse, final FilterChain chain) {
            this.httpRequest = httpRequest;
            this.httpResponse = httpResponse;
            this.chain = chain;
        }
        
        @Override
        public String getHeader(final String header) {
            return this.httpRequest.getHeader(header);
        }
        
        @Override
        public String getMethod() {
            return this.httpRequest.getMethod();
        }
        
        @Override
        public void proceed() throws IOException, ServletException {
            this.chain.doFilter(this.httpRequest, this.httpResponse);
        }
        
        @Override
        public void sendError(final int code, final String message) throws IOException {
            this.httpResponse.sendError(code, message);
        }
    }
    
    public interface HttpInteraction
    {
        String getHeader(final String p0);
        
        String getMethod();
        
        void proceed() throws IOException, ServletException;
        
        void sendError(final int p0, final String p1) throws IOException;
    }
}
