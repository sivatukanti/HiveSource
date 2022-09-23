// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch.jce;

import com.jcraft.jsch.JSchException;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import javax.crypto.spec.DHPublicKeySpec;
import java.security.KeyFactory;
import java.security.KeyPair;
import javax.crypto.interfaces.DHPublicKey;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.KeyAgreement;
import java.security.KeyPairGenerator;
import java.math.BigInteger;

public class DH implements com.jcraft.jsch.DH
{
    BigInteger p;
    BigInteger g;
    BigInteger e;
    byte[] e_array;
    BigInteger f;
    BigInteger K;
    byte[] K_array;
    private KeyPairGenerator myKpairGen;
    private KeyAgreement myKeyAgree;
    
    public void init() throws Exception {
        this.myKpairGen = KeyPairGenerator.getInstance("DH");
        this.myKeyAgree = KeyAgreement.getInstance("DH");
    }
    
    public byte[] getE() throws Exception {
        if (this.e == null) {
            final DHParameterSpec dhSkipParamSpec = new DHParameterSpec(this.p, this.g);
            this.myKpairGen.initialize(dhSkipParamSpec);
            final KeyPair myKpair = this.myKpairGen.generateKeyPair();
            this.myKeyAgree.init(myKpair.getPrivate());
            this.e = ((DHPublicKey)myKpair.getPublic()).getY();
            this.e_array = this.e.toByteArray();
        }
        return this.e_array;
    }
    
    public byte[] getK() throws Exception {
        if (this.K == null) {
            final KeyFactory myKeyFac = KeyFactory.getInstance("DH");
            final DHPublicKeySpec keySpec = new DHPublicKeySpec(this.f, this.p, this.g);
            final PublicKey yourPubKey = myKeyFac.generatePublic(keySpec);
            this.myKeyAgree.doPhase(yourPubKey, true);
            final byte[] mySharedSecret = this.myKeyAgree.generateSecret();
            this.K = new BigInteger(1, mySharedSecret);
            this.K_array = this.K.toByteArray();
            this.K_array = mySharedSecret;
        }
        return this.K_array;
    }
    
    public void setP(final byte[] p) {
        this.setP(new BigInteger(1, p));
    }
    
    public void setG(final byte[] g) {
        this.setG(new BigInteger(1, g));
    }
    
    public void setF(final byte[] f) {
        this.setF(new BigInteger(1, f));
    }
    
    void setP(final BigInteger p) {
        this.p = p;
    }
    
    void setG(final BigInteger g) {
        this.g = g;
    }
    
    void setF(final BigInteger f) {
        this.f = f;
    }
    
    public void checkRange() throws Exception {
    }
    
    private void checkRange(final BigInteger tmp) throws Exception {
        final BigInteger one = BigInteger.ONE;
        final BigInteger p_1 = this.p.subtract(one);
        if (one.compareTo(tmp) >= 0 || tmp.compareTo(p_1) >= 0) {
            throw new JSchException("invalid DH value");
        }
    }
}
