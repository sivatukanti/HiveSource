// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto;

import java.nio.ByteBuffer;
import com.google.common.base.Preconditions;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.Closeable;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.crypto.random.OsSecureRandom;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class OpensslAesCtrCryptoCodec extends AesCtrCryptoCodec
{
    private static final Logger LOG;
    private Configuration conf;
    private Random random;
    
    public OpensslAesCtrCryptoCodec() {
        final String loadingFailureReason = OpensslCipher.getLoadingFailureReason();
        if (loadingFailureReason != null) {
            throw new RuntimeException(loadingFailureReason);
        }
    }
    
    @Override
    public void setConf(final Configuration conf) {
        this.conf = conf;
        final Class<? extends Random> klass = conf.getClass("hadoop.security.secure.random.impl", OsSecureRandom.class, Random.class);
        try {
            this.random = ReflectionUtils.newInstance(klass, conf);
            if (OpensslAesCtrCryptoCodec.LOG.isDebugEnabled()) {
                OpensslAesCtrCryptoCodec.LOG.debug("Using " + klass.getName() + " as random number generator.");
            }
        }
        catch (Exception e) {
            OpensslAesCtrCryptoCodec.LOG.info("Unable to use " + klass.getName() + ".  Falling back to Java SecureRandom.", e);
            this.random = new SecureRandom();
        }
    }
    
    @Override
    public Configuration getConf() {
        return this.conf;
    }
    
    @Override
    public Encryptor createEncryptor() throws GeneralSecurityException {
        return new OpensslAesCtrCipher(1);
    }
    
    @Override
    public Decryptor createDecryptor() throws GeneralSecurityException {
        return new OpensslAesCtrCipher(0);
    }
    
    @Override
    public void generateSecureRandom(final byte[] bytes) {
        this.random.nextBytes(bytes);
    }
    
    @Override
    public void close() throws IOException {
        try {
            final Closeable r = (Closeable)this.random;
            r.close();
        }
        catch (ClassCastException ex) {}
        super.close();
    }
    
    static {
        LOG = LoggerFactory.getLogger(OpensslAesCtrCryptoCodec.class.getName());
    }
    
    private static class OpensslAesCtrCipher implements Encryptor, Decryptor
    {
        private final OpensslCipher cipher;
        private final int mode;
        private boolean contextReset;
        
        public OpensslAesCtrCipher(final int mode) throws GeneralSecurityException {
            this.contextReset = false;
            this.mode = mode;
            this.cipher = OpensslCipher.getInstance(AesCtrCryptoCodec.SUITE.getName());
        }
        
        @Override
        public void init(final byte[] key, final byte[] iv) throws IOException {
            Preconditions.checkNotNull(key);
            Preconditions.checkNotNull(iv);
            this.contextReset = false;
            this.cipher.init(this.mode, key, iv);
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
                    this.cipher.doFinal(outBuffer);
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
