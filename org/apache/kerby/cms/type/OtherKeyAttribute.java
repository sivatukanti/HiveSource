// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.type.Asn1Any;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.type.Asn1ObjectIdentifier;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class OtherKeyAttribute extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public OtherKeyAttribute() {
        super(OtherKeyAttribute.fieldInfos);
    }
    
    public Asn1ObjectIdentifier getAlgorithm() {
        return this.getFieldAs(OtherKeyAttributeField.KEY_ATTR_ID, Asn1ObjectIdentifier.class);
    }
    
    public void setAlgorithm(final Asn1ObjectIdentifier keyAttrId) {
        this.setFieldAs(OtherKeyAttributeField.KEY_ATTR_ID, keyAttrId);
    }
    
    public <T extends Asn1Type> T getKeyAttrAs(final Class<T> t) {
        return this.getFieldAsAny(OtherKeyAttributeField.KEY_ATTR, t);
    }
    
    public void setKeyAttr(final Asn1Type keyAttr) {
        this.setFieldAsAny(OtherKeyAttributeField.KEY_ATTR, keyAttr);
    }
    
    static {
        OtherKeyAttribute.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(OtherKeyAttributeField.KEY_ATTR_ID, Asn1ObjectIdentifier.class), new Asn1FieldInfo(OtherKeyAttributeField.KEY_ATTR, Asn1Any.class) };
    }
    
    protected enum OtherKeyAttributeField implements EnumType
    {
        KEY_ATTR_ID, 
        KEY_ATTR;
        
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
