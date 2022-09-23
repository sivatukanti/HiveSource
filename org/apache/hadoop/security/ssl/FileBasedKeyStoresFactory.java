// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.ssl;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import javax.net.ssl.KeyManagerFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import com.google.common.annotations.VisibleForTesting;
import java.text.MessageFormat;
import org.apache.hadoop.util.StringUtils;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class FileBasedKeyStoresFactory implements KeyStoresFactory
{
    private static final Logger LOG;
    public static final String SSL_KEYSTORE_LOCATION_TPL_KEY = "ssl.{0}.keystore.location";
    public static final String SSL_KEYSTORE_PASSWORD_TPL_KEY = "ssl.{0}.keystore.password";
    public static final String SSL_KEYSTORE_KEYPASSWORD_TPL_KEY = "ssl.{0}.keystore.keypassword";
    public static final String SSL_KEYSTORE_TYPE_TPL_KEY = "ssl.{0}.keystore.type";
    public static final String SSL_TRUSTSTORE_RELOAD_INTERVAL_TPL_KEY = "ssl.{0}.truststore.reload.interval";
    public static final String SSL_TRUSTSTORE_LOCATION_TPL_KEY = "ssl.{0}.truststore.location";
    public static final String SSL_TRUSTSTORE_PASSWORD_TPL_KEY = "ssl.{0}.truststore.password";
    public static final String SSL_TRUSTSTORE_TYPE_TPL_KEY = "ssl.{0}.truststore.type";
    public static final String SSL_EXCLUDE_CIPHER_LIST = "ssl.{0}.exclude.cipher.list";
    public static final String DEFAULT_KEYSTORE_TYPE = "jks";
    public static final int DEFAULT_SSL_TRUSTSTORE_RELOAD_INTERVAL = 10000;
    private Configuration conf;
    private KeyManager[] keyManagers;
    private TrustManager[] trustManagers;
    private ReloadingX509TrustManager trustManager;
    
    @VisibleForTesting
    public static String resolvePropertyName(final SSLFactory.Mode mode, final String template) {
        return MessageFormat.format(template, StringUtils.toLowerCase(mode.toString()));
    }
    
    @Override
    public void setConf(final Configuration conf) {
        this.conf = conf;
    }
    
    @Override
    public Configuration getConf() {
        return this.conf;
    }
    
    @Override
    public void init(final SSLFactory.Mode mode) throws IOException, GeneralSecurityException {
        final boolean requireClientCert = this.conf.getBoolean("hadoop.ssl.require.client.cert", false);
        final String keystoreType = this.conf.get(resolvePropertyName(mode, "ssl.{0}.keystore.type"), "jks");
        final KeyStore keystore = KeyStore.getInstance(keystoreType);
        String keystoreKeyPassword = null;
        if (requireClientCert || mode == SSLFactory.Mode.SERVER) {
            final String locationProperty = resolvePropertyName(mode, "ssl.{0}.keystore.location");
            final String keystoreLocation = this.conf.get(locationProperty, "");
            if (keystoreLocation.isEmpty()) {
                throw new GeneralSecurityException("The property '" + locationProperty + "' has not been set in the ssl configuration file.");
            }
            final String passwordProperty = resolvePropertyName(mode, "ssl.{0}.keystore.password");
            final String keystorePassword = this.getPassword(this.conf, passwordProperty, "");
            if (keystorePassword.isEmpty()) {
                throw new GeneralSecurityException("The property '" + passwordProperty + "' has not been set in the ssl configuration file.");
            }
            final String keyPasswordProperty = resolvePropertyName(mode, "ssl.{0}.keystore.keypassword");
            keystoreKeyPassword = this.getPassword(this.conf, keyPasswordProperty, keystorePassword);
            if (FileBasedKeyStoresFactory.LOG.isDebugEnabled()) {
                FileBasedKeyStoresFactory.LOG.debug(mode.toString() + " KeyStore: " + keystoreLocation);
            }
            final InputStream is = new FileInputStream(keystoreLocation);
            try {
                keystore.load(is, keystorePassword.toCharArray());
            }
            finally {
                is.close();
            }
            if (FileBasedKeyStoresFactory.LOG.isDebugEnabled()) {
                FileBasedKeyStoresFactory.LOG.debug(mode.toString() + " Loaded KeyStore: " + keystoreLocation);
            }
        }
        else {
            keystore.load(null, null);
        }
        final KeyManagerFactory keyMgrFactory = KeyManagerFactory.getInstance(SSLFactory.SSLCERTIFICATE);
        keyMgrFactory.init(keystore, (char[])((keystoreKeyPassword != null) ? keystoreKeyPassword.toCharArray() : null));
        this.keyManagers = keyMgrFactory.getKeyManagers();
        final String truststoreType = this.conf.get(resolvePropertyName(mode, "ssl.{0}.truststore.type"), "jks");
        final String locationProperty2 = resolvePropertyName(mode, "ssl.{0}.truststore.location");
        final String truststoreLocation = this.conf.get(locationProperty2, "");
        if (!truststoreLocation.isEmpty()) {
            final String passwordProperty2 = resolvePropertyName(mode, "ssl.{0}.truststore.password");
            String truststorePassword = this.getPassword(this.conf, passwordProperty2, "");
            if (truststorePassword.isEmpty()) {
                truststorePassword = null;
            }
            final long truststoreReloadInterval = this.conf.getLong(resolvePropertyName(mode, "ssl.{0}.truststore.reload.interval"), 10000L);
            if (FileBasedKeyStoresFactory.LOG.isDebugEnabled()) {
                FileBasedKeyStoresFactory.LOG.debug(mode.toString() + " TrustStore: " + truststoreLocation);
            }
            (this.trustManager = new ReloadingX509TrustManager(truststoreType, truststoreLocation, truststorePassword, truststoreReloadInterval)).init();
            if (FileBasedKeyStoresFactory.LOG.isDebugEnabled()) {
                FileBasedKeyStoresFactory.LOG.debug(mode.toString() + " Loaded TrustStore: " + truststoreLocation);
            }
            this.trustManagers = new TrustManager[] { this.trustManager };
        }
        else {
            if (FileBasedKeyStoresFactory.LOG.isDebugEnabled()) {
                FileBasedKeyStoresFactory.LOG.debug("The property '" + locationProperty2 + "' has not been set, no TrustStore will be loaded");
            }
            this.trustManagers = null;
        }
    }
    
    String getPassword(final Configuration conf, final String alias, final String defaultPass) {
        String password = defaultPass;
        try {
            final char[] passchars = conf.getPassword(alias);
            if (passchars != null) {
                password = new String(passchars);
            }
        }
        catch (IOException ioe) {
            FileBasedKeyStoresFactory.LOG.warn("Exception while trying to get password for alias " + alias + ": " + ioe.getMessage());
        }
        return password;
    }
    
    @Override
    public synchronized void destroy() {
        if (this.trustManager != null) {
            this.trustManager.destroy();
            this.trustManager = null;
            this.keyManagers = null;
            this.trustManagers = null;
        }
    }
    
    @Override
    public KeyManager[] getKeyManagers() {
        return this.keyManagers;
    }
    
    @Override
    public TrustManager[] getTrustManagers() {
        return this.trustManagers;
    }
    
    static {
        LOG = LoggerFactory.getLogger(FileBasedKeyStoresFactory.class);
    }
}
