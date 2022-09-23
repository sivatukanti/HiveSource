// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import com.google.common.base.Preconditions;
import javax.crypto.Cipher;
import org.slf4j.LoggerFactory;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class JceAesCtrCryptoCodec extends AesCtrCryptoCodec
{
    private static final Logger LOG;
    private Configuration conf;
    private String provider;
    private SecureRandom random;
    
    @Override
    public Configuration getConf() {
        return this.conf;
    }
    
    @Override
    public void setConf(final Configuration conf) {
        this.conf = conf;
        this.provider = conf.get("hadoop.security.crypto.jce.provider");
        final String secureRandomAlg = conf.get("hadoop.security.java.secure.random.algorithm", "SHA1PRNG");
        try {
            this.random = ((this.provider != null) ? SecureRandom.getInstance(secureRandomAlg, this.provider) : SecureRandom.getInstance(secureRandomAlg));
        }
        catch (GeneralSecurityException e) {
            JceAesCtrCryptoCodec.LOG.warn(e.getMessage());
            this.random = new SecureRandom();
        }
    }
    
    @Override
    public Encryptor createEncryptor() throws GeneralSecurityException {
        return new JceAesCtrCipher(1, this.provider);
    }
    
    @Override
    public Decryptor createDecryptor() throws GeneralSecurityException {
        return new JceAesCtrCipher(2, this.provider);
    }
    
    @Override
    public void generateSecureRandom(final byte[] bytes) {
        this.random.nextBytes(bytes);
    }
    
    static {
        LOG = LoggerFactory.getLogger(JceAesCtrCryptoCodec.class.getName());
    }
    
    private static class JceAesCtrCipher implements Encryptor, Decryptor
    {
        private final Cipher cipher;
        private final int mode;
        private boolean contextReset;
        
        public JceAesCtrCipher(final int mode, final String provider) throws GeneralSecurityException {
            this.contextReset = false;
            this.mode = mode;
            if (provider == null || provider.isEmpty()) {
                this.cipher = Cipher.getInstance(AesCtrCryptoCodec.SUITE.getName());
            }
            else {
                this.cipher = Cipher.getInstance(AesCtrCryptoCodec.SUITE.getName(), provider);
            }
        }
        
        @Override
        public void init(final byte[] key, final byte[] iv) throws IOException {
            Preconditions.checkNotNull(key);
            Preconditions.checkNotNull(iv);
            this.contextReset = false;
            try {
                this.cipher.init(this.mode, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
            }
            catch (Exception e) {
                throw new IOException(e);
            }
        }
        
        @Override
        public void encrypt(final ByteBuffer inBuffer, final ByteBuffer outBuffer) throws IOException {
            this.process(inBuffer, outBuffer);
        }
        
        @Override
        public void decrypt(final ByteBuffer inBuffer, final ByteBuffer outBuffer) throws IOException {
            this.process(inBuffer, outBuffer);
        }
        
        private void process(final ByteBuffer inBuffer, final ByteBuffer outBuffer) throws IOException {
            try {
                final int inputSize = inBuffer.remaining();
                final int n = this.cipher.update(inBuffer, outBuffer);
                if (n < inputSize) {
                    this.contextReset = true;
                    this.cipher.doFinal(inBuffer, outBuffer);
                }
            }
            catch (Exception e) {
                throw new IOException(e);
            }
        }
        
        @Override
        public boolean isContextReset() {
            return this.contextReset;
        }
    }
}
