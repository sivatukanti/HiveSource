// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1;

import org.apache.kerby.asn1.type.Asn1Type;

public class Asn1FieldInfo
{
    private EnumType index;
    private int tagNo;
    private boolean isImplicit;
    private Class<? extends Asn1Type> type;
    private Tag tag;
    
    public Asn1FieldInfo(final EnumType index, final Class<? extends Asn1Type> type, final boolean isImplicit) {
        this(index, index.getValue(), type, isImplicit);
    }
    
    public Asn1FieldInfo(final EnumType index, final int tagNo, final Class<? extends Asn1Type> type, final boolean isImplicit) {
        this.tagNo = -1;
        this.tag = null;
        this.index = index;
        this.tagNo = tagNo;
        this.type = type;
        this.isImplicit = isImplicit;
    }
    
    public Asn1FieldInfo(final EnumType index, final Class<? extends Asn1Type> type) {
        this.tagNo = -1;
        this.tag = null;
        this.index = index;
        this.type = type;
        this.tagNo = -1;
    }
    
    public boolean isTagged() {
        return this.tagNo != -1;
    }
    
    public TaggingOption getTaggingOption() {
        if (this.isImplicit) {
            return TaggingOption.newImplicitContextSpecific(this.tagNo);
        }
        return TaggingOption.newExplicitContextSpecific(this.tagNo);
    }
    
    public int getTagNo() {
        return this.tagNo;
    }
    
    public EnumType getIndex() {
        return this.index;
    }
    
    public boolean isImplicit() {
        return this.isImplicit;
    }
    
    public Asn1Type createFieldValue() {
        try {
            return (Asn1Type)this.type.newInstance();
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Bad field type provided, no default constructor?", e);
        }
    }
    
    public Tag getFieldTag() {
        if (this.tag == null) {
            final Asn1Type fieldValue = this.createFieldValue();
            this.tag = fieldValue.tag();
        }
        return this.tag;
    }
    
    public Class<? extends Asn1Type> getType() {
        return this.type;
    }
}
