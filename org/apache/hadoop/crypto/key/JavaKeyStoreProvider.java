// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto.key;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.fs.FSDataOutputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.io.FileNotFoundException;
import java.security.cert.Certificate;
import java.security.Key;
import java.util.Date;
import com.google.common.base.Preconditions;
import org.apache.hadoop.util.StringUtils;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.spec.SecretKeySpec;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FSDataInputStream;
import java.security.UnrecoverableKeyException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.NoSuchAlgorithmException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.io.IOException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.hadoop.security.ProviderUtils;
import com.google.common.annotations.VisibleForTesting;
import java.util.HashMap;
import org.apache.hadoop.conf.Configuration;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.security.KeyStore;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import java.net.URI;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class JavaKeyStoreProvider extends KeyProvider
{
    private static final String KEY_METADATA = "KeyMetadata";
    private static final Logger LOG;
    public static final String SCHEME_NAME = "jceks";
    public static final String KEYSTORE_PASSWORD_FILE_KEY = "hadoop.security.keystore.java-keystore-provider.password-file";
    public static final String KEYSTORE_PASSWORD_ENV_VAR = "HADOOP_KEYSTORE_PASSWORD";
    public static final char[] KEYSTORE_PASSWORD_DEFAULT;
    private final URI uri;
    private final Path path;
    private final FileSystem fs;
    private FsPermission permissions;
    private KeyStore keyStore;
    private char[] password;
    private boolean changed;
    private Lock readLock;
    private Lock writeLock;
    private final Map<String, Metadata> cache;
    
    @VisibleForTesting
    JavaKeyStoreProvider(final JavaKeyStoreProvider other) {
        super(new Configuration());
        this.changed = false;
        this.cache = new HashMap<String, Metadata>();
        this.uri = other.uri;
        this.path = other.path;
        this.fs = other.fs;
        this.permissions = other.permissions;
        this.keyStore = other.keyStore;
        this.password = other.password;
        this.changed = other.changed;
        this.readLock = other.readLock;
        this.writeLock = other.writeLock;
    }
    
    private JavaKeyStoreProvider(final URI uri, final Configuration conf) throws IOException {
        super(conf);
        this.changed = false;
        this.cache = new HashMap<String, Metadata>();
        this.uri = uri;
        this.path = ProviderUtils.unnestUri(uri);
        this.fs = this.path.getFileSystem(conf);
        this.locateKeystore();
        final ReadWriteLock lock = new ReentrantReadWriteLock(true);
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }
    
    private void locateKeystore() throws IOException {
        try {
            this.password = ProviderUtils.locatePassword("HADOOP_KEYSTORE_PASSWORD", this.getConf().get("hadoop.security.keystore.java-keystore-provider.password-file"));
            if (this.password == null) {
                this.password = JavaKeyStoreProvider.KEYSTORE_PASSWORD_DEFAULT;
            }
            final Path oldPath = constructOldPath(this.path);
            final Path newPath = constructNewPath(this.path);
            this.keyStore = KeyStore.getInstance("jceks");
            FsPermission perm = null;
            if (this.fs.exists(this.path)) {
                if (this.fs.exists(newPath)) {
                    throw new IOException(String.format("Keystore not loaded due to some inconsistency ('%s' and '%s' should not exist together)!!", this.path, newPath));
                }
                perm = this.tryLoadFromPath(this.path, oldPath);
            }
            else {
                perm = this.tryLoadIncompleteFlush(oldPath, newPath);
            }
            this.permissions = perm;
        }
        catch (KeyStoreException e) {
            throw new IOException("Can't create keystore: " + e, e);
        }
        catch (GeneralSecurityException e2) {
            throw new IOException("Can't load keystore " + this.path + " : " + e2, e2);
        }
    }
    
    private FsPermission tryLoadFromPath(final Path path, final Path backupPath) throws NoSuchAlgorithmException, CertificateException, IOException {
        FsPermission perm = null;
        try {
            perm = this.loadFromPath(path, this.password);
            this.fs.delete(backupPath, true);
            JavaKeyStoreProvider.LOG.debug("KeyStore loaded successfully !!");
        }
        catch (IOException ioe) {
            if (this.isBadorWrongPassword(ioe)) {
                throw ioe;
            }
            perm = this.loadFromPath(backupPath, this.password);
            this.renameOrFail(path, new Path(path.toString() + "_CORRUPTED_" + System.currentTimeMillis()));
            this.renameOrFail(backupPath, path);
            if (JavaKeyStoreProvider.LOG.isDebugEnabled()) {
                JavaKeyStoreProvider.LOG.debug(String.format("KeyStore loaded successfully from '%s' since '%s'was corrupted !!", backupPath, path));
            }
        }
        return perm;
    }
    
    private FsPermission tryLoadIncompleteFlush(final Path oldPath, final Path newPath) throws IOException, NoSuchAlgorithmException, CertificateException {
        FsPermission perm = null;
        if (this.fs.exists(newPath)) {
            perm = this.loadAndReturnPerm(newPath, oldPath);
        }
        if (perm == null && this.fs.exists(oldPath)) {
            perm = this.loadAndReturnPerm(oldPath, newPath);
        }
        if (perm == null) {
            this.keyStore.load(null, this.password);
            JavaKeyStoreProvider.LOG.debug("KeyStore initialized anew successfully !!");
            perm = new FsPermission("600");
        }
        return perm;
    }
    
    private FsPermission loadAndReturnPerm(final Path pathToLoad, final Path pathToDelete) throws NoSuchAlgorithmException, CertificateException, IOException {
        FsPermission perm = null;
        try {
            perm = this.loadFromPath(pathToLoad, this.password);
            this.renameOrFail(pathToLoad, this.path);
            if (JavaKeyStoreProvider.LOG.isDebugEnabled()) {
                JavaKeyStoreProvider.LOG.debug(String.format("KeyStore loaded successfully from '%s'!!", pathToLoad));
            }
            this.fs.delete(pathToDelete, true);
        }
        catch (IOException e) {
            if (this.isBadorWrongPassword(e)) {
                throw e;
            }
        }
        return perm;
    }
    
    private boolean isBadorWrongPassword(final IOException ioe) {
        return ioe.getCause() instanceof UnrecoverableKeyException || (ioe.getCause() == null && ioe.getMessage() != null && (ioe.getMessage().contains("Keystore was tampered") || ioe.getMessage().contains("password was incorrect")));
    }
    
    private FsPermission loadFromPath(final Path p, final char[] password) throws IOException, NoSuchAlgorithmException, CertificateException {
        try (final FSDataInputStream in = this.fs.open(p)) {
            final FileStatus s = this.fs.getFileStatus(p);
            this.keyStore.load(in, password);
            return s.getPermission();
        }
    }
    
    private static Path constructNewPath(final Path path) {
        return new Path(path.toString() + "_NEW");
    }
    
    private static Path constructOldPath(final Path path) {
        return new Path(path.toString() + "_OLD");
    }
    
    @Override
    public boolean needsPassword() throws IOException {
        return null == ProviderUtils.locatePassword("HADOOP_KEYSTORE_PASSWORD", this.getConf().get("hadoop.security.keystore.java-keystore-provider.password-file"));
    }
    
    @Override
    public String noPasswordWarning() {
        return ProviderUtils.noPasswordWarning("HADOOP_KEYSTORE_PASSWORD", "hadoop.security.keystore.java-keystore-provider.password-file");
    }
    
    @Override
    public String noPasswordError() {
        return ProviderUtils.noPasswordError("HADOOP_KEYSTORE_PASSWORD", "hadoop.security.keystore.java-keystore-provider.password-file");
    }
    
    @Override
    public KeyVersion getKeyVersion(final String versionName) throws IOException {
        this.readLock.lock();
        try {
            SecretKeySpec key = null;
            try {
                if (!this.keyStore.containsAlias(versionName)) {
                    return null;
                }
                key = (SecretKeySpec)this.keyStore.getKey(versionName, this.password);
            }
            catch (KeyStoreException e) {
                throw new IOException("Can't get key " + versionName + " from " + this.path, e);
            }
            catch (NoSuchAlgorithmException e2) {
                throw new IOException("Can't get algorithm for key " + key + " from " + this.path, e2);
            }
            catch (UnrecoverableKeyException e3) {
                throw new IOException("Can't recover key " + key + " from " + this.path, e3);
            }
            return new KeyVersion(KeyProvider.getBaseName(versionName), versionName, key.getEncoded());
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public List<String> getKeys() throws IOException {
        this.readLock.lock();
        try {
            final ArrayList<String> list = new ArrayList<String>();
            String alias = null;
            try {
                final Enumeration<String> e = this.keyStore.aliases();
                while (e.hasMoreElements()) {
                    alias = e.nextElement();
                    if (!alias.contains("@")) {
                        list.add(alias);
                    }
                }
            }
            catch (KeyStoreException e2) {
                throw new IOException("Can't get key " + alias + " from " + this.path, e2);
            }
            return list;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public List<KeyVersion> getKeyVersions(final String name) throws IOException {
        this.readLock.lock();
        try {
            final List<KeyVersion> list = new ArrayList<KeyVersion>();
            final Metadata km = this.getMetadata(name);
            if (km != null) {
                final int latestVersion = km.getVersions();
                KeyVersion v = null;
                String versionName = null;
                for (int i = 0; i < latestVersion; ++i) {
                    versionName = KeyProvider.buildVersionName(name, i);
                    v = this.getKeyVersion(versionName);
                    if (v != null) {
                        list.add(v);
                    }
                }
            }
            return list;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public Metadata getMetadata(final String name) throws IOException {
        this.readLock.lock();
        try {
            if (this.cache.containsKey(name)) {
                return this.cache.get(name);
            }
            try {
                if (!this.keyStore.containsAlias(name)) {
                    return null;
                }
                final Metadata meta = ((KeyMetadata)this.keyStore.getKey(name, this.password)).metadata;
                this.cache.put(name, meta);
                return meta;
            }
            catch (ClassCastException e) {
                throw new IOException("Can't cast key for " + name + " in keystore " + this.path + " to a KeyMetadata. Key may have been added using  keytool or some other non-Hadoop method.", e);
            }
            catch (KeyStoreException e2) {
                throw new IOException("Can't get metadata for " + name + " from keystore " + this.path, e2);
            }
            catch (NoSuchAlgorithmException e3) {
                throw new IOException("Can't get algorithm for " + name + " from keystore " + this.path, e3);
            }
            catch (UnrecoverableKeyException e4) {
                throw new IOException("Can't recover key for " + name + " from keystore " + this.path, e4);
            }
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public KeyVersion createKey(final String name, final byte[] material, final Options options) throws IOException {
        Preconditions.checkArgument(name.equals(StringUtils.toLowerCase(name)), "Uppercase key names are unsupported: %s", name);
        this.writeLock.lock();
        try {
            try {
                if (this.keyStore.containsAlias(name) || this.cache.containsKey(name)) {
                    throw new IOException("Key " + name + " already exists in " + this);
                }
            }
            catch (KeyStoreException e) {
                throw new IOException("Problem looking up key " + name + " in " + this, e);
            }
            final Metadata meta = new Metadata(options.getCipher(), options.getBitLength(), options.getDescription(), options.getAttributes(), new Date(), 1);
            if (options.getBitLength() != 8 * material.length) {
                throw new IOException("Wrong key length. Required " + options.getBitLength() + ", but got " + 8 * material.length);
            }
            this.cache.put(name, meta);
            final String versionName = KeyProvider.buildVersionName(name, 0);
            return this.innerSetKeyVersion(name, versionName, material, meta.getCipher());
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public void deleteKey(final String name) throws IOException {
        this.writeLock.lock();
        try {
            final Metadata meta = this.getMetadata(name);
            if (meta == null) {
                throw new IOException("Key " + name + " does not exist in " + this);
            }
            for (int v = 0; v < meta.getVersions(); ++v) {
                final String versionName = KeyProvider.buildVersionName(name, v);
                try {
                    if (this.keyStore.containsAlias(versionName)) {
                        this.keyStore.deleteEntry(versionName);
                    }
                }
                catch (KeyStoreException e) {
                    throw new IOException("Problem removing " + versionName + " from " + this, e);
                }
            }
            try {
                if (this.keyStore.containsAlias(name)) {
                    this.keyStore.deleteEntry(name);
                }
            }
            catch (KeyStoreException e2) {
                throw new IOException("Problem removing " + name + " from " + this, e2);
            }
            this.cache.remove(name);
            this.changed = true;
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    KeyVersion innerSetKeyVersion(final String name, final String versionName, final byte[] material, final String cipher) throws IOException {
        try {
            this.keyStore.setKeyEntry(versionName, new SecretKeySpec(material, cipher), this.password, null);
        }
        catch (KeyStoreException e) {
            throw new IOException("Can't store key " + versionName + " in " + this, e);
        }
        this.changed = true;
        return new KeyVersion(name, versionName, material);
    }
    
    @Override
    public KeyVersion rollNewVersion(final String name, final byte[] material) throws IOException {
        this.writeLock.lock();
        try {
            final Metadata meta = this.getMetadata(name);
            if (meta == null) {
                throw new IOException("Key " + name + " not found");
            }
            if (meta.getBitLength() != 8 * material.length) {
                throw new IOException("Wrong key length. Required " + meta.getBitLength() + ", but got " + 8 * material.length);
            }
            final int nextVersion = meta.addVersion();
            final String versionName = KeyProvider.buildVersionName(name, nextVersion);
            return this.innerSetKeyVersion(name, versionName, material, meta.getCipher());
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public void flush() throws IOException {
        final Path newPath = constructNewPath(this.path);
        final Path oldPath = constructOldPath(this.path);
        Path resetPath = this.path;
        this.writeLock.lock();
        try {
            if (!this.changed) {
                return;
            }
            try {
                this.renameOrFail(newPath, new Path(newPath.toString() + "_ORPHANED_" + System.currentTimeMillis()));
            }
            catch (FileNotFoundException ex) {}
            try {
                this.renameOrFail(oldPath, new Path(oldPath.toString() + "_ORPHANED_" + System.currentTimeMillis()));
            }
            catch (FileNotFoundException ex2) {}
            for (final Map.Entry<String, Metadata> entry : this.cache.entrySet()) {
                try {
                    this.keyStore.setKeyEntry(entry.getKey(), new KeyMetadata((Metadata)entry.getValue()), this.password, null);
                }
                catch (KeyStoreException e) {
                    throw new IOException("Can't set metadata key " + entry.getKey(), e);
                }
            }
            final boolean fileExisted = this.backupToOld(oldPath);
            if (fileExisted) {
                resetPath = oldPath;
            }
            try {
                this.writeToNew(newPath);
            }
            catch (IOException ioe) {
                this.revertFromOld(oldPath, fileExisted);
                resetPath = this.path;
                throw ioe;
            }
            this.cleanupNewAndOld(newPath, oldPath);
            this.changed = false;
        }
        catch (IOException ioe2) {
            this.resetKeyStoreState(resetPath);
            throw ioe2;
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    private void resetKeyStoreState(final Path path) {
        JavaKeyStoreProvider.LOG.debug("Could not flush Keystore..attempting to reset to previous state !!");
        this.cache.clear();
        try {
            this.loadFromPath(path, this.password);
            JavaKeyStoreProvider.LOG.debug("KeyStore resetting to previously flushed state !!");
        }
        catch (Exception e) {
            JavaKeyStoreProvider.LOG.debug("Could not reset Keystore to previous state", e);
        }
    }
    
    private void cleanupNewAndOld(final Path newPath, final Path oldPath) throws IOException {
        this.renameOrFail(newPath, this.path);
        this.fs.delete(oldPath, true);
    }
    
    protected void writeToNew(final Path newPath) throws IOException {
        try (final FSDataOutputStream out = FileSystem.create(this.fs, newPath, this.permissions)) {
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
    }
    
    protected boolean backupToOld(final Path oldPath) throws IOException {
        try {
            this.renameOrFail(this.path, oldPath);
            return true;
        }
        catch (FileNotFoundException e) {
            return false;
        }
    }
    
    private void revertFromOld(final Path oldPath, final boolean fileExisted) throws IOException {
        if (fileExisted) {
            this.renameOrFail(oldPath, this.path);
        }
    }
    
    private void renameOrFail(final Path src, final Path dest) throws IOException {
        if (!this.fs.rename(src, dest)) {
            throw new IOException("Rename unsuccessful : " + String.format("'%s' to '%s'", src, dest));
        }
    }
    
    @Override
    public String toString() {
        return this.uri.toString();
    }
    
    static {
        LOG = LoggerFactory.getLogger(JavaKeyStoreProvider.class);
        KEYSTORE_PASSWORD_DEFAULT = "none".toCharArray();
    }
    
    public static class Factory extends KeyProviderFactory
    {
        @Override
        public KeyProvider createProvider(final URI providerName, final Configuration conf) throws IOException {
            if ("jceks".equals(providerName.getScheme())) {
                return new JavaKeyStoreProvider(providerName, conf, null);
            }
            return null;
        }
    }
    
    public static class KeyMetadata implements Key, Serializable
    {
        private Metadata metadata;
        private static final long serialVersionUID = 8405872419967874451L;
        
        private KeyMetadata(final Metadata meta) {
            this.metadata = meta;
        }
        
        @Override
        public String getAlgorithm() {
            return this.metadata.getCipher();
        }
        
        @Override
        public String getFormat() {
            return "KeyMetadata";
        }
        
        @Override
        public byte[] getEncoded() {
            return new byte[0];
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            final byte[] serialized = this.metadata.serialize();
            out.writeInt(serialized.length);
            out.write(serialized);
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            final byte[] buf = new byte[in.readInt()];
            in.readFully(buf);
            this.metadata = new Metadata(buf);
        }
    }
}
