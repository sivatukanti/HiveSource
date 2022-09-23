// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Any;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.type.Asn1ObjectIdentifier;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class OtherName extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public OtherName() {
        super(OtherName.fieldInfos);
    }
    
    public Asn1ObjectIdentifier getTypeId() {
        return this.getFieldAs(OtherNameField.TYPE_ID, Asn1ObjectIdentifier.class);
    }
    
    public void setTypeId(final Asn1ObjectIdentifier algorithm) {
        this.setFieldAs(OtherNameField.TYPE_ID, algorithm);
    }
    
    public <T extends Asn1Type> T getOtherNameValueAs(final Class<T> t) {
        return this.getFieldAsAny(OtherNameField.VALUE, t);
    }
    
    public void setOtherNameValue(final Asn1Type value) {
        this.setFieldAsAny(OtherNameField.VALUE, value);
    }
    
    static {
        OtherName.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(OtherNameField.TYPE_ID, Asn1ObjectIdentifier.class), new ExplicitField(OtherNameField.VALUE, 0, Asn1Any.class) };
    }
    
    protected enum OtherNameField implements EnumType
    {
        TYPE_ID, 
        VALUE;
        
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
