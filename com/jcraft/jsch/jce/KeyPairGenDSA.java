// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch.jce;

import java.security.interfaces.DSAParams;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyPair;
import java.security.interfaces.DSAKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.DSAPrivateKey;
import java.security.SecureRandom;
import java.security.KeyPairGenerator;

public class KeyPairGenDSA implements com.jcraft.jsch.KeyPairGenDSA
{
    byte[] x;
    byte[] y;
    byte[] p;
    byte[] q;
    byte[] g;
    
    public void init(final int key_size) throws Exception {
        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
        keyGen.initialize(key_size, new SecureRandom());
        final KeyPair pair = keyGen.generateKeyPair();
        final PublicKey pubKey = pair.getPublic();
        final PrivateKey prvKey = pair.getPrivate();
        this.x = ((DSAPrivateKey)prvKey).getX().toByteArray();
        this.y = ((DSAPublicKey)pubKey).getY().toByteArray();
        final DSAParams params = ((DSAKey)prvKey).getParams();
        this.p = params.getP().toByteArray();
        this.q = params.getQ().toByteArray();
        this.g = params.getG().toByteArray();
    }
    
    public byte[] getX() {
        return this.x;
    }
    
    public byte[] getY() {
        return this.y;
    }
    
    public byte[] getP() {
        return this.p;
    }
    
    public byte[] getQ() {
        return this.q;
    }
    
    public byte[] getG() {
        return this.g;
    }
}
