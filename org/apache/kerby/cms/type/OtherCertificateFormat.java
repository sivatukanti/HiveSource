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

public class OtherCertificateFormat extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public OtherCertificateFormat() {
        super(OtherCertificateFormat.fieldInfos);
    }
    
    public Asn1ObjectIdentifier getOtherCertFormat() {
        return this.getFieldAs(OtherCertificateFormatField.OTHER_CERT_FORMAT, Asn1ObjectIdentifier.class);
    }
    
    public void setOtherCertFormat(final Asn1ObjectIdentifier otherCertFormat) {
        this.setFieldAs(OtherCertificateFormatField.OTHER_CERT_FORMAT, otherCertFormat);
    }
    
    public <T extends Asn1Type> T getOtherCertAs(final Class<T> t) {
        return this.getFieldAsAny(OtherCertificateFormatField.OTHER_CERT, t);
    }
    
    public void setOtherCert(final Asn1Type otherCert) {
        this.setFieldAsAny(OtherCertificateFormatField.OTHER_CERT, otherCert);
    }
    
    static {
        OtherCertificateFormat.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(OtherCertificateFormatField.OTHER_CERT_FORMAT, Asn1ObjectIdentifier.class), new Asn1FieldInfo(OtherCertificateFormatField.OTHER_CERT, Asn1Any.class) };
    }
    
    protected enum OtherCertificateFormatField implements EnumType
    {
        OTHER_CERT_FORMAT, 
        OTHER_CERT;
        
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
