// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.server;

import org.slf4j.LoggerFactory;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.Oid;
import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.security.authentication.client.AuthenticationException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.security.authentication.util.KerberosName;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KeyTab;
import org.apache.hadoop.security.authentication.util.KerberosUtil;
import java.util.regex.Pattern;
import java.io.File;
import javax.servlet.ServletException;
import java.util.Properties;
import javax.security.auth.Subject;
import org.ietf.jgss.GSSManager;
import org.slf4j.Logger;

public class KerberosAuthenticationHandler implements AuthenticationHandler
{
    public static final Logger LOG;
    public static final String TYPE = "kerberos";
    public static final String PRINCIPAL = "kerberos.principal";
    public static final String KEYTAB = "kerberos.keytab";
    public static final String NAME_RULES = "kerberos.name.rules";
    public static final String RULE_MECHANISM = "kerberos.name.rules.mechanism";
    private String type;
    private String keytab;
    private GSSManager gssManager;
    private Subject serverSubject;
    
    public KerberosAuthenticationHandler() {
        this("kerberos");
    }
    
    public KerberosAuthenticationHandler(final String type) {
        this.serverSubject = new Subject();
        this.type = type;
    }
    
    @Override
    public void init(final Properties config) throws ServletException {
        try {
            final String principal = config.getProperty("kerberos.principal");
            if (principal == null || principal.trim().length() == 0) {
                throw new ServletException("Principal not defined in configuration");
            }
            this.keytab = config.getProperty("kerberos.keytab", this.keytab);
            if (this.keytab == null || this.keytab.trim().length() == 0) {
                throw new ServletException("Keytab not defined in configuration");
            }
            final File keytabFile = new File(this.keytab);
            if (!keytabFile.exists()) {
                throw new ServletException("Keytab does not exist: " + this.keytab);
            }
            String[] spnegoPrincipals;
            if (principal.equals("*")) {
                spnegoPrincipals = KerberosUtil.getPrincipalNames(this.keytab, Pattern.compile("HTTP/.*"));
                if (spnegoPrincipals.length == 0) {
                    throw new ServletException("Principals do not exist in the keytab");
                }
            }
            else {
                spnegoPrincipals = new String[] { principal };
            }
            final KeyTab keytabInstance = KeyTab.getInstance(keytabFile);
            this.serverSubject.getPrivateCredentials().add(keytabInstance);
            for (final String spnegoPrincipal : spnegoPrincipals) {
                final Principal krbPrincipal = new KerberosPrincipal(spnegoPrincipal);
                KerberosAuthenticationHandler.LOG.info("Using keytab {}, for principal {}", this.keytab, krbPrincipal);
                this.serverSubject.getPrincipals().add(krbPrincipal);
            }
            final String nameRules = config.getProperty("kerberos.name.rules", null);
            if (nameRules != null) {
                KerberosName.setRules(nameRules);
            }
            final String ruleMechanism = config.getProperty("kerberos.name.rules.mechanism", null);
            if (ruleMechanism != null) {
                KerberosName.setRuleMechanism(ruleMechanism);
            }
            try {
                this.gssManager = Subject.doAs(this.serverSubject, (PrivilegedExceptionAction<GSSManager>)new PrivilegedExceptionAction<GSSManager>() {
                    @Override
                    public GSSManager run() throws Exception {
                        return GSSManager.getInstance();
                    }
                });
            }
            catch (PrivilegedActionException ex) {
                throw ex.getException();
            }
        }
        catch (Exception ex2) {
            throw new ServletException(ex2);
        }
    }
    
    @Override
    public void destroy() {
        this.keytab = null;
        this.serverSubject = null;
    }
    
    @Override
    public String getType() {
        return this.type;
    }
    
    protected Set<KerberosPrincipal> getPrincipals() {
        return this.serverSubject.getPrincipals(KerberosPrincipal.class);
    }
    
    protected String getKeytab() {
        return this.keytab;
    }
    
    @Override
    public boolean managementOperation(final AuthenticationToken token, final HttpServletRequest request, final HttpServletResponse response) throws IOException, AuthenticationException {
        return true;
    }
    
    @Override
    public AuthenticationToken authenticate(final HttpServletRequest request, final HttpServletResponse response) throws IOException, AuthenticationException {
        AuthenticationToken token = null;
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Negotiate")) {
            response.setHeader("WWW-Authenticate", "Negotiate");
            response.setStatus(401);
            if (authorization == null) {
                KerberosAuthenticationHandler.LOG.trace("SPNEGO starting for url: {}", request.getRequestURL());
            }
            else {
                KerberosAuthenticationHandler.LOG.warn("'Authorization' does not start with 'Negotiate' :  {}", authorization);
            }
        }
        else {
            authorization = authorization.substring("Negotiate".length()).trim();
            final Base64 base64 = new Base64(0);
            final byte[] clientToken = base64.decode(authorization);
            try {
                final String serverPrincipal = KerberosUtil.getTokenServerName(clientToken);
                if (!serverPrincipal.startsWith("HTTP/")) {
                    throw new IllegalArgumentException("Invalid server principal " + serverPrincipal + "decoded from client request");
                }
                token = Subject.doAs(this.serverSubject, (PrivilegedExceptionAction<AuthenticationToken>)new PrivilegedExceptionAction<AuthenticationToken>() {
                    @Override
                    public AuthenticationToken run() throws Exception {
                        return KerberosAuthenticationHandler.this.runWithPrincipal(serverPrincipal, clientToken, base64, response);
                    }
                });
            }
            catch (PrivilegedActionException ex) {
                if (ex.getException() instanceof IOException) {
                    throw (IOException)ex.getException();
                }
                throw new AuthenticationException(ex.getException());
            }
            catch (Exception ex2) {
                throw new AuthenticationException(ex2);
            }
        }
        return token;
    }
    
    private AuthenticationToken runWithPrincipal(final String serverPrincipal, final byte[] clientToken, final Base64 base64, final HttpServletResponse response) throws IOException, GSSException {
        GSSContext gssContext = null;
        GSSCredential gssCreds = null;
        AuthenticationToken token = null;
        try {
            KerberosAuthenticationHandler.LOG.trace("SPNEGO initiated with server principal [{}]", serverPrincipal);
            gssCreds = this.gssManager.createCredential(this.gssManager.createName(serverPrincipal, KerberosUtil.NT_GSS_KRB5_PRINCIPAL_OID), Integer.MAX_VALUE, new Oid[] { KerberosUtil.GSS_SPNEGO_MECH_OID, KerberosUtil.GSS_KRB5_MECH_OID }, 2);
            gssContext = this.gssManager.createContext(gssCreds);
            final byte[] serverToken = gssContext.acceptSecContext(clientToken, 0, clientToken.length);
            if (serverToken != null && serverToken.length > 0) {
                final String authenticate = base64.encodeToString(serverToken);
                response.setHeader("WWW-Authenticate", "Negotiate " + authenticate);
            }
            if (!gssContext.isEstablished()) {
                response.setStatus(401);
                KerberosAuthenticationHandler.LOG.trace("SPNEGO in progress");
            }
            else {
                final String clientPrincipal = gssContext.getSrcName().toString();
                final KerberosName kerberosName = new KerberosName(clientPrincipal);
                final String userName = kerberosName.getShortName();
                token = new AuthenticationToken(userName, clientPrincipal, this.getType());
                response.setStatus(200);
                KerberosAuthenticationHandler.LOG.trace("SPNEGO completed for client principal [{}]", clientPrincipal);
            }
        }
        finally {
            if (gssContext != null) {
                gssContext.dispose();
            }
            if (gssCreds != null) {
                gssCreds.dispose();
            }
        }
        return token;
    }
    
    static {
        LOG = LoggerFactory.getLogger(KerberosAuthenticationHandler.class);
    }
}
