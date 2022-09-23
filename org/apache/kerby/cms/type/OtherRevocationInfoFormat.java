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

public class OtherRevocationInfoFormat extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public OtherRevocationInfoFormat() {
        super(OtherRevocationInfoFormat.fieldInfos);
    }
    
    public Asn1ObjectIdentifier getOtherRevInfoFormat() {
        return this.getFieldAs(OtherRevocationInfoFormatField.OTHER_REV_INFO_FORMAT, Asn1ObjectIdentifier.class);
    }
    
    public void setOtherRevInfoFormat(final Asn1ObjectIdentifier otherRevInfoFormat) {
        this.setFieldAs(OtherRevocationInfoFormatField.OTHER_REV_INFO_FORMAT, otherRevInfoFormat);
    }
    
    public <T extends Asn1Type> T getOtherRevInfoAs(final Class<T> t) {
        return this.getFieldAsAny(OtherRevocationInfoFormatField.OTHER_REV_INFO, t);
    }
    
    public void setOtherRevInfo(final Asn1Type otherRevInfo) {
        this.setFieldAsAny(OtherRevocationInfoFormatField.OTHER_REV_INFO, otherRevInfo);
    }
    
    static {
        OtherRevocationInfoFormat.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(OtherRevocationInfoFormatField.OTHER_REV_INFO_FORMAT, Asn1ObjectIdentifier.class), new Asn1FieldInfo(OtherRevocationInfoFormatField.OTHER_REV_INFO, Asn1Any.class) };
    }
    
    protected enum OtherRevocationInfoFormatField implements EnumType
    {
        OTHER_REV_INFO_FORMAT, 
        OTHER_REV_INFO;
        
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
