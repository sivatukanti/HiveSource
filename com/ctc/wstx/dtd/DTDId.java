// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import java.net.URI;

public final class DTDId
{
    protected final String mPublicId;
    protected final URI mSystemId;
    protected final int mConfigFlags;
    protected final boolean mXml11;
    protected int mHashCode;
    
    private DTDId(final String publicId, final URI systemId, final int configFlags, final boolean xml11) {
        this.mHashCode = 0;
        this.mPublicId = publicId;
        this.mSystemId = systemId;
        this.mConfigFlags = configFlags;
        this.mXml11 = xml11;
    }
    
    public static DTDId constructFromPublicId(final String publicId, final int configFlags, final boolean xml11) {
        if (publicId == null || publicId.length() == 0) {
            throw new IllegalArgumentException("Empty/null public id.");
        }
        return new DTDId(publicId, null, configFlags, xml11);
    }
    
    public static DTDId constructFromSystemId(final URI systemId, final int configFlags, final boolean xml11) {
        if (systemId == null) {
            throw new IllegalArgumentException("Null system id.");
        }
        return new DTDId(null, systemId, configFlags, xml11);
    }
    
    public static DTDId construct(final String publicId, final URI systemId, final int configFlags, final boolean xml11) {
        if (publicId != null && publicId.length() > 0) {
            return new DTDId(publicId, null, configFlags, xml11);
        }
        if (systemId == null) {
            throw new IllegalArgumentException("Illegal arguments; both public and system id null/empty.");
        }
        return new DTDId(null, systemId, configFlags, xml11);
    }
    
    @Override
    public int hashCode() {
        int hash = this.mHashCode;
        if (hash == 0) {
            hash = this.mConfigFlags;
            if (this.mPublicId != null) {
                hash ^= this.mPublicId.hashCode();
            }
            else {
                hash ^= this.mSystemId.hashCode();
            }
            if (this.mXml11) {
                hash ^= 0x1;
            }
            this.mHashCode = hash;
        }
        return hash;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(60);
        sb.append("Public-id: ");
        sb.append(this.mPublicId);
        sb.append(", system-id: ");
        sb.append(this.mSystemId);
        sb.append(" [config flags: 0x");
        sb.append(Integer.toHexString(this.mConfigFlags));
        sb.append("], xml11: ");
        sb.append(this.mXml11);
        return sb.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        final DTDId other = (DTDId)o;
        if (other.mConfigFlags != this.mConfigFlags || other.mXml11 != this.mXml11) {
            return false;
        }
        if (this.mPublicId != null) {
            final String op = other.mPublicId;
            return op != null && op.equals(this.mPublicId);
        }
        return this.mSystemId.equals(other.mSystemId);
    }
}
