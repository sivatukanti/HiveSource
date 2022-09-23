// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token.delegation.web;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenIdentifier;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.SecurityUtil;
import java.net.InetSocketAddress;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.token.TokenIdentifier;
import java.net.URLEncoder;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.UserGroupInformation;
import java.util.HashMap;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.util.Map;
import org.apache.hadoop.security.authentication.client.AuthenticationException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.hadoop.security.authentication.client.Authenticator;
import org.apache.hadoop.security.authentication.client.ConnectionConfigurator;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.authentication.client.AuthenticatedURL;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class DelegationTokenAuthenticatedURL extends AuthenticatedURL
{
    private static final Logger LOG;
    static final String DO_AS = "doAs";
    private static Class<? extends DelegationTokenAuthenticator> DEFAULT_AUTHENTICATOR;
    private boolean useQueryStringforDelegationToken;
    
    public static void setDefaultDelegationTokenAuthenticator(final Class<? extends DelegationTokenAuthenticator> authenticator) {
        DelegationTokenAuthenticatedURL.DEFAULT_AUTHENTICATOR = authenticator;
    }
    
    public static Class<? extends DelegationTokenAuthenticator> getDefaultDelegationTokenAuthenticator() {
        return DelegationTokenAuthenticatedURL.DEFAULT_AUTHENTICATOR;
    }
    
    private static DelegationTokenAuthenticator obtainDelegationTokenAuthenticator(DelegationTokenAuthenticator dta, final ConnectionConfigurator connConfigurator) {
        try {
            if (dta == null) {
                dta = (DelegationTokenAuthenticator)DelegationTokenAuthenticatedURL.DEFAULT_AUTHENTICATOR.newInstance();
                dta.setConnectionConfigurator(connConfigurator);
            }
            return dta;
        }
        catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    public DelegationTokenAuthenticatedURL() {
        this(null, null);
    }
    
    public DelegationTokenAuthenticatedURL(final DelegationTokenAuthenticator authenticator) {
        this(authenticator, null);
    }
    
    public DelegationTokenAuthenticatedURL(final ConnectionConfigurator connConfigurator) {
        this(null, connConfigurator);
    }
    
    public DelegationTokenAuthenticatedURL(final DelegationTokenAuthenticator authenticator, final ConnectionConfigurator connConfigurator) {
        super(obtainDelegationTokenAuthenticator(authenticator, connConfigurator), connConfigurator);
        this.useQueryStringforDelegationToken = false;
    }
    
    @Deprecated
    protected void setUseQueryStringForDelegationToken(final boolean useQueryString) {
        this.useQueryStringforDelegationToken = useQueryString;
    }
    
    public boolean useQueryStringForDelegationToken() {
        return this.useQueryStringforDelegationToken;
    }
    
    @Override
    public HttpURLConnection openConnection(final URL url, final AuthenticatedURL.Token token) throws IOException, AuthenticationException {
        return (token instanceof Token) ? this.openConnection(url, (Token)token) : super.openConnection(url, token);
    }
    
    public HttpURLConnection openConnection(final URL url, final Token token) throws IOException, AuthenticationException {
        return this.openConnection(url, token, null);
    }
    
    private URL augmentURL(URL url, final Map<String, String> params) throws IOException {
        if (params != null && params.size() > 0) {
            final String urlStr = url.toExternalForm();
            final StringBuilder sb = new StringBuilder(urlStr);
            String separator = urlStr.contains("?") ? "&" : "?";
            for (final Map.Entry<String, String> param : params.entrySet()) {
                sb.append(separator).append(param.getKey()).append("=").append(param.getValue());
                separator = "&";
            }
            url = new URL(sb.toString());
        }
        return url;
    }
    
    public HttpURLConnection openConnection(URL url, final Token token, final String doAs) throws IOException, AuthenticationException {
        Preconditions.checkNotNull(url, (Object)"url");
        Preconditions.checkNotNull(token, (Object)"token");
        final Map<String, String> extraParams = new HashMap<String, String>();
        org.apache.hadoop.security.token.Token<? extends TokenIdentifier> dToken = null;
        DelegationTokenAuthenticatedURL.LOG.debug("Connecting to url {} with token {} as {}", url, token, doAs);
        if (!token.isSet()) {
            final Credentials creds = UserGroupInformation.getCurrentUser().getCredentials();
            if (DelegationTokenAuthenticatedURL.LOG.isDebugEnabled()) {
                DelegationTokenAuthenticatedURL.LOG.debug("Token not set, looking for delegation token. Creds:{}, size:{}", creds.getAllTokens(), creds.numberOfTokens());
            }
            if (!creds.getAllTokens().isEmpty()) {
                dToken = this.selectDelegationToken(url, creds);
                if (dToken != null) {
                    if (this.useQueryStringForDelegationToken()) {
                        extraParams.put("delegation", dToken.encodeToUrlString());
                    }
                    else {
                        token.delegationToken = (org.apache.hadoop.security.token.Token<AbstractDelegationTokenIdentifier>)dToken;
                    }
                }
            }
        }
        if (doAs != null) {
            extraParams.put("doAs", URLEncoder.encode(doAs, "UTF-8"));
        }
        url = this.augmentURL(url, extraParams);
        final HttpURLConnection conn = super.openConnection(url, token);
        if (!token.isSet() && !this.useQueryStringForDelegationToken() && dToken != null) {
            conn.setRequestProperty("X-Hadoop-Delegation-Token", dToken.encodeToUrlString());
        }
        return conn;
    }
    
    @InterfaceAudience.Private
    public org.apache.hadoop.security.token.Token<? extends TokenIdentifier> selectDelegationToken(final URL url, final Credentials creds) {
        final InetSocketAddress serviceAddr = new InetSocketAddress(url.getHost(), url.getPort());
        final Text service = SecurityUtil.buildTokenService(serviceAddr);
        final org.apache.hadoop.security.token.Token<? extends TokenIdentifier> dToken = creds.getToken(service);
        DelegationTokenAuthenticatedURL.LOG.debug("Using delegation token {} from service:{}", dToken, service);
        return dToken;
    }
    
    public org.apache.hadoop.security.token.Token<AbstractDelegationTokenIdentifier> getDelegationToken(final URL url, final Token token, final String renewer) throws IOException, AuthenticationException {
        return this.getDelegationToken(url, token, renewer, null);
    }
    
    public org.apache.hadoop.security.token.Token<AbstractDelegationTokenIdentifier> getDelegationToken(final URL url, final Token token, final String renewer, final String doAsUser) throws IOException, AuthenticationException {
        Preconditions.checkNotNull(url, (Object)"url");
        Preconditions.checkNotNull(token, (Object)"token");
        try {
            token.delegationToken = ((KerberosDelegationTokenAuthenticator)this.getAuthenticator()).getDelegationToken(url, token, renewer, doAsUser);
            return token.delegationToken;
        }
        catch (IOException ex) {
            token.delegationToken = null;
            throw ex;
        }
    }
    
    public long renewDelegationToken(final URL url, final Token token) throws IOException, AuthenticationException {
        return this.renewDelegationToken(url, token, null);
    }
    
    public long renewDelegationToken(final URL url, final Token token, final String doAsUser) throws IOException, AuthenticationException {
        Preconditions.checkNotNull(url, (Object)"url");
        Preconditions.checkNotNull(token, (Object)"token");
        Preconditions.checkNotNull(token.delegationToken, (Object)"No delegation token available");
        try {
            return ((KerberosDelegationTokenAuthenticator)this.getAuthenticator()).renewDelegationToken(url, token, token.delegationToken, doAsUser);
        }
        catch (IOException ex) {
            token.delegationToken = null;
            throw ex;
        }
    }
    
    public void cancelDelegationToken(final URL url, final Token token) throws IOException {
        this.cancelDelegationToken(url, token, null);
    }
    
    public void cancelDelegationToken(final URL url, final Token token, final String doAsUser) throws IOException {
        Preconditions.checkNotNull(url, (Object)"url");
        Preconditions.checkNotNull(token, (Object)"token");
        Preconditions.checkNotNull(token.delegationToken, (Object)"No delegation token available");
        try {
            ((KerberosDelegationTokenAuthenticator)this.getAuthenticator()).cancelDelegationToken(url, token, token.delegationToken, doAsUser);
        }
        finally {
            token.delegationToken = null;
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(DelegationTokenAuthenticatedURL.class);
        DelegationTokenAuthenticatedURL.DEFAULT_AUTHENTICATOR = KerberosDelegationTokenAuthenticator.class;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static class Token extends AuthenticatedURL.Token
    {
        private org.apache.hadoop.security.token.Token<AbstractDelegationTokenIdentifier> delegationToken;
        
        public org.apache.hadoop.security.token.Token<AbstractDelegationTokenIdentifier> getDelegationToken() {
            return this.delegationToken;
        }
        
        public void setDelegationToken(final org.apache.hadoop.security.token.Token<AbstractDelegationTokenIdentifier> delegationToken) {
            this.delegationToken = delegationToken;
        }
    }
}
