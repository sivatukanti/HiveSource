// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.server;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.security.authentication.client.AuthenticationException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import java.util.Iterator;
import com.google.common.base.Preconditions;
import java.util.Properties;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Collection;
import java.util.Map;
import com.google.common.base.Splitter;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class MultiSchemeAuthenticationHandler implements CompositeAuthenticationHandler
{
    private static Logger logger;
    public static final String SCHEMES_PROPERTY = "multi-scheme-auth-handler.schemes";
    public static final String AUTH_HANDLER_PROPERTY = "multi-scheme-auth-handler.schemes.%s.handler";
    private static final Splitter STR_SPLITTER;
    private final Map<String, AuthenticationHandler> schemeToAuthHandlerMapping;
    private final Collection<String> types;
    private final String authType;
    public static final String TYPE = "multi-scheme";
    
    public MultiSchemeAuthenticationHandler() {
        this("multi-scheme");
    }
    
    public MultiSchemeAuthenticationHandler(final String authType) {
        this.schemeToAuthHandlerMapping = new HashMap<String, AuthenticationHandler>();
        this.types = new HashSet<String>();
        this.authType = authType;
    }
    
    @Override
    public String getType() {
        return this.authType;
    }
    
    @Override
    public Collection<String> getTokenTypes() {
        return this.types;
    }
    
    @Override
    public void init(final Properties config) throws ServletException {
        for (final Map.Entry prop : config.entrySet()) {
            MultiSchemeAuthenticationHandler.logger.info("{} : {}", prop.getKey(), prop.getValue());
        }
        this.types.clear();
        final String schemesProperty = Preconditions.checkNotNull(config.getProperty("multi-scheme-auth-handler.schemes"), "%s system property is not specified.", "multi-scheme-auth-handler.schemes");
        for (String scheme : MultiSchemeAuthenticationHandler.STR_SPLITTER.split(schemesProperty)) {
            scheme = AuthenticationHandlerUtil.checkAuthScheme(scheme);
            if (this.schemeToAuthHandlerMapping.containsKey(scheme)) {
                throw new IllegalArgumentException("Handler is already specified for " + scheme + " authentication scheme.");
            }
            final String authHandlerPropName = String.format("multi-scheme-auth-handler.schemes.%s.handler", scheme).toLowerCase();
            final String authHandlerName = config.getProperty(authHandlerPropName);
            Preconditions.checkNotNull(authHandlerName, "No auth handler configured for scheme %s.", scheme);
            final String authHandlerClassName = AuthenticationHandlerUtil.getAuthenticationHandlerClassName(authHandlerName);
            final AuthenticationHandler handler = this.initializeAuthHandler(authHandlerClassName, config);
            this.schemeToAuthHandlerMapping.put(scheme, handler);
            this.types.add(handler.getType());
        }
        MultiSchemeAuthenticationHandler.logger.info("Successfully initialized MultiSchemeAuthenticationHandler");
    }
    
    protected AuthenticationHandler initializeAuthHandler(final String authHandlerClassName, final Properties config) throws ServletException {
        try {
            Preconditions.checkNotNull(authHandlerClassName);
            MultiSchemeAuthenticationHandler.logger.debug("Initializing Authentication handler of type " + authHandlerClassName);
            final Class<?> klass = Thread.currentThread().getContextClassLoader().loadClass(authHandlerClassName);
            final AuthenticationHandler authHandler = (AuthenticationHandler)klass.newInstance();
            authHandler.init(config);
            MultiSchemeAuthenticationHandler.logger.info("Successfully initialized Authentication handler of type " + authHandlerClassName);
            return authHandler;
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex3) {
            final ReflectiveOperationException ex2;
            final ReflectiveOperationException ex = ex2;
            MultiSchemeAuthenticationHandler.logger.error("Failed to initialize authentication handler " + authHandlerClassName, ex);
            throw new ServletException(ex);
        }
    }
    
    @Override
    public void destroy() {
        for (final AuthenticationHandler handler : this.schemeToAuthHandlerMapping.values()) {
            handler.destroy();
        }
    }
    
    @Override
    public boolean managementOperation(final AuthenticationToken token, final HttpServletRequest request, final HttpServletResponse response) throws IOException, AuthenticationException {
        return true;
    }
    
    @Override
    public AuthenticationToken authenticate(final HttpServletRequest request, final HttpServletResponse response) throws IOException, AuthenticationException {
        final String authorization = request.getHeader("Authorization");
        if (authorization != null) {
            for (final Map.Entry<String, AuthenticationHandler> entry : this.schemeToAuthHandlerMapping.entrySet()) {
                if (AuthenticationHandlerUtil.matchAuthScheme(entry.getKey(), authorization)) {
                    final AuthenticationToken token = entry.getValue().authenticate(request, response);
                    MultiSchemeAuthenticationHandler.logger.trace("Token generated with type {}", token.getType());
                    return token;
                }
            }
        }
        response.setStatus(401);
        for (final String scheme : this.schemeToAuthHandlerMapping.keySet()) {
            response.addHeader("WWW-Authenticate", scheme);
        }
        return null;
    }
    
    static {
        MultiSchemeAuthenticationHandler.logger = LoggerFactory.getLogger(MultiSchemeAuthenticationHandler.class);
        STR_SPLITTER = Splitter.on(',').trimResults().omitEmptyStrings();
    }
}
