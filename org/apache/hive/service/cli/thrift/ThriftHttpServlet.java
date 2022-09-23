// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import org.apache.hadoop.hive.shims.HadoopShims;
import org.apache.hadoop.hive.shims.ShimLoader;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import org.ietf.jgss.GSSManager;
import org.apache.commons.logging.LogFactory;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import javax.servlet.http.HttpUtils;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.binary.Base64;
import java.security.PrivilegedExceptionAction;
import org.apache.hive.service.auth.PasswdAuthenticationProvider;
import org.apache.hive.service.auth.AuthenticationProviderFactory;
import javax.ws.rs.core.NewCookie;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import org.apache.hive.service.auth.HttpAuthenticationException;
import org.apache.hive.service.auth.HttpAuthUtils;
import org.apache.hive.service.auth.HiveAuthFactory;
import org.apache.hive.service.cli.session.SessionManager;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.TProcessor;
import java.util.Random;
import org.apache.hive.service.CookieSigner;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.commons.logging.Log;
import org.apache.thrift.server.TServlet;

public class ThriftHttpServlet extends TServlet
{
    private static final long serialVersionUID = 1L;
    public static final Log LOG;
    private final String authType;
    private final UserGroupInformation serviceUGI;
    private final UserGroupInformation httpUGI;
    private HiveConf hiveConf;
    private CookieSigner signer;
    public static final String AUTH_COOKIE = "hive.server2.auth";
    private static final Random RAN;
    private boolean isCookieAuthEnabled;
    private String cookieDomain;
    private String cookiePath;
    private int cookieMaxAge;
    private boolean isCookieSecure;
    private boolean isHttpOnlyCookie;
    
    public ThriftHttpServlet(final TProcessor processor, final TProtocolFactory protocolFactory, final String authType, final UserGroupInformation serviceUGI, final UserGroupInformation httpUGI) {
        super(processor, protocolFactory);
        this.hiveConf = new HiveConf();
        this.authType = authType;
        this.serviceUGI = serviceUGI;
        this.httpUGI = httpUGI;
        this.isCookieAuthEnabled = this.hiveConf.getBoolVar(HiveConf.ConfVars.HIVE_SERVER2_THRIFT_HTTP_COOKIE_AUTH_ENABLED);
        if (this.isCookieAuthEnabled) {
            final String secret = Long.toString(ThriftHttpServlet.RAN.nextLong());
            ThriftHttpServlet.LOG.debug("Using the random number as the secret for cookie generation " + secret);
            this.signer = new CookieSigner(secret.getBytes());
            this.cookieMaxAge = (int)this.hiveConf.getTimeVar(HiveConf.ConfVars.HIVE_SERVER2_THRIFT_HTTP_COOKIE_MAX_AGE, TimeUnit.SECONDS);
            this.cookieDomain = this.hiveConf.getVar(HiveConf.ConfVars.HIVE_SERVER2_THRIFT_HTTP_COOKIE_DOMAIN);
            this.cookiePath = this.hiveConf.getVar(HiveConf.ConfVars.HIVE_SERVER2_THRIFT_HTTP_COOKIE_PATH);
            this.isCookieSecure = this.hiveConf.getBoolVar(HiveConf.ConfVars.HIVE_SERVER2_THRIFT_HTTP_COOKIE_IS_SECURE);
            this.isHttpOnlyCookie = this.hiveConf.getBoolVar(HiveConf.ConfVars.HIVE_SERVER2_THRIFT_HTTP_COOKIE_IS_HTTPONLY);
        }
    }
    
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String clientUserName = null;
        boolean requireNewCookie = false;
        try {
            if (this.isCookieAuthEnabled) {
                clientUserName = this.validateCookie(request);
                requireNewCookie = (clientUserName == null);
                if (requireNewCookie) {
                    ThriftHttpServlet.LOG.info("Could not validate cookie sent, will try to generate a new cookie");
                }
            }
            if (clientUserName == null) {
                if (this.isKerberosAuthMode(this.authType)) {
                    clientUserName = this.doKerberosAuth(request);
                }
                else {
                    clientUserName = this.doPasswdAuth(request, this.authType);
                }
            }
            ThriftHttpServlet.LOG.debug("Client username: " + clientUserName);
            SessionManager.setUserName(clientUserName);
            final String doAsQueryParam = getDoAsQueryParam(request.getQueryString());
            if (doAsQueryParam != null) {
                SessionManager.setProxyUserName(doAsQueryParam);
            }
            final String clientIpAddress = request.getRemoteAddr();
            ThriftHttpServlet.LOG.debug("Client IP Address: " + clientIpAddress);
            SessionManager.setIpAddress(clientIpAddress);
            if (requireNewCookie && !this.authType.equalsIgnoreCase(HiveAuthFactory.AuthTypes.NOSASL.toString())) {
                final String cookieToken = HttpAuthUtils.createCookieToken(clientUserName);
                final Cookie hs2Cookie = this.createCookie(this.signer.signCookie(cookieToken));
                if (this.isHttpOnlyCookie) {
                    response.setHeader("SET-COOKIE", getHttpOnlyCookieHeader(hs2Cookie));
                }
                else {
                    response.addCookie(hs2Cookie);
                }
                ThriftHttpServlet.LOG.info("Cookie added for clientUserName " + clientUserName);
            }
            super.doPost(request, response);
        }
        catch (HttpAuthenticationException e) {
            ThriftHttpServlet.LOG.error("Error: ", e);
            response.setStatus(401);
            if (this.isKerberosAuthMode(this.authType)) {
                response.addHeader("WWW-Authenticate", "Negotiate");
            }
            response.getWriter().println("Authentication Error: " + e.getMessage());
        }
        finally {
            SessionManager.clearUserName();
            SessionManager.clearIpAddress();
            SessionManager.clearProxyUserName();
        }
    }
    
    private String getClientNameFromCookie(final Cookie[] cookies) {
        for (final Cookie currCookie : cookies) {
            final String currName = currCookie.getName();
            if (currName.equals("hive.server2.auth")) {
                String currValue = currCookie.getValue();
                currValue = this.signer.verifyAndExtract(currValue);
                if (currValue != null) {
                    final String userName = HttpAuthUtils.getUserNameFromCookieToken(currValue);
                    if (userName != null) {
                        if (ThriftHttpServlet.LOG.isDebugEnabled()) {
                            ThriftHttpServlet.LOG.debug("Validated the cookie for user " + userName);
                        }
                        return userName;
                    }
                    ThriftHttpServlet.LOG.warn("Invalid cookie token " + currValue);
                }
            }
        }
        return null;
    }
    
    private String toCookieStr(final Cookie[] cookies) {
        String cookieStr = "";
        for (final Cookie c : cookies) {
            cookieStr = cookieStr + c.getName() + "=" + c.getValue() + " ;\n";
        }
        return cookieStr;
    }
    
    private String validateCookie(final HttpServletRequest request) throws UnsupportedEncodingException {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            if (ThriftHttpServlet.LOG.isDebugEnabled()) {
                ThriftHttpServlet.LOG.debug("No valid cookies associated with the request " + request);
            }
            return null;
        }
        if (ThriftHttpServlet.LOG.isDebugEnabled()) {
            ThriftHttpServlet.LOG.debug("Received cookies: " + this.toCookieStr(cookies));
        }
        return this.getClientNameFromCookie(cookies);
    }
    
    private Cookie createCookie(final String str) throws UnsupportedEncodingException {
        if (ThriftHttpServlet.LOG.isDebugEnabled()) {
            ThriftHttpServlet.LOG.debug("Cookie name = hive.server2.auth value = " + str);
        }
        final Cookie cookie = new Cookie("hive.server2.auth", str);
        cookie.setMaxAge(this.cookieMaxAge);
        if (this.cookieDomain != null) {
            cookie.setDomain(this.cookieDomain);
        }
        if (this.cookiePath != null) {
            cookie.setPath(this.cookiePath);
        }
        cookie.setSecure(this.isCookieSecure);
        return cookie;
    }
    
    private static String getHttpOnlyCookieHeader(final Cookie cookie) {
        final NewCookie newCookie = new NewCookie(cookie.getName(), cookie.getValue(), cookie.getPath(), cookie.getDomain(), cookie.getVersion(), cookie.getComment(), cookie.getMaxAge(), cookie.getSecure());
        return newCookie + "; HttpOnly";
    }
    
    private String doPasswdAuth(final HttpServletRequest request, final String authType) throws HttpAuthenticationException {
        final String userName = this.getUsername(request, authType);
        if (!authType.equalsIgnoreCase(HiveAuthFactory.AuthTypes.NOSASL.toString())) {
            try {
                final AuthenticationProviderFactory.AuthMethods authMethod = AuthenticationProviderFactory.AuthMethods.getValidAuthMethod(authType);
                final PasswdAuthenticationProvider provider = AuthenticationProviderFactory.getAuthenticationProvider(authMethod);
                provider.Authenticate(userName, this.getPassword(request, authType));
            }
            catch (Exception e) {
                throw new HttpAuthenticationException(e);
            }
        }
        return userName;
    }
    
    private String doKerberosAuth(final HttpServletRequest request) throws HttpAuthenticationException {
        if (this.httpUGI != null) {
            try {
                return this.httpUGI.doAs((PrivilegedExceptionAction<String>)new HttpKerberosServerAction(request, this.httpUGI));
            }
            catch (Exception e) {
                ThriftHttpServlet.LOG.info("Failed to authenticate with http/_HOST kerberos principal, trying with hive/_HOST kerberos principal");
            }
        }
        try {
            return this.serviceUGI.doAs((PrivilegedExceptionAction<String>)new HttpKerberosServerAction(request, this.serviceUGI));
        }
        catch (Exception e) {
            ThriftHttpServlet.LOG.error("Failed to authenticate with hive/_HOST kerberos principal");
            throw new HttpAuthenticationException(e);
        }
    }
    
    private String getUsername(final HttpServletRequest request, final String authType) throws HttpAuthenticationException {
        final String[] creds = this.getAuthHeaderTokens(request, authType);
        if (creds[0] == null || creds[0].isEmpty()) {
            throw new HttpAuthenticationException("Authorization header received from the client does not contain username.");
        }
        return creds[0];
    }
    
    private String getPassword(final HttpServletRequest request, final String authType) throws HttpAuthenticationException {
        final String[] creds = this.getAuthHeaderTokens(request, authType);
        if (creds[1] == null || creds[1].isEmpty()) {
            throw new HttpAuthenticationException("Authorization header received from the client does not contain username.");
        }
        return creds[1];
    }
    
    private String[] getAuthHeaderTokens(final HttpServletRequest request, final String authType) throws HttpAuthenticationException {
        final String authHeaderBase64 = this.getAuthHeader(request, authType);
        final String authHeaderString = StringUtils.newStringUtf8(Base64.decodeBase64(authHeaderBase64.getBytes()));
        final String[] creds = authHeaderString.split(":");
        return creds;
    }
    
    private String getAuthHeader(final HttpServletRequest request, final String authType) throws HttpAuthenticationException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.isEmpty()) {
            throw new HttpAuthenticationException("Authorization header received from the client is empty.");
        }
        int beginIndex;
        if (this.isKerberosAuthMode(authType)) {
            beginIndex = "Negotiate ".length();
        }
        else {
            beginIndex = "Basic ".length();
        }
        final String authHeaderBase64String = authHeader.substring(beginIndex);
        if (authHeaderBase64String == null || authHeaderBase64String.isEmpty()) {
            throw new HttpAuthenticationException("Authorization header received from the client does not contain any data.");
        }
        return authHeaderBase64String;
    }
    
    private boolean isKerberosAuthMode(final String authType) {
        return authType.equalsIgnoreCase(HiveAuthFactory.AuthTypes.KERBEROS.toString());
    }
    
    private static String getDoAsQueryParam(final String queryString) {
        if (ThriftHttpServlet.LOG.isDebugEnabled()) {
            ThriftHttpServlet.LOG.debug("URL query string:" + queryString);
        }
        if (queryString == null) {
            return null;
        }
        final Map<String, String[]> params = HttpUtils.parseQueryString(queryString);
        final Set<String> keySet = params.keySet();
        for (final String key : keySet) {
            if (key.equalsIgnoreCase("doAs")) {
                return params.get(key)[0];
            }
        }
        return null;
    }
    
    static {
        LOG = LogFactory.getLog(ThriftHttpServlet.class.getName());
        RAN = new Random();
    }
    
    class HttpKerberosServerAction implements PrivilegedExceptionAction<String>
    {
        HttpServletRequest request;
        UserGroupInformation serviceUGI;
        
        HttpKerberosServerAction(final HttpServletRequest request, final UserGroupInformation serviceUGI) {
            this.request = request;
            this.serviceUGI = serviceUGI;
        }
        
        @Override
        public String run() throws HttpAuthenticationException {
            final GSSManager manager = GSSManager.getInstance();
            GSSContext gssContext = null;
            final String serverPrincipal = this.getPrincipalWithoutRealm(this.serviceUGI.getUserName());
            try {
                final Oid kerberosMechOid = new Oid("1.2.840.113554.1.2.2");
                final Oid spnegoMechOid = new Oid("1.3.6.1.5.5.2");
                final Oid krb5PrincipalOid = new Oid("1.2.840.113554.1.2.2.1");
                final GSSName serverName = manager.createName(serverPrincipal, krb5PrincipalOid);
                final GSSCredential serverCreds = manager.createCredential(serverName, 0, new Oid[] { kerberosMechOid, spnegoMechOid }, 2);
                gssContext = manager.createContext(serverCreds);
                final String serviceTicketBase64 = ThriftHttpServlet.this.getAuthHeader(this.request, ThriftHttpServlet.this.authType);
                final byte[] inToken = Base64.decodeBase64(serviceTicketBase64.getBytes());
                gssContext.acceptSecContext(inToken, 0, inToken.length);
                if (!gssContext.isEstablished()) {
                    throw new HttpAuthenticationException("Kerberos authentication failed: unable to establish context with the service ticket provided by the client.");
                }
                return this.getPrincipalWithoutRealmAndHost(gssContext.getSrcName().toString());
            }
            catch (GSSException e) {
                throw new HttpAuthenticationException("Kerberos authentication failed: ", e);
            }
            finally {
                if (gssContext != null) {
                    try {
                        gssContext.dispose();
                    }
                    catch (GSSException ex) {}
                }
            }
        }
        
        private String getPrincipalWithoutRealm(final String fullPrincipal) throws HttpAuthenticationException {
            HadoopShims.KerberosNameShim fullKerberosName;
            try {
                fullKerberosName = ShimLoader.getHadoopShims().getKerberosNameShim(fullPrincipal);
            }
            catch (IOException e) {
                throw new HttpAuthenticationException(e);
            }
            final String serviceName = fullKerberosName.getServiceName();
            final String hostName = fullKerberosName.getHostName();
            String principalWithoutRealm = serviceName;
            if (hostName != null) {
                principalWithoutRealm = serviceName + "/" + hostName;
            }
            return principalWithoutRealm;
        }
        
        private String getPrincipalWithoutRealmAndHost(final String fullPrincipal) throws HttpAuthenticationException {
            try {
                final HadoopShims.KerberosNameShim fullKerberosName = ShimLoader.getHadoopShims().getKerberosNameShim(fullPrincipal);
                return fullKerberosName.getShortName();
            }
            catch (IOException e) {
                throw new HttpAuthenticationException(e);
            }
        }
    }
}
