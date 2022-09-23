// 
// Decompiled by Procyon v0.5.36
// 

package org.xbill.DNS;

import org.xbill.DNS.utils.base16;
import java.io.IOException;

public class DSRecord extends Record
{
    public static final int SHA1_DIGEST_ID = 1;
    public static final int SHA256_DIGEST_ID = 2;
    public static final int GOST3411_DIGEST_ID = 3;
    public static final int SHA384_DIGEST_ID = 4;
    private static final long serialVersionUID = -9001819329700081493L;
    private int footprint;
    private int alg;
    private int digestid;
    private byte[] digest;
    
    DSRecord() {
    }
    
    Record getObject() {
        return new DSRecord();
    }
    
    public DSRecord(final Name name, final int dclass, final long ttl, final int footprint, final int alg, final int digestid, final byte[] digest) {
        super(name, 43, dclass, ttl);
        this.footprint = Record.checkU16("footprint", footprint);
        this.alg = Record.checkU8("alg", alg);
        this.digestid = Record.checkU8("digestid", digestid);
        this.digest = digest;
    }
    
    public DSRecord(final Name name, final int dclass, final long ttl, final int digestid, final DNSKEYRecord key) {
        this(name, dclass, ttl, key.getFootprint(), key.getAlgorithm(), digestid, DNSSEC.generateDSDigest(key, digestid));
    }
    
    void rrFromWire(final DNSInput in) throws IOException {
        this.footprint = in.readU16();
        this.alg = in.readU8();
        this.digestid = in.readU8();
        this.digest = in.readByteArray();
    }
    
    void rdataFromString(final Tokenizer st, final Name origin) throws IOException {
        this.footprint = st.getUInt16();
        this.alg = st.getUInt8();
        this.digestid = st.getUInt8();
        this.digest = st.getHex();
    }
    
    String rrToString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.footprint);
        sb.append(" ");
        sb.append(this.alg);
        sb.append(" ");
        sb.append(this.digestid);
        if (this.digest != null) {
            sb.append(" ");
            sb.append(base16.toString(this.digest));
        }
        return sb.toString();
    }
    
    public int getAlgorithm() {
        return this.alg;
    }
    
    public int getDigestID() {
        return this.digestid;
    }
    
    public byte[] getDigest() {
        return this.digest;
    }
    
    public int getFootprint() {
        return this.footprint;
    }
    
    void rrToWire(final DNSOutput out, final Compression c, final boolean canonical) {
        out.writeU16(this.footprint);
        out.writeU8(this.alg);
        out.writeU8(this.digestid);
        if (this.digest != null) {
            out.writeByteArray(this.digest);
        }
    }
    
    public static class Digest
    {
        public static final int SHA1 = 1;
        public static final int SHA256 = 2;
        public static final int GOST3411 = 3;
        public static final int SHA384 = 4;
        
        private Digest() {
        }
    }
}
