// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security.authentication;

import org.eclipse.jetty.util.security.CertificateUtils;
import org.eclipse.jetty.util.resource.Resource;
import java.io.InputStream;
import org.eclipse.jetty.server.UserIdentity;
import java.security.Principal;
import java.security.cert.CRL;
import java.util.Collection;
import java.security.KeyStore;
import org.eclipse.jetty.security.ServerAuthException;
import org.eclipse.jetty.security.UserAuthentication;
import org.eclipse.jetty.util.B64Code;
import java.security.cert.Certificate;
import org.eclipse.jetty.util.security.CertificateValidator;
import java.security.cert.X509Certificate;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Authentication;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import org.eclipse.jetty.util.security.Password;

public class ClientCertAuthenticator extends LoginAuthenticator
{
    private static final String PASSWORD_PROPERTY = "org.eclipse.jetty.ssl.password";
    private String _trustStorePath;
    private String _trustStoreProvider;
    private String _trustStoreType;
    private transient Password _trustStorePassword;
    private boolean _validateCerts;
    private String _crlPath;
    private int _maxCertPathLength;
    private boolean _enableCRLDP;
    private boolean _enableOCSP;
    private String _ocspResponderURL;
    
    public ClientCertAuthenticator() {
        this._trustStoreType = "JKS";
        this._maxCertPathLength = -1;
        this._enableCRLDP = false;
        this._enableOCSP = false;
    }
    
    @Override
    public String getAuthMethod() {
        return "CLIENT_CERT";
    }
    
    @Override
    public Authentication validateRequest(final ServletRequest req, final ServletResponse res, final boolean mandatory) throws ServerAuthException {
        if (!mandatory) {
            return new DeferredAuthentication(this);
        }
        final HttpServletRequest request = (HttpServletRequest)req;
        final HttpServletResponse response = (HttpServletResponse)res;
        final X509Certificate[] certs = (X509Certificate[])request.getAttribute("javax.servlet.request.X509Certificate");
        try {
            if (certs != null && certs.length > 0) {
                if (this._validateCerts) {
                    final KeyStore trustStore = this.getKeyStore(this._trustStorePath, this._trustStoreType, this._trustStoreProvider, (this._trustStorePassword == null) ? null : this._trustStorePassword.toString());
                    final Collection<? extends CRL> crls = this.loadCRL(this._crlPath);
                    final CertificateValidator validator = new CertificateValidator(trustStore, crls);
                    validator.validate(certs);
                }
                for (final X509Certificate cert : certs) {
                    if (cert != null) {
                        Principal principal = cert.getSubjectDN();
                        if (principal == null) {
                            principal = cert.getIssuerDN();
                        }
                        final String username = (principal == null) ? "clientcert" : principal.getName();
                        final char[] credential = B64Code.encode(cert.getSignature());
                        final UserIdentity user = this.login(username, credential, req);
                        if (user != null) {
                            return new UserAuthentication(this.getAuthMethod(), user);
                        }
                    }
                }
            }
            if (!DeferredAuthentication.isDeferred(response)) {
                response.sendError(403);
                return Authentication.SEND_FAILURE;
            }
            return Authentication.UNAUTHENTICATED;
        }
        catch (Exception e) {
            throw new ServerAuthException(e.getMessage());
        }
    }
    
    @Deprecated
    protected KeyStore getKeyStore(final InputStream storeStream, final String storePath, final String storeType, final String storeProvider, final String storePassword) throws Exception {
        return this.getKeyStore(storePath, storeType, storeProvider, storePassword);
    }
    
    protected KeyStore getKeyStore(final String storePath, final String storeType, final String storeProvider, final String storePassword) throws Exception {
        return CertificateUtils.getKeyStore(Resource.newResource(storePath), storeType, storeProvider, storePassword);
    }
    
    protected Collection<? extends CRL> loadCRL(final String crlPath) throws Exception {
        return CertificateUtils.loadCRL(crlPath);
    }
    
    @Override
    public boolean secureResponse(final ServletRequest req, final ServletResponse res, final boolean mandatory, final Authentication.User validatedUser) throws ServerAuthException {
        return true;
    }
    
    public boolean isValidateCerts() {
        return this._validateCerts;
    }
    
    public void setValidateCerts(final boolean validateCerts) {
        this._validateCerts = validateCerts;
    }
    
    public String getTrustStore() {
        return this._trustStorePath;
    }
    
    public void setTrustStore(final String trustStorePath) {
        this._trustStorePath = trustStorePath;
    }
    
    public String getTrustStoreProvider() {
        return this._trustStoreProvider;
    }
    
    public void setTrustStoreProvider(final String trustStoreProvider) {
        this._trustStoreProvider = trustStoreProvider;
    }
    
    public String getTrustStoreType() {
        return this._trustStoreType;
    }
    
    public void setTrustStoreType(final String trustStoreType) {
        this._trustStoreType = trustStoreType;
    }
    
    public void setTrustStorePassword(final String password) {
        this._trustStorePassword = Password.getPassword("org.eclipse.jetty.ssl.password", password, null);
    }
    
    public String getCrlPath() {
        return this._crlPath;
    }
    
    public void setCrlPath(final String crlPath) {
        this._crlPath = crlPath;
    }
    
    public int getMaxCertPathLength() {
        return this._maxCertPathLength;
    }
    
    public void setMaxCertPathLength(final int maxCertPathLength) {
        this._maxCertPathLength = maxCertPathLength;
    }
    
    public boolean isEnableCRLDP() {
        return this._enableCRLDP;
    }
    
    public void setEnableCRLDP(final boolean enableCRLDP) {
        this._enableCRLDP = enableCRLDP;
    }
    
    public boolean isEnableOCSP() {
        return this._enableOCSP;
    }
    
    public void setEnableOCSP(final boolean enableOCSP) {
        this._enableOCSP = enableOCSP;
    }
    
    public String getOcspResponderURL() {
        return this._ocspResponderURL;
    }
    
    public void setOcspResponderURL(final String ocspResponderURL) {
        this._ocspResponderURL = ocspResponderURL;
    }
}
