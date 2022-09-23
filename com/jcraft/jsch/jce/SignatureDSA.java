// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch.jce;

import java.security.PrivateKey;
import java.security.spec.DSAPrivateKeySpec;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.DSAPublicKeySpec;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.Signature;

public class SignatureDSA implements com.jcraft.jsch.SignatureDSA
{
    java.security.Signature signature;
    KeyFactory keyFactory;
    
    public void init() throws Exception {
        this.signature = java.security.Signature.getInstance("SHA1withDSA");
        this.keyFactory = KeyFactory.getInstance("DSA");
    }
    
    public void setPubKey(final byte[] y, final byte[] p, final byte[] q, final byte[] g) throws Exception {
        final DSAPublicKeySpec dsaPubKeySpec = new DSAPublicKeySpec(new BigInteger(y), new BigInteger(p), new BigInteger(q), new BigInteger(g));
        final PublicKey pubKey = this.keyFactory.generatePublic(dsaPubKeySpec);
        this.signature.initVerify(pubKey);
    }
    
    public void setPrvKey(final byte[] x, final byte[] p, final byte[] q, final byte[] g) throws Exception {
        final DSAPrivateKeySpec dsaPrivKeySpec = new DSAPrivateKeySpec(new BigInteger(x), new BigInteger(p), new BigInteger(q), new BigInteger(g));
        final PrivateKey prvKey = this.keyFactory.generatePrivate(dsaPrivKeySpec);
        this.signature.initSign(prvKey);
    }
    
    public byte[] sign() throws Exception {
        final byte[] sig = this.signature.sign();
        int len = 0;
        int index = 3;
        len = (sig[index++] & 0xFF);
        final byte[] r = new byte[len];
        System.arraycopy(sig, index, r, 0, r.length);
        index = index + len + 1;
        len = (sig[index++] & 0xFF);
        final byte[] s = new byte[len];
        System.arraycopy(sig, index, s, 0, s.length);
        final byte[] result = new byte[40];
        System.arraycopy(r, (r.length > 20) ? 1 : 0, result, (r.length > 20) ? 0 : (20 - r.length), (r.length > 20) ? 20 : r.length);
        System.arraycopy(s, (s.length > 20) ? 1 : 0, result, (s.length > 20) ? 20 : (40 - s.length), (s.length > 20) ? 20 : s.length);
        return result;
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
        final int frst = ((sig[0] & 0x80) != 0x0) ? 1 : 0;
        final int scnd = ((sig[20] & 0x80) != 0x0) ? 1 : 0;
        final int length = sig.length + 6 + frst + scnd;
        final byte[] tmp = new byte[length];
        tmp[0] = 48;
        tmp[1] = 44;
        final byte[] array = tmp;
        final int n = 1;
        array[n] += (byte)frst;
        final byte[] array2 = tmp;
        final int n2 = 1;
        array2[n2] += (byte)scnd;
        tmp[2] = 2;
        tmp[3] = 20;
        final byte[] array3 = tmp;
        final int n3 = 3;
        array3[n3] += (byte)frst;
        System.arraycopy(sig, 0, tmp, 4 + frst, 20);
        tmp[4 + tmp[3]] = 2;
        tmp[5 + tmp[3]] = 20;
        final byte[] array4 = tmp;
        final int n4 = 5 + tmp[3];
        array4[n4] += (byte)scnd;
        System.arraycopy(sig, 20, tmp, 6 + tmp[3] + scnd, 20);
        sig = tmp;
        return this.signature.verify(sig);
    }
}
