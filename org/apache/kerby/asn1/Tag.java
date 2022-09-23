// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1;

public class Tag
{
    private int tagFlags;
    private int tagNo;
    
    public Tag(final int tag) {
        this.tagFlags = 0;
        this.tagNo = 0;
        this.tagFlags = (tag & 0xE0);
        this.tagNo = (tag & 0x1F);
    }
    
    public Tag(final UniversalTag tag) {
        this.tagFlags = 0;
        this.tagNo = 0;
        this.tagFlags = TagClass.UNIVERSAL.getValue();
        this.tagNo = tag.getValue();
    }
    
    public Tag(final int tagFlags, final int tagNo) {
        this.tagFlags = 0;
        this.tagNo = 0;
        this.tagFlags = (tagFlags & 0xE0);
        this.tagNo = tagNo;
    }
    
    public Tag(final TagClass tagClass, final int tagNo) {
        this.tagFlags = 0;
        this.tagNo = 0;
        this.tagFlags = tagClass.getValue();
        this.tagNo = tagNo;
    }
    
    public Tag(final Tag other) {
        this(other.tagFlags, other.tagNo);
    }
    
    public TagClass tagClass() {
        return TagClass.fromTag(this.tagFlags);
    }
    
    public void usePrimitive(final boolean isPrimitive) {
        if (isPrimitive) {
            this.tagFlags &= 0xFFFFFFDF;
        }
        else {
            this.tagFlags |= 0x20;
        }
    }
    
    public boolean isPrimitive() {
        return (this.tagFlags & 0x20) == 0x0;
    }
    
    public int tagFlags() {
        return this.tagFlags;
    }
    
    public int tagNo() {
        return this.tagNo;
    }
    
    public UniversalTag universalTag() {
        if (this.isUniversal()) {
            return UniversalTag.fromValue(this.tagNo());
        }
        return UniversalTag.UNKNOWN;
    }
    
    public boolean isEOC() {
        return this.universalTag() == UniversalTag.EOC;
    }
    
    public boolean isNull() {
        return this.universalTag() == UniversalTag.NULL;
    }
    
    public boolean isUniversal() {
        return this.tagClass().isUniversal();
    }
    
    public boolean isAppSpecific() {
        return this.tagClass().isAppSpecific();
    }
    
    public boolean isContextSpecific() {
        return this.tagClass().isContextSpecific();
    }
    
    public boolean isSpecific() {
        return this.tagClass().isSpecific();
    }
    
    public byte tagByte() {
        final int n = this.tagFlags | ((this.tagNo < 31) ? this.tagNo : 31);
        return (byte)(n & 0xFF);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Tag tag = (Tag)o;
        return this.tagFlags == tag.tagFlags && this.tagNo == tag.tagNo;
    }
    
    @Override
    public int hashCode() {
        int result = this.tagFlags;
        result = 31 * result + this.tagNo;
        return result;
    }
    
    @Override
    public String toString() {
        return String.format("0x%02X", this.tagByte());
    }
    
    public String typeStr() {
        if (this.isUniversal()) {
            return this.universalTag().toStr();
        }
        if (this.isAppSpecific()) {
            return "application [" + this.tagNo() + "]";
        }
        return "context [" + this.tagNo() + "]";
    }
    
    public static Tag newAppTag(final int tagNo) {
        return new Tag(TagClass.APPLICATION, tagNo);
    }
    
    public static Tag newCtxTag(final int tagNo) {
        return new Tag(TagClass.CONTEXT_SPECIFIC, tagNo);
    }
}
