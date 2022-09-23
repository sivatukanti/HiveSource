// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token.delegation.web;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.security.Principal;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.hadoop.security.authorize.AuthorizationException;
import org.apache.hadoop.util.HttpExceptionUtils;
import org.apache.hadoop.security.authentication.server.AuthenticationToken;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.FilterChain;
import com.google.common.annotations.VisibleForTesting;
import java.util.Iterator;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import javax.servlet.http.HttpServletRequest;
import org.apache.hadoop.security.token.delegation.ZKDelegationTokenSecretManager;
import org.apache.curator.framework.CuratorFramework;
import org.apache.hadoop.security.authentication.server.AuthenticationHandler;
import org.apache.hadoop.security.authorize.ProxyUsers;
import org.apache.hadoop.security.authentication.server.KerberosAuthenticationHandler;
import org.apache.hadoop.security.authentication.server.PseudoAuthenticationHandler;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenSecretManager;
import java.util.Enumeration;
import org.apache.hadoop.conf.Configuration;
import javax.servlet.ServletException;
import java.util.Properties;
import javax.servlet.FilterConfig;
import org.apache.hadoop.security.SaslRpcServer;
import org.apache.hadoop.security.UserGroupInformation;
import java.nio.charset.Charset;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.authentication.server.AuthenticationFilter;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class DelegationTokenAuthenticationFilter extends AuthenticationFilter
{
    private static final String APPLICATION_JSON_MIME = "application/json";
    private static final String ERROR_EXCEPTION_JSON = "exception";
    private static final String ERROR_MESSAGE_JSON = "message";
    private static final Logger LOG;
    public static final String DELEGATION_TOKEN_SECRET_MANAGER_ATTR = "hadoop.http.delegation-token-secret-manager";
    private static final Charset UTF8_CHARSET;
    private static final ThreadLocal<UserGroupInformation> UGI_TL;
    public static final String PROXYUSER_PREFIX = "proxyuser";
    private SaslRpcServer.AuthMethod handlerAuthMethod;
    
    @Override
    protected Properties getConfiguration(final String configPrefix, final FilterConfig filterConfig) throws ServletException {
        final Properties props = super.getConfiguration(configPrefix, filterConfig);
        this.setAuthHandlerClass(props);
        return props;
    }
    
    protected void setAuthHandlerClass(final Properties props) throws ServletException {
        final String authType = props.getProperty("type");
        if (authType == null) {
            throw new ServletException("Config property type doesn't exist");
        }
        if (authType.equals("simple")) {
            props.setProperty("type", PseudoDelegationTokenAuthenticationHandler.class.getName());
        }
        else if (authType.equals("kerberos")) {
            props.setProperty("type", KerberosDelegationTokenAuthenticationHandler.class.getName());
        }
        else if (authType.equals("multi-scheme")) {
            props.setProperty("type", MultiSchemeDelegationTokenAuthenticationHandler.class.getName());
        }
    }
    
    protected Configuration getProxyuserConfiguration(final FilterConfig filterConfig) throws ServletException {
        final Configuration conf = new Configuration(false);
        final Enumeration<?> names = filterConfig.getInitParameterNames();
        while (names.hasMoreElements()) {
            final String name = (String)names.nextElement();
            if (name.startsWith("proxyuser.")) {
                final String value = filterConfig.getInitParameter(name);
                conf.set(name, value);
            }
        }
        return conf;
    }
    
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        final AuthenticationHandler handler = this.getAuthenticationHandler();
        final AbstractDelegationTokenSecretManager dtSecretManager = (AbstractDelegationTokenSecretManager)filterConfig.getServletContext().getAttribute("hadoop.http.delegation-token-secret-manager");
        if (dtSecretManager != null && handler instanceof DelegationTokenAuthenticationHandler) {
            final DelegationTokenAuthenticationHandler dtHandler = (DelegationTokenAuthenticationHandler)this.getAuthenticationHandler();
            dtHandler.setExternalDelegationTokenSecretManager(dtSecretManager);
        }
        if (handler instanceof PseudoAuthenticationHandler || handler instanceof PseudoDelegationTokenAuthenticationHandler) {
            this.setHandlerAuthMethod(SaslRpcServer.AuthMethod.SIMPLE);
        }
        if (handler instanceof KerberosAuthenticationHandler || handler instanceof KerberosDelegationTokenAuthenticationHandler) {
            this.setHandlerAuthMethod(SaslRpcServer.AuthMethod.KERBEROS);
        }
        final Configuration conf = this.getProxyuserConfiguration(filterConfig);
        ProxyUsers.refreshSuperUserGroupsConfiguration(conf, "proxyuser");
    }
    
    @Override
    protected void initializeAuthHandler(final String authHandlerClassName, final FilterConfig filterConfig) throws ServletException {
        ZKDelegationTokenSecretManager.setCurator((CuratorFramework)filterConfig.getServletContext().getAttribute("signer.secret.provider.zookeeper.curator.client"));
        super.initializeAuthHandler(authHandlerClassName, filterConfig);
        ZKDelegationTokenSecretManager.setCurator(null);
    }
    
    protected void setHandlerAuthMethod(final SaslRpcServer.AuthMethod authMethod) {
        this.handlerAuthMethod = authMethod;
    }
    
    @VisibleForTesting
    static String getDoAs(final HttpServletRequest request) {
        final String queryString = request.getQueryString();
        if (queryString == null) {
            return null;
        }
        final List<NameValuePair> list = URLEncodedUtils.parse(queryString, DelegationTokenAuthenticationFilter.UTF8_CHARSET);
        if (list != null) {
            for (final NameValuePair nv : list) {
                if ("doAs".equalsIgnoreCase(nv.getName())) {
                    return nv.getValue();
                }
            }
        }
        return null;
    }
    
    static UserGroupInformation getHttpUserGroupInformationInContext() {
        return DelegationTokenAuthenticationFilter.UGI_TL.get();
    }
    
    @Override
    protected void doFilter(final FilterChain filterChain, HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        boolean requestCompleted = false;
        UserGroupInformation ugi = null;
        final AuthenticationToken authToken = (AuthenticationToken)request.getUserPrincipal();
        if (authToken != null && authToken != AuthenticationToken.ANONYMOUS) {
            ugi = (UserGroupInformation)request.getAttribute("hadoop.security.delegation-token.ugi");
            if (ugi == null) {
                final String realUser = request.getUserPrincipal().getName();
                ugi = UserGroupInformation.createRemoteUser(realUser, this.handlerAuthMethod);
                final String doAsUser = getDoAs(request);
                if (doAsUser != null) {
                    ugi = UserGroupInformation.createProxyUser(doAsUser, ugi);
                    try {
                        ProxyUsers.authorize(ugi, request.getRemoteAddr());
                    }
                    catch (AuthorizationException ex) {
                        HttpExceptionUtils.createServletExceptionResponse(response, 403, ex);
                        requestCompleted = true;
                        if (DelegationTokenAuthenticationFilter.LOG.isDebugEnabled()) {
                            DelegationTokenAuthenticationFilter.LOG.debug("Authentication exception: " + ex.getMessage(), ex);
                        }
                        else {
                            DelegationTokenAuthenticationFilter.LOG.warn("Authentication exception: " + ex.getMessage());
                        }
                    }
                }
            }
            DelegationTokenAuthenticationFilter.UGI_TL.set(ugi);
        }
        if (!requestCompleted) {
            final UserGroupInformation ugiF = ugi;
            try {
                request = new HttpServletRequestWrapper(request) {
                    @Override
                    public String getAuthType() {
                        return (ugiF != null) ? DelegationTokenAuthenticationFilter.this.handlerAuthMethod.toString() : null;
                    }
                    
                    @Override
                    public String getRemoteUser() {
                        return (ugiF != null) ? ugiF.getShortUserName() : null;
                    }
                    
                    @Override
                    public Principal getUserPrincipal() {
                        return (ugiF != null) ? new Principal() {
                            @Override
                            public String getName() {
                                return ugiF.getUserName();
                            }
                        } : null;
                    }
                };
                super.doFilter(filterChain, request, response);
            }
            finally {
                DelegationTokenAuthenticationFilter.UGI_TL.remove();
            }
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(DelegationTokenAuthenticationFilter.class);
        UTF8_CHARSET = Charset.forName("UTF-8");
        UGI_TL = new ThreadLocal<UserGroupInformation>();
    }
}
