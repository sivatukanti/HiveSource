// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch.jce;

import java.security.PrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.Signature;

public class SignatureRSA implements com.jcraft.jsch.SignatureRSA
{
    java.security.Signature signature;
    KeyFactory keyFactory;
    
    public void init() throws Exception {
        this.signature = java.security.Signature.getInstance("SHA1withRSA");
        this.keyFactory = KeyFactory.getInstance("RSA");
    }
    
    public void setPubKey(final byte[] e, final byte[] n) throws Exception {
        final RSAPublicKeySpec rsaPubKeySpec = new RSAPublicKeySpec(new BigInteger(n), new BigInteger(e));
        final PublicKey pubKey = this.keyFactory.generatePublic(rsaPubKeySpec);
        this.signature.initVerify(pubKey);
    }
    
    public void setPrvKey(final byte[] d, final byte[] n) throws Exception {
        final RSAPrivateKeySpec rsaPrivKeySpec = new RSAPrivateKeySpec(new BigInteger(n), new BigInteger(d));
        final PrivateKey prvKey = this.keyFactory.generatePrivate(rsaPrivKeySpec);
        this.signature.initSign(prvKey);
    }
    
    public byte[] sign() throws Exception {
        final byte[] sig = this.signature.sign();
        return sig;
    }
    
    public void update(final byte[] foo) throws Exception {
        this.signature.update(foo);
    }
    
    public boolean verify(byte[] sig) throws Exception {
        int i = 0;
        int j = 0;
        if (sig[0] == 0 && sig[1] == 0 && sig[2] == 0) {
            j = ((sig[i++] << 24 & 0xFF000000) | (sig[i++] << 16 & 0xFF0000) | (sig[i++] << 8 & 0xFF00) | (sig[i++] & 0xFF));
            i += j;
            j = ((sig[i++] << 24 & 0xFF000000) | (sig[i++] << 16 & 0xFF0000) | (sig[i++] << 8 & 0xFF00) | (sig[i++] & 0xFF));
            final byte[] tmp = new byte[j];
            System.arraycopy(sig, i, tmp, 0, j);
            sig = tmp;
        }
        return this.signature.verify(sig);
    }
}
