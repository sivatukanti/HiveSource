// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.ssl;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import javax.crypto.SecretKey;
import javax.crypto.Cipher;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.net.ssl.SSLSessionContext;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import org.jboss.netty.buffer.ChannelBuffer;
import java.util.Iterator;
import java.security.SecureRandom;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManagerFactory;
import java.security.Key;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import java.security.cert.Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.KeyFactory;
import java.security.cert.CertificateFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.Security;
import java.util.Collections;
import java.util.ArrayList;
import javax.net.ssl.SSLException;
import java.io.File;
import java.util.List;
import javax.net.ssl.SSLContext;

public final class JdkSslServerContext extends JdkSslContext
{
    private final SSLContext ctx;
    private final List<String> nextProtocols;
    
    public JdkSslServerContext(final File certChainFile, final File keyFile) throws SSLException {
        this(certChainFile, keyFile, null);
    }
    
    public JdkSslServerContext(final File certChainFile, final File keyFile, final String keyPassword) throws SSLException {
        this(null, certChainFile, keyFile, keyPassword, null, null, 0L, 0L);
    }
    
    public JdkSslServerContext(final SslBufferPool bufPool, final File certChainFile, final File keyFile, String keyPassword, final Iterable<String> ciphers, final Iterable<String> nextProtocols, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        super(bufPool, ciphers);
        if (certChainFile == null) {
            throw new NullPointerException("certChainFile");
        }
        if (keyFile == null) {
            throw new NullPointerException("keyFile");
        }
        if (keyPassword == null) {
            keyPassword = "";
        }
        if (nextProtocols != null && nextProtocols.iterator().hasNext()) {
            if (!JettyNpnSslEngine.isAvailable()) {
                throw new SSLException("NPN/ALPN unsupported: " + nextProtocols);
            }
            final List<String> list = new ArrayList<String>();
            for (final String p : nextProtocols) {
                if (p == null) {
                    break;
                }
                list.add(p);
            }
            this.nextProtocols = Collections.unmodifiableList((List<? extends String>)list);
        }
        else {
            this.nextProtocols = Collections.emptyList();
        }
        String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX509";
        }
        try {
            final KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(null, null);
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            final KeyFactory rsaKF = KeyFactory.getInstance("RSA");
            final KeyFactory dsaKF = KeyFactory.getInstance("DSA");
            final ChannelBuffer encodedKeyBuf = PemReader.readPrivateKey(keyFile);
            final byte[] encodedKey = new byte[encodedKeyBuf.readableBytes()];
            encodedKeyBuf.readBytes(encodedKey);
            final char[] keyPasswordChars = keyPassword.toCharArray();
            final PKCS8EncodedKeySpec encodedKeySpec = generateKeySpec(keyPasswordChars, encodedKey);
            PrivateKey key;
            try {
                key = rsaKF.generatePrivate(encodedKeySpec);
            }
            catch (InvalidKeySpecException ignore) {
                key = dsaKF.generatePrivate(encodedKeySpec);
            }
            final List<Certificate> certChain = new ArrayList<Certificate>();
            for (final ChannelBuffer buf : PemReader.readCertificates(certChainFile)) {
                certChain.add(cf.generateCertificate(new ChannelBufferInputStream(buf)));
            }
            ks.setKeyEntry("key", key, keyPasswordChars, certChain.toArray(new Certificate[certChain.size()]));
            final KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
            kmf.init(ks, keyPasswordChars);
            (this.ctx = SSLContext.getInstance("TLS")).init(kmf.getKeyManagers(), null, null);
            final SSLSessionContext sessCtx = this.ctx.getServerSessionContext();
            if (sessionCacheSize > 0L) {
                sessCtx.setSessionCacheSize((int)Math.min(sessionCacheSize, 2147483647L));
            }
            if (sessionTimeout > 0L) {
                sessCtx.setSessionTimeout((int)Math.min(sessionTimeout, 2147483647L));
            }
        }
        catch (Exception e) {
            throw new SSLException("failed to initialize the server-side SSL context", e);
        }
    }
    
    @Override
    public boolean isClient() {
        return false;
    }
    
    @Override
    public List<String> nextProtocols() {
        return this.nextProtocols;
    }
    
    @Override
    public SSLContext context() {
        return this.ctx;
    }
    
    private static PKCS8EncodedKeySpec generateKeySpec(final char[] password, final byte[] key) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException {
        if (password == null || password.length == 0) {
            return new PKCS8EncodedKeySpec(key);
        }
        final EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(key);
        final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(encryptedPrivateKeyInfo.getAlgName());
        final PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
        final SecretKey pbeKey = keyFactory.generateSecret(pbeKeySpec);
        final Cipher cipher = Cipher.getInstance(encryptedPrivateKeyInfo.getAlgName());
        cipher.init(2, pbeKey, encryptedPrivateKeyInfo.getAlgParameters());
        return encryptedPrivateKeyInfo.getKeySpec(cipher);
    }
}
