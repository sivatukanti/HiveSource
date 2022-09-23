// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch.jce;

import com.jcraft.jsch.Buffer;
import java.security.PrivateKey;
import java.security.spec.ECPrivateKeySpec;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.ECPoint;
import java.math.BigInteger;
import java.security.spec.ECParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.Signature;

public class SignatureECDSA implements com.jcraft.jsch.SignatureECDSA
{
    java.security.Signature signature;
    KeyFactory keyFactory;
    
    public void init() throws Exception {
        this.signature = java.security.Signature.getInstance("SHA256withECDSA");
        this.keyFactory = KeyFactory.getInstance("EC");
    }
    
    public void setPubKey(byte[] r, byte[] s) throws Exception {
        r = this.insert0(r);
        s = this.insert0(s);
        String name = "secp256r1";
        if (r.length >= 64) {
            name = "secp521r1";
        }
        else if (r.length >= 48) {
            name = "secp384r1";
        }
        final AlgorithmParameters param = AlgorithmParameters.getInstance("EC");
        param.init(new ECGenParameterSpec(name));
        final ECParameterSpec ecparam = param.getParameterSpec(ECParameterSpec.class);
        final ECPoint w = new ECPoint(new BigInteger(1, r), new BigInteger(1, s));
        final PublicKey pubKey = this.keyFactory.generatePublic(new ECPublicKeySpec(w, ecparam));
        this.signature.initVerify(pubKey);
    }
    
    public void setPrvKey(byte[] d) throws Exception {
        d = this.insert0(d);
        String name = "secp256r1";
        if (d.length >= 64) {
            name = "secp521r1";
        }
        else if (d.length >= 48) {
            name = "secp384r1";
        }
        final AlgorithmParameters param = AlgorithmParameters.getInstance("EC");
        param.init(new ECGenParameterSpec(name));
        final ECParameterSpec ecparam = param.getParameterSpec(ECParameterSpec.class);
        final BigInteger _d = new BigInteger(1, d);
        final PrivateKey prvKey = this.keyFactory.generatePrivate(new ECPrivateKeySpec(_d, ecparam));
        this.signature.initSign(prvKey);
    }
    
    public byte[] sign() throws Exception {
        byte[] sig = this.signature.sign();
        if (sig[0] == 48 && (sig[1] + 2 == sig.length || ((sig[1] & 0x80) != 0x0 && (sig[2] & 0xFF) + 3 == sig.length))) {
            int index = 3;
            if ((sig[1] & 0x80) != 0x0 && (sig[2] & 0xFF) + 3 == sig.length) {
                index = 4;
            }
            byte[] r = new byte[sig[index]];
            byte[] s = new byte[sig[index + 2 + sig[index]]];
            System.arraycopy(sig, index + 1, r, 0, r.length);
            System.arraycopy(sig, index + 3 + sig[index], s, 0, s.length);
            r = this.chop0(r);
            s = this.chop0(s);
            final Buffer buf = new Buffer();
            buf.putMPInt(r);
            buf.putMPInt(s);
            sig = new byte[buf.getLength()];
            buf.setOffSet(0);
            buf.getByte(sig);
        }
        return sig;
    }
    
    public void update(final byte[] foo) throws Exception {
        this.signature.update(foo);
    }
    
    public boolean verify(byte[] sig) throws Exception {
        if (sig[0] != 48 || (sig[1] + 2 != sig.length && ((sig[1] & 0x80) == 0x0 || (sig[2] & 0xFF) + 3 != sig.length))) {
            final Buffer b = new Buffer(sig);
            b.getString();
            b.getInt();
            byte[] r = b.getMPInt();
            byte[] s = b.getMPInt();
            r = this.insert0(r);
            s = this.insert0(s);
            byte[] asn1 = null;
            if (r.length < 64) {
                asn1 = new byte[6 + r.length + s.length];
                asn1[0] = 48;
                asn1[1] = (byte)(4 + r.length + s.length);
                asn1[2] = 2;
                asn1[3] = (byte)r.length;
                System.arraycopy(r, 0, asn1, 4, r.length);
                asn1[r.length + 4] = 2;
                asn1[r.length + 5] = (byte)s.length;
                System.arraycopy(s, 0, asn1, 6 + r.length, s.length);
            }
            else {
                asn1 = new byte[6 + r.length + s.length + 1];
                asn1[0] = 48;
                asn1[1] = -127;
                asn1[2] = (byte)(4 + r.length + s.length);
                asn1[3] = 2;
                asn1[4] = (byte)r.length;
                System.arraycopy(r, 0, asn1, 5, r.length);
                asn1[r.length + 5] = 2;
                asn1[r.length + 6] = (byte)s.length;
                System.arraycopy(s, 0, asn1, 7 + r.length, s.length);
            }
            sig = asn1;
        }
        return this.signature.verify(sig);
    }
    
    private byte[] insert0(final byte[] buf) {
        if ((buf[0] & 0x80) == 0x0) {
            return buf;
        }
        final byte[] tmp = new byte[buf.length + 1];
        System.arraycopy(buf, 0, tmp, 1, buf.length);
        this.bzero(buf);
        return tmp;
    }
    
    private byte[] chop0(final byte[] buf) {
        if (buf[0] != 0) {
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
