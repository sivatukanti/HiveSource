// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.alias;

import org.slf4j.LoggerFactory;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.Certificate;
import java.security.Key;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;
import com.google.common.base.Charsets;
import java.security.UnrecoverableKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.KeyStoreException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.hadoop.security.ProviderUtils;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.hadoop.conf.Configuration;
import java.util.concurrent.locks.Lock;
import java.security.KeyStore;
import java.net.URI;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public abstract class AbstractJavaKeyStoreProvider extends CredentialProvider
{
    public static final Logger LOG;
    public static final String CREDENTIAL_PASSWORD_ENV_VAR = "HADOOP_CREDSTORE_PASSWORD";
    public static final String CREDENTIAL_PASSWORD_FILE_KEY = "hadoop.security.credstore.java-keystore-provider.password-file";
    public static final String CREDENTIAL_PASSWORD_DEFAULT = "none";
    private Path path;
    private final URI uri;
    private KeyStore keyStore;
    private char[] password;
    private boolean changed;
    private Lock readLock;
    private Lock writeLock;
    private final Configuration conf;
    
    protected AbstractJavaKeyStoreProvider(final URI uri, final Configuration conf) throws IOException {
        this.password = null;
        this.changed = false;
        this.uri = uri;
        this.conf = conf;
        this.initFileSystem(uri);
        this.locateKeystore();
        final ReadWriteLock lock = new ReentrantReadWriteLock(true);
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }
    
    protected Configuration getConf() {
        return this.conf;
    }
    
    public Path getPath() {
        return this.path;
    }
    
    public void setPath(final Path p) {
        this.path = p;
    }
    
    public char[] getPassword() {
        return this.password;
    }
    
    public void setPassword(final char[] pass) {
        this.password = pass;
    }
    
    public boolean isChanged() {
        return this.changed;
    }
    
    public void setChanged(final boolean chg) {
        this.changed = chg;
    }
    
    public Lock getReadLock() {
        return this.readLock;
    }
    
    public void setReadLock(final Lock rl) {
        this.readLock = rl;
    }
    
    public Lock getWriteLock() {
        return this.writeLock;
    }
    
    public void setWriteLock(final Lock wl) {
        this.writeLock = wl;
    }
    
    public URI getUri() {
        return this.uri;
    }
    
    public KeyStore getKeyStore() {
        return this.keyStore;
    }
    
    protected final String getPathAsString() {
        return this.getPath().toString();
    }
    
    protected abstract String getSchemeName();
    
    protected abstract OutputStream getOutputStreamForKeystore() throws IOException;
    
    protected abstract boolean keystoreExists() throws IOException;
    
    protected abstract InputStream getInputStreamForFile() throws IOException;
    
    protected abstract void createPermissions(final String p0) throws IOException;
    
    protected abstract void stashOriginalFilePermissions() throws IOException;
    
    protected void initFileSystem(final URI keystoreUri) throws IOException {
        this.path = ProviderUtils.unnestUri(keystoreUri);
        if (AbstractJavaKeyStoreProvider.LOG.isDebugEnabled()) {
            AbstractJavaKeyStoreProvider.LOG.debug("backing jks path initialized to " + this.path);
        }
    }
    
    @Override
    public CredentialEntry getCredentialEntry(final String alias) throws IOException {
        this.readLock.lock();
        try {
            SecretKeySpec key = null;
            try {
                if (!this.keyStore.containsAlias(alias)) {
                    return null;
                }
                key = (SecretKeySpec)this.keyStore.getKey(alias, this.password);
            }
            catch (KeyStoreException e) {
                throw new IOException("Can't get credential " + alias + " from " + this.getPathAsString(), e);
            }
            catch (NoSuchAlgorithmException e2) {
                throw new IOException("Can't get algorithm for credential " + alias + " from " + this.getPathAsString(), e2);
            }
            catch (UnrecoverableKeyException e3) {
                throw new IOException("Can't recover credential " + alias + " from " + this.getPathAsString(), e3);
            }
            return new CredentialEntry(alias, bytesToChars(key.getEncoded()));
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public static char[] bytesToChars(final byte[] bytes) throws IOException {
        final String pass = new String(bytes, Charsets.UTF_8);
        return pass.toCharArray();
    }
    
    @Override
    public List<String> getAliases() throws IOException {
        this.readLock.lock();
        try {
            final ArrayList<String> list = new ArrayList<String>();
            String alias = null;
            try {
                final Enumeration<String> e = this.keyStore.aliases();
                while (e.hasMoreElements()) {
                    alias = e.nextElement();
                    list.add(alias);
                }
            }
            catch (KeyStoreException e2) {
                throw new IOException("Can't get alias " + alias + " from " + this.getPathAsString(), e2);
            }
            return list;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public CredentialEntry createCredentialEntry(final String alias, final char[] credential) throws IOException {
        this.writeLock.lock();
        try {
            if (this.keyStore.containsAlias(alias)) {
                throw new IOException("Credential " + alias + " already exists in " + this);
            }
            return this.innerSetCredential(alias, credential);
        }
        catch (KeyStoreException e) {
            throw new IOException("Problem looking up credential " + alias + " in " + this, e);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public void deleteCredentialEntry(final String name) throws IOException {
        this.writeLock.lock();
        try {
            try {
                if (!this.keyStore.containsAlias(name)) {
                    throw new IOException("Credential " + name + " does not exist in " + this);
                }
                this.keyStore.deleteEntry(name);
            }
            catch (KeyStoreException e) {
                throw new IOException("Problem removing " + name + " from " + this, e);
            }
            this.changed = true;
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    CredentialEntry innerSetCredential(final String alias, final char[] material) throws IOException {
        this.writeLock.lock();
        try {
            this.keyStore.setKeyEntry(alias, new SecretKeySpec(new String(material).getBytes("UTF-8"), "AES"), this.password, null);
        }
        catch (KeyStoreException e) {
            throw new IOException("Can't store credential " + alias + " in " + this, e);
        }
        finally {
            this.writeLock.unlock();
        }
        this.changed = true;
        return new CredentialEntry(alias, material);
    }
    
    @Override
    public void flush() throws IOException {
        this.writeLock.lock();
        try {
            if (!this.changed) {
                AbstractJavaKeyStoreProvider.LOG.debug("Keystore hasn't changed, returning.");
                return;
            }
            AbstractJavaKeyStoreProvider.LOG.debug("Writing out keystore.");
            try (final OutputStream out = this.getOutputStreamForKeystore()) {
                this.keyStore.store(out, this.password);
            }
            catch (KeyStoreException e) {
                throw new IOException("Can't store keystore " + this, e);
            }
            catch (NoSuchAlgorithmException e2) {
                throw new IOException("No such algorithm storing keystore " + this, e2);
            }
            catch (CertificateException e3) {
                throw new IOException("Certificate exception storing keystore " + this, e3);
            }
            this.changed = false;
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    private void locateKeystore() throws IOException {
        try {
            this.password = ProviderUtils.locatePassword("HADOOP_CREDSTORE_PASSWORD", this.conf.get("hadoop.security.credstore.java-keystore-provider.password-file"));
            if (this.password == null) {
                this.password = "none".toCharArray();
            }
            final KeyStore ks = KeyStore.getInstance("jceks");
            if (this.keystoreExists()) {
                this.stashOriginalFilePermissions();
                try (final InputStream in = this.getInputStreamForFile()) {
                    ks.load(in, this.password);
                }
            }
            else {
                this.createPermissions("600");
                ks.load(null, this.password);
            }
            this.keyStore = ks;
        }
        catch (KeyStoreException e) {
            throw new IOException("Can't create keystore", e);
        }
        catch (GeneralSecurityException e2) {
            throw new IOException("Can't load keystore " + this.getPathAsString(), e2);
        }
    }
    
    @Override
    public boolean needsPassword() throws IOException {
        return null == ProviderUtils.locatePassword("HADOOP_CREDSTORE_PASSWORD", this.conf.get("hadoop.security.credstore.java-keystore-provider.password-file"));
    }
    
    @Override
    public String noPasswordWarning() {
        return ProviderUtils.noPasswordWarning("HADOOP_CREDSTORE_PASSWORD", "hadoop.security.credstore.java-keystore-provider.password-file");
    }
    
    @Override
    public String noPasswordError() {
        return ProviderUtils.noPasswordError("HADOOP_CREDSTORE_PASSWORD", "hadoop.security.credstore.java-keystore-provider.password-file");
    }
    
    @Override
    public String toString() {
        return this.uri.toString();
    }
    
    static {
        LOG = LoggerFactory.getLogger(AbstractJavaKeyStoreProvider.class);
    }
}
