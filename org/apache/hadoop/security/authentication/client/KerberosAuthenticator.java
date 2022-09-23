// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.client;

import java.util.HashMap;
import org.apache.hadoop.util.PlatformName;
import java.util.Map;
import javax.security.auth.login.AppConfigurationEntry;
import org.slf4j.LoggerFactory;
import java.security.AccessControlContext;
import javax.security.auth.login.LoginException;
import java.security.PrivilegedActionException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSManager;
import java.security.PrivilegedExceptionAction;
import javax.security.auth.login.Configuration;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import org.apache.hadoop.security.authentication.util.KerberosUtil;
import javax.security.auth.Subject;
import java.security.AccessController;
import org.apache.hadoop.security.authentication.util.AuthToken;
import com.google.common.annotations.VisibleForTesting;
import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import java.net.URL;
import org.slf4j.Logger;

public class KerberosAuthenticator implements Authenticator
{
    private static Logger LOG;
    public static final String WWW_AUTHENTICATE = "WWW-Authenticate";
    public static final String AUTHORIZATION = "Authorization";
    public static final String NEGOTIATE = "Negotiate";
    private static final String AUTH_HTTP_METHOD = "OPTIONS";
    private URL url;
    private Base64 base64;
    private ConnectionConfigurator connConfigurator;
    
    @Override
    public void setConnectionConfigurator(final ConnectionConfigurator configurator) {
        this.connConfigurator = configurator;
    }
    
    @Override
    public void authenticate(final URL url, final AuthenticatedURL.Token token) throws IOException, AuthenticationException {
        if (!token.isSet()) {
            this.url = url;
            this.base64 = new Base64(0);
            try {
                final HttpURLConnection conn = token.openConnection(url, this.connConfigurator);
                conn.setRequestMethod("OPTIONS");
                conn.connect();
                boolean needFallback = false;
                if (conn.getResponseCode() == 200) {
                    KerberosAuthenticator.LOG.debug("JDK performed authentication on our behalf.");
                    AuthenticatedURL.extractToken(conn, token);
                    if (this.isTokenKerberos(token)) {
                        return;
                    }
                    needFallback = true;
                }
                if (!needFallback && this.isNegotiate(conn)) {
                    KerberosAuthenticator.LOG.debug("Performing our own SPNEGO sequence.");
                    this.doSpnegoSequence(token);
                }
                else {
                    KerberosAuthenticator.LOG.debug("Using fallback authenticator sequence.");
                    final Authenticator auth = this.getFallBackAuthenticator();
                    auth.setConnectionConfigurator(this.connConfigurator);
                    auth.authenticate(url, token);
                }
            }
            catch (IOException ex) {
                throw wrapExceptionWithMessage(ex, "Error while authenticating with endpoint: " + url);
            }
            catch (AuthenticationException ex2) {
                throw wrapExceptionWithMessage(ex2, "Error while authenticating with endpoint: " + url);
            }
        }
    }
    
    @VisibleForTesting
    static <T extends Exception> T wrapExceptionWithMessage(final T exception, final String msg) {
        final Class<? extends Throwable> exceptionClass = exception.getClass();
        try {
            final Constructor<? extends Throwable> ctor = exceptionClass.getConstructor(String.class);
            final Throwable t = (Throwable)ctor.newInstance(msg);
            return (T)t.initCause(exception);
        }
        catch (Throwable e) {
            KerberosAuthenticator.LOG.debug("Unable to wrap exception of type {}, it has no (String) constructor.", exceptionClass, e);
            return exception;
        }
    }
    
    protected Authenticator getFallBackAuthenticator() {
        final Authenticator auth = new PseudoAuthenticator();
        if (this.connConfigurator != null) {
            auth.setConnectionConfigurator(this.connConfigurator);
        }
        return auth;
    }
    
    private boolean isTokenKerberos(final AuthenticatedURL.Token token) throws AuthenticationException {
        if (token.isSet()) {
            final AuthToken aToken = AuthToken.parse(token.toString());
            if (aToken.getType().equals("kerberos") || aToken.getType().equals("kerberos-dt")) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isNegotiate(final HttpURLConnection conn) throws IOException {
        boolean negotiate = false;
        if (conn.getResponseCode() == 401) {
            final String authHeader = conn.getHeaderField("WWW-Authenticate");
            negotiate = (authHeader != null && authHeader.trim().startsWith("Negotiate"));
        }
        return negotiate;
    }
    
    private void doSpnegoSequence(final AuthenticatedURL.Token token) throws IOException, AuthenticationException {
        try {
            final AccessControlContext context = AccessController.getContext();
            Subject subject = Subject.getSubject(context);
            if (subject == null || (!KerberosUtil.hasKerberosKeyTab(subject) && !KerberosUtil.hasKerberosTicket(subject))) {
                KerberosAuthenticator.LOG.debug("No subject in context, logging in");
                subject = new Subject();
                final LoginContext login = new LoginContext("", subject, null, new KerberosConfiguration());
                login.login();
            }
            if (KerberosAuthenticator.LOG.isDebugEnabled()) {
                KerberosAuthenticator.LOG.debug("Using subject: " + subject);
            }
            Subject.doAs(subject, (PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    GSSContext gssContext = null;
                    try {
                        final GSSManager gssManager = GSSManager.getInstance();
                        final String servicePrincipal = KerberosUtil.getServicePrincipal("HTTP", KerberosAuthenticator.this.url.getHost());
                        Oid oid = KerberosUtil.NT_GSS_KRB5_PRINCIPAL_OID;
                        final GSSName serviceName = gssManager.createName(servicePrincipal, oid);
                        oid = KerberosUtil.GSS_KRB5_MECH_OID;
                        gssContext = gssManager.createContext(serviceName, oid, null, 0);
                        gssContext.requestCredDeleg(true);
                        gssContext.requestMutualAuth(true);
                        byte[] inToken = new byte[0];
                        boolean established = false;
                        while (!established) {
                            final HttpURLConnection conn = token.openConnection(KerberosAuthenticator.this.url, KerberosAuthenticator.this.connConfigurator);
                            final byte[] outToken = gssContext.initSecContext(inToken, 0, inToken.length);
                            if (outToken != null) {
                                KerberosAuthenticator.this.sendToken(conn, outToken);
                            }
                            if (!gssContext.isEstablished()) {
                                inToken = KerberosAuthenticator.this.readToken(conn);
                            }
                            else {
                                established = true;
                            }
                        }
                    }
                    finally {
                        if (gssContext != null) {
                            gssContext.dispose();
                            gssContext = null;
                        }
                    }
                    return null;
                }
            });
        }
        catch (PrivilegedActionException ex) {
            if (ex.getException() instanceof IOException) {
                throw (IOException)ex.getException();
            }
            throw new AuthenticationException(ex.getException());
        }
        catch (LoginException ex2) {
            throw new AuthenticationException(ex2);
        }
    }
    
    private void sendToken(final HttpURLConnection conn, final byte[] outToken) throws IOException {
        final String token = this.base64.encodeToString(outToken);
        conn.setRequestMethod("OPTIONS");
        conn.setRequestProperty("Authorization", "Negotiate " + token);
        conn.connect();
    }
    
    private byte[] readToken(final HttpURLConnection conn) throws IOException, AuthenticationException {
        final int status = conn.getResponseCode();
        if (status != 200 && status != 401) {
            throw new AuthenticationException("Invalid SPNEGO sequence, status code: " + status);
        }
        final String authHeader = conn.getHeaderField("WWW-Authenticate");
        if (authHeader == null || !authHeader.trim().startsWith("Negotiate")) {
            throw new AuthenticationException("Invalid SPNEGO sequence, 'WWW-Authenticate' header incorrect: " + authHeader);
        }
        final String negotiation = authHeader.trim().substring("Negotiate ".length()).trim();
        return this.base64.decode(negotiation);
    }
    
    static {
        KerberosAuthenticator.LOG = LoggerFactory.getLogger(KerberosAuthenticator.class);
    }
    
    private static class KerberosConfiguration extends Configuration
    {
        private static final String OS_LOGIN_MODULE_NAME;
        private static final boolean windows;
        private static final boolean is64Bit;
        private static final boolean aix;
        private static final AppConfigurationEntry OS_SPECIFIC_LOGIN;
        private static final Map<String, String> USER_KERBEROS_OPTIONS;
        private static final AppConfigurationEntry USER_KERBEROS_LOGIN;
        private static final AppConfigurationEntry[] USER_KERBEROS_CONF;
        
        private static String getOSLoginModuleName() {
            if (!PlatformName.IBM_JAVA) {
                return KerberosConfiguration.windows ? "com.sun.security.auth.module.NTLoginModule" : "com.sun.security.auth.module.UnixLoginModule";
            }
            if (KerberosConfiguration.windows) {
                return KerberosConfiguration.is64Bit ? "com.ibm.security.auth.module.Win64LoginModule" : "com.ibm.security.auth.module.NTLoginModule";
            }
            if (KerberosConfiguration.aix) {
                return KerberosConfiguration.is64Bit ? "com.ibm.security.auth.module.AIX64LoginModule" : "com.ibm.security.auth.module.AIXLoginModule";
            }
            return "com.ibm.security.auth.module.LinuxLoginModule";
        }
        
        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(final String appName) {
            return KerberosConfiguration.USER_KERBEROS_CONF;
        }
        
        static {
            windows = System.getProperty("os.name").startsWith("Windows");
            is64Bit = System.getProperty("os.arch").contains("64");
            aix = System.getProperty("os.name").equals("AIX");
            OS_LOGIN_MODULE_NAME = getOSLoginModuleName();
            OS_SPECIFIC_LOGIN = new AppConfigurationEntry(KerberosConfiguration.OS_LOGIN_MODULE_NAME, AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, new HashMap<String, Object>());
            USER_KERBEROS_OPTIONS = new HashMap<String, String>();
            final String ticketCache = System.getenv("KRB5CCNAME");
            if (PlatformName.IBM_JAVA) {
                KerberosConfiguration.USER_KERBEROS_OPTIONS.put("useDefaultCcache", "true");
            }
            else {
                KerberosConfiguration.USER_KERBEROS_OPTIONS.put("doNotPrompt", "true");
                KerberosConfiguration.USER_KERBEROS_OPTIONS.put("useTicketCache", "true");
            }
            if (ticketCache != null) {
                if (PlatformName.IBM_JAVA) {
                    System.setProperty("KRB5CCNAME", ticketCache);
                }
                else {
                    KerberosConfiguration.USER_KERBEROS_OPTIONS.put("ticketCache", ticketCache);
                }
            }
            KerberosConfiguration.USER_KERBEROS_OPTIONS.put("renewTGT", "true");
            USER_KERBEROS_LOGIN = new AppConfigurationEntry(KerberosUtil.getKrb5LoginModuleName(), AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL, KerberosConfiguration.USER_KERBEROS_OPTIONS);
            USER_KERBEROS_CONF = new AppConfigurationEntry[] { KerberosConfiguration.OS_SPECIFIC_LOGIN, KerberosConfiguration.USER_KERBEROS_LOGIN };
        }
    }
}
