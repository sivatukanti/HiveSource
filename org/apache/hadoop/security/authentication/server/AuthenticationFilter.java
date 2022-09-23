// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.server;

import org.slf4j.LoggerFactory;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.security.Principal;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.Iterator;
import java.util.Collection;
import java.io.IOException;
import javax.servlet.http.Cookie;
import org.apache.hadoop.security.authentication.util.SignerException;
import org.apache.hadoop.security.authentication.client.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import org.apache.hadoop.security.authentication.util.ZKSignerSecretProvider;
import org.apache.hadoop.security.authentication.util.RandomSignerSecretProvider;
import org.apache.hadoop.security.authentication.util.FileSignerSecretProvider;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import org.apache.hadoop.security.authentication.util.SignerSecretProvider;
import org.apache.hadoop.security.authentication.util.Signer;
import java.util.Properties;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import javax.servlet.Filter;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class AuthenticationFilter implements Filter
{
    private static Logger LOG;
    public static final String CONFIG_PREFIX = "config.prefix";
    public static final String AUTH_TYPE = "type";
    public static final String SIGNATURE_SECRET = "signature.secret";
    public static final String SIGNATURE_SECRET_FILE = "signature.secret.file";
    public static final String AUTH_TOKEN_MAX_INACTIVE_INTERVAL = "token.max-inactive-interval";
    public static final String AUTH_TOKEN_VALIDITY = "token.validity";
    public static final String COOKIE_DOMAIN = "cookie.domain";
    public static final String COOKIE_PATH = "cookie.path";
    public static final String COOKIE_PERSISTENT = "cookie.persistent";
    public static final String SIGNER_SECRET_PROVIDER = "signer.secret.provider";
    public static final String SIGNER_SECRET_PROVIDER_ATTRIBUTE = "signer.secret.provider.object";
    private Properties config;
    private Signer signer;
    private SignerSecretProvider secretProvider;
    private AuthenticationHandler authHandler;
    private long maxInactiveInterval;
    private long validity;
    private String cookieDomain;
    private String cookiePath;
    private boolean isCookiePersistent;
    private boolean destroySecretProvider;
    
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        String configPrefix = filterConfig.getInitParameter("config.prefix");
        configPrefix = ((configPrefix != null) ? (configPrefix + ".") : "");
        this.config = this.getConfiguration(configPrefix, filterConfig);
        final String authHandlerName = this.config.getProperty("type", null);
        if (authHandlerName == null) {
            throw new ServletException("Authentication type must be specified: simple|kerberos|<class>");
        }
        final String authHandlerClassName = AuthenticationHandlerUtil.getAuthenticationHandlerClassName(authHandlerName);
        this.maxInactiveInterval = Long.parseLong(this.config.getProperty("token.max-inactive-interval", "-1"));
        if (this.maxInactiveInterval > 0L) {
            this.maxInactiveInterval *= 1000L;
        }
        this.validity = Long.parseLong(this.config.getProperty("token.validity", "36000")) * 1000L;
        this.initializeSecretProvider(filterConfig);
        this.initializeAuthHandler(authHandlerClassName, filterConfig);
        this.cookieDomain = this.config.getProperty("cookie.domain", null);
        this.cookiePath = this.config.getProperty("cookie.path", null);
        this.isCookiePersistent = Boolean.parseBoolean(this.config.getProperty("cookie.persistent", "false"));
    }
    
    protected void initializeAuthHandler(final String authHandlerClassName, final FilterConfig filterConfig) throws ServletException {
        try {
            final Class<?> klass = Thread.currentThread().getContextClassLoader().loadClass(authHandlerClassName);
            (this.authHandler = (AuthenticationHandler)klass.newInstance()).init(this.config);
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex3) {
            final ReflectiveOperationException ex2;
            final ReflectiveOperationException ex = ex2;
            throw new ServletException(ex);
        }
    }
    
    protected void initializeSecretProvider(final FilterConfig filterConfig) throws ServletException {
        this.secretProvider = (SignerSecretProvider)filterConfig.getServletContext().getAttribute("signer.secret.provider.object");
        if (this.secretProvider == null) {
            try {
                this.secretProvider = constructSecretProvider(filterConfig.getServletContext(), this.config, false);
                this.destroySecretProvider = true;
            }
            catch (Exception ex) {
                throw new ServletException(ex);
            }
        }
        this.signer = new Signer(this.secretProvider);
    }
    
    public static SignerSecretProvider constructSecretProvider(final ServletContext ctx, final Properties config, final boolean disallowFallbackToRandomSecretProvider) throws Exception {
        String name = config.getProperty("signer.secret.provider", "file");
        final long validity = Long.parseLong(config.getProperty("token.validity", "36000")) * 1000L;
        if (!disallowFallbackToRandomSecretProvider && "file".equals(name) && config.getProperty("signature.secret.file") == null) {
            name = "random";
        }
        SignerSecretProvider provider;
        if ("file".equals(name)) {
            provider = new FileSignerSecretProvider();
            try {
                provider.init(config, ctx, validity);
            }
            catch (Exception e) {
                if (disallowFallbackToRandomSecretProvider) {
                    throw e;
                }
                AuthenticationFilter.LOG.info("Unable to initialize FileSignerSecretProvider, falling back to use random secrets.");
                provider = new RandomSignerSecretProvider();
                provider.init(config, ctx, validity);
            }
        }
        else if ("random".equals(name)) {
            provider = new RandomSignerSecretProvider();
            provider.init(config, ctx, validity);
        }
        else if ("zookeeper".equals(name)) {
            provider = new ZKSignerSecretProvider();
            provider.init(config, ctx, validity);
        }
        else {
            provider = (SignerSecretProvider)Thread.currentThread().getContextClassLoader().loadClass(name).newInstance();
            provider.init(config, ctx, validity);
        }
        return provider;
    }
    
    protected Properties getConfiguration() {
        return this.config;
    }
    
    protected AuthenticationHandler getAuthenticationHandler() {
        return this.authHandler;
    }
    
    protected boolean isRandomSecret() {
        return this.secretProvider.getClass() == RandomSignerSecretProvider.class;
    }
    
    protected boolean isCustomSignerSecretProvider() {
        final Class<?> clazz = this.secretProvider.getClass();
        return clazz != FileSignerSecretProvider.class && clazz != RandomSignerSecretProvider.class && clazz != ZKSignerSecretProvider.class;
    }
    
    protected long getMaxInactiveInterval() {
        return this.maxInactiveInterval / 1000L;
    }
    
    protected long getValidity() {
        return this.validity / 1000L;
    }
    
    protected String getCookieDomain() {
        return this.cookieDomain;
    }
    
    protected String getCookiePath() {
        return this.cookiePath;
    }
    
    protected boolean isCookiePersistent() {
        return this.isCookiePersistent;
    }
    
    @Override
    public void destroy() {
        if (this.authHandler != null) {
            this.authHandler.destroy();
            this.authHandler = null;
        }
        if (this.secretProvider != null && this.destroySecretProvider) {
            this.secretProvider.destroy();
            this.secretProvider = null;
        }
    }
    
    protected Properties getConfiguration(final String configPrefix, final FilterConfig filterConfig) throws ServletException {
        final Properties props = new Properties();
        final Enumeration<?> names = filterConfig.getInitParameterNames();
        while (names.hasMoreElements()) {
            final String name = (String)names.nextElement();
            if (name.startsWith(configPrefix)) {
                final String value = filterConfig.getInitParameter(name);
                props.put(name.substring(configPrefix.length()), value);
            }
        }
        return props;
    }
    
    protected String getRequestURL(final HttpServletRequest request) {
        final StringBuffer sb = request.getRequestURL();
        if (request.getQueryString() != null) {
            sb.append("?").append(request.getQueryString());
        }
        return sb.toString();
    }
    
    protected AuthenticationToken getToken(final HttpServletRequest request) throws IOException, AuthenticationException {
        AuthenticationToken token = null;
        String tokenStr = null;
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (cookie.getName().equals("hadoop.auth")) {
                    tokenStr = cookie.getValue();
                    if (tokenStr.isEmpty()) {
                        throw new AuthenticationException("Unauthorized access");
                    }
                    try {
                        tokenStr = this.signer.verifyAndExtract(tokenStr);
                        break;
                    }
                    catch (SignerException ex) {
                        throw new AuthenticationException(ex);
                    }
                }
            }
        }
        if (tokenStr != null) {
            token = AuthenticationToken.parse(tokenStr);
            final boolean match = this.verifyTokenType(this.getAuthenticationHandler(), token);
            if (!match) {
                throw new AuthenticationException("Invalid AuthenticationToken type");
            }
            if (token.isExpired()) {
                throw new AuthenticationException("AuthenticationToken expired");
            }
        }
        return token;
    }
    
    protected boolean verifyTokenType(final AuthenticationHandler handler, final AuthenticationToken token) {
        if (!(handler instanceof CompositeAuthenticationHandler)) {
            return handler.getType().equals(token.getType());
        }
        boolean match = false;
        final Collection<String> tokenTypes = ((CompositeAuthenticationHandler)handler).getTokenTypes();
        for (final String tokenType : tokenTypes) {
            if (tokenType.equals(token.getType())) {
                match = true;
                break;
            }
        }
        return match;
    }
    
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
        boolean unauthorizedResponse = true;
        int errCode = 401;
        AuthenticationException authenticationEx = null;
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        final HttpServletResponse httpResponse = (HttpServletResponse)response;
        final boolean isHttps = "https".equals(httpRequest.getScheme());
        try {
            boolean newToken = false;
            AuthenticationToken token;
            try {
                token = this.getToken(httpRequest);
                if (AuthenticationFilter.LOG.isDebugEnabled()) {
                    AuthenticationFilter.LOG.debug("Got token {} from httpRequest {}", token, this.getRequestURL(httpRequest));
                }
            }
            catch (AuthenticationException ex) {
                AuthenticationFilter.LOG.warn("AuthenticationToken ignored: " + ex.getMessage());
                authenticationEx = ex;
                token = null;
            }
            if (this.authHandler.managementOperation(token, httpRequest, httpResponse)) {
                if (token == null) {
                    if (AuthenticationFilter.LOG.isDebugEnabled()) {
                        AuthenticationFilter.LOG.debug("Request [{}] triggering authentication. handler: {}", this.getRequestURL(httpRequest), this.authHandler.getClass());
                    }
                    token = this.authHandler.authenticate(httpRequest, httpResponse);
                    if (token != null && token != AuthenticationToken.ANONYMOUS) {
                        if (token.getMaxInactives() > 0L) {
                            token.setMaxInactives(System.currentTimeMillis() + this.getMaxInactiveInterval() * 1000L);
                        }
                        if (token.getExpires() != 0L) {
                            token.setExpires(System.currentTimeMillis() + this.getValidity() * 1000L);
                        }
                    }
                    newToken = true;
                }
                if (token != null) {
                    unauthorizedResponse = false;
                    if (AuthenticationFilter.LOG.isDebugEnabled()) {
                        AuthenticationFilter.LOG.debug("Request [{}] user [{}] authenticated", this.getRequestURL(httpRequest), token.getUserName());
                    }
                    final AuthenticationToken authToken = token;
                    httpRequest = new HttpServletRequestWrapper(httpRequest) {
                        @Override
                        public String getAuthType() {
                            return authToken.getType();
                        }
                        
                        @Override
                        public String getRemoteUser() {
                            return authToken.getUserName();
                        }
                        
                        @Override
                        public Principal getUserPrincipal() {
                            return (authToken != AuthenticationToken.ANONYMOUS) ? authToken : null;
                        }
                    };
                    if (!newToken && !this.isCookiePersistent() && this.getMaxInactiveInterval() > 0L) {
                        token.setMaxInactives(System.currentTimeMillis() + this.getMaxInactiveInterval() * 1000L);
                        token.setExpires(token.getExpires());
                        newToken = true;
                    }
                    if (newToken && !token.isExpired() && token != AuthenticationToken.ANONYMOUS) {
                        final String signedToken = this.signer.sign(token.toString());
                        createAuthCookie(httpResponse, signedToken, this.getCookieDomain(), this.getCookiePath(), token.getExpires(), this.isCookiePersistent(), isHttps);
                    }
                    this.doFilter(filterChain, httpRequest, httpResponse);
                }
            }
            else {
                if (AuthenticationFilter.LOG.isDebugEnabled()) {
                    AuthenticationFilter.LOG.debug("managementOperation returned false for request {}. token: {}", this.getRequestURL(httpRequest), token);
                }
                unauthorizedResponse = false;
            }
        }
        catch (AuthenticationException ex2) {
            errCode = 403;
            authenticationEx = ex2;
            if (AuthenticationFilter.LOG.isDebugEnabled()) {
                AuthenticationFilter.LOG.debug("Authentication exception: " + ex2.getMessage(), ex2);
            }
            else {
                AuthenticationFilter.LOG.warn("Authentication exception: " + ex2.getMessage());
            }
        }
        if (unauthorizedResponse && !httpResponse.isCommitted()) {
            createAuthCookie(httpResponse, "", this.getCookieDomain(), this.getCookiePath(), 0L, this.isCookiePersistent(), isHttps);
            if (errCode == 401 && !httpResponse.containsHeader("WWW-Authenticate")) {
                errCode = 403;
            }
            if (authenticationEx == null) {
                httpResponse.sendError(errCode, "Authentication required");
            }
            else {
                httpResponse.sendError(errCode, authenticationEx.getMessage());
            }
        }
    }
    
    protected void doFilter(final FilterChain filterChain, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        filterChain.doFilter(request, response);
    }
    
    public static void createAuthCookie(final HttpServletResponse resp, final String token, final String domain, final String path, final long expires, final boolean isCookiePersistent, final boolean isSecure) {
        final StringBuilder sb = new StringBuilder("hadoop.auth").append("=");
        if (token != null && token.length() > 0) {
            sb.append("\"").append(token).append("\"");
        }
        if (path != null) {
            sb.append("; Path=").append(path);
        }
        if (domain != null) {
            sb.append("; Domain=").append(domain);
        }
        if (expires >= 0L && isCookiePersistent) {
            final Date date = new Date(expires);
            final SimpleDateFormat df = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss zzz");
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            sb.append("; Expires=").append(df.format(date));
        }
        if (isSecure) {
            sb.append("; Secure");
        }
        sb.append("; HttpOnly");
        resp.addHeader("Set-Cookie", sb.toString());
    }
    
    static {
        AuthenticationFilter.LOG = LoggerFactory.getLogger(AuthenticationFilter.class);
    }
}
