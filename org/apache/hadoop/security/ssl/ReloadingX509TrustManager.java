// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.ssl;

import org.slf4j.LoggerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.GeneralSecurityException;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.concurrent.atomic.AtomicReference;
import java.io.File;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import javax.net.ssl.X509TrustManager;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public final class ReloadingX509TrustManager implements X509TrustManager, Runnable
{
    @VisibleForTesting
    static final Logger LOG;
    @VisibleForTesting
    static final String RELOAD_ERROR_MESSAGE = "Could not load truststore (keep using existing one) : ";
    private String type;
    private File file;
    private String password;
    private long lastLoaded;
    private long reloadInterval;
    private AtomicReference<X509TrustManager> trustManagerRef;
    private volatile boolean running;
    private Thread reloader;
    private static final X509Certificate[] EMPTY;
    
    public ReloadingX509TrustManager(final String type, final String location, final String password, final long reloadInterval) throws IOException, GeneralSecurityException {
        this.type = type;
        this.file = new File(location);
        this.password = password;
        (this.trustManagerRef = new AtomicReference<X509TrustManager>()).set(this.loadTrustManager());
        this.reloadInterval = reloadInterval;
    }
    
    public void init() {
        (this.reloader = new Thread(this, "Truststore reloader thread")).setDaemon(true);
        this.running = true;
        this.reloader.start();
    }
    
    public void destroy() {
        this.running = false;
        this.reloader.interrupt();
    }
    
    public long getReloadInterval() {
        return this.reloadInterval;
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        final X509TrustManager tm = this.trustManagerRef.get();
        if (tm != null) {
            tm.checkClientTrusted(chain, authType);
            return;
        }
        throw new CertificateException("Unknown client chain certificate: " + chain[0].toString());
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        final X509TrustManager tm = this.trustManagerRef.get();
        if (tm != null) {
            tm.checkServerTrusted(chain, authType);
            return;
        }
        throw new CertificateException("Unknown server chain certificate: " + chain[0].toString());
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        X509Certificate[] issuers = ReloadingX509TrustManager.EMPTY;
        final X509TrustManager tm = this.trustManagerRef.get();
        if (tm != null) {
            issuers = tm.getAcceptedIssuers();
        }
        return issuers;
    }
    
    boolean needsReload() {
        boolean reload = true;
        if (this.file.exists()) {
            if (this.file.lastModified() == this.lastLoaded) {
                reload = false;
            }
        }
        else {
            this.lastLoaded = 0L;
        }
        return reload;
    }
    
    X509TrustManager loadTrustManager() throws IOException, GeneralSecurityException {
        X509TrustManager trustManager = null;
        final KeyStore ks = KeyStore.getInstance(this.type);
        final FileInputStream in = new FileInputStream(this.file);
        try {
            ks.load(in, (char[])((this.password == null) ? null : this.password.toCharArray()));
            this.lastLoaded = this.file.lastModified();
            ReloadingX509TrustManager.LOG.debug("Loaded truststore '" + this.file + "'");
        }
        finally {
            in.close();
        }
        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(SSLFactory.SSLCERTIFICATE);
        trustManagerFactory.init(ks);
        final TrustManager[] trustManagers2;
        final TrustManager[] trustManagers = trustManagers2 = trustManagerFactory.getTrustManagers();
        for (final TrustManager trustManager2 : trustManagers2) {
            if (trustManager2 instanceof X509TrustManager) {
                trustManager = (X509TrustManager)trustManager2;
                break;
            }
        }
        return trustManager;
    }
    
    @Override
    public void run() {
        while (this.running) {
            try {
                Thread.sleep(this.reloadInterval);
            }
            catch (InterruptedException ex2) {}
            if (this.running && this.needsReload()) {
                try {
                    this.trustManagerRef.set(this.loadTrustManager());
                }
                catch (Exception ex) {
                    ReloadingX509TrustManager.LOG.warn("Could not load truststore (keep using existing one) : " + ex.toString(), ex);
                }
            }
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(ReloadingX509TrustManager.class);
        EMPTY = new X509Certificate[0];
    }
}
