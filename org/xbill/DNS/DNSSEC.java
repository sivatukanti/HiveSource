// 
// Decompiled by Procyon v0.5.36
// 

package org.xbill.DNS;

import java.security.spec.ECField;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.EllipticCurve;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.PrivateKey;
import java.util.Date;
import java.security.Signature;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.ECPoint;
import java.security.spec.DSAPublicKeySpec;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Arrays;

public class DNSSEC
{
    private static final ECKeyInfo GOST;
    private static final ECKeyInfo ECDSA_P256;
    private static final ECKeyInfo ECDSA_P384;
    private static final int ASN1_SEQ = 48;
    private static final int ASN1_INT = 2;
    private static final int DSA_LEN = 20;
    
    private DNSSEC() {
    }
    
    private static void digestSIG(final DNSOutput out, final SIGBase sig) {
        out.writeU16(sig.getTypeCovered());
        out.writeU8(sig.getAlgorithm());
        out.writeU8(sig.getLabels());
        out.writeU32(sig.getOrigTTL());
        out.writeU32(sig.getExpire().getTime() / 1000L);
        out.writeU32(sig.getTimeSigned().getTime() / 1000L);
        out.writeU16(sig.getFootprint());
        sig.getSigner().toWireCanonical(out);
    }
    
    public static byte[] digestRRset(final RRSIGRecord rrsig, final RRset rrset) {
        final DNSOutput out = new DNSOutput();
        digestSIG(out, rrsig);
        int size = rrset.size();
        final Record[] records = new Record[size];
        final Iterator it = rrset.rrs();
        final Name name = rrset.getName();
        Name wild = null;
        final int sigLabels = rrsig.getLabels() + 1;
        if (name.labels() > sigLabels) {
            wild = name.wild(name.labels() - sigLabels);
        }
        while (it.hasNext()) {
            records[--size] = it.next();
        }
        Arrays.sort(records);
        final DNSOutput header = new DNSOutput();
        if (wild != null) {
            wild.toWireCanonical(header);
        }
        else {
            name.toWireCanonical(header);
        }
        header.writeU16(rrset.getType());
        header.writeU16(rrset.getDClass());
        header.writeU32(rrsig.getOrigTTL());
        for (int i = 0; i < records.length; ++i) {
            out.writeByteArray(header.toByteArray());
            final int lengthPosition = out.current();
            out.writeU16(0);
            out.writeByteArray(records[i].rdataToWireCanonical());
            final int rrlength = out.current() - lengthPosition - 2;
            out.save();
            out.jump(lengthPosition);
            out.writeU16(rrlength);
            out.restore();
        }
        return out.toByteArray();
    }
    
    public static byte[] digestMessage(final SIGRecord sig, final Message msg, final byte[] previous) {
        final DNSOutput out = new DNSOutput();
        digestSIG(out, sig);
        if (previous != null) {
            out.writeByteArray(previous);
        }
        msg.toWire(out);
        return out.toByteArray();
    }
    
    private static int BigIntegerLength(final BigInteger i) {
        return (i.bitLength() + 7) / 8;
    }
    
    private static BigInteger readBigInteger(final DNSInput in, final int len) throws IOException {
        final byte[] b = in.readByteArray(len);
        return new BigInteger(1, b);
    }
    
    private static BigInteger readBigInteger(final DNSInput in) {
        final byte[] b = in.readByteArray();
        return new BigInteger(1, b);
    }
    
    private static byte[] trimByteArray(final byte[] array) {
        if (array[0] == 0) {
            final byte[] trimmedArray = new byte[array.length - 1];
            System.arraycopy(array, 1, trimmedArray, 0, array.length - 1);
            return trimmedArray;
        }
        return array;
    }
    
    private static void reverseByteArray(final byte[] array) {
        for (int i = 0; i < array.length / 2; ++i) {
            final int j = array.length - i - 1;
            final byte tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
        }
    }
    
    private static BigInteger readBigIntegerLittleEndian(final DNSInput in, final int len) throws IOException {
        final byte[] b = in.readByteArray(len);
        reverseByteArray(b);
        return new BigInteger(1, b);
    }
    
    private static void writeBigInteger(final DNSOutput out, final BigInteger val) {
        final byte[] b = trimByteArray(val.toByteArray());
        out.writeByteArray(b);
    }
    
    private static void writePaddedBigInteger(final DNSOutput out, final BigInteger val, final int len) {
        final byte[] b = trimByteArray(val.toByteArray());
        if (b.length > len) {
            throw new IllegalArgumentException();
        }
        if (b.length < len) {
            final byte[] pad = new byte[len - b.length];
            out.writeByteArray(pad);
        }
        out.writeByteArray(b);
    }
    
    private static void writePaddedBigIntegerLittleEndian(final DNSOutput out, final BigInteger val, final int len) {
        final byte[] b = trimByteArray(val.toByteArray());
        if (b.length > len) {
            throw new IllegalArgumentException();
        }
        reverseByteArray(b);
        out.writeByteArray(b);
        if (b.length < len) {
            final byte[] pad = new byte[len - b.length];
            out.writeByteArray(pad);
        }
    }
    
    private static PublicKey toRSAPublicKey(final KEYBase r) throws IOException, GeneralSecurityException {
        final DNSInput in = new DNSInput(r.getKey());
        int exponentLength = in.readU8();
        if (exponentLength == 0) {
            exponentLength = in.readU16();
        }
        final BigInteger exponent = readBigInteger(in, exponentLength);
        final BigInteger modulus = readBigInteger(in);
        final KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(new RSAPublicKeySpec(modulus, exponent));
    }
    
    private static PublicKey toDSAPublicKey(final KEYBase r) throws IOException, GeneralSecurityException, MalformedKeyException {
        final DNSInput in = new DNSInput(r.getKey());
        final int t = in.readU8();
        if (t > 8) {
            throw new MalformedKeyException(r);
        }
        final BigInteger q = readBigInteger(in, 20);
        final BigInteger p = readBigInteger(in, 64 + t * 8);
        final BigInteger g = readBigInteger(in, 64 + t * 8);
        final BigInteger y = readBigInteger(in, 64 + t * 8);
        final KeyFactory factory = KeyFactory.getInstance("DSA");
        return factory.generatePublic(new DSAPublicKeySpec(y, p, q, g));
    }
    
    private static PublicKey toECGOSTPublicKey(final KEYBase r, final ECKeyInfo keyinfo) throws IOException, GeneralSecurityException, MalformedKeyException {
        final DNSInput in = new DNSInput(r.getKey());
        final BigInteger x = readBigIntegerLittleEndian(in, keyinfo.length);
        final BigInteger y = readBigIntegerLittleEndian(in, keyinfo.length);
        final ECPoint q = new ECPoint(x, y);
        final KeyFactory factory = KeyFactory.getInstance("ECGOST3410");
        return factory.generatePublic(new ECPublicKeySpec(q, keyinfo.spec));
    }
    
    private static PublicKey toECDSAPublicKey(final KEYBase r, final ECKeyInfo keyinfo) throws IOException, GeneralSecurityException, MalformedKeyException {
        final DNSInput in = new DNSInput(r.getKey());
        final BigInteger x = readBigInteger(in, keyinfo.length);
        final BigInteger y = readBigInteger(in, keyinfo.length);
        final ECPoint q = new ECPoint(x, y);
        final KeyFactory factory = KeyFactory.getInstance("EC");
        return factory.generatePublic(new ECPublicKeySpec(q, keyinfo.spec));
    }
    
    static PublicKey toPublicKey(final KEYBase r) throws DNSSECException {
        final int alg = r.getAlgorithm();
        try {
            switch (alg) {
                case 1:
                case 5:
                case 7:
                case 8:
                case 10: {
                    return toRSAPublicKey(r);
                }
                case 3:
                case 6: {
                    return toDSAPublicKey(r);
                }
                case 12: {
                    return toECGOSTPublicKey(r, DNSSEC.GOST);
                }
                case 13: {
                    return toECDSAPublicKey(r, DNSSEC.ECDSA_P256);
                }
                case 14: {
                    return toECDSAPublicKey(r, DNSSEC.ECDSA_P384);
                }
                default: {
                    throw new UnsupportedAlgorithmException(alg);
                }
            }
        }
        catch (IOException e2) {
            throw new MalformedKeyException(r);
        }
        catch (GeneralSecurityException e) {
            throw new DNSSECException(e.toString());
        }
    }
    
    private static byte[] fromRSAPublicKey(final RSAPublicKey key) {
        final DNSOutput out = new DNSOutput();
        final BigInteger exponent = key.getPublicExponent();
        final BigInteger modulus = key.getModulus();
        final int exponentLength = BigIntegerLength(exponent);
        if (exponentLength < 256) {
            out.writeU8(exponentLength);
        }
        else {
            out.writeU8(0);
            out.writeU16(exponentLength);
        }
        writeBigInteger(out, exponent);
        writeBigInteger(out, modulus);
        return out.toByteArray();
    }
    
    private static byte[] fromDSAPublicKey(final DSAPublicKey key) {
        final DNSOutput out = new DNSOutput();
        final BigInteger q = key.getParams().getQ();
        final BigInteger p = key.getParams().getP();
        final BigInteger g = key.getParams().getG();
        final BigInteger y = key.getY();
        final int t = (p.toByteArray().length - 64) / 8;
        out.writeU8(t);
        writeBigInteger(out, q);
        writeBigInteger(out, p);
        writePaddedBigInteger(out, g, 8 * t + 64);
        writePaddedBigInteger(out, y, 8 * t + 64);
        return out.toByteArray();
    }
    
    private static byte[] fromECGOSTPublicKey(final ECPublicKey key, final ECKeyInfo keyinfo) {
        final DNSOutput out = new DNSOutput();
        final BigInteger x = key.getW().getAffineX();
        final BigInteger y = key.getW().getAffineY();
        writePaddedBigIntegerLittleEndian(out, x, keyinfo.length);
        writePaddedBigIntegerLittleEndian(out, y, keyinfo.length);
        return out.toByteArray();
    }
    
    private static byte[] fromECDSAPublicKey(final ECPublicKey key, final ECKeyInfo keyinfo) {
        final DNSOutput out = new DNSOutput();
        final BigInteger x = key.getW().getAffineX();
        final BigInteger y = key.getW().getAffineY();
        writePaddedBigInteger(out, x, keyinfo.length);
        writePaddedBigInteger(out, y, keyinfo.length);
        return out.toByteArray();
    }
    
    static byte[] fromPublicKey(final PublicKey key, final int alg) throws DNSSECException {
        switch (alg) {
            case 1:
            case 5:
            case 7:
            case 8:
            case 10: {
                if (!(key instanceof RSAPublicKey)) {
                    throw new IncompatibleKeyException();
                }
                return fromRSAPublicKey((RSAPublicKey)key);
            }
            case 3:
            case 6: {
                if (!(key instanceof DSAPublicKey)) {
                    throw new IncompatibleKeyException();
                }
                return fromDSAPublicKey((DSAPublicKey)key);
            }
            case 12: {
                if (!(key instanceof ECPublicKey)) {
                    throw new IncompatibleKeyException();
                }
                return fromECGOSTPublicKey((ECPublicKey)key, DNSSEC.GOST);
            }
            case 13: {
                if (!(key instanceof ECPublicKey)) {
                    throw new IncompatibleKeyException();
                }
                return fromECDSAPublicKey((ECPublicKey)key, DNSSEC.ECDSA_P256);
            }
            case 14: {
                if (!(key instanceof ECPublicKey)) {
                    throw new IncompatibleKeyException();
                }
                return fromECDSAPublicKey((ECPublicKey)key, DNSSEC.ECDSA_P384);
            }
            default: {
                throw new UnsupportedAlgorithmException(alg);
            }
        }
    }
    
    public static String algString(final int alg) throws UnsupportedAlgorithmException {
        switch (alg) {
            case 1: {
                return "MD5withRSA";
            }
            case 3:
            case 6: {
                return "SHA1withDSA";
            }
            case 5:
            case 7: {
                return "SHA1withRSA";
            }
            case 8: {
                return "SHA256withRSA";
            }
            case 10: {
                return "SHA512withRSA";
            }
            case 12: {
                return "GOST3411withECGOST3410";
            }
            case 13: {
                return "SHA256withECDSA";
            }
            case 14: {
                return "SHA384withECDSA";
            }
            default: {
                throw new UnsupportedAlgorithmException(alg);
            }
        }
    }
    
    private static byte[] DSASignaturefromDNS(final byte[] dns) throws DNSSECException, IOException {
        if (dns.length != 41) {
            throw new SignatureVerificationException();
        }
        final DNSInput in = new DNSInput(dns);
        final DNSOutput out = new DNSOutput();
        final int t = in.readU8();
        final byte[] r = in.readByteArray(20);
        int rlen = 20;
        if (r[0] < 0) {
            ++rlen;
        }
        final byte[] s = in.readByteArray(20);
        int slen = 20;
        if (s[0] < 0) {
            ++slen;
        }
        out.writeU8(48);
        out.writeU8(rlen + slen + 4);
        out.writeU8(2);
        out.writeU8(rlen);
        if (rlen > 20) {
            out.writeU8(0);
        }
        out.writeByteArray(r);
        out.writeU8(2);
        out.writeU8(slen);
        if (slen > 20) {
            out.writeU8(0);
        }
        out.writeByteArray(s);
        return out.toByteArray();
    }
    
    private static byte[] DSASignaturetoDNS(final byte[] signature, final int t) throws IOException {
        final DNSInput in = new DNSInput(signature);
        final DNSOutput out = new DNSOutput();
        out.writeU8(t);
        int tmp = in.readU8();
        if (tmp != 48) {
            throw new IOException();
        }
        final int seqlen = in.readU8();
        tmp = in.readU8();
        if (tmp != 2) {
            throw new IOException();
        }
        final int rlen = in.readU8();
        if (rlen == 21) {
            if (in.readU8() != 0) {
                throw new IOException();
            }
        }
        else if (rlen != 20) {
            throw new IOException();
        }
        byte[] bytes = in.readByteArray(20);
        out.writeByteArray(bytes);
        tmp = in.readU8();
        if (tmp != 2) {
            throw new IOException();
        }
        final int slen = in.readU8();
        if (slen == 21) {
            if (in.readU8() != 0) {
                throw new IOException();
            }
        }
        else if (slen != 20) {
            throw new IOException();
        }
        bytes = in.readByteArray(20);
        out.writeByteArray(bytes);
        return out.toByteArray();
    }
    
    private static byte[] ECGOSTSignaturefromDNS(final byte[] signature, final ECKeyInfo keyinfo) throws DNSSECException, IOException {
        if (signature.length != keyinfo.length * 2) {
            throw new SignatureVerificationException();
        }
        return signature;
    }
    
    private static byte[] ECDSASignaturefromDNS(final byte[] signature, final ECKeyInfo keyinfo) throws DNSSECException, IOException {
        if (signature.length != keyinfo.length * 2) {
            throw new SignatureVerificationException();
        }
        final DNSInput in = new DNSInput(signature);
        final DNSOutput out = new DNSOutput();
        final byte[] r = in.readByteArray(keyinfo.length);
        int rlen = keyinfo.length;
        if (r[0] < 0) {
            ++rlen;
        }
        final byte[] s = in.readByteArray(keyinfo.length);
        int slen = keyinfo.length;
        if (s[0] < 0) {
            ++slen;
        }
        out.writeU8(48);
        out.writeU8(rlen + slen + 4);
        out.writeU8(2);
        out.writeU8(rlen);
        if (rlen > keyinfo.length) {
            out.writeU8(0);
        }
        out.writeByteArray(r);
        out.writeU8(2);
        out.writeU8(slen);
        if (slen > keyinfo.length) {
            out.writeU8(0);
        }
        out.writeByteArray(s);
        return out.toByteArray();
    }
    
    private static byte[] ECDSASignaturetoDNS(final byte[] signature, final ECKeyInfo keyinfo) throws IOException {
        final DNSInput in = new DNSInput(signature);
        final DNSOutput out = new DNSOutput();
        int tmp = in.readU8();
        if (tmp != 48) {
            throw new IOException();
        }
        final int seqlen = in.readU8();
        tmp = in.readU8();
        if (tmp != 2) {
            throw new IOException();
        }
        final int rlen = in.readU8();
        if (rlen == keyinfo.length + 1) {
            if (in.readU8() != 0) {
                throw new IOException();
            }
        }
        else if (rlen != keyinfo.length) {
            throw new IOException();
        }
        byte[] bytes = in.readByteArray(keyinfo.length);
        out.writeByteArray(bytes);
        tmp = in.readU8();
        if (tmp != 2) {
            throw new IOException();
        }
        final int slen = in.readU8();
        if (slen == keyinfo.length + 1) {
            if (in.readU8() != 0) {
                throw new IOException();
            }
        }
        else if (slen != keyinfo.length) {
            throw new IOException();
        }
        bytes = in.readByteArray(keyinfo.length);
        out.writeByteArray(bytes);
        return out.toByteArray();
    }
    
    private static void verify(final PublicKey key, final int alg, final byte[] data, byte[] signature) throws DNSSECException {
        Label_0115: {
            if (key instanceof DSAPublicKey) {
                try {
                    signature = DSASignaturefromDNS(signature);
                    break Label_0115;
                }
                catch (IOException e2) {
                    throw new IllegalStateException();
                }
            }
            if (key instanceof ECPublicKey) {
                try {
                    switch (alg) {
                        case 12: {
                            signature = ECGOSTSignaturefromDNS(signature, DNSSEC.GOST);
                            break;
                        }
                        case 13: {
                            signature = ECDSASignaturefromDNS(signature, DNSSEC.ECDSA_P256);
                            break;
                        }
                        case 14: {
                            signature = ECDSASignaturefromDNS(signature, DNSSEC.ECDSA_P384);
                            break;
                        }
                        default: {
                            throw new UnsupportedAlgorithmException(alg);
                        }
                    }
                }
                catch (IOException e2) {
                    throw new IllegalStateException();
                }
            }
            try {
                final Signature s = Signature.getInstance(algString(alg));
                s.initVerify(key);
                s.update(data);
                if (!s.verify(signature)) {
                    throw new SignatureVerificationException();
                }
            }
            catch (GeneralSecurityException e) {
                throw new DNSSECException(e.toString());
            }
        }
    }
    
    private static boolean matches(final SIGBase sig, final KEYBase key) {
        return key.getAlgorithm() == sig.getAlgorithm() && key.getFootprint() == sig.getFootprint() && key.getName().equals(sig.getSigner());
    }
    
    public static void verify(final RRset rrset, final RRSIGRecord rrsig, final DNSKEYRecord key) throws DNSSECException {
        if (!matches(rrsig, key)) {
            throw new KeyMismatchException(key, rrsig);
        }
        final Date now = new Date();
        if (now.compareTo(rrsig.getExpire()) > 0) {
            throw new SignatureExpiredException(rrsig.getExpire(), now);
        }
        if (now.compareTo(rrsig.getTimeSigned()) < 0) {
            throw new SignatureNotYetValidException(rrsig.getTimeSigned(), now);
        }
        verify(key.getPublicKey(), rrsig.getAlgorithm(), digestRRset(rrsig, rrset), rrsig.getSignature());
    }
    
    private static byte[] sign(final PrivateKey privkey, final PublicKey pubkey, final int alg, final byte[] data, final String provider) throws DNSSECException {
        byte[] signature;
        try {
            Signature s;
            if (provider != null) {
                s = Signature.getInstance(algString(alg), provider);
            }
            else {
                s = Signature.getInstance(algString(alg));
            }
            s.initSign(privkey);
            s.update(data);
            signature = s.sign();
        }
        catch (GeneralSecurityException e) {
            throw new DNSSECException(e.toString());
        }
        if (pubkey instanceof DSAPublicKey) {
            try {
                final DSAPublicKey dsa = (DSAPublicKey)pubkey;
                final BigInteger P = dsa.getParams().getP();
                final int t = (BigIntegerLength(P) - 64) / 8;
                signature = DSASignaturetoDNS(signature, t);
                return signature;
            }
            catch (IOException e2) {
                throw new IllegalStateException();
            }
        }
        if (pubkey instanceof ECPublicKey) {
            try {
                switch (alg) {
                    case 12: {
                        break;
                    }
                    case 13: {
                        signature = ECDSASignaturetoDNS(signature, DNSSEC.ECDSA_P256);
                        break;
                    }
                    case 14: {
                        signature = ECDSASignaturetoDNS(signature, DNSSEC.ECDSA_P384);
                        break;
                    }
                    default: {
                        throw new UnsupportedAlgorithmException(alg);
                    }
                }
            }
            catch (IOException e2) {
                throw new IllegalStateException();
            }
        }
        return signature;
    }
    
    static void checkAlgorithm(final PrivateKey key, final int alg) throws UnsupportedAlgorithmException {
        switch (alg) {
            case 1:
            case 5:
            case 7:
            case 8:
            case 10: {
                if (!(key instanceof RSAPrivateKey)) {
                    throw new IncompatibleKeyException();
                }
                break;
            }
            case 3:
            case 6: {
                if (!(key instanceof DSAPrivateKey)) {
                    throw new IncompatibleKeyException();
                }
                break;
            }
            case 12:
            case 13:
            case 14: {
                if (!(key instanceof ECPrivateKey)) {
                    throw new IncompatibleKeyException();
                }
                break;
            }
            default: {
                throw new UnsupportedAlgorithmException(alg);
            }
        }
    }
    
    public static RRSIGRecord sign(final RRset rrset, final DNSKEYRecord key, final PrivateKey privkey, final Date inception, final Date expiration) throws DNSSECException {
        return sign(rrset, key, privkey, inception, expiration, null);
    }
    
    public static RRSIGRecord sign(final RRset rrset, final DNSKEYRecord key, final PrivateKey privkey, final Date inception, final Date expiration, final String provider) throws DNSSECException {
        final int alg = key.getAlgorithm();
        checkAlgorithm(privkey, alg);
        final RRSIGRecord rrsig = new RRSIGRecord(rrset.getName(), rrset.getDClass(), rrset.getTTL(), rrset.getType(), alg, rrset.getTTL(), expiration, inception, key.getFootprint(), key.getName(), null);
        rrsig.setSignature(sign(privkey, key.getPublicKey(), alg, digestRRset(rrsig, rrset), provider));
        return rrsig;
    }
    
    static SIGRecord signMessage(final Message message, final SIGRecord previous, final KEYRecord key, final PrivateKey privkey, final Date inception, final Date expiration) throws DNSSECException {
        final int alg = key.getAlgorithm();
        checkAlgorithm(privkey, alg);
        final SIGRecord sig = new SIGRecord(Name.root, 255, 0L, 0, alg, 0L, expiration, inception, key.getFootprint(), key.getName(), null);
        final DNSOutput out = new DNSOutput();
        digestSIG(out, sig);
        if (previous != null) {
            out.writeByteArray(previous.getSignature());
        }
        out.writeByteArray(message.toWire());
        sig.setSignature(sign(privkey, key.getPublicKey(), alg, out.toByteArray(), null));
        return sig;
    }
    
    static void verifyMessage(final Message message, final byte[] bytes, final SIGRecord sig, final SIGRecord previous, final KEYRecord key) throws DNSSECException {
        if (message.sig0start == 0) {
            throw new NoSignatureException();
        }
        if (!matches(sig, key)) {
            throw new KeyMismatchException(key, sig);
        }
        final Date now = new Date();
        if (now.compareTo(sig.getExpire()) > 0) {
            throw new SignatureExpiredException(sig.getExpire(), now);
        }
        if (now.compareTo(sig.getTimeSigned()) < 0) {
            throw new SignatureNotYetValidException(sig.getTimeSigned(), now);
        }
        final DNSOutput out = new DNSOutput();
        digestSIG(out, sig);
        if (previous != null) {
            out.writeByteArray(previous.getSignature());
        }
        final Header header = (Header)message.getHeader().clone();
        header.decCount(3);
        out.writeByteArray(header.toWire());
        out.writeByteArray(bytes, 12, message.sig0start - 12);
        verify(key.getPublicKey(), sig.getAlgorithm(), out.toByteArray(), sig.getSignature());
    }
    
    static byte[] generateDSDigest(final DNSKEYRecord key, final int digestid) {
        MessageDigest digest = null;
        try {
            switch (digestid) {
                case 1: {
                    digest = MessageDigest.getInstance("sha-1");
                    break;
                }
                case 2: {
                    digest = MessageDigest.getInstance("sha-256");
                    break;
                }
                case 3: {
                    digest = MessageDigest.getInstance("GOST3411");
                    break;
                }
                case 4: {
                    digest = MessageDigest.getInstance("sha-384");
                    break;
                }
                default: {
                    throw new IllegalArgumentException("unknown DS digest type " + digestid);
                }
            }
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("no message digest support");
        }
        digest.update(key.getName().toWireCanonical());
        digest.update(key.rdataToWireCanonical());
        return digest.digest();
    }
    
    static {
        GOST = new ECKeyInfo(32, "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFD97", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFD94", "A6", "1", "8D91E471E0989CDA27DF505A453F2B7635294F2DDF23E3B122ACC99C9E9F1E14", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF6C611070995AD10045841B09B761B893");
        ECDSA_P256 = new ECKeyInfo(32, "FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFF", "FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFC", "5AC635D8AA3A93E7B3EBBD55769886BC651D06B0CC53B0F63BCE3C3E27D2604B", "6B17D1F2E12C4247F8BCE6E563A440F277037D812DEB33A0F4A13945D898C296", "4FE342E2FE1A7F9B8EE7EB4A7C0F9E162BCE33576B315ECECBB6406837BF51F5", "FFFFFFFF00000000FFFFFFFFFFFFFFFFBCE6FAADA7179E84F3B9CAC2FC632551");
        ECDSA_P384 = new ECKeyInfo(48, "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFF0000000000000000FFFFFFFF", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFF0000000000000000FFFFFFFC", "B3312FA7E23EE7E4988E056BE3F82D19181D9C6EFE8141120314088F5013875AC656398D8A2ED19D2A85C8EDD3EC2AEF", "AA87CA22BE8B05378EB1C71EF320AD746E1D3B628BA79B9859F741E082542A385502F25DBF55296C3A545E3872760AB7", "3617DE4A96262C6F5D9E98BF9292DC29F8F41DBD289A147CE9DA3113B5F0B8C00A60B1CE1D7E819D7A431D7C90EA0E5F", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFC7634D81F4372DDF581A0DB248B0A77AECEC196ACCC52973");
    }
    
    public static class Algorithm
    {
        public static final int RSAMD5 = 1;
        public static final int DH = 2;
        public static final int DSA = 3;
        public static final int RSASHA1 = 5;
        public static final int DSA_NSEC3_SHA1 = 6;
        public static final int RSA_NSEC3_SHA1 = 7;
        public static final int RSASHA256 = 8;
        public static final int RSASHA512 = 10;
        public static final int ECC_GOST = 12;
        public static final int ECDSAP256SHA256 = 13;
        public static final int ECDSAP384SHA384 = 14;
        public static final int INDIRECT = 252;
        public static final int PRIVATEDNS = 253;
        public static final int PRIVATEOID = 254;
        private static Mnemonic algs;
        
        private Algorithm() {
        }
        
        public static String string(final int alg) {
            return Algorithm.algs.getText(alg);
        }
        
        public static int value(final String s) {
            return Algorithm.algs.getValue(s);
        }
        
        static {
            (Algorithm.algs = new Mnemonic("DNSSEC algorithm", 2)).setMaximum(255);
            Algorithm.algs.setNumericAllowed(true);
            Algorithm.algs.add(1, "RSAMD5");
            Algorithm.algs.add(2, "DH");
            Algorithm.algs.add(3, "DSA");
            Algorithm.algs.add(5, "RSASHA1");
            Algorithm.algs.add(6, "DSA-NSEC3-SHA1");
            Algorithm.algs.add(7, "RSA-NSEC3-SHA1");
            Algorithm.algs.add(8, "RSASHA256");
            Algorithm.algs.add(10, "RSASHA512");
            Algorithm.algs.add(12, "ECC-GOST");
            Algorithm.algs.add(13, "ECDSAP256SHA256");
            Algorithm.algs.add(14, "ECDSAP384SHA384");
            Algorithm.algs.add(252, "INDIRECT");
            Algorithm.algs.add(253, "PRIVATEDNS");
            Algorithm.algs.add(254, "PRIVATEOID");
        }
    }
    
    public static class DNSSECException extends Exception
    {
        DNSSECException(final String s) {
            super(s);
        }
    }
    
    public static class UnsupportedAlgorithmException extends DNSSECException
    {
        UnsupportedAlgorithmException(final int alg) {
            super("Unsupported algorithm: " + alg);
        }
    }
    
    public static class MalformedKeyException extends DNSSECException
    {
        MalformedKeyException(final KEYBase rec) {
            super("Invalid key data: " + rec.rdataToString());
        }
    }
    
    public static class KeyMismatchException extends DNSSECException
    {
        private KEYBase key;
        private SIGBase sig;
        
        KeyMismatchException(final KEYBase key, final SIGBase sig) {
            super("key " + key.getName() + "/" + Algorithm.string(key.getAlgorithm()) + "/" + key.getFootprint() + " " + "does not match signature " + sig.getSigner() + "/" + Algorithm.string(sig.getAlgorithm()) + "/" + sig.getFootprint());
        }
    }
    
    public static class SignatureExpiredException extends DNSSECException
    {
        private Date when;
        private Date now;
        
        SignatureExpiredException(final Date when, final Date now) {
            super("signature expired");
            this.when = when;
            this.now = now;
        }
        
        public Date getExpiration() {
            return this.when;
        }
        
        public Date getVerifyTime() {
            return this.now;
        }
    }
    
    public static class SignatureNotYetValidException extends DNSSECException
    {
        private Date when;
        private Date now;
        
        SignatureNotYetValidException(final Date when, final Date now) {
            super("signature is not yet valid");
            this.when = when;
            this.now = now;
        }
        
        public Date getExpiration() {
            return this.when;
        }
        
        public Date getVerifyTime() {
            return this.now;
        }
    }
    
    public static class SignatureVerificationException extends DNSSECException
    {
        SignatureVerificationException() {
            super("signature verification failed");
        }
    }
    
    public static class IncompatibleKeyException extends IllegalArgumentException
    {
        IncompatibleKeyException() {
            super("incompatible keys");
        }
    }
    
    public static class NoSignatureException extends DNSSECException
    {
        NoSignatureException() {
            super("no signature found");
        }
    }
    
    private static class ECKeyInfo
    {
        int length;
        public BigInteger p;
        public BigInteger a;
        public BigInteger b;
        public BigInteger gx;
        public BigInteger gy;
        public BigInteger n;
        EllipticCurve curve;
        ECParameterSpec spec;
        
        ECKeyInfo(final int length, final String p_str, final String a_str, final String b_str, final String gx_str, final String gy_str, final String n_str) {
            this.length = length;
            this.p = new BigInteger(p_str, 16);
            this.a = new BigInteger(a_str, 16);
            this.b = new BigInteger(b_str, 16);
            this.gx = new BigInteger(gx_str, 16);
            this.gy = new BigInteger(gy_str, 16);
            this.n = new BigInteger(n_str, 16);
            this.curve = new EllipticCurve(new ECFieldFp(this.p), this.a, this.b);
            this.spec = new ECParameterSpec(this.curve, new ECPoint(this.gx, this.gy), this.n, 1);
        }
    }
}
