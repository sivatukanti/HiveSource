// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.ssl;

import org.eclipse.jetty.util.StringUtil;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import org.eclipse.jetty.util.log.Log;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import javax.net.ssl.SSLSession;
import java.util.function.Consumer;
import javax.net.ssl.SNIMatcher;
import java.net.InetSocketAddress;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.net.InetAddress;
import javax.net.ssl.SSLServerSocket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.ManagerFactoryParameters;
import java.security.cert.CertPathParameters;
import javax.net.ssl.CertPathTrustManagerParameters;
import javax.net.ssl.TrustManagerFactory;
import java.security.Security;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.CertSelector;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.X509CertSelector;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.KeyManagerFactory;
import org.eclipse.jetty.util.security.CertificateUtils;
import java.net.MalformedURLException;
import java.io.IOException;
import javax.net.ssl.SSLEngine;
import java.security.NoSuchAlgorithmException;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSessionContext;
import java.util.Iterator;
import java.security.cert.CRL;
import java.util.Collection;
import java.util.Arrays;
import java.security.cert.Certificate;
import org.eclipse.jetty.util.security.CertificateValidator;
import java.security.cert.X509Certificate;
import java.util.Collections;
import javax.net.ssl.KeyManager;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import org.eclipse.jetty.util.security.Password;
import org.eclipse.jetty.util.resource.Resource;
import java.util.Comparator;
import java.util.Map;
import java.util.List;
import java.util.Set;
import org.eclipse.jetty.util.log.Logger;
import javax.net.ssl.TrustManager;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public class SslContextFactory extends AbstractLifeCycle implements Dumpable
{
    public static final TrustManager[] TRUST_ALL_CERTS;
    private static final Logger LOG;
    public static final String DEFAULT_KEYMANAGERFACTORY_ALGORITHM;
    public static final String DEFAULT_TRUSTMANAGERFACTORY_ALGORITHM;
    public static final String KEYPASSWORD_PROPERTY = "org.eclipse.jetty.ssl.keypassword";
    public static final String PASSWORD_PROPERTY = "org.eclipse.jetty.ssl.password";
    private final Set<String> _excludeProtocols;
    private final Set<String> _includeProtocols;
    private final Set<String> _excludeCipherSuites;
    private final List<String> _includeCipherSuites;
    private final Map<String, X509> _aliasX509;
    private final Map<String, X509> _certHosts;
    private final Map<String, X509> _certWilds;
    private String[] _selectedProtocols;
    private boolean _useCipherSuitesOrder;
    private Comparator<String> _cipherComparator;
    private String[] _selectedCipherSuites;
    private Resource _keyStoreResource;
    private String _keyStoreProvider;
    private String _keyStoreType;
    private String _certAlias;
    private Resource _trustStoreResource;
    private String _trustStoreProvider;
    private String _trustStoreType;
    private boolean _needClientAuth;
    private boolean _wantClientAuth;
    private Password _keyStorePassword;
    private Password _keyManagerPassword;
    private Password _trustStorePassword;
    private String _sslProvider;
    private String _sslProtocol;
    private String _secureRandomAlgorithm;
    private String _keyManagerFactoryAlgorithm;
    private String _trustManagerFactoryAlgorithm;
    private boolean _validateCerts;
    private boolean _validatePeerCerts;
    private int _maxCertPathLength;
    private String _crlPath;
    private boolean _enableCRLDP;
    private boolean _enableOCSP;
    private String _ocspResponderURL;
    private KeyStore _setKeyStore;
    private KeyStore _setTrustStore;
    private boolean _sessionCachingEnabled;
    private int _sslSessionCacheSize;
    private int _sslSessionTimeout;
    private SSLContext _setContext;
    private String _endpointIdentificationAlgorithm;
    private boolean _trustAll;
    private boolean _renegotiationAllowed;
    private int _renegotiationLimit;
    private Factory _factory;
    
    public SslContextFactory() {
        this(false);
    }
    
    public SslContextFactory(final boolean trustAll) {
        this(trustAll, null);
    }
    
    public SslContextFactory(final String keyStorePath) {
        this(false, keyStorePath);
    }
    
    private SslContextFactory(final boolean trustAll, final String keyStorePath) {
        this._excludeProtocols = new LinkedHashSet<String>();
        this._includeProtocols = new LinkedHashSet<String>();
        this._excludeCipherSuites = new LinkedHashSet<String>();
        this._includeCipherSuites = new ArrayList<String>();
        this._aliasX509 = new HashMap<String, X509>();
        this._certHosts = new HashMap<String, X509>();
        this._certWilds = new HashMap<String, X509>();
        this._useCipherSuitesOrder = true;
        this._keyStoreType = "JKS";
        this._trustStoreType = "JKS";
        this._needClientAuth = false;
        this._wantClientAuth = false;
        this._sslProtocol = "TLS";
        this._keyManagerFactoryAlgorithm = SslContextFactory.DEFAULT_KEYMANAGERFACTORY_ALGORITHM;
        this._trustManagerFactoryAlgorithm = SslContextFactory.DEFAULT_TRUSTMANAGERFACTORY_ALGORITHM;
        this._maxCertPathLength = -1;
        this._enableCRLDP = false;
        this._enableOCSP = false;
        this._sessionCachingEnabled = true;
        this._sslSessionCacheSize = -1;
        this._sslSessionTimeout = -1;
        this._endpointIdentificationAlgorithm = null;
        this._renegotiationAllowed = true;
        this._renegotiationLimit = 5;
        this.setTrustAll(trustAll);
        this.addExcludeProtocols("SSL", "SSLv2", "SSLv2Hello", "SSLv3");
        this.setExcludeCipherSuites("^.*_(MD5|SHA|SHA1)$");
        if (keyStorePath != null) {
            this.setKeyStorePath(keyStorePath);
        }
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
        synchronized (this) {
            this.load();
        }
    }
    
    private void load() throws Exception {
        SSLContext context = this._setContext;
        KeyStore keyStore = this._setKeyStore;
        KeyStore trustStore = this._setTrustStore;
        if (context == null) {
            if (keyStore == null && this._keyStoreResource == null && trustStore == null && this._trustStoreResource == null) {
                TrustManager[] trust_managers = null;
                if (this.isTrustAll()) {
                    if (SslContextFactory.LOG.isDebugEnabled()) {
                        SslContextFactory.LOG.debug("No keystore or trust store configured.  ACCEPTING UNTRUSTED CERTIFICATES!!!!!", new Object[0]);
                    }
                    trust_managers = SslContextFactory.TRUST_ALL_CERTS;
                }
                final String algorithm = this.getSecureRandomAlgorithm();
                final SecureRandom secureRandom = (algorithm == null) ? null : SecureRandom.getInstance(algorithm);
                context = ((this._sslProvider == null) ? SSLContext.getInstance(this._sslProtocol) : SSLContext.getInstance(this._sslProtocol, this._sslProvider));
                context.init(null, trust_managers, secureRandom);
            }
            else {
                if (keyStore == null) {
                    keyStore = this.loadKeyStore(this._keyStoreResource);
                }
                if (trustStore == null) {
                    trustStore = this.loadTrustStore(this._trustStoreResource);
                }
                final Collection<? extends CRL> crls = this.loadCRL(this.getCrlPath());
                if (keyStore != null) {
                    for (final String alias : Collections.list(keyStore.aliases())) {
                        final Certificate certificate = keyStore.getCertificate(alias);
                        if (certificate != null && "X.509".equals(certificate.getType())) {
                            final X509Certificate x509C = (X509Certificate)certificate;
                            if (X509.isCertSign(x509C)) {
                                if (!SslContextFactory.LOG.isDebugEnabled()) {
                                    continue;
                                }
                                SslContextFactory.LOG.debug("Skipping " + x509C, new Object[0]);
                            }
                            else {
                                final X509 x509 = new X509(alias, x509C);
                                this._aliasX509.put(alias, x509);
                                if (this.isValidateCerts()) {
                                    final CertificateValidator validator = new CertificateValidator(trustStore, crls);
                                    validator.setMaxCertPathLength(this.getMaxCertPathLength());
                                    validator.setEnableCRLDP(this.isEnableCRLDP());
                                    validator.setEnableOCSP(this.isEnableOCSP());
                                    validator.setOcspResponderURL(this.getOcspResponderURL());
                                    validator.validate(keyStore, x509C);
                                }
                                SslContextFactory.LOG.info("x509={} for {}", x509, this);
                                for (final String h : x509.getHosts()) {
                                    this._certHosts.put(h, x509);
                                }
                                for (final String w : x509.getWilds()) {
                                    this._certWilds.put(w, x509);
                                }
                            }
                        }
                    }
                }
                final KeyManager[] keyManagers = this.getKeyManagers(keyStore);
                final TrustManager[] trustManagers = this.getTrustManagers(trustStore, crls);
                final SecureRandom secureRandom2 = (this._secureRandomAlgorithm == null) ? null : SecureRandom.getInstance(this._secureRandomAlgorithm);
                context = ((this._sslProvider == null) ? SSLContext.getInstance(this._sslProtocol) : SSLContext.getInstance(this._sslProtocol, this._sslProvider));
                context.init(keyManagers, trustManagers, secureRandom2);
            }
        }
        final SSLSessionContext serverContext = context.getServerSessionContext();
        if (serverContext != null) {
            if (this.getSslSessionCacheSize() > -1) {
                serverContext.setSessionCacheSize(this.getSslSessionCacheSize());
            }
            if (this.getSslSessionTimeout() > -1) {
                serverContext.setSessionTimeout(this.getSslSessionTimeout());
            }
        }
        final SSLParameters enabled = context.getDefaultSSLParameters();
        final SSLParameters supported = context.getSupportedSSLParameters();
        this.selectCipherSuites(enabled.getCipherSuites(), supported.getCipherSuites());
        this.selectProtocols(enabled.getProtocols(), supported.getProtocols());
        this._factory = new Factory(keyStore, trustStore, context);
        if (SslContextFactory.LOG.isDebugEnabled()) {
            SslContextFactory.LOG.debug("Selected Protocols {} of {}", Arrays.asList(this._selectedProtocols), Arrays.asList(supported.getProtocols()));
            SslContextFactory.LOG.debug("Selected Ciphers   {} of {}", Arrays.asList(this._selectedCipherSuites), Arrays.asList(supported.getCipherSuites()));
        }
    }
    
    @Override
    public String dump() {
        return ContainerLifeCycle.dump(this);
    }
    
    @Override
    public void dump(final Appendable out, final String indent) throws IOException {
        out.append(String.valueOf(this)).append(" trustAll=").append(Boolean.toString(this._trustAll)).append(System.lineSeparator());
        try {
            final SSLEngine sslEngine = SSLContext.getDefault().createSSLEngine();
            final List<Object> selections = new ArrayList<Object>();
            selections.add(new SslSelectionDump("Protocol", sslEngine.getSupportedProtocols(), sslEngine.getEnabledProtocols(), this.getExcludeProtocols(), this.getIncludeProtocols()));
            selections.add(new SslSelectionDump("Cipher Suite", sslEngine.getSupportedCipherSuites(), sslEngine.getEnabledCipherSuites(), this.getExcludeCipherSuites(), this.getIncludeCipherSuites()));
            ContainerLifeCycle.dump(out, indent, selections);
        }
        catch (NoSuchAlgorithmException ignore) {
            SslContextFactory.LOG.ignore(ignore);
        }
    }
    
    @Override
    protected void doStop() throws Exception {
        synchronized (this) {
            this.unload();
        }
        super.doStop();
    }
    
    private void unload() {
        this._factory = null;
        this._selectedProtocols = null;
        this._selectedCipherSuites = null;
        this._aliasX509.clear();
        this._certHosts.clear();
        this._certWilds.clear();
    }
    
    public String[] getSelectedProtocols() {
        return Arrays.copyOf(this._selectedProtocols, this._selectedProtocols.length);
    }
    
    public String[] getSelectedCipherSuites() {
        return Arrays.copyOf(this._selectedCipherSuites, this._selectedCipherSuites.length);
    }
    
    public Comparator<String> getCipherComparator() {
        return this._cipherComparator;
    }
    
    public void setCipherComparator(final Comparator<String> cipherComparator) {
        if (cipherComparator != null) {
            this.setUseCipherSuitesOrder(true);
        }
        this._cipherComparator = cipherComparator;
    }
    
    public Set<String> getAliases() {
        return Collections.unmodifiableSet((Set<? extends String>)this._aliasX509.keySet());
    }
    
    public X509 getX509(final String alias) {
        return this._aliasX509.get(alias);
    }
    
    public String[] getExcludeProtocols() {
        return this._excludeProtocols.toArray(new String[0]);
    }
    
    public void setExcludeProtocols(final String... protocols) {
        this._excludeProtocols.clear();
        this._excludeProtocols.addAll(Arrays.asList(protocols));
    }
    
    public void addExcludeProtocols(final String... protocol) {
        this._excludeProtocols.addAll(Arrays.asList(protocol));
    }
    
    public String[] getIncludeProtocols() {
        return this._includeProtocols.toArray(new String[0]);
    }
    
    public void setIncludeProtocols(final String... protocols) {
        this._includeProtocols.clear();
        this._includeProtocols.addAll(Arrays.asList(protocols));
    }
    
    public String[] getExcludeCipherSuites() {
        return this._excludeCipherSuites.toArray(new String[0]);
    }
    
    public void setExcludeCipherSuites(final String... cipherSuites) {
        this._excludeCipherSuites.clear();
        this._excludeCipherSuites.addAll(Arrays.asList(cipherSuites));
    }
    
    public void addExcludeCipherSuites(final String... cipher) {
        this._excludeCipherSuites.addAll(Arrays.asList(cipher));
    }
    
    public String[] getIncludeCipherSuites() {
        return this._includeCipherSuites.toArray(new String[0]);
    }
    
    public void setIncludeCipherSuites(final String... cipherSuites) {
        this._includeCipherSuites.clear();
        this._includeCipherSuites.addAll(Arrays.asList(cipherSuites));
    }
    
    public boolean isUseCipherSuitesOrder() {
        return this._useCipherSuitesOrder;
    }
    
    public void setUseCipherSuitesOrder(final boolean useCipherSuitesOrder) {
        this._useCipherSuitesOrder = useCipherSuitesOrder;
    }
    
    public String getKeyStorePath() {
        return this._keyStoreResource.toString();
    }
    
    public void setKeyStorePath(final String keyStorePath) {
        try {
            this._keyStoreResource = Resource.newResource(keyStorePath);
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public String getKeyStoreProvider() {
        return this._keyStoreProvider;
    }
    
    public void setKeyStoreProvider(final String keyStoreProvider) {
        this._keyStoreProvider = keyStoreProvider;
    }
    
    public String getKeyStoreType() {
        return this._keyStoreType;
    }
    
    public void setKeyStoreType(final String keyStoreType) {
        this._keyStoreType = keyStoreType;
    }
    
    public String getCertAlias() {
        return this._certAlias;
    }
    
    public void setCertAlias(final String certAlias) {
        this._certAlias = certAlias;
    }
    
    public void setTrustStorePath(final String trustStorePath) {
        try {
            this._trustStoreResource = Resource.newResource(trustStorePath);
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
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
    
    public boolean getNeedClientAuth() {
        return this._needClientAuth;
    }
    
    public void setNeedClientAuth(final boolean needClientAuth) {
        this._needClientAuth = needClientAuth;
    }
    
    public boolean getWantClientAuth() {
        return this._wantClientAuth;
    }
    
    public void setWantClientAuth(final boolean wantClientAuth) {
        this._wantClientAuth = wantClientAuth;
    }
    
    public boolean isValidateCerts() {
        return this._validateCerts;
    }
    
    public void setValidateCerts(final boolean validateCerts) {
        this._validateCerts = validateCerts;
    }
    
    public boolean isValidatePeerCerts() {
        return this._validatePeerCerts;
    }
    
    public void setValidatePeerCerts(final boolean validatePeerCerts) {
        this._validatePeerCerts = validatePeerCerts;
    }
    
    public void setKeyStorePassword(final String password) {
        if (password == null) {
            if (this._keyStoreResource != null) {
                this._keyStorePassword = Password.getPassword("org.eclipse.jetty.ssl.password", null, null);
            }
            else {
                this._keyStorePassword = null;
            }
        }
        else {
            this._keyStorePassword = new Password(password);
        }
    }
    
    public void setKeyManagerPassword(final String password) {
        if (password == null) {
            if (System.getProperty("org.eclipse.jetty.ssl.keypassword") != null) {
                this._keyManagerPassword = Password.getPassword("org.eclipse.jetty.ssl.keypassword", null, null);
            }
            else {
                this._keyManagerPassword = null;
            }
        }
        else {
            this._keyManagerPassword = new Password(password);
        }
    }
    
    public void setTrustStorePassword(final String password) {
        if (password == null) {
            if (this._trustStoreResource != null && !this._trustStoreResource.equals(this._keyStoreResource)) {
                this._trustStorePassword = Password.getPassword("org.eclipse.jetty.ssl.password", null, null);
            }
            else {
                this._trustStorePassword = null;
            }
        }
        else {
            this._trustStorePassword = new Password(password);
        }
    }
    
    public String getProvider() {
        return this._sslProvider;
    }
    
    public void setProvider(final String provider) {
        this._sslProvider = provider;
    }
    
    public String getProtocol() {
        return this._sslProtocol;
    }
    
    public void setProtocol(final String protocol) {
        this._sslProtocol = protocol;
    }
    
    public String getSecureRandomAlgorithm() {
        return this._secureRandomAlgorithm;
    }
    
    public void setSecureRandomAlgorithm(final String algorithm) {
        this._secureRandomAlgorithm = algorithm;
    }
    
    @Deprecated
    public String getSslKeyManagerFactoryAlgorithm() {
        return this.getKeyManagerFactoryAlgorithm();
    }
    
    @Deprecated
    public void setSslKeyManagerFactoryAlgorithm(final String algorithm) {
        this.setKeyManagerFactoryAlgorithm(algorithm);
    }
    
    public String getKeyManagerFactoryAlgorithm() {
        return this._keyManagerFactoryAlgorithm;
    }
    
    public void setKeyManagerFactoryAlgorithm(final String algorithm) {
        this._keyManagerFactoryAlgorithm = algorithm;
    }
    
    public String getTrustManagerFactoryAlgorithm() {
        return this._trustManagerFactoryAlgorithm;
    }
    
    public boolean isTrustAll() {
        return this._trustAll;
    }
    
    public void setTrustAll(final boolean trustAll) {
        this._trustAll = trustAll;
        if (trustAll) {
            this.setEndpointIdentificationAlgorithm(null);
        }
    }
    
    public void setTrustManagerFactoryAlgorithm(final String algorithm) {
        this._trustManagerFactoryAlgorithm = algorithm;
    }
    
    public boolean isRenegotiationAllowed() {
        return this._renegotiationAllowed;
    }
    
    public void setRenegotiationAllowed(final boolean renegotiationAllowed) {
        this._renegotiationAllowed = renegotiationAllowed;
    }
    
    public int getRenegotiationLimit() {
        return this._renegotiationLimit;
    }
    
    public void setRenegotiationLimit(final int renegotiationLimit) {
        this._renegotiationLimit = renegotiationLimit;
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
    
    public SSLContext getSslContext() {
        if (!this.isStarted()) {
            return this._setContext;
        }
        synchronized (this) {
            return this._factory._context;
        }
    }
    
    public void setSslContext(final SSLContext sslContext) {
        this._setContext = sslContext;
    }
    
    public String getEndpointIdentificationAlgorithm() {
        return this._endpointIdentificationAlgorithm;
    }
    
    public void setEndpointIdentificationAlgorithm(final String endpointIdentificationAlgorithm) {
        this._endpointIdentificationAlgorithm = endpointIdentificationAlgorithm;
    }
    
    protected KeyStore loadKeyStore(final Resource resource) throws Exception {
        final String storePassword = (this._keyStorePassword == null) ? null : this._keyStorePassword.toString();
        return CertificateUtils.getKeyStore(resource, this.getKeyStoreType(), this.getKeyStoreProvider(), storePassword);
    }
    
    protected KeyStore loadTrustStore(Resource resource) throws Exception {
        String type = this.getTrustStoreType();
        String provider = this.getTrustStoreProvider();
        String passwd = (this._trustStorePassword == null) ? null : this._trustStorePassword.toString();
        if (resource == null || resource.equals(this._keyStoreResource)) {
            resource = this._keyStoreResource;
            if (type == null) {
                type = this._keyStoreType;
            }
            if (provider == null) {
                provider = this._keyStoreProvider;
            }
            if (passwd == null) {
                passwd = ((this._keyStorePassword == null) ? null : this._keyStorePassword.toString());
            }
        }
        return CertificateUtils.getKeyStore(resource, type, provider, passwd);
    }
    
    protected Collection<? extends CRL> loadCRL(final String crlPath) throws Exception {
        return CertificateUtils.loadCRL(crlPath);
    }
    
    protected KeyManager[] getKeyManagers(final KeyStore keyStore) throws Exception {
        KeyManager[] managers = null;
        if (keyStore != null) {
            final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(this.getKeyManagerFactoryAlgorithm());
            keyManagerFactory.init(keyStore, (char[])((this._keyManagerPassword == null) ? ((this._keyStorePassword == null) ? null : this._keyStorePassword.toString().toCharArray()) : this._keyManagerPassword.toString().toCharArray()));
            managers = keyManagerFactory.getKeyManagers();
            if (managers != null) {
                final String alias = this.getCertAlias();
                if (alias != null) {
                    for (int idx = 0; idx < managers.length; ++idx) {
                        if (managers[idx] instanceof X509ExtendedKeyManager) {
                            managers[idx] = new AliasedX509ExtendedKeyManager((X509ExtendedKeyManager)managers[idx], alias);
                        }
                    }
                }
                if (!this._certHosts.isEmpty() || !this._certWilds.isEmpty()) {
                    for (int idx = 0; idx < managers.length; ++idx) {
                        if (managers[idx] instanceof X509ExtendedKeyManager) {
                            managers[idx] = new SniX509ExtendedKeyManager((X509ExtendedKeyManager)managers[idx]);
                        }
                    }
                }
            }
        }
        if (SslContextFactory.LOG.isDebugEnabled()) {
            SslContextFactory.LOG.debug("managers={} for {}", managers, this);
        }
        return managers;
    }
    
    protected TrustManager[] getTrustManagers(final KeyStore trustStore, final Collection<? extends CRL> crls) throws Exception {
        TrustManager[] managers = null;
        if (trustStore != null) {
            if (this.isValidatePeerCerts() && "PKIX".equalsIgnoreCase(this.getTrustManagerFactoryAlgorithm())) {
                final PKIXBuilderParameters pbParams = new PKIXBuilderParameters(trustStore, new X509CertSelector());
                pbParams.setMaxPathLength(this._maxCertPathLength);
                pbParams.setRevocationEnabled(true);
                if (crls != null && !crls.isEmpty()) {
                    pbParams.addCertStore(CertStore.getInstance("Collection", new CollectionCertStoreParameters(crls)));
                }
                if (this._enableCRLDP) {
                    System.setProperty("com.sun.security.enableCRLDP", "true");
                }
                if (this._enableOCSP) {
                    Security.setProperty("ocsp.enable", "true");
                    if (this._ocspResponderURL != null) {
                        Security.setProperty("ocsp.responderURL", this._ocspResponderURL);
                    }
                }
                final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(this._trustManagerFactoryAlgorithm);
                trustManagerFactory.init(new CertPathTrustManagerParameters(pbParams));
                managers = trustManagerFactory.getTrustManagers();
            }
            else {
                final TrustManagerFactory trustManagerFactory2 = TrustManagerFactory.getInstance(this._trustManagerFactoryAlgorithm);
                trustManagerFactory2.init(trustStore);
                managers = trustManagerFactory2.getTrustManagers();
            }
        }
        return managers;
    }
    
    public void selectProtocols(final String[] enabledProtocols, final String[] supportedProtocols) {
        final Set<String> selected_protocols = new LinkedHashSet<String>();
        if (!this._includeProtocols.isEmpty()) {
            for (final String protocol : this._includeProtocols) {
                if (Arrays.asList(supportedProtocols).contains(protocol)) {
                    selected_protocols.add(protocol);
                }
                else {
                    SslContextFactory.LOG.info("Protocol {} not supported in {}", protocol, Arrays.asList(supportedProtocols));
                }
            }
        }
        else {
            selected_protocols.addAll(Arrays.asList(enabledProtocols));
        }
        selected_protocols.removeAll(this._excludeProtocols);
        if (selected_protocols.isEmpty()) {
            SslContextFactory.LOG.warn("No selected protocols from {}", Arrays.asList(supportedProtocols));
        }
        this._selectedProtocols = selected_protocols.toArray(new String[0]);
    }
    
    protected void selectCipherSuites(final String[] enabledCipherSuites, final String[] supportedCipherSuites) {
        final List<String> selected_ciphers = new ArrayList<String>();
        if (this._includeCipherSuites.isEmpty()) {
            selected_ciphers.addAll(Arrays.asList(enabledCipherSuites));
        }
        else {
            this.processIncludeCipherSuites(supportedCipherSuites, selected_ciphers);
        }
        this.removeExcludedCipherSuites(selected_ciphers);
        if (selected_ciphers.isEmpty()) {
            SslContextFactory.LOG.warn("No supported ciphers from {}", Arrays.asList(supportedCipherSuites));
        }
        final Comparator<String> comparator = this.getCipherComparator();
        if (comparator != null) {
            if (SslContextFactory.LOG.isDebugEnabled()) {
                SslContextFactory.LOG.debug("Sorting selected ciphers with {}", comparator);
            }
            Collections.sort(selected_ciphers, comparator);
        }
        this._selectedCipherSuites = selected_ciphers.toArray(new String[0]);
    }
    
    protected void processIncludeCipherSuites(final String[] supportedCipherSuites, final List<String> selected_ciphers) {
        for (final String cipherSuite : this._includeCipherSuites) {
            final Pattern p = Pattern.compile(cipherSuite);
            boolean added = false;
            for (final String supportedCipherSuite : supportedCipherSuites) {
                final Matcher m = p.matcher(supportedCipherSuite);
                if (m.matches()) {
                    added = true;
                    selected_ciphers.add(supportedCipherSuite);
                }
            }
            if (!added) {
                SslContextFactory.LOG.info("No Cipher matching '{}' is supported", cipherSuite);
            }
        }
    }
    
    protected void removeExcludedCipherSuites(final List<String> selected_ciphers) {
        for (final String excludeCipherSuite : this._excludeCipherSuites) {
            final Pattern excludeCipherPattern = Pattern.compile(excludeCipherSuite);
            final Iterator<String> i = selected_ciphers.iterator();
            while (i.hasNext()) {
                final String selectedCipherSuite = i.next();
                final Matcher m = excludeCipherPattern.matcher(selectedCipherSuite);
                if (m.matches()) {
                    i.remove();
                }
            }
        }
    }
    
    private void checkIsStarted() {
        if (!this.isStarted()) {
            throw new IllegalStateException("!STARTED: " + this);
        }
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
    
    public void setKeyStore(final KeyStore keyStore) {
        this._setKeyStore = keyStore;
    }
    
    public KeyStore getKeyStore() {
        if (!this.isStarted()) {
            return this._setKeyStore;
        }
        synchronized (this) {
            return this._factory._keyStore;
        }
    }
    
    public void setTrustStore(final KeyStore trustStore) {
        this._setTrustStore = trustStore;
    }
    
    public KeyStore getTrustStore() {
        if (!this.isStarted()) {
            return this._setTrustStore;
        }
        synchronized (this) {
            return this._factory._trustStore;
        }
    }
    
    public void setKeyStoreResource(final Resource resource) {
        this._keyStoreResource = resource;
    }
    
    public Resource getKeyStoreResource() {
        return this._keyStoreResource;
    }
    
    public void setTrustStoreResource(final Resource resource) {
        this._trustStoreResource = resource;
    }
    
    public Resource getTrustStoreResource() {
        return this._trustStoreResource;
    }
    
    public boolean isSessionCachingEnabled() {
        return this._sessionCachingEnabled;
    }
    
    public void setSessionCachingEnabled(final boolean enableSessionCaching) {
        this._sessionCachingEnabled = enableSessionCaching;
    }
    
    public int getSslSessionCacheSize() {
        return this._sslSessionCacheSize;
    }
    
    public void setSslSessionCacheSize(final int sslSessionCacheSize) {
        this._sslSessionCacheSize = sslSessionCacheSize;
    }
    
    public int getSslSessionTimeout() {
        return this._sslSessionTimeout;
    }
    
    public void setSslSessionTimeout(final int sslSessionTimeout) {
        this._sslSessionTimeout = sslSessionTimeout;
    }
    
    public SSLServerSocket newSslServerSocket(final String host, final int port, final int backlog) throws IOException {
        this.checkIsStarted();
        final SSLContext context = this.getSslContext();
        final SSLServerSocketFactory factory = context.getServerSocketFactory();
        final SSLServerSocket socket = (SSLServerSocket)((host == null) ? factory.createServerSocket(port, backlog) : factory.createServerSocket(port, backlog, InetAddress.getByName(host)));
        socket.setSSLParameters(this.customize(socket.getSSLParameters()));
        return socket;
    }
    
    public SSLSocket newSslSocket() throws IOException {
        this.checkIsStarted();
        final SSLContext context = this.getSslContext();
        final SSLSocketFactory factory = context.getSocketFactory();
        final SSLSocket socket = (SSLSocket)factory.createSocket();
        socket.setSSLParameters(this.customize(socket.getSSLParameters()));
        return socket;
    }
    
    public SSLEngine newSSLEngine() {
        this.checkIsStarted();
        final SSLContext context = this.getSslContext();
        final SSLEngine sslEngine = context.createSSLEngine();
        this.customize(sslEngine);
        return sslEngine;
    }
    
    public SSLEngine newSSLEngine(final String host, final int port) {
        this.checkIsStarted();
        final SSLContext context = this.getSslContext();
        final SSLEngine sslEngine = this.isSessionCachingEnabled() ? context.createSSLEngine(host, port) : context.createSSLEngine();
        this.customize(sslEngine);
        return sslEngine;
    }
    
    public SSLEngine newSSLEngine(final InetSocketAddress address) {
        if (address == null) {
            return this.newSSLEngine();
        }
        final boolean useHostName = this.getNeedClientAuth();
        final String hostName = useHostName ? address.getHostName() : address.getAddress().getHostAddress();
        return this.newSSLEngine(hostName, address.getPort());
    }
    
    public void customize(final SSLEngine sslEngine) {
        if (SslContextFactory.LOG.isDebugEnabled()) {
            SslContextFactory.LOG.debug("Customize {}", sslEngine);
        }
        sslEngine.setSSLParameters(this.customize(sslEngine.getSSLParameters()));
    }
    
    public SSLParameters customize(final SSLParameters sslParams) {
        sslParams.setEndpointIdentificationAlgorithm(this.getEndpointIdentificationAlgorithm());
        sslParams.setUseCipherSuitesOrder(this.isUseCipherSuitesOrder());
        if (!this._certHosts.isEmpty() || !this._certWilds.isEmpty()) {
            sslParams.setSNIMatchers((Collection<SNIMatcher>)Collections.singletonList(new AliasSNIMatcher()));
        }
        if (this._selectedCipherSuites != null) {
            sslParams.setCipherSuites(this._selectedCipherSuites);
        }
        if (this._selectedProtocols != null) {
            sslParams.setProtocols(this._selectedProtocols);
        }
        if (this.getWantClientAuth()) {
            sslParams.setWantClientAuth(true);
        }
        if (this.getNeedClientAuth()) {
            sslParams.setNeedClientAuth(true);
        }
        return sslParams;
    }
    
    public void reload(final Consumer<SslContextFactory> consumer) throws Exception {
        synchronized (this) {
            consumer.accept(this);
            this.unload();
            this.load();
        }
    }
    
    public static X509Certificate[] getCertChain(final SSLSession sslSession) {
        try {
            final Certificate[] javaxCerts = sslSession.getPeerCertificates();
            if (javaxCerts == null || javaxCerts.length == 0) {
                return null;
            }
            final int length = javaxCerts.length;
            final X509Certificate[] javaCerts = new X509Certificate[length];
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            for (int i = 0; i < length; ++i) {
                final byte[] bytes = javaxCerts[i].getEncoded();
                final ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
                javaCerts[i] = (X509Certificate)cf.generateCertificate(stream);
            }
            return javaCerts;
        }
        catch (SSLPeerUnverifiedException pue) {
            return null;
        }
        catch (Exception e) {
            SslContextFactory.LOG.warn("EXCEPTION ", e);
            return null;
        }
    }
    
    public static int deduceKeyLength(final String cipherSuite) {
        if (cipherSuite == null) {
            return 0;
        }
        if (cipherSuite.contains("WITH_AES_256_")) {
            return 256;
        }
        if (cipherSuite.contains("WITH_RC4_128_")) {
            return 128;
        }
        if (cipherSuite.contains("WITH_AES_128_")) {
            return 128;
        }
        if (cipherSuite.contains("WITH_RC4_40_")) {
            return 40;
        }
        if (cipherSuite.contains("WITH_3DES_EDE_CBC_")) {
            return 168;
        }
        if (cipherSuite.contains("WITH_IDEA_CBC_")) {
            return 128;
        }
        if (cipherSuite.contains("WITH_RC2_CBC_40_")) {
            return 40;
        }
        if (cipherSuite.contains("WITH_DES40_CBC_")) {
            return 40;
        }
        if (cipherSuite.contains("WITH_DES_CBC_")) {
            return 56;
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return String.format("%s@%x(%s,%s)", this.getClass().getSimpleName(), this.hashCode(), this._keyStoreResource, this._trustStoreResource);
    }
    
    static {
        TRUST_ALL_CERTS = new X509TrustManager[] { new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
                
                @Override
                public void checkClientTrusted(final X509Certificate[] certs, final String authType) {
                }
                
                @Override
                public void checkServerTrusted(final X509Certificate[] certs, final String authType) {
                }
            } };
        LOG = Log.getLogger(SslContextFactory.class);
        DEFAULT_KEYMANAGERFACTORY_ALGORITHM = ((Security.getProperty("ssl.KeyManagerFactory.algorithm") == null) ? KeyManagerFactory.getDefaultAlgorithm() : Security.getProperty("ssl.KeyManagerFactory.algorithm"));
        DEFAULT_TRUSTMANAGERFACTORY_ALGORITHM = ((Security.getProperty("ssl.TrustManagerFactory.algorithm") == null) ? TrustManagerFactory.getDefaultAlgorithm() : Security.getProperty("ssl.TrustManagerFactory.algorithm"));
    }
    
    class Factory
    {
        private final KeyStore _keyStore;
        private final KeyStore _trustStore;
        private final SSLContext _context;
        
        Factory(final KeyStore keyStore, final KeyStore trustStore, final SSLContext context) {
            this._keyStore = keyStore;
            this._trustStore = trustStore;
            this._context = context;
        }
    }
    
    class AliasSNIMatcher extends SNIMatcher
    {
        private String _host;
        private X509 _x509;
        
        AliasSNIMatcher() {
            super(0);
        }
        
        @Override
        public boolean matches(final SNIServerName serverName) {
            if (SslContextFactory.LOG.isDebugEnabled()) {
                SslContextFactory.LOG.debug("SNI matching for {}", serverName);
            }
            if (serverName instanceof SNIHostName) {
                final String asciiName = ((SNIHostName)serverName).getAsciiName();
                this._host = asciiName;
                String host = asciiName;
                host = StringUtil.asciiToLowerCase(host);
                this._x509 = SslContextFactory.this._certHosts.get(host);
                if (this._x509 == null) {
                    this._x509 = SslContextFactory.this._certWilds.get(host);
                    if (this._x509 == null) {
                        final int dot = host.indexOf(46);
                        if (dot >= 0) {
                            final String domain = host.substring(dot + 1);
                            this._x509 = SslContextFactory.this._certWilds.get(domain);
                        }
                    }
                }
                if (SslContextFactory.LOG.isDebugEnabled()) {
                    SslContextFactory.LOG.debug("SNI matched {}->{}", host, this._x509);
                }
            }
            else if (SslContextFactory.LOG.isDebugEnabled()) {
                SslContextFactory.LOG.debug("SNI no match for {}", serverName);
            }
            return true;
        }
        
        public String getHost() {
            return this._host;
        }
        
        public X509 getX509() {
            return this._x509;
        }
    }
}
