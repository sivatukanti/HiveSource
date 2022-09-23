// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.type.Asn1GeneralizedTime;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class KEKIdentifier extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public KEKIdentifier() {
        super(KEKIdentifier.fieldInfos);
    }
    
    public Asn1OctetString getKeyIdentifier() {
        return this.getFieldAs(KEKIdentifierField.KEY_IDENTIFIER, Asn1OctetString.class);
    }
    
    public void setKeyIdentifier(final Asn1OctetString keyIdentifier) {
        this.setFieldAs(KEKIdentifierField.KEY_IDENTIFIER, keyIdentifier);
    }
    
    public Asn1GeneralizedTime getDate() {
        return this.getFieldAs(KEKIdentifierField.DATE, Asn1GeneralizedTime.class);
    }
    
    public void setDate(final Asn1GeneralizedTime date) {
        this.setFieldAs(KEKIdentifierField.DATE, date);
    }
    
    public OtherKeyAttribute getOther() {
        return this.getFieldAs(KEKIdentifierField.OTHER, OtherKeyAttribute.class);
    }
    
    public void setOther(final OtherKeyAttribute other) {
        this.setFieldAs(KEKIdentifierField.OTHER, other);
    }
    
    static {
        KEKIdentifier.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(KEKIdentifierField.KEY_IDENTIFIER, Asn1OctetString.class), new Asn1FieldInfo(KEKIdentifierField.DATE, Asn1GeneralizedTime.class), new Asn1FieldInfo(KEKIdentifierField.OTHER, OtherKeyAttribute.class) };
    }
    
    protected enum KEKIdentifierField implements EnumType
    {
        KEY_IDENTIFIER, 
        DATE, 
        OTHER;
        
        @Override
        public int getValue() {
            return this.ordinal();
        }
        
        @Override
        public String getName() {
            return this.name();
        }
    }
}
