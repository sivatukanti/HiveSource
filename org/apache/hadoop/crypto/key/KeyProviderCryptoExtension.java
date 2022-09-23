// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto.key;

import java.util.ListIterator;
import org.apache.hadoop.crypto.Decryptor;
import java.nio.ByteBuffer;
import org.apache.hadoop.crypto.Encryptor;
import org.apache.hadoop.crypto.CryptoCodec;
import com.google.common.base.Preconditions;
import java.security.SecureRandom;
import java.util.List;
import java.security.GeneralSecurityException;
import java.io.IOException;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class KeyProviderCryptoExtension extends KeyProviderExtension<CryptoExtension>
{
    public static final String EEK = "EEK";
    public static final String EK = "EK";
    
    protected KeyProviderCryptoExtension(final KeyProvider keyProvider, final CryptoExtension extension) {
        super(keyProvider, extension);
    }
    
    public void warmUpEncryptedKeys(final String... keyNames) throws IOException {
        this.getExtension().warmUpEncryptedKeys(keyNames);
    }
    
    public EncryptedKeyVersion generateEncryptedKey(final String encryptionKeyName) throws IOException, GeneralSecurityException {
        return this.getExtension().generateEncryptedKey(encryptionKeyName);
    }
    
    public KeyVersion decryptEncryptedKey(final EncryptedKeyVersion encryptedKey) throws IOException, GeneralSecurityException {
        return this.getExtension().decryptEncryptedKey(encryptedKey);
    }
    
    public EncryptedKeyVersion reencryptEncryptedKey(final EncryptedKeyVersion ekv) throws IOException, GeneralSecurityException {
        return this.getExtension().reencryptEncryptedKey(ekv);
    }
    
    public void drain(final String keyName) {
        this.getExtension().drain(keyName);
    }
    
    public void reencryptEncryptedKeys(final List<EncryptedKeyVersion> ekvs) throws IOException, GeneralSecurityException {
        this.getExtension().reencryptEncryptedKeys(ekvs);
    }
    
    public static KeyProviderCryptoExtension createKeyProviderCryptoExtension(final KeyProvider keyProvider) {
        CryptoExtension cryptoExtension = null;
        if (keyProvider instanceof CryptoExtension) {
            cryptoExtension = (CryptoExtension)keyProvider;
        }
        else if (keyProvider instanceof KeyProviderExtension && ((KeyProviderExtension)keyProvider).getKeyProvider() instanceof CryptoExtension) {
            final KeyProviderExtension keyProviderExtension = (KeyProviderExtension)keyProvider;
            cryptoExtension = (CryptoExtension)keyProviderExtension.getKeyProvider();
        }
        else {
            cryptoExtension = new DefaultCryptoExtension(keyProvider);
        }
        return new KeyProviderCryptoExtension(keyProvider, cryptoExtension);
    }
    
    @Override
    public void close() throws IOException {
        final KeyProvider provider = this.getKeyProvider();
        if (provider != null && provider != this) {
            provider.close();
        }
    }
    
    public static class EncryptedKeyVersion
    {
        private String encryptionKeyName;
        private String encryptionKeyVersionName;
        private byte[] encryptedKeyIv;
        private KeyVersion encryptedKeyVersion;
        
        protected EncryptedKeyVersion(final String keyName, final String encryptionKeyVersionName, final byte[] encryptedKeyIv, final KeyVersion encryptedKeyVersion) {
            this.encryptionKeyName = ((keyName == null) ? null : keyName.intern());
            this.encryptionKeyVersionName = ((encryptionKeyVersionName == null) ? null : encryptionKeyVersionName.intern());
            this.encryptedKeyIv = encryptedKeyIv;
            this.encryptedKeyVersion = encryptedKeyVersion;
        }
        
        public static EncryptedKeyVersion createForDecryption(final String keyName, final String encryptionKeyVersionName, final byte[] encryptedKeyIv, final byte[] encryptedKeyMaterial) {
            final KeyVersion encryptedKeyVersion = new KeyVersion(null, "EEK", encryptedKeyMaterial);
            return new EncryptedKeyVersion(keyName, encryptionKeyVersionName, encryptedKeyIv, encryptedKeyVersion);
        }
        
        public String getEncryptionKeyName() {
            return this.encryptionKeyName;
        }
        
        public String getEncryptionKeyVersionName() {
            return this.encryptionKeyVersionName;
        }
        
        public byte[] getEncryptedKeyIv() {
            return this.encryptedKeyIv;
        }
        
        public KeyVersion getEncryptedKeyVersion() {
            return this.encryptedKeyVersion;
        }
        
        protected static byte[] deriveIV(final byte[] encryptedKeyIV) {
            final byte[] rIv = new byte[encryptedKeyIV.length];
            for (int i = 0; i < encryptedKeyIV.length; ++i) {
                rIv[i] = (byte)(encryptedKeyIV[i] ^ 0xFF);
            }
            return rIv;
        }
    }
    
    private static class DefaultCryptoExtension implements CryptoExtension
    {
        private final KeyProvider keyProvider;
        private static final ThreadLocal<SecureRandom> RANDOM;
        
        private DefaultCryptoExtension(final KeyProvider keyProvider) {
            this.keyProvider = keyProvider;
        }
        
        @Override
        public EncryptedKeyVersion generateEncryptedKey(final String encryptionKeyName) throws IOException, GeneralSecurityException {
            final KeyVersion encryptionKey = this.keyProvider.getCurrentKey(encryptionKeyName);
            Preconditions.checkNotNull(encryptionKey, "No KeyVersion exists for key '%s' ", encryptionKeyName);
            final CryptoCodec cc = CryptoCodec.getInstance(this.keyProvider.getConf());
            try {
                final byte[] newKey = new byte[encryptionKey.getMaterial().length];
                cc.generateSecureRandom(newKey);
                final byte[] iv = new byte[cc.getCipherSuite().getAlgorithmBlockSize()];
                cc.generateSecureRandom(iv);
                final Encryptor encryptor = cc.createEncryptor();
                return this.generateEncryptedKey(encryptor, encryptionKey, newKey, iv);
            }
            finally {
                cc.close();
            }
        }
        
        private EncryptedKeyVersion generateEncryptedKey(final Encryptor encryptor, final KeyVersion encryptionKey, final byte[] key, final byte[] iv) throws IOException, GeneralSecurityException {
            final byte[] encryptionIV = EncryptedKeyVersion.deriveIV(iv);
            encryptor.init(encryptionKey.getMaterial(), encryptionIV);
            final int keyLen = key.length;
            final ByteBuffer bbIn = ByteBuffer.allocateDirect(keyLen);
            final ByteBuffer bbOut = ByteBuffer.allocateDirect(keyLen);
            bbIn.put(key);
            bbIn.flip();
            encryptor.encrypt(bbIn, bbOut);
            bbOut.flip();
            final byte[] encryptedKey = new byte[keyLen];
            bbOut.get(encryptedKey);
            return new EncryptedKeyVersion(encryptionKey.getName(), encryptionKey.getVersionName(), iv, new KeyVersion(encryptionKey.getName(), "EEK", encryptedKey));
        }
        
        @Override
        public EncryptedKeyVersion reencryptEncryptedKey(final EncryptedKeyVersion ekv) throws IOException, GeneralSecurityException {
            final String ekName = ekv.getEncryptionKeyName();
            final KeyVersion ekNow = this.keyProvider.getCurrentKey(ekName);
            Preconditions.checkNotNull(ekNow, "KeyVersion name '%s' does not exist", ekName);
            Preconditions.checkArgument(ekv.getEncryptedKeyVersion().getVersionName().equals("EEK"), "encryptedKey version name must be '%s', but found '%s'", "EEK", ekv.getEncryptedKeyVersion().getVersionName());
            if (ekv.getEncryptedKeyVersion().equals(ekNow)) {
                return ekv;
            }
            final KeyVersion dek = this.decryptEncryptedKey(ekv);
            final CryptoCodec cc = CryptoCodec.getInstance(this.keyProvider.getConf());
            try {
                final Encryptor encryptor = cc.createEncryptor();
                return this.generateEncryptedKey(encryptor, ekNow, dek.getMaterial(), ekv.getEncryptedKeyIv());
            }
            finally {
                cc.close();
            }
        }
        
        @Override
        public void reencryptEncryptedKeys(final List<EncryptedKeyVersion> ekvs) throws IOException, GeneralSecurityException {
            Preconditions.checkNotNull(ekvs, (Object)"Input list is null");
            KeyVersion ekNow = null;
            Decryptor decryptor = null;
            Encryptor encryptor = null;
            try (final CryptoCodec cc = CryptoCodec.getInstance(this.keyProvider.getConf())) {
                decryptor = cc.createDecryptor();
                encryptor = cc.createEncryptor();
                final ListIterator<EncryptedKeyVersion> iter = ekvs.listIterator();
                while (iter.hasNext()) {
                    final EncryptedKeyVersion ekv = iter.next();
                    Preconditions.checkNotNull(ekv, (Object)"EncryptedKeyVersion is null");
                    final String ekName = ekv.getEncryptionKeyName();
                    Preconditions.checkNotNull(ekName, (Object)"Key name is null");
                    Preconditions.checkNotNull(ekv.getEncryptedKeyVersion(), (Object)"EncryptedKeyVersion is null");
                    Preconditions.checkArgument(ekv.getEncryptedKeyVersion().getVersionName().equals("EEK"), "encryptedKey version name must be '%s', but found '%s'", "EEK", ekv.getEncryptedKeyVersion().getVersionName());
                    if (ekNow == null) {
                        ekNow = this.keyProvider.getCurrentKey(ekName);
                        Preconditions.checkNotNull(ekNow, "Key name '%s' does not exist", ekName);
                    }
                    else {
                        Preconditions.checkArgument(ekNow.getName().equals(ekName), "All keys must have the same key name. Expected '%s' but found '%s'", ekNow.getName(), ekName);
                    }
                    final String encryptionKeyVersionName = ekv.getEncryptionKeyVersionName();
                    final KeyVersion encryptionKey = this.keyProvider.getKeyVersion(encryptionKeyVersionName);
                    Preconditions.checkNotNull(encryptionKey, "KeyVersion name '%s' does not exist", encryptionKeyVersionName);
                    if (encryptionKey.equals(ekNow)) {
                        continue;
                    }
                    final KeyVersion ek = this.decryptEncryptedKey(decryptor, encryptionKey, ekv);
                    iter.set(this.generateEncryptedKey(encryptor, ekNow, ek.getMaterial(), ekv.getEncryptedKeyIv()));
                }
            }
        }
        
        private KeyVersion decryptEncryptedKey(final Decryptor decryptor, final KeyVersion encryptionKey, final EncryptedKeyVersion encryptedKeyVersion) throws IOException, GeneralSecurityException {
            final byte[] encryptionIV = EncryptedKeyVersion.deriveIV(encryptedKeyVersion.getEncryptedKeyIv());
            decryptor.init(encryptionKey.getMaterial(), encryptionIV);
            final KeyVersion encryptedKV = encryptedKeyVersion.getEncryptedKeyVersion();
            final int keyLen = encryptedKV.getMaterial().length;
            final ByteBuffer bbIn = ByteBuffer.allocateDirect(keyLen);
            final ByteBuffer bbOut = ByteBuffer.allocateDirect(keyLen);
            bbIn.put(encryptedKV.getMaterial());
            bbIn.flip();
            decryptor.decrypt(bbIn, bbOut);
            bbOut.flip();
            final byte[] decryptedKey = new byte[keyLen];
            bbOut.get(decryptedKey);
            return new KeyVersion(encryptionKey.getName(), "EK", decryptedKey);
        }
        
        @Override
        public KeyVersion decryptEncryptedKey(final EncryptedKeyVersion encryptedKeyVersion) throws IOException, GeneralSecurityException {
            final String encryptionKeyVersionName = encryptedKeyVersion.getEncryptionKeyVersionName();
            final KeyVersion encryptionKey = this.keyProvider.getKeyVersion(encryptionKeyVersionName);
            Preconditions.checkNotNull(encryptionKey, "KeyVersion name '%s' does not exist", encryptionKeyVersionName);
            Preconditions.checkArgument(encryptedKeyVersion.getEncryptedKeyVersion().getVersionName().equals("EEK"), "encryptedKey version name must be '%s', but found '%s'", "EEK", encryptedKeyVersion.getEncryptedKeyVersion().getVersionName());
            try (final CryptoCodec cc = CryptoCodec.getInstance(this.keyProvider.getConf())) {
                final Decryptor decryptor = cc.createDecryptor();
                return this.decryptEncryptedKey(decryptor, encryptionKey, encryptedKeyVersion);
            }
        }
        
        @Override
        public void warmUpEncryptedKeys(final String... keyNames) throws IOException {
        }
        
        @Override
        public void drain(final String keyName) {
        }
        
        static {
            RANDOM = new ThreadLocal<SecureRandom>() {
                @Override
                protected SecureRandom initialValue() {
                    return new SecureRandom();
                }
            };
        }
    }
    
    public interface CryptoExtension extends Extension
    {
        void warmUpEncryptedKeys(final String... p0) throws IOException;
        
        void drain(final String p0);
        
        EncryptedKeyVersion generateEncryptedKey(final String p0) throws IOException, GeneralSecurityException;
        
        KeyVersion decryptEncryptedKey(final EncryptedKeyVersion p0) throws IOException, GeneralSecurityException;
        
        EncryptedKeyVersion reencryptEncryptedKey(final EncryptedKeyVersion p0) throws IOException, GeneralSecurityException;
        
        void reencryptEncryptedKeys(final List<EncryptedKeyVersion> p0) throws IOException, GeneralSecurityException;
    }
}
