// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch.jce;

import java.security.spec.EllipticCurve;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECFieldFp;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.ECPoint;
import java.security.KeyFactory;
import java.security.Key;
import java.math.BigInteger;
import javax.crypto.KeyAgreement;
import java.security.interfaces.ECPublicKey;
import com.jcraft.jsch.ECDH;

public class ECDHN implements ECDH
{
    byte[] Q_array;
    ECPublicKey publicKey;
    private KeyAgreement myKeyAgree;
    private static BigInteger two;
    private static BigInteger three;
    
    public void init(final int size) throws Exception {
        this.myKeyAgree = KeyAgreement.getInstance("ECDH");
        final KeyPairGenECDSA kpair = new KeyPairGenECDSA();
        kpair.init(size);
        this.publicKey = kpair.getPublicKey();
        final byte[] r = kpair.getR();
        final byte[] s = kpair.getS();
        this.Q_array = this.toPoint(r, s);
        this.myKeyAgree.init(kpair.getPrivateKey());
    }
    
    public byte[] getQ() throws Exception {
        return this.Q_array;
    }
    
    public byte[] getSecret(final byte[] r, final byte[] s) throws Exception {
        final KeyFactory kf = KeyFactory.getInstance("EC");
        final ECPoint w = new ECPoint(new BigInteger(1, r), new BigInteger(1, s));
        final ECPublicKeySpec spec = new ECPublicKeySpec(w, this.publicKey.getParams());
        final PublicKey theirPublicKey = kf.generatePublic(spec);
        this.myKeyAgree.doPhase(theirPublicKey, true);
        return this.myKeyAgree.generateSecret();
    }
    
    public boolean validate(final byte[] r, final byte[] s) throws Exception {
        final BigInteger x = new BigInteger(1, r);
        final BigInteger y = new BigInteger(1, s);
        final ECPoint w = new ECPoint(x, y);
        if (w.equals(ECPoint.POINT_INFINITY)) {
            return false;
        }
        final ECParameterSpec params = this.publicKey.getParams();
        final EllipticCurve curve = params.getCurve();
        final BigInteger p = ((ECFieldFp)curve.getField()).getP();
        final BigInteger p_sub1 = p.subtract(BigInteger.ONE);
        if (x.compareTo(p_sub1) > 0 || y.compareTo(p_sub1) > 0) {
            return false;
        }
        final BigInteger tmp = x.multiply(curve.getA()).add(curve.getB()).add(x.modPow(ECDHN.three, p)).mod(p);
        final BigInteger y_2 = y.modPow(ECDHN.two, p);
        return y_2.equals(tmp);
    }
    
    private byte[] toPoint(final byte[] r_array, final byte[] s_array) {
        final byte[] tmp = new byte[1 + r_array.length + s_array.length];
        tmp[0] = 4;
        System.arraycopy(r_array, 0, tmp, 1, r_array.length);
        System.arraycopy(s_array, 0, tmp, 1 + r_array.length, s_array.length);
        return tmp;
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
    
    static {
        ECDHN.two = BigInteger.ONE.add(BigInteger.ONE);
        ECDHN.three = ECDHN.two.add(BigInteger.ONE);
    }
}
