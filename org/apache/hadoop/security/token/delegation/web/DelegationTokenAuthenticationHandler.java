// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token.delegation.web;

import java.util.HashSet;
import org.slf4j.LoggerFactory;
import java.util.LinkedHashMap;
import java.io.Writer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenIdentifier;
import java.text.MessageFormat;
import org.apache.hadoop.security.authentication.client.AuthenticationException;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.authorize.AuthorizationException;
import org.apache.hadoop.util.HttpExceptionUtils;
import org.apache.hadoop.security.authorize.ProxyUsers;
import org.apache.hadoop.security.UserGroupInformation;
import javax.servlet.http.HttpServletResponse;
import org.apache.hadoop.security.authentication.server.AuthenticationToken;
import java.io.IOException;
import org.apache.hadoop.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.core.JsonGenerator;
import java.util.Iterator;
import org.apache.hadoop.io.Text;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenSecretManager;
import javax.servlet.ServletException;
import java.util.Properties;
import com.google.common.annotations.VisibleForTesting;
import com.fasterxml.jackson.core.JsonFactory;
import java.util.Set;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.authentication.server.AuthenticationHandler;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public abstract class DelegationTokenAuthenticationHandler implements AuthenticationHandler
{
    private static final Logger LOG;
    protected static final String TYPE_POSTFIX = "-dt";
    public static final String PREFIX = "delegation-token.";
    public static final String TOKEN_KIND = "delegation-token.token-kind";
    private static final Set<String> DELEGATION_TOKEN_OPS;
    public static final String DELEGATION_TOKEN_UGI_ATTRIBUTE = "hadoop.security.delegation-token.ugi";
    public static final String JSON_MAPPER_PREFIX = "delegation-token.json-mapper.";
    private AuthenticationHandler authHandler;
    private DelegationTokenManager tokenManager;
    private String authType;
    private JsonFactory jsonFactory;
    private static final String ENTER;
    
    public DelegationTokenAuthenticationHandler(final AuthenticationHandler handler) {
        this.authHandler = handler;
        this.authType = handler.getType();
    }
    
    @VisibleForTesting
    DelegationTokenManager getTokenManager() {
        return this.tokenManager;
    }
    
    AuthenticationHandler getAuthHandler() {
        return this.authHandler;
    }
    
    @Override
    public void init(final Properties config) throws ServletException {
        this.authHandler.init(config);
        this.initTokenManager(config);
        this.initJsonFactory(config);
    }
    
    public void setExternalDelegationTokenSecretManager(final AbstractDelegationTokenSecretManager secretManager) {
        this.tokenManager.setExternalDelegationTokenSecretManager(secretManager);
    }
    
    @VisibleForTesting
    public void initTokenManager(final Properties config) {
        final Configuration conf = new Configuration(false);
        for (final Map.Entry entry : config.entrySet()) {
            conf.set(entry.getKey(), entry.getValue());
        }
        String tokenKind = conf.get("delegation-token.token-kind");
        if (tokenKind == null) {
            throw new IllegalArgumentException("The configuration does not define the token kind");
        }
        tokenKind = tokenKind.trim();
        (this.tokenManager = new DelegationTokenManager(conf, new Text(tokenKind))).init();
    }
    
    @VisibleForTesting
    public void initJsonFactory(final Properties config) {
        boolean hasFeature = false;
        final JsonFactory tmpJsonFactory = new JsonFactory();
        for (final Map.Entry entry : config.entrySet()) {
            final String key = entry.getKey();
            if (key.startsWith("delegation-token.json-mapper.")) {
                final JsonGenerator.Feature feature = JsonGenerator.Feature.valueOf(key.substring("delegation-token.json-mapper.".length()));
                if (feature == null) {
                    continue;
                }
                hasFeature = true;
                final boolean enabled = Boolean.parseBoolean(entry.getValue());
                tmpJsonFactory.configure(feature, enabled);
            }
        }
        if (hasFeature) {
            this.jsonFactory = tmpJsonFactory;
        }
    }
    
    @Override
    public void destroy() {
        this.tokenManager.destroy();
        this.authHandler.destroy();
    }
    
    @Override
    public String getType() {
        return this.authType;
    }
    
    protected final boolean isManagementOperation(final HttpServletRequest request) throws IOException {
        String op = ServletUtils.getParameter(request, "op");
        op = ((op != null) ? StringUtils.toUpperCase(op) : null);
        return DelegationTokenAuthenticationHandler.DELEGATION_TOKEN_OPS.contains(op) && !request.getMethod().equals("OPTIONS");
    }
    
    @Override
    public boolean managementOperation(AuthenticationToken token, final HttpServletRequest request, final HttpServletResponse response) throws IOException, AuthenticationException {
        boolean requestContinues = true;
        DelegationTokenAuthenticationHandler.LOG.trace("Processing operation for req=({}), token: {}", request, token);
        String op = ServletUtils.getParameter(request, "op");
        op = ((op != null) ? StringUtils.toUpperCase(op) : null);
        if (this.isManagementOperation(request)) {
            final DelegationTokenAuthenticator.DelegationTokenOperation dtOp = DelegationTokenAuthenticator.DelegationTokenOperation.valueOf(op);
            if (dtOp.getHttpMethod().equals(request.getMethod())) {
                boolean doManagement;
                if (dtOp.requiresKerberosCredentials() && token == null) {
                    token = this.authHandler.authenticate(request, response);
                    DelegationTokenAuthenticationHandler.LOG.trace("Got token: {}.", token);
                    if (token == null) {
                        requestContinues = false;
                        doManagement = false;
                    }
                    else {
                        doManagement = true;
                    }
                }
                else {
                    doManagement = true;
                }
                if (doManagement) {
                    UserGroupInformation requestUgi = (token != null) ? UserGroupInformation.createRemoteUser(token.getUserName()) : null;
                    final String doAsUser = DelegationTokenAuthenticationFilter.getDoAs(request);
                    if (requestUgi != null && doAsUser != null) {
                        requestUgi = UserGroupInformation.createProxyUser(doAsUser, requestUgi);
                        try {
                            ProxyUsers.authorize(requestUgi, request.getRemoteAddr());
                        }
                        catch (AuthorizationException ex) {
                            HttpExceptionUtils.createServletExceptionResponse(response, 403, ex);
                            return false;
                        }
                    }
                    Map map = null;
                    switch (dtOp) {
                        case GETDELEGATIONTOKEN: {
                            if (requestUgi == null) {
                                throw new IllegalStateException("request UGI cannot be NULL");
                            }
                            final String renewer = ServletUtils.getParameter(request, "renewer");
                            final String service = ServletUtils.getParameter(request, "service");
                            try {
                                final Token<?> dToken = this.tokenManager.createToken(requestUgi, renewer, service);
                                map = delegationTokenToJSON(dToken);
                                break;
                            }
                            catch (IOException ex2) {
                                throw new AuthenticationException(ex2.toString(), ex2);
                            }
                        }
                        case RENEWDELEGATIONTOKEN: {
                            if (requestUgi == null) {
                                throw new IllegalStateException("request UGI cannot be NULL");
                            }
                            final String tokenToRenew = ServletUtils.getParameter(request, "token");
                            if (tokenToRenew == null) {
                                response.sendError(400, MessageFormat.format("Operation [{0}] requires the parameter [{1}]", dtOp, "token"));
                                requestContinues = false;
                                break;
                            }
                            final Token<AbstractDelegationTokenIdentifier> dt = new Token<AbstractDelegationTokenIdentifier>();
                            try {
                                dt.decodeFromUrlString(tokenToRenew);
                                final long expirationTime = this.tokenManager.renewToken(dt, requestUgi.getShortUserName());
                                map = new HashMap();
                                map.put("long", expirationTime);
                            }
                            catch (IOException ex3) {
                                throw new AuthenticationException(ex3.toString(), ex3);
                            }
                            break;
                        }
                        case CANCELDELEGATIONTOKEN: {
                            final String tokenToCancel = ServletUtils.getParameter(request, "token");
                            if (tokenToCancel == null) {
                                response.sendError(400, MessageFormat.format("Operation [{0}] requires the parameter [{1}]", dtOp, "token"));
                                requestContinues = false;
                                break;
                            }
                            final Token<AbstractDelegationTokenIdentifier> dt2 = new Token<AbstractDelegationTokenIdentifier>();
                            try {
                                dt2.decodeFromUrlString(tokenToCancel);
                                this.tokenManager.cancelToken(dt2, (requestUgi != null) ? requestUgi.getShortUserName() : null);
                            }
                            catch (IOException ex4) {
                                response.sendError(404, "Invalid delegation token, cannot cancel");
                                requestContinues = false;
                            }
                            break;
                        }
                    }
                    if (requestContinues) {
                        response.setStatus(200);
                        if (map != null) {
                            response.setContentType("application/json");
                            final Writer writer = response.getWriter();
                            final ObjectMapper jsonMapper = new ObjectMapper(this.jsonFactory);
                            jsonMapper.writeValue(writer, map);
                            writer.write(DelegationTokenAuthenticationHandler.ENTER);
                            writer.flush();
                        }
                        requestContinues = false;
                    }
                }
            }
            else {
                response.sendError(400, MessageFormat.format("Wrong HTTP method [{0}] for operation [{1}], it should be [{2}]", request.getMethod(), dtOp, dtOp.getHttpMethod()));
                requestContinues = false;
            }
        }
        return requestContinues;
    }
    
    private static Map delegationTokenToJSON(final Token token) throws IOException {
        final Map json = new LinkedHashMap();
        json.put("urlString", token.encodeToUrlString());
        final Map response = new LinkedHashMap();
        response.put("Token", json);
        return response;
    }
    
    @Override
    public AuthenticationToken authenticate(final HttpServletRequest request, final HttpServletResponse response) throws IOException, AuthenticationException {
        final String delegationParam = this.getDelegationToken(request);
        AuthenticationToken token;
        if (delegationParam != null) {
            DelegationTokenAuthenticationHandler.LOG.debug("Authenticating with dt param: {}", delegationParam);
            try {
                final Token<AbstractDelegationTokenIdentifier> dt = new Token<AbstractDelegationTokenIdentifier>();
                dt.decodeFromUrlString(delegationParam);
                final UserGroupInformation ugi = this.tokenManager.verifyToken(dt);
                final String shortName = ugi.getShortUserName();
                token = new AuthenticationToken(shortName, ugi.getUserName(), this.getType());
                token.setExpires(0L);
                request.setAttribute("hadoop.security.delegation-token.ugi", ugi);
            }
            catch (Throwable ex) {
                token = null;
                HttpExceptionUtils.createServletExceptionResponse(response, 403, new AuthenticationException(ex));
            }
        }
        else {
            DelegationTokenAuthenticationHandler.LOG.debug("Falling back to {} (req={})", this.authHandler.getClass(), request);
            token = this.authHandler.authenticate(request, response);
        }
        return token;
    }
    
    private String getDelegationToken(final HttpServletRequest request) throws IOException {
        String dToken = request.getHeader("X-Hadoop-Delegation-Token");
        if (dToken == null) {
            dToken = ServletUtils.getParameter(request, "delegation");
        }
        return dToken;
    }
    
    static {
        LOG = LoggerFactory.getLogger(DelegationTokenAuthenticationHandler.class);
        (DELEGATION_TOKEN_OPS = new HashSet<String>()).add(DelegationTokenAuthenticator.DelegationTokenOperation.GETDELEGATIONTOKEN.toString());
        DelegationTokenAuthenticationHandler.DELEGATION_TOKEN_OPS.add(DelegationTokenAuthenticator.DelegationTokenOperation.RENEWDELEGATIONTOKEN.toString());
        DelegationTokenAuthenticationHandler.DELEGATION_TOKEN_OPS.add(DelegationTokenAuthenticator.DelegationTokenOperation.CANCELDELEGATIONTOKEN.toString());
        ENTER = System.getProperty("line.separator");
    }
}
