// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x500.type;

import org.apache.kerby.asn1.type.Asn1Any;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.type.Asn1ObjectIdentifier;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class AttributeTypeAndValue extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public AttributeTypeAndValue() {
        super(AttributeTypeAndValue.fieldInfos);
    }
    
    public Asn1ObjectIdentifier getType() {
        return this.getFieldAs(AttributeTypeAndValueField.TYPE, Asn1ObjectIdentifier.class);
    }
    
    public void setType(final Asn1ObjectIdentifier type) {
        this.setFieldAs(AttributeTypeAndValueField.TYPE, type);
    }
    
    public <T extends Asn1Type> T getAttributeValueAs(final Class<T> t) {
        return this.getFieldAsAny(AttributeTypeAndValueField.VALUE, t);
    }
    
    public void setAttributeValue(final Asn1Type value) {
        this.setFieldAsAny(AttributeTypeAndValueField.VALUE, value);
    }
    
    static {
        AttributeTypeAndValue.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(AttributeTypeAndValueField.TYPE, -1, Asn1ObjectIdentifier.class, true), new Asn1FieldInfo(AttributeTypeAndValueField.VALUE, -1, Asn1Any.class, true) };
    }
    
    protected enum AttributeTypeAndValueField implements EnumType
    {
        TYPE, 
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
