// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.server;

import org.slf4j.LoggerFactory;
import javax.naming.Context;
import javax.naming.directory.InitialDirContext;
import javax.naming.ldap.LdapContext;
import javax.naming.NamingException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.HostnameVerifier;
import javax.naming.ldap.ExtendedRequest;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import java.util.Hashtable;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.security.authentication.client.AuthenticationException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import com.google.common.base.Preconditions;
import java.util.Properties;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class LdapAuthenticationHandler implements AuthenticationHandler
{
    private static Logger logger;
    public static final String TYPE = "ldap";
    public static final String SECURITY_AUTHENTICATION = "simple";
    public static final String PROVIDER_URL = "ldap.providerurl";
    public static final String BASE_DN = "ldap.basedn";
    public static final String LDAP_BIND_DOMAIN = "ldap.binddomain";
    public static final String ENABLE_START_TLS = "ldap.enablestarttls";
    private String ldapDomain;
    private String baseDN;
    private String providerUrl;
    private Boolean enableStartTls;
    private Boolean disableHostNameVerification;
    
    @VisibleForTesting
    public void setEnableStartTls(final Boolean enableStartTls) {
        this.enableStartTls = enableStartTls;
    }
    
    @VisibleForTesting
    public void setDisableHostNameVerification(final Boolean disableHostNameVerification) {
        this.disableHostNameVerification = disableHostNameVerification;
    }
    
    @Override
    public String getType() {
        return "ldap";
    }
    
    @Override
    public void init(final Properties config) throws ServletException {
        this.baseDN = config.getProperty("ldap.basedn");
        this.providerUrl = config.getProperty("ldap.providerurl");
        this.ldapDomain = config.getProperty("ldap.binddomain");
        this.enableStartTls = Boolean.valueOf(config.getProperty("ldap.enablestarttls", "false"));
        Preconditions.checkNotNull(this.providerUrl, (Object)"The LDAP URI can not be null");
        Preconditions.checkArgument(this.baseDN == null ^ this.ldapDomain == null, (Object)"Either LDAP base DN or LDAP domain value needs to be specified");
        if (this.enableStartTls) {
            final String tmp = this.providerUrl.toLowerCase();
            Preconditions.checkArgument(!tmp.startsWith("ldaps"), (Object)"Can not use ldaps and StartTLS option at the same time");
        }
    }
    
    @Override
    public void destroy() {
    }
    
    @Override
    public boolean managementOperation(final AuthenticationToken token, final HttpServletRequest request, final HttpServletResponse response) throws IOException, AuthenticationException {
        return true;
    }
    
    @Override
    public AuthenticationToken authenticate(final HttpServletRequest request, final HttpServletResponse response) throws IOException, AuthenticationException {
        AuthenticationToken token = null;
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !AuthenticationHandlerUtil.matchAuthScheme("Basic", authorization)) {
            response.setHeader("WWW-Authenticate", "Basic");
            response.setStatus(401);
            if (authorization == null) {
                LdapAuthenticationHandler.logger.trace("Basic auth starting");
            }
            else {
                LdapAuthenticationHandler.logger.warn("'Authorization' does not start with 'Basic' :  {}", authorization);
            }
        }
        else {
            authorization = authorization.substring("Basic".length()).trim();
            final Base64 base64 = new Base64(0);
            final String[] credentials = new String(base64.decode(authorization), StandardCharsets.UTF_8).split(":", 2);
            if (credentials.length == 2) {
                token = this.authenticateUser(credentials[0], credentials[1]);
                response.setStatus(200);
            }
        }
        return token;
    }
    
    private AuthenticationToken authenticateUser(String userName, final String password) throws AuthenticationException {
        if (userName == null || userName.isEmpty()) {
            throw new AuthenticationException("Error validating LDAP user: a null or blank username has been provided");
        }
        if (!hasDomain(userName) && this.ldapDomain != null) {
            userName = userName + "@" + this.ldapDomain;
        }
        if (password == null || password.isEmpty() || password.getBytes(StandardCharsets.UTF_8)[0] == 0) {
            throw new AuthenticationException("Error validating LDAP user: a null or blank password has been provided");
        }
        String bindDN;
        if (this.baseDN == null) {
            bindDN = userName;
        }
        else {
            bindDN = "uid=" + userName + "," + this.baseDN;
        }
        if (this.enableStartTls) {
            this.authenticateWithTlsExtension(bindDN, password);
        }
        else {
            this.authenticateWithoutTlsExtension(bindDN, password);
        }
        return new AuthenticationToken(userName, userName, "ldap");
    }
    
    private void authenticateWithTlsExtension(final String userDN, final String password) throws AuthenticationException {
        LdapContext ctx = null;
        final Hashtable<String, Object> env = new Hashtable<String, Object>();
        env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
        env.put("java.naming.provider.url", this.providerUrl);
        try {
            ctx = new InitialLdapContext(env, null);
            final StartTlsResponse tls = (StartTlsResponse)ctx.extendedOperation(new StartTlsRequest());
            if (this.disableHostNameVerification) {
                tls.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(final String hostname, final SSLSession session) {
                        return true;
                    }
                });
            }
            tls.negotiate();
            ctx.addToEnvironment("java.naming.security.authentication", "simple");
            ctx.addToEnvironment("java.naming.security.principal", userDN);
            ctx.addToEnvironment("java.naming.security.credentials", password);
            ctx.lookup(userDN);
            LdapAuthenticationHandler.logger.debug("Authentication successful for {}", userDN);
        }
        catch (NamingException ex2) {}
        catch (IOException ex) {
            throw new AuthenticationException("Error validating LDAP user", ex);
        }
        finally {
            if (ctx != null) {
                try {
                    ctx.close();
                }
                catch (NamingException ex3) {}
            }
        }
    }
    
    private void authenticateWithoutTlsExtension(final String userDN, final String password) throws AuthenticationException {
        final Hashtable<String, Object> env = new Hashtable<String, Object>();
        env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
        env.put("java.naming.provider.url", this.providerUrl);
        env.put("java.naming.security.authentication", "simple");
        env.put("java.naming.security.principal", userDN);
        env.put("java.naming.security.credentials", password);
        try {
            final Context ctx = new InitialDirContext(env);
            ctx.close();
            LdapAuthenticationHandler.logger.debug("Authentication successful for {}", userDN);
        }
        catch (NamingException e) {
            throw new AuthenticationException("Error validating LDAP user", e);
        }
    }
    
    private static boolean hasDomain(final String userName) {
        return indexOfDomainMatch(userName) > 0;
    }
    
    private static int indexOfDomainMatch(final String userName) {
        if (userName == null) {
            return -1;
        }
        final int idx = userName.indexOf(47);
        final int idx2 = userName.indexOf(64);
        int endIdx = Math.min(idx, idx2);
        if (endIdx == -1) {
            endIdx = Math.max(idx, idx2);
        }
        return endIdx;
    }
    
    static {
        LdapAuthenticationHandler.logger = LoggerFactory.getLogger(LdapAuthenticationHandler.class);
    }
}
