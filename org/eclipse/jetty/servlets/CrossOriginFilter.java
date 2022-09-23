// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlets;

import org.eclipse.jetty.util.log.Log;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Iterator;
import java.util.Enumeration;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import java.util.Collection;
import java.util.Arrays;
import javax.servlet.FilterConfig;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.util.log.Logger;
import javax.servlet.Filter;

public class CrossOriginFilter implements Filter
{
    private static final Logger LOG;
    private static final String ORIGIN_HEADER = "Origin";
    public static final String ACCESS_CONTROL_REQUEST_METHOD_HEADER = "Access-Control-Request-Method";
    public static final String ACCESS_CONTROL_REQUEST_HEADERS_HEADER = "Access-Control-Request-Headers";
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN_HEADER = "Access-Control-Allow-Origin";
    public static final String ACCESS_CONTROL_ALLOW_METHODS_HEADER = "Access-Control-Allow-Methods";
    public static final String ACCESS_CONTROL_ALLOW_HEADERS_HEADER = "Access-Control-Allow-Headers";
    public static final String ACCESS_CONTROL_MAX_AGE_HEADER = "Access-Control-Max-Age";
    public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER = "Access-Control-Allow-Credentials";
    public static final String ALLOWED_ORIGINS_PARAM = "allowedOrigins";
    public static final String ALLOWED_METHODS_PARAM = "allowedMethods";
    public static final String ALLOWED_HEADERS_PARAM = "allowedHeaders";
    public static final String PREFLIGHT_MAX_AGE_PARAM = "preflightMaxAge";
    public static final String ALLOW_CREDENTIALS_PARAM = "allowCredentials";
    private static final String ANY_ORIGIN = "*";
    private static final List<String> SIMPLE_HTTP_METHODS;
    private boolean anyOriginAllowed;
    private List<String> allowedOrigins;
    private List<String> allowedMethods;
    private List<String> allowedHeaders;
    private int preflightMaxAge;
    private boolean allowCredentials;
    
    public CrossOriginFilter() {
        this.allowedOrigins = new ArrayList<String>();
        this.allowedMethods = new ArrayList<String>();
        this.allowedHeaders = new ArrayList<String>();
        this.preflightMaxAge = 0;
    }
    
    public void init(final FilterConfig config) throws ServletException {
        String allowedOriginsConfig = config.getInitParameter("allowedOrigins");
        if (allowedOriginsConfig == null) {
            allowedOriginsConfig = "*";
        }
        final String[] arr$;
        final String[] allowedOrigins = arr$ = allowedOriginsConfig.split(",");
        for (String allowedOrigin : arr$) {
            allowedOrigin = allowedOrigin.trim();
            if (allowedOrigin.length() > 0) {
                if ("*".equals(allowedOrigin)) {
                    this.anyOriginAllowed = true;
                    this.allowedOrigins.clear();
                    break;
                }
                this.allowedOrigins.add(allowedOrigin);
            }
        }
        String allowedMethodsConfig = config.getInitParameter("allowedMethods");
        if (allowedMethodsConfig == null) {
            allowedMethodsConfig = "GET,POST,HEAD";
        }
        this.allowedMethods.addAll(Arrays.asList(allowedMethodsConfig.split(",")));
        String allowedHeadersConfig = config.getInitParameter("allowedHeaders");
        if (allowedHeadersConfig == null) {
            allowedHeadersConfig = "X-Requested-With,Content-Type,Accept,Origin";
        }
        this.allowedHeaders.addAll(Arrays.asList(allowedHeadersConfig.split(",")));
        String preflightMaxAgeConfig = config.getInitParameter("preflightMaxAge");
        if (preflightMaxAgeConfig == null) {
            preflightMaxAgeConfig = "1800";
        }
        try {
            this.preflightMaxAge = Integer.parseInt(preflightMaxAgeConfig);
        }
        catch (NumberFormatException x) {
            CrossOriginFilter.LOG.info("Cross-origin filter, could not parse '{}' parameter as integer: {}", "preflightMaxAge", preflightMaxAgeConfig);
        }
        String allowedCredentialsConfig = config.getInitParameter("allowCredentials");
        if (allowedCredentialsConfig == null) {
            allowedCredentialsConfig = "true";
        }
        this.allowCredentials = Boolean.parseBoolean(allowedCredentialsConfig);
        if (CrossOriginFilter.LOG.isDebugEnabled()) {
            CrossOriginFilter.LOG.debug("Cross-origin filter configuration: allowedOrigins = " + allowedOriginsConfig + ", " + "allowedMethods" + " = " + allowedMethodsConfig + ", " + "allowedHeaders" + " = " + allowedHeadersConfig + ", " + "preflightMaxAge" + " = " + preflightMaxAgeConfig + ", " + "allowCredentials" + " = " + allowedCredentialsConfig, new Object[0]);
        }
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        this.handle((HttpServletRequest)request, (HttpServletResponse)response, chain);
    }
    
    private void handle(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final String origin = request.getHeader("Origin");
        if (origin != null && this.isEnabled(request)) {
            if (this.originMatches(origin)) {
                if (this.isSimpleRequest(request)) {
                    CrossOriginFilter.LOG.debug("Cross-origin request to {} is a simple cross-origin request", request.getRequestURI());
                    this.handleSimpleResponse(request, response, origin);
                }
                else if (this.isPreflightRequest(request)) {
                    CrossOriginFilter.LOG.debug("Cross-origin request to {} is a preflight cross-origin request", request.getRequestURI());
                    this.handlePreflightResponse(request, response, origin);
                }
                else {
                    CrossOriginFilter.LOG.debug("Cross-origin request to {} is a non-simple cross-origin request", request.getRequestURI());
                    this.handleSimpleResponse(request, response, origin);
                }
            }
            else {
                CrossOriginFilter.LOG.debug("Cross-origin request to " + request.getRequestURI() + " with origin " + origin + " does not match allowed origins " + this.allowedOrigins, new Object[0]);
            }
        }
        chain.doFilter(request, response);
    }
    
    protected boolean isEnabled(final HttpServletRequest request) {
        final Enumeration connections = request.getHeaders("Connection");
        while (connections.hasMoreElements()) {
            final String connection = connections.nextElement();
            if ("Upgrade".equalsIgnoreCase(connection)) {
                final Enumeration upgrades = request.getHeaders("Upgrade");
                while (upgrades.hasMoreElements()) {
                    final String upgrade = upgrades.nextElement();
                    if ("WebSocket".equalsIgnoreCase(upgrade)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private boolean originMatches(final String originList) {
        if (this.anyOriginAllowed) {
            return true;
        }
        if (originList.trim().length() == 0) {
            return false;
        }
        final String[] arr$;
        final String[] origins = arr$ = originList.split(" ");
        for (final String origin : arr$) {
            if (origin.trim().length() != 0) {
                for (final String allowedOrigin : this.allowedOrigins) {
                    if (allowedOrigin.contains("*")) {
                        final Matcher matcher = this.createMatcher(origin, allowedOrigin);
                        if (matcher.matches()) {
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
        }
        return false;
    }
    
    private Matcher createMatcher(final String origin, final String allowedOrigin) {
        final String regex = this.parseAllowedWildcardOriginToRegex(allowedOrigin);
        final Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(origin);
    }
    
    private String parseAllowedWildcardOriginToRegex(final String allowedOrigin) {
        final String regex = allowedOrigin.replace(".", "\\.");
        return regex.replace("*", ".*");
    }
    
    private boolean isSimpleRequest(final HttpServletRequest request) {
        final String method = request.getMethod();
        return CrossOriginFilter.SIMPLE_HTTP_METHODS.contains(method) && request.getHeader("Access-Control-Request-Method") == null;
    }
    
    private boolean isPreflightRequest(final HttpServletRequest request) {
        final String method = request.getMethod();
        return "OPTIONS".equalsIgnoreCase(method) && request.getHeader("Access-Control-Request-Method") != null;
    }
    
    private void handleSimpleResponse(final HttpServletRequest request, final HttpServletResponse response, final String origin) {
        response.setHeader("Access-Control-Allow-Origin", origin);
        if (this.allowCredentials) {
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }
    }
    
    private void handlePreflightResponse(final HttpServletRequest request, final HttpServletResponse response, final String origin) {
        final boolean methodAllowed = this.isMethodAllowed(request);
        if (!methodAllowed) {
            return;
        }
        final boolean headersAllowed = this.areHeadersAllowed(request);
        if (!headersAllowed) {
            return;
        }
        response.setHeader("Access-Control-Allow-Origin", origin);
        if (this.allowCredentials) {
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }
        if (this.preflightMaxAge > 0) {
            response.setHeader("Access-Control-Max-Age", String.valueOf(this.preflightMaxAge));
        }
        response.setHeader("Access-Control-Allow-Methods", this.commify(this.allowedMethods));
        response.setHeader("Access-Control-Allow-Headers", this.commify(this.allowedHeaders));
    }
    
    private boolean isMethodAllowed(final HttpServletRequest request) {
        final String accessControlRequestMethod = request.getHeader("Access-Control-Request-Method");
        CrossOriginFilter.LOG.debug("{} is {}", "Access-Control-Request-Method", accessControlRequestMethod);
        boolean result = false;
        if (accessControlRequestMethod != null) {
            result = this.allowedMethods.contains(accessControlRequestMethod);
        }
        CrossOriginFilter.LOG.debug("Method {} is" + (result ? "" : " not") + " among allowed methods {}", accessControlRequestMethod, this.allowedMethods);
        return result;
    }
    
    private boolean areHeadersAllowed(final HttpServletRequest request) {
        final String accessControlRequestHeaders = request.getHeader("Access-Control-Request-Headers");
        CrossOriginFilter.LOG.debug("{} is {}", "Access-Control-Request-Headers", accessControlRequestHeaders);
        boolean result = true;
        if (accessControlRequestHeaders != null) {
            final String[] arr$;
            final String[] headers = arr$ = accessControlRequestHeaders.split(",");
            for (final String header : arr$) {
                boolean headerAllowed = false;
                for (final String allowedHeader : this.allowedHeaders) {
                    if (header.trim().equalsIgnoreCase(allowedHeader.trim())) {
                        headerAllowed = true;
                        break;
                    }
                }
                if (!headerAllowed) {
                    result = false;
                    break;
                }
            }
        }
        CrossOriginFilter.LOG.debug("Headers [{}] are" + (result ? "" : " not") + " among allowed headers {}", accessControlRequestHeaders, this.allowedHeaders);
        return result;
    }
    
    private String commify(final List<String> strings) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < strings.size(); ++i) {
            if (i > 0) {
                builder.append(",");
            }
            final String string = strings.get(i);
            builder.append(string);
        }
        return builder.toString();
    }
    
    public void destroy() {
        this.anyOriginAllowed = false;
        this.allowedOrigins.clear();
        this.allowedMethods.clear();
        this.allowedHeaders.clear();
        this.preflightMaxAge = 0;
        this.allowCredentials = false;
    }
    
    static {
        LOG = Log.getLogger(CrossOriginFilter.class);
        SIMPLE_HTTP_METHODS = Arrays.asList("GET", "POST", "HEAD");
    }
}
