// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1;

public final class TaggingOption
{
    private int tagNo;
    private boolean isImplicit;
    private boolean isAppSpecific;
    
    public static TaggingOption newImplicitAppSpecific(final int tagNo) {
        return new TaggingOption(tagNo, true, true);
    }
    
    public static TaggingOption newExplicitAppSpecific(final int tagNo) {
        return new TaggingOption(tagNo, false, true);
    }
    
    public static TaggingOption newImplicitContextSpecific(final int tagNo) {
        return new TaggingOption(tagNo, true, false);
    }
    
    public static TaggingOption newExplicitContextSpecific(final int tagNo) {
        return new TaggingOption(tagNo, false, false);
    }
    
    private TaggingOption(final int tagNo, final boolean isImplicit, final boolean isAppSpecific) {
        this.tagNo = tagNo;
        this.isImplicit = isImplicit;
        this.isAppSpecific = isAppSpecific;
    }
    
    public Tag getTag(final boolean isTaggedConstructed) {
        final boolean isConstructed = !this.isImplicit || isTaggedConstructed;
        final TagClass tagClass = this.isAppSpecific ? TagClass.APPLICATION : TagClass.CONTEXT_SPECIFIC;
        final int flags = tagClass.getValue() | (isConstructed ? 32 : 0);
        return new Tag(flags, this.tagNo);
    }
    
    public int getTagNo() {
        return this.tagNo;
    }
    
    public boolean isAppSpecific() {
        return this.isAppSpecific;
    }
    
    public boolean isImplicit() {
        return this.isImplicit;
    }
}
