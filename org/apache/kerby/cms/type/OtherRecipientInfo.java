// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.type.Asn1Any;
import org.apache.kerby.asn1.type.Asn1BitString;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.type.Asn1ObjectIdentifier;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class OtherRecipientInfo extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public OtherRecipientInfo() {
        super(OtherRecipientInfo.fieldInfos);
    }
    
    public Asn1ObjectIdentifier getOriType() {
        return this.getFieldAs(OtherRecipientInfoField.ORI_TYPE, Asn1ObjectIdentifier.class);
    }
    
    public void setOriType(final Asn1ObjectIdentifier oriType) {
        this.setFieldAs(OtherRecipientInfoField.ORI_TYPE, oriType);
    }
    
    public Asn1BitString getPublicKey() {
        return this.getFieldAs(OtherRecipientInfoField.ORI_VALUE, Asn1BitString.class);
    }
    
    public void setOriValue(final Asn1BitString oriValue) {
        this.setFieldAs(OtherRecipientInfoField.ORI_VALUE, oriValue);
    }
    
    static {
        OtherRecipientInfo.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(OtherRecipientInfoField.ORI_TYPE, Asn1ObjectIdentifier.class), new Asn1FieldInfo(OtherRecipientInfoField.ORI_VALUE, Asn1Any.class) };
    }
    
    protected enum OtherRecipientInfoField implements EnumType
    {
        ORI_TYPE, 
        ORI_VALUE;
        
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
