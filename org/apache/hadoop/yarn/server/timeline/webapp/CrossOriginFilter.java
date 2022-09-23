// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.timeline.webapp;

import org.apache.commons.logging.LogFactory;
import java.util.regex.Matcher;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.Arrays;
import com.google.common.annotations.VisibleForTesting;
import java.util.Collection;
import org.apache.commons.lang.StringUtils;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import javax.servlet.Filter;

public class CrossOriginFilter implements Filter
{
    private static final Log LOG;
    static final String ORIGIN = "Origin";
    static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
    static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
    static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
    public static final String ALLOWED_ORIGINS = "allowed-origins";
    public static final String ALLOWED_ORIGINS_DEFAULT = "*";
    public static final String ALLOWED_METHODS = "allowed-methods";
    public static final String ALLOWED_METHODS_DEFAULT = "GET,POST,HEAD";
    public static final String ALLOWED_HEADERS = "allowed-headers";
    public static final String ALLOWED_HEADERS_DEFAULT = "X-Requested-With,Content-Type,Accept,Origin";
    public static final String MAX_AGE = "max-age";
    public static final String MAX_AGE_DEFAULT = "1800";
    private List<String> allowedMethods;
    private List<String> allowedHeaders;
    private List<String> allowedOrigins;
    private boolean allowAllOrigins;
    private String maxAge;
    
    public CrossOriginFilter() {
        this.allowedMethods = new ArrayList<String>();
        this.allowedHeaders = new ArrayList<String>();
        this.allowedOrigins = new ArrayList<String>();
        this.allowAllOrigins = true;
    }
    
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.initializeAllowedMethods(filterConfig);
        this.initializeAllowedHeaders(filterConfig);
        this.initializeAllowedOrigins(filterConfig);
        this.initializeMaxAge(filterConfig);
    }
    
    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
        this.doCrossFilter((HttpServletRequest)req, (HttpServletResponse)res);
        chain.doFilter(req, res);
    }
    
    @Override
    public void destroy() {
        this.allowedMethods.clear();
        this.allowedHeaders.clear();
        this.allowedOrigins.clear();
    }
    
    private void doCrossFilter(final HttpServletRequest req, final HttpServletResponse res) {
        final String originsList = encodeHeader(req.getHeader("Origin"));
        if (!isCrossOrigin(originsList)) {
            return;
        }
        if (!this.areOriginsAllowed(originsList)) {
            return;
        }
        final String accessControlRequestMethod = req.getHeader("Access-Control-Request-Method");
        if (!this.isMethodAllowed(accessControlRequestMethod)) {
            return;
        }
        final String accessControlRequestHeaders = req.getHeader("Access-Control-Request-Headers");
        if (!this.areHeadersAllowed(accessControlRequestHeaders)) {
            return;
        }
        res.setHeader("Access-Control-Allow-Origin", originsList);
        res.setHeader("Access-Control-Allow-Credentials", Boolean.TRUE.toString());
        res.setHeader("Access-Control-Allow-Methods", this.getAllowedMethodsHeader());
        res.setHeader("Access-Control-Allow-Headers", this.getAllowedHeadersHeader());
        res.setHeader("Access-Control-Max-Age", this.maxAge);
    }
    
    @VisibleForTesting
    String getAllowedHeadersHeader() {
        return StringUtils.join(this.allowedHeaders, ',');
    }
    
    @VisibleForTesting
    String getAllowedMethodsHeader() {
        return StringUtils.join(this.allowedMethods, ',');
    }
    
    private void initializeAllowedMethods(final FilterConfig filterConfig) {
        String allowedMethodsConfig = filterConfig.getInitParameter("allowed-methods");
        if (allowedMethodsConfig == null) {
            allowedMethodsConfig = "GET,POST,HEAD";
        }
        this.allowedMethods.addAll(Arrays.asList(allowedMethodsConfig.trim().split("\\s*,\\s*")));
        CrossOriginFilter.LOG.info("Allowed Methods: " + this.getAllowedMethodsHeader());
    }
    
    private void initializeAllowedHeaders(final FilterConfig filterConfig) {
        String allowedHeadersConfig = filterConfig.getInitParameter("allowed-headers");
        if (allowedHeadersConfig == null) {
            allowedHeadersConfig = "X-Requested-With,Content-Type,Accept,Origin";
        }
        this.allowedHeaders.addAll(Arrays.asList(allowedHeadersConfig.trim().split("\\s*,\\s*")));
        CrossOriginFilter.LOG.info("Allowed Headers: " + this.getAllowedHeadersHeader());
    }
    
    private void initializeAllowedOrigins(final FilterConfig filterConfig) {
        String allowedOriginsConfig = filterConfig.getInitParameter("allowed-origins");
        if (allowedOriginsConfig == null) {
            allowedOriginsConfig = "*";
        }
        this.allowedOrigins.addAll(Arrays.asList(allowedOriginsConfig.trim().split("\\s*,\\s*")));
        this.allowAllOrigins = this.allowedOrigins.contains("*");
        CrossOriginFilter.LOG.info("Allowed Origins: " + StringUtils.join(this.allowedOrigins, ','));
        CrossOriginFilter.LOG.info("Allow All Origins: " + this.allowAllOrigins);
    }
    
    private void initializeMaxAge(final FilterConfig filterConfig) {
        this.maxAge = filterConfig.getInitParameter("max-age");
        if (this.maxAge == null) {
            this.maxAge = "1800";
        }
        CrossOriginFilter.LOG.info("Max Age: " + this.maxAge);
    }
    
    static String encodeHeader(final String header) {
        if (header == null) {
            return null;
        }
        return header.split("\n|\r")[0].trim();
    }
    
    static boolean isCrossOrigin(final String originsList) {
        return originsList != null;
    }
    
    @VisibleForTesting
    boolean areOriginsAllowed(final String originsList) {
        if (this.allowAllOrigins) {
            return true;
        }
        final String[] arr$;
        final String[] origins = arr$ = originsList.trim().split("\\s+");
        for (final String origin : arr$) {
            for (final String allowedOrigin : this.allowedOrigins) {
                if (allowedOrigin.contains("*")) {
                    final String regex = allowedOrigin.replace(".", "\\.").replace("*", ".*");
                    final Pattern p = Pattern.compile(regex);
                    final Matcher m = p.matcher(origin);
                    if (m.matches()) {
                        return true;
                    }
                    continue;
                }
                else {
                    if (allowedOrigin.equals(origin)) {
                        return true;
                    }
                    continue;
                }
            }
        }
        return false;
    }
    
    private boolean areHeadersAllowed(final String accessControlRequestHeaders) {
        if (accessControlRequestHeaders == null) {
            return true;
        }
        final String[] headers = accessControlRequestHeaders.trim().split("\\s*,\\s*");
        return this.allowedHeaders.containsAll(Arrays.asList(headers));
    }
    
    private boolean isMethodAllowed(final String accessControlRequestMethod) {
        return accessControlRequestMethod == null || this.allowedMethods.contains(accessControlRequestMethod);
    }
    
    static {
        LOG = LogFactory.getLog(CrossOriginFilter.class);
    }
}
