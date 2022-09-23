// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token;

import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import javax.crypto.SecretKey;
import java.io.IOException;
import org.apache.hadoop.ipc.RetriableException;
import org.apache.hadoop.ipc.StandbyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class SecretManager<T extends TokenIdentifier>
{
    private static final String DEFAULT_HMAC_ALGORITHM = "HmacSHA1";
    private static final int KEY_LENGTH = 64;
    private static final ThreadLocal<Mac> threadLocalMac;
    private final KeyGenerator keyGen;
    
    public SecretManager() {
        try {
            (this.keyGen = KeyGenerator.getInstance("HmacSHA1")).init(64);
        }
        catch (NoSuchAlgorithmException nsa) {
            throw new IllegalArgumentException("Can't find HmacSHA1 algorithm.");
        }
    }
    
    protected abstract byte[] createPassword(final T p0);
    
    public abstract byte[] retrievePassword(final T p0) throws InvalidToken;
    
    public byte[] retriableRetrievePassword(final T identifier) throws InvalidToken, StandbyException, RetriableException, IOException {
        return this.retrievePassword(identifier);
    }
    
    public abstract T createIdentifier();
    
    public void checkAvailableForRead() throws StandbyException {
    }
    
    protected SecretKey generateSecret() {
        final SecretKey key;
        synchronized (this.keyGen) {
            key = this.keyGen.generateKey();
        }
        return key;
    }
    
    protected static byte[] createPassword(final byte[] identifier, final SecretKey key) {
        final Mac mac = SecretManager.threadLocalMac.get();
        try {
            mac.init(key);
        }
        catch (InvalidKeyException ike) {
            throw new IllegalArgumentException("Invalid key to HMAC computation", ike);
        }
        return mac.doFinal(identifier);
    }
    
    protected static SecretKey createSecretKey(final byte[] key) {
        return new SecretKeySpec(key, "HmacSHA1");
    }
    
    static {
        threadLocalMac = new ThreadLocal<Mac>() {
            @Override
            protected Mac initialValue() {
                try {
                    return Mac.getInstance("HmacSHA1");
                }
                catch (NoSuchAlgorithmException nsa) {
                    throw new IllegalArgumentException("Can't find HmacSHA1 algorithm.");
                }
            }
        };
    }
    
    @InterfaceStability.Evolving
    public static class InvalidToken extends IOException
    {
        public InvalidToken(final String msg) {
            super(msg);
        }
    }
}
