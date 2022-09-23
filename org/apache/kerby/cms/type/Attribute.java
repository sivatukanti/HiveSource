// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.x509.type.AttributeValues;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.type.Asn1ObjectIdentifier;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class Attribute extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public Attribute() {
        super(Attribute.fieldInfos);
    }
    
    public Asn1ObjectIdentifier getAttrType() {
        return this.getFieldAs(AttributeField.ATTR_TYPE, Asn1ObjectIdentifier.class);
    }
    
    public void setAttrType(final Asn1ObjectIdentifier attrType) {
        this.setFieldAs(AttributeField.ATTR_TYPE, attrType);
    }
    
    public AttributeValues getAttrValues() {
        return this.getFieldAs(AttributeField.ATTR_VALUES, AttributeValues.class);
    }
    
    public void setAttrValues(final AttributeValues values) {
        this.setFieldAs(AttributeField.ATTR_VALUES, values);
    }
    
    static {
        Attribute.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(AttributeField.ATTR_TYPE, Asn1ObjectIdentifier.class), new Asn1FieldInfo(AttributeField.ATTR_VALUES, AttributeValues.class) };
    }
    
    protected enum AttributeField implements EnumType
    {
        ATTR_TYPE, 
        ATTR_VALUES;
        
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
