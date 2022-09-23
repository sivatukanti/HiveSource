// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto.key;

import java.util.List;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;

public abstract class KeyProviderExtension<E extends Extension> extends KeyProvider
{
    private KeyProvider keyProvider;
    private E extension;
    
    public KeyProviderExtension(final KeyProvider keyProvider, final E extensions) {
        super(keyProvider.getConf());
        this.keyProvider = keyProvider;
        this.extension = extensions;
    }
    
    protected E getExtension() {
        return this.extension;
    }
    
    protected KeyProvider getKeyProvider() {
        return this.keyProvider;
    }
    
    @Override
    public boolean isTransient() {
        return this.keyProvider.isTransient();
    }
    
    @Override
    public Metadata[] getKeysMetadata(final String... names) throws IOException {
        return this.keyProvider.getKeysMetadata(names);
    }
    
    @Override
    public KeyVersion getCurrentKey(final String name) throws IOException {
        return this.keyProvider.getCurrentKey(name);
    }
    
    @Override
    public KeyVersion createKey(final String name, final Options options) throws NoSuchAlgorithmException, IOException {
        return this.keyProvider.createKey(name, options);
    }
    
    @Override
    public KeyVersion rollNewVersion(final String name) throws NoSuchAlgorithmException, IOException {
        return this.keyProvider.rollNewVersion(name);
    }
    
    @Override
    public KeyVersion getKeyVersion(final String versionName) throws IOException {
        return this.keyProvider.getKeyVersion(versionName);
    }
    
    @Override
    public List<String> getKeys() throws IOException {
        return this.keyProvider.getKeys();
    }
    
    @Override
    public List<KeyVersion> getKeyVersions(final String name) throws IOException {
        return this.keyProvider.getKeyVersions(name);
    }
    
    @Override
    public Metadata getMetadata(final String name) throws IOException {
        return this.keyProvider.getMetadata(name);
    }
    
    @Override
    public KeyVersion createKey(final String name, final byte[] material, final Options options) throws IOException {
        return this.keyProvider.createKey(name, material, options);
    }
    
    @Override
    public void deleteKey(final String name) throws IOException {
        this.keyProvider.deleteKey(name);
    }
    
    @Override
    public KeyVersion rollNewVersion(final String name, final byte[] material) throws IOException {
        return this.keyProvider.rollNewVersion(name, material);
    }
    
    @Override
    public void invalidateCache(final String name) throws IOException {
        this.keyProvider.invalidateCache(name);
    }
    
    @Override
    public void flush() throws IOException {
        this.keyProvider.flush();
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + this.keyProvider.toString();
    }
    
    public interface Extension
    {
    }
}
