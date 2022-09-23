// 
// Decompiled by Procyon v0.5.36
// 

package org.xbill.DNS;

import org.xbill.DNS.utils.HMAC;
import java.util.Date;
import org.xbill.DNS.utils.base64;

public class TSIG
{
    private static final String HMAC_MD5_STR = "HMAC-MD5.SIG-ALG.REG.INT.";
    private static final String HMAC_SHA1_STR = "hmac-sha1.";
    private static final String HMAC_SHA224_STR = "hmac-sha224.";
    private static final String HMAC_SHA256_STR = "hmac-sha256.";
    private static final String HMAC_SHA384_STR = "hmac-sha384.";
    private static final String HMAC_SHA512_STR = "hmac-sha512.";
    public static final Name HMAC_MD5;
    public static final Name HMAC;
    public static final Name HMAC_SHA1;
    public static final Name HMAC_SHA224;
    public static final Name HMAC_SHA256;
    public static final Name HMAC_SHA384;
    public static final Name HMAC_SHA512;
    public static final short FUDGE = 300;
    private Name name;
    private Name alg;
    private String digest;
    private int digestBlockLength;
    private byte[] key;
    
    private void getDigest() {
        if (this.alg.equals(TSIG.HMAC_MD5)) {
            this.digest = "md5";
            this.digestBlockLength = 64;
        }
        else if (this.alg.equals(TSIG.HMAC_SHA1)) {
            this.digest = "sha-1";
            this.digestBlockLength = 64;
        }
        else if (this.alg.equals(TSIG.HMAC_SHA224)) {
            this.digest = "sha-224";
            this.digestBlockLength = 64;
        }
        else if (this.alg.equals(TSIG.HMAC_SHA256)) {
            this.digest = "sha-256";
            this.digestBlockLength = 64;
        }
        else if (this.alg.equals(TSIG.HMAC_SHA512)) {
            this.digest = "sha-512";
            this.digestBlockLength = 128;
        }
        else {
            if (!this.alg.equals(TSIG.HMAC_SHA384)) {
                throw new IllegalArgumentException("Invalid algorithm");
            }
            this.digest = "sha-384";
            this.digestBlockLength = 128;
        }
    }
    
    public TSIG(final Name algorithm, final Name name, final byte[] key) {
        this.name = name;
        this.alg = algorithm;
        this.key = key;
        this.getDigest();
    }
    
    public TSIG(final Name name, final byte[] key) {
        this(TSIG.HMAC_MD5, name, key);
    }
    
    public TSIG(final Name algorithm, final String name, final String key) {
        this.key = base64.fromString(key);
        if (this.key == null) {
            throw new IllegalArgumentException("Invalid TSIG key string");
        }
        try {
            this.name = Name.fromString(name, Name.root);
        }
        catch (TextParseException e) {
            throw new IllegalArgumentException("Invalid TSIG key name");
        }
        this.alg = algorithm;
        this.getDigest();
    }
    
    public TSIG(final String algorithm, final String name, final String key) {
        this(TSIG.HMAC_MD5, name, key);
        if (algorithm.equalsIgnoreCase("hmac-md5")) {
            this.alg = TSIG.HMAC_MD5;
        }
        else if (algorithm.equalsIgnoreCase("hmac-sha1")) {
            this.alg = TSIG.HMAC_SHA1;
        }
        else if (algorithm.equalsIgnoreCase("hmac-sha224")) {
            this.alg = TSIG.HMAC_SHA224;
        }
        else if (algorithm.equalsIgnoreCase("hmac-sha256")) {
            this.alg = TSIG.HMAC_SHA256;
        }
        else if (algorithm.equalsIgnoreCase("hmac-sha384")) {
            this.alg = TSIG.HMAC_SHA384;
        }
        else {
            if (!algorithm.equalsIgnoreCase("hmac-sha512")) {
                throw new IllegalArgumentException("Invalid TSIG algorithm");
            }
            this.alg = TSIG.HMAC_SHA512;
        }
        this.getDigest();
    }
    
    public TSIG(final String name, final String key) {
        this(TSIG.HMAC_MD5, name, key);
    }
    
    public static TSIG fromString(final String str) {
        String[] parts = str.split("[:/]", 3);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid TSIG key specification");
        }
        if (parts.length == 3) {
            try {
                return new TSIG(parts[0], parts[1], parts[2]);
            }
            catch (IllegalArgumentException e) {
                parts = str.split("[:/]", 2);
            }
        }
        return new TSIG(TSIG.HMAC_MD5, parts[0], parts[1]);
    }
    
    public TSIGRecord generate(final Message m, final byte[] b, final int error, final TSIGRecord old) {
        Date timeSigned;
        if (error != 18) {
            timeSigned = new Date();
        }
        else {
            timeSigned = old.getTimeSigned();
        }
        HMAC hmac = null;
        if (error == 0 || error == 18) {
            hmac = new HMAC(this.digest, this.digestBlockLength, this.key);
        }
        int fudge = Options.intValue("tsigfudge");
        if (fudge < 0 || fudge > 32767) {
            fudge = 300;
        }
        if (old != null) {
            final DNSOutput out = new DNSOutput();
            out.writeU16(old.getSignature().length);
            if (hmac != null) {
                hmac.update(out.toByteArray());
                hmac.update(old.getSignature());
            }
        }
        if (hmac != null) {
            hmac.update(b);
        }
        DNSOutput out = new DNSOutput();
        this.name.toWireCanonical(out);
        out.writeU16(255);
        out.writeU32(0L);
        this.alg.toWireCanonical(out);
        long time = timeSigned.getTime() / 1000L;
        int timeHigh = (int)(time >> 32);
        long timeLow = time & 0xFFFFFFFFL;
        out.writeU16(timeHigh);
        out.writeU32(timeLow);
        out.writeU16(fudge);
        out.writeU16(error);
        out.writeU16(0);
        if (hmac != null) {
            hmac.update(out.toByteArray());
        }
        byte[] signature;
        if (hmac != null) {
            signature = hmac.sign();
        }
        else {
            signature = new byte[0];
        }
        byte[] other = null;
        if (error == 18) {
            out = new DNSOutput();
            time = new Date().getTime() / 1000L;
            timeHigh = (int)(time >> 32);
            timeLow = (time & 0xFFFFFFFFL);
            out.writeU16(timeHigh);
            out.writeU32(timeLow);
            other = out.toByteArray();
        }
        return new TSIGRecord(this.name, 255, 0L, this.alg, timeSigned, fudge, signature, m.getHeader().getID(), error, other);
    }
    
    public void apply(final Message m, final int error, final TSIGRecord old) {
        final Record r = this.generate(m, m.toWire(), error, old);
        m.addRecord(r, 3);
        m.tsigState = 3;
    }
    
    public void apply(final Message m, final TSIGRecord old) {
        this.apply(m, 0, old);
    }
    
    public void applyStream(final Message m, final TSIGRecord old, final boolean first) {
        if (first) {
            this.apply(m, old);
            return;
        }
        final Date timeSigned = new Date();
        final HMAC hmac = new HMAC(this.digest, this.digestBlockLength, this.key);
        int fudge = Options.intValue("tsigfudge");
        if (fudge < 0 || fudge > 32767) {
            fudge = 300;
        }
        DNSOutput out = new DNSOutput();
        out.writeU16(old.getSignature().length);
        hmac.update(out.toByteArray());
        hmac.update(old.getSignature());
        hmac.update(m.toWire());
        out = new DNSOutput();
        final long time = timeSigned.getTime() / 1000L;
        final int timeHigh = (int)(time >> 32);
        final long timeLow = time & 0xFFFFFFFFL;
        out.writeU16(timeHigh);
        out.writeU32(timeLow);
        out.writeU16(fudge);
        hmac.update(out.toByteArray());
        final byte[] signature = hmac.sign();
        final byte[] other = null;
        final Record r = new TSIGRecord(this.name, 255, 0L, this.alg, timeSigned, fudge, signature, m.getHeader().getID(), 0, other);
        m.addRecord(r, 3);
        m.tsigState = 3;
    }
    
    public byte verify(final Message m, final byte[] b, final int length, final TSIGRecord old) {
        m.tsigState = 4;
        final TSIGRecord tsig = m.getTSIG();
        final HMAC hmac = new HMAC(this.digest, this.digestBlockLength, this.key);
        if (tsig == null) {
            return 1;
        }
        if (!tsig.getName().equals(this.name) || !tsig.getAlgorithm().equals(this.alg)) {
            if (Options.check("verbose")) {
                System.err.println("BADKEY failure");
            }
            return 17;
        }
        final long now = System.currentTimeMillis();
        final long then = tsig.getTimeSigned().getTime();
        final long fudge = tsig.getFudge();
        if (Math.abs(now - then) > fudge * 1000L) {
            if (Options.check("verbose")) {
                System.err.println("BADTIME failure");
            }
            return 18;
        }
        if (old != null && tsig.getError() != 17 && tsig.getError() != 16) {
            final DNSOutput out = new DNSOutput();
            out.writeU16(old.getSignature().length);
            hmac.update(out.toByteArray());
            hmac.update(old.getSignature());
        }
        m.getHeader().decCount(3);
        final byte[] header = m.getHeader().toWire();
        m.getHeader().incCount(3);
        hmac.update(header);
        final int len = m.tsigstart - header.length;
        hmac.update(b, header.length, len);
        final DNSOutput out2 = new DNSOutput();
        tsig.getName().toWireCanonical(out2);
        out2.writeU16(tsig.dclass);
        out2.writeU32(tsig.ttl);
        tsig.getAlgorithm().toWireCanonical(out2);
        final long time = tsig.getTimeSigned().getTime() / 1000L;
        final int timeHigh = (int)(time >> 32);
        final long timeLow = time & 0xFFFFFFFFL;
        out2.writeU16(timeHigh);
        out2.writeU32(timeLow);
        out2.writeU16(tsig.getFudge());
        out2.writeU16(tsig.getError());
        if (tsig.getOther() != null) {
            out2.writeU16(tsig.getOther().length);
            out2.writeByteArray(tsig.getOther());
        }
        else {
            out2.writeU16(0);
        }
        hmac.update(out2.toByteArray());
        final byte[] signature = tsig.getSignature();
        final int digestLength = hmac.digestLength();
        final int minDigestLength = this.digest.equals("md5") ? 10 : (digestLength / 2);
        if (signature.length > digestLength) {
            if (Options.check("verbose")) {
                System.err.println("BADSIG: signature too long");
            }
            return 16;
        }
        if (signature.length < minDigestLength) {
            if (Options.check("verbose")) {
                System.err.println("BADSIG: signature too short");
            }
            return 16;
        }
        if (!hmac.verify(signature, true)) {
            if (Options.check("verbose")) {
                System.err.println("BADSIG: signature verification");
            }
            return 16;
        }
        m.tsigState = 1;
        return 0;
    }
    
    public int verify(final Message m, final byte[] b, final TSIGRecord old) {
        return this.verify(m, b, b.length, old);
    }
    
    public int recordLength() {
        return this.name.length() + 10 + this.alg.length() + 8 + 18 + 4 + 8;
    }
    
    static {
        HMAC_MD5 = Name.fromConstantString("HMAC-MD5.SIG-ALG.REG.INT.");
        HMAC = TSIG.HMAC_MD5;
        HMAC_SHA1 = Name.fromConstantString("hmac-sha1.");
        HMAC_SHA224 = Name.fromConstantString("hmac-sha224.");
        HMAC_SHA256 = Name.fromConstantString("hmac-sha256.");
        HMAC_SHA384 = Name.fromConstantString("hmac-sha384.");
        HMAC_SHA512 = Name.fromConstantString("hmac-sha512.");
    }
    
    public static class StreamVerifier
    {
        private TSIG key;
        private HMAC verifier;
        private int nresponses;
        private int lastsigned;
        private TSIGRecord lastTSIG;
        
        public StreamVerifier(final TSIG tsig, final TSIGRecord old) {
            this.key = tsig;
            this.verifier = new HMAC(this.key.digest, this.key.digestBlockLength, this.key.key);
            this.nresponses = 0;
            this.lastTSIG = old;
        }
        
        public int verify(final Message m, final byte[] b) {
            final TSIGRecord tsig = m.getTSIG();
            ++this.nresponses;
            if (this.nresponses == 1) {
                final int result = this.key.verify(m, b, this.lastTSIG);
                if (result == 0) {
                    final byte[] signature = tsig.getSignature();
                    final DNSOutput out = new DNSOutput();
                    out.writeU16(signature.length);
                    this.verifier.update(out.toByteArray());
                    this.verifier.update(signature);
                }
                this.lastTSIG = tsig;
                return result;
            }
            if (tsig != null) {
                m.getHeader().decCount(3);
            }
            final byte[] header = m.getHeader().toWire();
            if (tsig != null) {
                m.getHeader().incCount(3);
            }
            this.verifier.update(header);
            int len;
            if (tsig == null) {
                len = b.length - header.length;
            }
            else {
                len = m.tsigstart - header.length;
            }
            this.verifier.update(b, header.length, len);
            if (tsig != null) {
                this.lastsigned = this.nresponses;
                this.lastTSIG = tsig;
                if (!tsig.getName().equals(this.key.name) || !tsig.getAlgorithm().equals(this.key.alg)) {
                    if (Options.check("verbose")) {
                        System.err.println("BADKEY failure");
                    }
                    m.tsigState = 4;
                    return 17;
                }
                DNSOutput out = new DNSOutput();
                final long time = tsig.getTimeSigned().getTime() / 1000L;
                final int timeHigh = (int)(time >> 32);
                final long timeLow = time & 0xFFFFFFFFL;
                out.writeU16(timeHigh);
                out.writeU32(timeLow);
                out.writeU16(tsig.getFudge());
                this.verifier.update(out.toByteArray());
                if (!this.verifier.verify(tsig.getSignature())) {
                    if (Options.check("verbose")) {
                        System.err.println("BADSIG failure");
                    }
                    m.tsigState = 4;
                    return 16;
                }
                this.verifier.clear();
                out = new DNSOutput();
                out.writeU16(tsig.getSignature().length);
                this.verifier.update(out.toByteArray());
                this.verifier.update(tsig.getSignature());
                m.tsigState = 1;
                return 0;
            }
            else {
                final boolean required = this.nresponses - this.lastsigned >= 100;
                if (required) {
                    m.tsigState = 4;
                    return 1;
                }
                m.tsigState = 2;
                return 0;
            }
        }
    }
}
