// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.ImplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.x509.type.Certificate;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1Choice;

public class CertificateChoices extends Asn1Choice
{
    static Asn1FieldInfo[] fieldInfos;
    
    public CertificateChoices() {
        super(CertificateChoices.fieldInfos);
    }
    
    public Certificate getCertificate() {
        return this.getChoiceValueAs(CertificateChoicesField.CERTIFICATE, Certificate.class);
    }
    
    public void setCertificate(final Certificate certificate) {
        this.setChoiceValue(CertificateChoicesField.CERTIFICATE, certificate);
    }
    
    public ExtendedCertificate getExtendedCertificate() {
        return this.getChoiceValueAs(CertificateChoicesField.EXTENDED_CERTIFICATE, ExtendedCertificate.class);
    }
    
    public void setExtendedCertificate(final ExtendedCertificate extendedCertificate) {
        this.setChoiceValue(CertificateChoicesField.EXTENDED_CERTIFICATE, extendedCertificate);
    }
    
    public AttributeCertificateV1 getV1AttrCert() {
        return this.getChoiceValueAs(CertificateChoicesField.V1_ATTR_CERT, AttributeCertificateV1.class);
    }
    
    public void setV1AttrCert(final AttributeCertificateV1 v1AttrCert) {
        this.setChoiceValue(CertificateChoicesField.V1_ATTR_CERT, v1AttrCert);
    }
    
    public AttributeCertificateV2 getV2AttrCert() {
        return this.getChoiceValueAs(CertificateChoicesField.V2_ATTR_CERT, AttributeCertificateV2.class);
    }
    
    public void setV2AttrCert(final AttributeCertificateV2 v2AttrCert) {
        this.setChoiceValue(CertificateChoicesField.V2_ATTR_CERT, v2AttrCert);
    }
    
    public OtherCertificateFormat getOther() {
        return this.getChoiceValueAs(CertificateChoicesField.OTHER, OtherCertificateFormat.class);
    }
    
    public void setOther(final OtherCertificateFormat other) {
        this.setChoiceValue(CertificateChoicesField.OTHER, other);
    }
    
    static {
        CertificateChoices.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(CertificateChoicesField.CERTIFICATE, Certificate.class), new ImplicitField(CertificateChoicesField.EXTENDED_CERTIFICATE, 0, ExtendedCertificate.class), new ImplicitField(CertificateChoicesField.V1_ATTR_CERT, 1, AttributeCertificateV1.class), new ImplicitField(CertificateChoicesField.V2_ATTR_CERT, 2, AttributeCertificateV2.class), new ImplicitField(CertificateChoicesField.OTHER, 3, OtherCertificateFormat.class) };
    }
    
    protected enum CertificateChoicesField implements EnumType
    {
        CERTIFICATE, 
        EXTENDED_CERTIFICATE, 
        V1_ATTR_CERT, 
        V2_ATTR_CERT, 
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
