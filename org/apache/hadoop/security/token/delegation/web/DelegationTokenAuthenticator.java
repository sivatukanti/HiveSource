// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token.delegation.web;

import org.slf4j.LoggerFactory;
import java.net.HttpURLConnection;
import java.util.Iterator;
import org.apache.hadoop.util.JsonSerialization;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.util.HttpExceptionUtils;
import java.net.URLEncoder;
import java.util.HashMap;
import org.apache.hadoop.security.SecurityUtil;
import java.net.InetSocketAddress;
import java.util.Map;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenIdentifier;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.authentication.client.AuthenticationException;
import java.io.IOException;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authentication.client.AuthenticatedURL;
import java.net.URL;
import org.apache.hadoop.security.authentication.client.ConnectionConfigurator;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.authentication.client.Authenticator;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class DelegationTokenAuthenticator implements Authenticator
{
    private static Logger LOG;
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON_MIME = "application/json";
    private static final String HTTP_GET = "GET";
    private static final String HTTP_PUT = "PUT";
    public static final String OP_PARAM = "op";
    private static final String OP_PARAM_EQUALS = "op=";
    public static final String DELEGATION_TOKEN_HEADER = "X-Hadoop-Delegation-Token";
    public static final String DELEGATION_PARAM = "delegation";
    public static final String TOKEN_PARAM = "token";
    public static final String RENEWER_PARAM = "renewer";
    public static final String SERVICE_PARAM = "service";
    public static final String DELEGATION_TOKEN_JSON = "Token";
    public static final String DELEGATION_TOKEN_URL_STRING_JSON = "urlString";
    public static final String RENEW_DELEGATION_TOKEN_JSON = "long";
    private Authenticator authenticator;
    private ConnectionConfigurator connConfigurator;
    
    public DelegationTokenAuthenticator(final Authenticator authenticator) {
        this.authenticator = authenticator;
    }
    
    @Override
    public void setConnectionConfigurator(final ConnectionConfigurator configurator) {
        this.authenticator.setConnectionConfigurator(configurator);
        this.connConfigurator = configurator;
    }
    
    private boolean hasDelegationToken(final URL url, final AuthenticatedURL.Token token) {
        boolean hasDt = false;
        if (token instanceof DelegationTokenAuthenticatedURL.Token) {
            hasDt = (((DelegationTokenAuthenticatedURL.Token)token).getDelegationToken() != null);
            if (hasDt) {
                DelegationTokenAuthenticator.LOG.trace("Delegation token found: {}", ((DelegationTokenAuthenticatedURL.Token)token).getDelegationToken());
            }
        }
        if (!hasDt) {
            final String queryStr = url.getQuery();
            hasDt = (queryStr != null && queryStr.contains("delegation="));
            DelegationTokenAuthenticator.LOG.trace("hasDt={}, queryStr={}", (Object)hasDt, queryStr);
        }
        return hasDt;
    }
    
    @Override
    public void authenticate(final URL url, final AuthenticatedURL.Token token) throws IOException, AuthenticationException {
        if (!this.hasDelegationToken(url, token)) {
            try {
                UserGroupInformation.getCurrentUser().checkTGTAndReloginFromKeytab();
                DelegationTokenAuthenticator.LOG.debug("No delegation token found for url={}, token={}, authenticating with {}", url, token, this.authenticator.getClass());
                this.authenticator.authenticate(url, token);
                return;
            }
            catch (IOException ex) {
                throw NetUtils.wrapException(url.getHost(), url.getPort(), null, 0, ex);
            }
        }
        DelegationTokenAuthenticator.LOG.debug("Authenticated from delegation token. url={}, token={}", url, token);
    }
    
    public Token<AbstractDelegationTokenIdentifier> getDelegationToken(final URL url, final AuthenticatedURL.Token token, final String renewer) throws IOException, AuthenticationException {
        return this.getDelegationToken(url, token, renewer, null);
    }
    
    public Token<AbstractDelegationTokenIdentifier> getDelegationToken(final URL url, final AuthenticatedURL.Token token, final String renewer, final String doAsUser) throws IOException, AuthenticationException {
        Map json = this.doDelegationTokenOperation(url, token, DelegationTokenOperation.GETDELEGATIONTOKEN, renewer, null, true, doAsUser);
        json = json.get("Token");
        final String tokenStr = json.get("urlString");
        final Token<AbstractDelegationTokenIdentifier> dToken = new Token<AbstractDelegationTokenIdentifier>();
        dToken.decodeFromUrlString(tokenStr);
        final InetSocketAddress service = new InetSocketAddress(url.getHost(), url.getPort());
        SecurityUtil.setTokenService(dToken, service);
        return dToken;
    }
    
    public long renewDelegationToken(final URL url, final AuthenticatedURL.Token token, final Token<AbstractDelegationTokenIdentifier> dToken) throws IOException, AuthenticationException {
        return this.renewDelegationToken(url, token, dToken, null);
    }
    
    public long renewDelegationToken(final URL url, final AuthenticatedURL.Token token, final Token<AbstractDelegationTokenIdentifier> dToken, final String doAsUser) throws IOException, AuthenticationException {
        final Map json = this.doDelegationTokenOperation(url, token, DelegationTokenOperation.RENEWDELEGATIONTOKEN, null, dToken, true, doAsUser);
        return json.get("long");
    }
    
    public void cancelDelegationToken(final URL url, final AuthenticatedURL.Token token, final Token<AbstractDelegationTokenIdentifier> dToken) throws IOException {
        this.cancelDelegationToken(url, token, dToken, null);
    }
    
    public void cancelDelegationToken(final URL url, final AuthenticatedURL.Token token, final Token<AbstractDelegationTokenIdentifier> dToken, final String doAsUser) throws IOException {
        try {
            this.doDelegationTokenOperation(url, token, DelegationTokenOperation.CANCELDELEGATIONTOKEN, null, dToken, false, doAsUser);
        }
        catch (AuthenticationException ex) {
            throw new IOException("This should not happen: " + ex.getMessage(), ex);
        }
    }
    
    private Map doDelegationTokenOperation(URL url, final AuthenticatedURL.Token token, final DelegationTokenOperation operation, final String renewer, final Token<?> dToken, final boolean hasResponse, final String doAsUser) throws IOException, AuthenticationException {
        Map ret = null;
        final Map<String, String> params = new HashMap<String, String>();
        params.put("op", operation.toString());
        if (renewer != null) {
            params.put("renewer", renewer);
        }
        if (dToken != null) {
            params.put("token", dToken.encodeToUrlString());
        }
        if (doAsUser != null) {
            params.put("doAs", URLEncoder.encode(doAsUser, "UTF-8"));
        }
        final String urlStr = url.toExternalForm();
        final StringBuilder sb = new StringBuilder(urlStr);
        String separator = urlStr.contains("?") ? "&" : "?";
        for (final Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(separator).append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF8"));
            separator = "&";
        }
        url = new URL(sb.toString());
        final AuthenticatedURL aUrl = new AuthenticatedURL(this, this.connConfigurator);
        Token<AbstractDelegationTokenIdentifier> dt = null;
        if (token instanceof DelegationTokenAuthenticatedURL.Token && operation.requiresKerberosCredentials()) {
            dt = ((DelegationTokenAuthenticatedURL.Token)token).getDelegationToken();
            ((DelegationTokenAuthenticatedURL.Token)token).setDelegationToken(null);
        }
        try {
            final HttpURLConnection conn = aUrl.openConnection(url, token);
            conn.setRequestMethod(operation.getHttpMethod());
            HttpExceptionUtils.validateResponse(conn, 200);
            if (hasResponse) {
                String contentType = conn.getHeaderField("Content-Type");
                contentType = ((contentType != null) ? StringUtils.toLowerCase(contentType) : null);
                if (contentType != null && contentType.contains("application/json")) {
                    try {
                        ret = JsonSerialization.mapReader().readValue(conn.getInputStream());
                        return ret;
                    }
                    catch (Exception ex) {
                        throw new AuthenticationException(String.format("'%s' did not handle the '%s' delegation token operation: %s", url.getAuthority(), operation, ex.getMessage()), ex);
                    }
                }
                throw new AuthenticationException(String.format("'%s' did not respond with JSON to the '%s' delegation token operation", url.getAuthority(), operation));
            }
        }
        finally {
            if (dt != null) {
                ((DelegationTokenAuthenticatedURL.Token)token).setDelegationToken(dt);
            }
        }
        return ret;
    }
    
    static {
        DelegationTokenAuthenticator.LOG = LoggerFactory.getLogger(DelegationTokenAuthenticator.class);
    }
    
    @InterfaceAudience.Private
    public enum DelegationTokenOperation
    {
        GETDELEGATIONTOKEN("GET", true), 
        RENEWDELEGATIONTOKEN("PUT", true), 
        CANCELDELEGATIONTOKEN("PUT", false);
        
        private String httpMethod;
        private boolean requiresKerberosCredentials;
        
        private DelegationTokenOperation(final String httpMethod, final boolean requiresKerberosCredentials) {
            this.httpMethod = httpMethod;
            this.requiresKerberosCredentials = requiresKerberosCredentials;
        }
        
        public String getHttpMethod() {
            return this.httpMethod;
        }
        
        public boolean requiresKerberosCredentials() {
            return this.requiresKerberosCredentials;
        }
    }
}
