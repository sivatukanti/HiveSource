// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.ssl;

import org.apache.hadoop.util.PlatformName;
import org.slf4j.LoggerFactory;
import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import javax.net.ssl.SSLEngine;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import org.apache.hadoop.util.StringUtils;
import java.util.Arrays;
import org.apache.hadoop.util.ReflectionUtils;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.authentication.client.ConnectionConfigurator;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class SSLFactory implements ConnectionConfigurator
{
    static final Logger LOG;
    public static final String SSL_CLIENT_CONF_KEY = "hadoop.ssl.client.conf";
    public static final String SSL_CLIENT_CONF_DEFAULT = "ssl-client.xml";
    public static final String SSL_SERVER_CONF_KEY = "hadoop.ssl.server.conf";
    public static final String SSL_SERVER_CONF_DEFAULT = "ssl-server.xml";
    public static final String SSL_REQUIRE_CLIENT_CERT_KEY = "hadoop.ssl.require.client.cert";
    public static final boolean SSL_REQUIRE_CLIENT_CERT_DEFAULT = false;
    public static final String SSL_HOSTNAME_VERIFIER_KEY = "hadoop.ssl.hostname.verifier";
    public static final String SSL_ENABLED_PROTOCOLS_KEY = "hadoop.ssl.enabled.protocols";
    public static final String SSL_ENABLED_PROTOCOLS_DEFAULT = "TLSv1,SSLv2Hello,TLSv1.1,TLSv1.2";
    public static final String SSL_SERVER_NEED_CLIENT_AUTH = "ssl.server.need.client.auth";
    public static final boolean SSL_SERVER_NEED_CLIENT_AUTH_DEFAULT = false;
    public static final String SSL_SERVER_KEYSTORE_LOCATION = "ssl.server.keystore.location";
    public static final String SSL_SERVER_KEYSTORE_PASSWORD = "ssl.server.keystore.password";
    public static final String SSL_SERVER_KEYSTORE_TYPE = "ssl.server.keystore.type";
    public static final String SSL_SERVER_KEYSTORE_TYPE_DEFAULT = "jks";
    public static final String SSL_SERVER_KEYSTORE_KEYPASSWORD = "ssl.server.keystore.keypassword";
    public static final String SSL_SERVER_TRUSTSTORE_LOCATION = "ssl.server.truststore.location";
    public static final String SSL_SERVER_TRUSTSTORE_PASSWORD = "ssl.server.truststore.password";
    public static final String SSL_SERVER_TRUSTSTORE_TYPE = "ssl.server.truststore.type";
    public static final String SSL_SERVER_TRUSTSTORE_TYPE_DEFAULT = "jks";
    public static final String SSL_SERVER_EXCLUDE_CIPHER_LIST = "ssl.server.exclude.cipher.list";
    public static final String SSLCERTIFICATE;
    public static final String KEYSTORES_FACTORY_CLASS_KEY = "hadoop.ssl.keystores.factory.class";
    private Configuration conf;
    private Mode mode;
    private boolean requireClientCert;
    private SSLContext context;
    private HostnameVerifier hostnameVerifier;
    private KeyStoresFactory keystoresFactory;
    private String[] enabledProtocols;
    private List<String> excludeCiphers;
    
    public SSLFactory(final Mode mode, final Configuration conf) {
        this.enabledProtocols = null;
        this.conf = conf;
        if (mode == null) {
            throw new IllegalArgumentException("mode cannot be NULL");
        }
        this.mode = mode;
        final Configuration sslConf = readSSLConfiguration(conf, mode);
        this.requireClientCert = sslConf.getBoolean("hadoop.ssl.require.client.cert", false);
        final Class<? extends KeyStoresFactory> klass = conf.getClass("hadoop.ssl.keystores.factory.class", FileBasedKeyStoresFactory.class, KeyStoresFactory.class);
        this.keystoresFactory = ReflectionUtils.newInstance(klass, sslConf);
        this.enabledProtocols = conf.getStrings("hadoop.ssl.enabled.protocols", "TLSv1,SSLv2Hello,TLSv1.1,TLSv1.2");
        this.excludeCiphers = Arrays.asList(sslConf.getTrimmedStrings("ssl.server.exclude.cipher.list"));
        if (SSLFactory.LOG.isDebugEnabled()) {
            SSLFactory.LOG.debug("will exclude cipher suites: {}", StringUtils.join(",", this.excludeCiphers));
        }
    }
    
    public static Configuration readSSLConfiguration(final Configuration conf, final Mode mode) {
        final Configuration sslConf = new Configuration(false);
        sslConf.setBoolean("hadoop.ssl.require.client.cert", conf.getBoolean("hadoop.ssl.require.client.cert", false));
        String sslConfResource;
        if (mode == Mode.CLIENT) {
            sslConfResource = conf.get("hadoop.ssl.client.conf", "ssl-client.xml");
        }
        else {
            sslConfResource = conf.get("hadoop.ssl.server.conf", "ssl-server.xml");
        }
        sslConf.addResource(sslConfResource);
        return sslConf;
    }
    
    public void init() throws GeneralSecurityException, IOException {
        this.keystoresFactory.init(this.mode);
        (this.context = SSLContext.getInstance("TLS")).init(this.keystoresFactory.getKeyManagers(), this.keystoresFactory.getTrustManagers(), null);
        this.context.getDefaultSSLParameters().setProtocols(this.enabledProtocols);
        this.hostnameVerifier = this.getHostnameVerifier(this.conf);
    }
    
    private HostnameVerifier getHostnameVerifier(final Configuration conf) throws GeneralSecurityException, IOException {
        return getHostnameVerifier(StringUtils.toUpperCase(conf.get("hadoop.ssl.hostname.verifier", "DEFAULT").trim()));
    }
    
    public static HostnameVerifier getHostnameVerifier(final String verifier) throws GeneralSecurityException, IOException {
        HostnameVerifier hostnameVerifier;
        if (verifier.equals("DEFAULT")) {
            hostnameVerifier = SSLHostnameVerifier.DEFAULT;
        }
        else if (verifier.equals("DEFAULT_AND_LOCALHOST")) {
            hostnameVerifier = SSLHostnameVerifier.DEFAULT_AND_LOCALHOST;
        }
        else if (verifier.equals("STRICT")) {
            hostnameVerifier = SSLHostnameVerifier.STRICT;
        }
        else if (verifier.equals("STRICT_IE6")) {
            hostnameVerifier = SSLHostnameVerifier.STRICT_IE6;
        }
        else {
            if (!verifier.equals("ALLOW_ALL")) {
                throw new GeneralSecurityException("Invalid hostname verifier: " + verifier);
            }
            hostnameVerifier = SSLHostnameVerifier.ALLOW_ALL;
        }
        return hostnameVerifier;
    }
    
    public void destroy() {
        this.keystoresFactory.destroy();
    }
    
    public KeyStoresFactory getKeystoresFactory() {
        return this.keystoresFactory;
    }
    
    public SSLEngine createSSLEngine() throws GeneralSecurityException, IOException {
        final SSLEngine sslEngine = this.context.createSSLEngine();
        if (this.mode == Mode.CLIENT) {
            sslEngine.setUseClientMode(true);
        }
        else {
            sslEngine.setUseClientMode(false);
            sslEngine.setNeedClientAuth(this.requireClientCert);
            this.disableExcludedCiphers(sslEngine);
        }
        sslEngine.setEnabledProtocols(this.enabledProtocols);
        return sslEngine;
    }
    
    private void disableExcludedCiphers(final SSLEngine sslEngine) {
        String[] cipherSuites = sslEngine.getEnabledCipherSuites();
        final ArrayList<String> defaultEnabledCipherSuites = new ArrayList<String>(Arrays.asList(cipherSuites));
        for (final String cipherName : this.excludeCiphers) {
            if (defaultEnabledCipherSuites.contains(cipherName)) {
                defaultEnabledCipherSuites.remove(cipherName);
                SSLFactory.LOG.debug("Disabling cipher suite {}.", cipherName);
            }
        }
        cipherSuites = defaultEnabledCipherSuites.toArray(new String[defaultEnabledCipherSuites.size()]);
        sslEngine.setEnabledCipherSuites(cipherSuites);
    }
    
    public SSLServerSocketFactory createSSLServerSocketFactory() throws GeneralSecurityException, IOException {
        if (this.mode != Mode.SERVER) {
            throw new IllegalStateException("Factory is not in SERVER mode. Actual mode is " + this.mode.toString());
        }
        return this.context.getServerSocketFactory();
    }
    
    public SSLSocketFactory createSSLSocketFactory() throws GeneralSecurityException, IOException {
        if (this.mode != Mode.CLIENT) {
            throw new IllegalStateException("Factory is not in CLIENT mode. Actual mode is " + this.mode.toString());
        }
        return this.context.getSocketFactory();
    }
    
    public HostnameVerifier getHostnameVerifier() {
        if (this.mode != Mode.CLIENT) {
            throw new IllegalStateException("Factory is not in CLIENT mode. Actual mode is " + this.mode.toString());
        }
        return this.hostnameVerifier;
    }
    
    public boolean isClientCertRequired() {
        return this.requireClientCert;
    }
    
    @Override
    public HttpURLConnection configure(HttpURLConnection conn) throws IOException {
        if (conn instanceof HttpsURLConnection) {
            final HttpsURLConnection sslConn = (HttpsURLConnection)conn;
            try {
                sslConn.setSSLSocketFactory(this.createSSLSocketFactory());
            }
            catch (GeneralSecurityException ex) {
                throw new IOException(ex);
            }
            sslConn.setHostnameVerifier(this.getHostnameVerifier());
            conn = sslConn;
        }
        return conn;
    }
    
    static {
        LOG = LoggerFactory.getLogger(SSLFactory.class);
        SSLCERTIFICATE = (PlatformName.IBM_JAVA ? "ibmX509" : "SunX509");
    }
    
    @InterfaceAudience.Private
    public enum Mode
    {
        CLIENT, 
        SERVER;
    }
}
