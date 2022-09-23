// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto.key;

import java.net.URI;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import org.apache.hadoop.io.Text;
import java.io.IOException;
import java.util.HashMap;
import org.apache.hadoop.conf.Configuration;
import java.util.Map;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class UserProvider extends KeyProvider
{
    public static final String SCHEME_NAME = "user";
    private final UserGroupInformation user;
    private final Credentials credentials;
    private final Map<String, Metadata> cache;
    
    private UserProvider(final Configuration conf) throws IOException {
        super(conf);
        this.cache = new HashMap<String, Metadata>();
        this.user = UserGroupInformation.getCurrentUser();
        this.credentials = this.user.getCredentials();
    }
    
    @Override
    public boolean isTransient() {
        return true;
    }
    
    @Override
    public synchronized KeyVersion getKeyVersion(final String versionName) throws IOException {
        final byte[] bytes = this.credentials.getSecretKey(new Text(versionName));
        if (bytes == null) {
            return null;
        }
        return new KeyVersion(KeyProvider.getBaseName(versionName), versionName, bytes);
    }
    
    @Override
    public synchronized Metadata getMetadata(final String name) throws IOException {
        if (this.cache.containsKey(name)) {
            return this.cache.get(name);
        }
        final byte[] serialized = this.credentials.getSecretKey(new Text(name));
        if (serialized == null) {
            return null;
        }
        final Metadata result = new Metadata(serialized);
        this.cache.put(name, result);
        return result;
    }
    
    @Override
    public synchronized KeyVersion createKey(final String name, final byte[] material, final Options options) throws IOException {
        final Text nameT = new Text(name);
        if (this.credentials.getSecretKey(nameT) != null) {
            throw new IOException("Key " + name + " already exists in " + this);
        }
        if (options.getBitLength() != 8 * material.length) {
            throw new IOException("Wrong key length. Required " + options.getBitLength() + ", but got " + 8 * material.length);
        }
        final Metadata meta = new Metadata(options.getCipher(), options.getBitLength(), options.getDescription(), options.getAttributes(), new Date(), 1);
        this.cache.put(name, meta);
        final String versionName = KeyProvider.buildVersionName(name, 0);
        this.credentials.addSecretKey(nameT, meta.serialize());
        this.credentials.addSecretKey(new Text(versionName), material);
        return new KeyVersion(name, versionName, material);
    }
    
    @Override
    public synchronized void deleteKey(final String name) throws IOException {
        final Metadata meta = this.getMetadata(name);
        if (meta == null) {
            throw new IOException("Key " + name + " does not exist in " + this);
        }
        for (int v = 0; v < meta.getVersions(); ++v) {
            this.credentials.removeSecretKey(new Text(KeyProvider.buildVersionName(name, v)));
        }
        this.credentials.removeSecretKey(new Text(name));
        this.cache.remove(name);
    }
    
    @Override
    public synchronized KeyVersion rollNewVersion(final String name, final byte[] material) throws IOException {
        final Metadata meta = this.getMetadata(name);
        if (meta == null) {
            throw new IOException("Key " + name + " not found");
        }
        if (meta.getBitLength() != 8 * material.length) {
            throw new IOException("Wrong key length. Required " + meta.getBitLength() + ", but got " + 8 * material.length);
        }
        final int nextVersion = meta.addVersion();
        this.credentials.addSecretKey(new Text(name), meta.serialize());
        final String versionName = KeyProvider.buildVersionName(name, nextVersion);
        this.credentials.addSecretKey(new Text(versionName), material);
        return new KeyVersion(name, versionName, material);
    }
    
    @Override
    public String toString() {
        return "user:///";
    }
    
    @Override
    public synchronized void flush() {
        this.user.addCredentials(this.credentials);
    }
    
    @Override
    public synchronized List<String> getKeys() throws IOException {
        final List<String> list = new ArrayList<String>();
        final List<Text> keys = this.credentials.getAllSecretKeys();
        for (final Text key : keys) {
            if (key.find("@") == -1) {
                list.add(key.toString());
            }
        }
        return list;
    }
    
    @Override
    public synchronized List<KeyVersion> getKeyVersions(final String name) throws IOException {
        final List<KeyVersion> list = new ArrayList<KeyVersion>();
        final Metadata km = this.getMetadata(name);
        if (km != null) {
            for (int latestVersion = km.getVersions(), i = 0; i < latestVersion; ++i) {
                final KeyVersion v = this.getKeyVersion(KeyProvider.buildVersionName(name, i));
                if (v != null) {
                    list.add(v);
                }
            }
        }
        return list;
    }
    
    public static class Factory extends KeyProviderFactory
    {
        @Override
        public KeyProvider createProvider(final URI providerName, final Configuration conf) throws IOException {
            if ("user".equals(providerName.getScheme())) {
                return new UserProvider(conf, null);
            }
            return null;
        }
    }
}
