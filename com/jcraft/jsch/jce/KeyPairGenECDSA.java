// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch.jce;

import java.security.spec.ECPoint;
import java.security.KeyPair;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.KeyPairGenerator;
import com.jcraft.jsch.JSchException;
import java.security.spec.ECParameterSpec;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

public class KeyPairGenECDSA implements com.jcraft.jsch.KeyPairGenECDSA
{
    byte[] d;
    byte[] r;
    byte[] s;
    ECPublicKey pubKey;
    ECPrivateKey prvKey;
    ECParameterSpec params;
    
    public void init(final int key_size) throws Exception {
        String name = null;
        if (key_size == 256) {
            name = "secp256r1";
        }
        else if (key_size == 384) {
            name = "secp384r1";
        }
        else {
            if (key_size != 521) {
                throw new JSchException("unsupported key size: " + key_size);
            }
            name = "secp521r1";
        }
        for (int i = 0; i < 1000; ++i) {
            final KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
            final ECGenParameterSpec ecsp = new ECGenParameterSpec(name);
            kpg.initialize(ecsp);
            final KeyPair kp = kpg.genKeyPair();
            this.prvKey = (ECPrivateKey)kp.getPrivate();
            this.pubKey = (ECPublicKey)kp.getPublic();
            this.params = this.pubKey.getParams();
            this.d = this.prvKey.getS().toByteArray();
            final ECPoint w = this.pubKey.getW();
            this.r = w.getAffineX().toByteArray();
            this.s = w.getAffineY().toByteArray();
            if (this.r.length == this.s.length) {
                if (key_size == 256 && this.r.length == 32) {
                    break;
                }
                if (key_size == 384 && this.r.length == 48) {
                    break;
                }
                if (key_size == 521 && this.r.length == 66) {
                    break;
                }
            }
        }
        if (this.d.length < this.r.length) {
            this.d = this.insert0(this.d);
        }
    }
    
    public byte[] getD() {
        return this.d;
    }
    
    public byte[] getR() {
        return this.r;
    }
    
    public byte[] getS() {
        return this.s;
    }
    
    ECPublicKey getPublicKey() {
        return this.pubKey;
    }
    
    ECPrivateKey getPrivateKey() {
        return this.prvKey;
    }
    
    private byte[] insert0(final byte[] buf) {
        final byte[] tmp = new byte[buf.length + 1];
        System.arraycopy(buf, 0, tmp, 1, buf.length);
        this.bzero(buf);
        return tmp;
    }
    
    private byte[] chop0(final byte[] buf) {
        if (buf[0] != 0 || (buf[1] & 0x80) == 0x0) {
            return buf;
        }
        final byte[] tmp = new byte[buf.length - 1];
        System.arraycopy(buf, 1, tmp, 0, tmp.length);
        this.bzero(buf);
        return tmp;
    }
    
    private void bzero(final byte[] buf) {
        for (int i = 0; i < buf.length; ++i) {
            buf[i] = 0;
        }
    }
}
