// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1BitString;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class AttributeCertificate extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public AttributeCertificate() {
        super(AttributeCertificate.fieldInfos);
    }
    
    public AttributeCertificateInfo getAcinfo() {
        return this.getFieldAs(AttributeCertificateField.ACI_INFO, AttributeCertificateInfo.class);
    }
    
    public void setAciInfo(final AttributeCertificateInfo aciInfo) {
        this.setFieldAs(AttributeCertificateField.ACI_INFO, aciInfo);
    }
    
    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.getFieldAs(AttributeCertificateField.SIGNATURE_ALGORITHM, AlgorithmIdentifier.class);
    }
    
    public void setSignatureAlgorithm(final AlgorithmIdentifier signatureAlgorithm) {
        this.setFieldAs(AttributeCertificateField.SIGNATURE_ALGORITHM, signatureAlgorithm);
    }
    
    public Asn1BitString getSignatureValue() {
        return this.getFieldAs(AttributeCertificateField.SIGNATURE_VALUE, Asn1BitString.class);
    }
    
    public void setSignatureValue(final Asn1BitString signatureValue) {
        this.setFieldAs(AttributeCertificateField.SIGNATURE_VALUE, signatureValue);
    }
    
    static {
        AttributeCertificate.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(AttributeCertificateField.ACI_INFO, AttributeCertificateInfo.class), new Asn1FieldInfo(AttributeCertificateField.SIGNATURE_ALGORITHM, AlgorithmIdentifier.class), new Asn1FieldInfo(AttributeCertificateField.SIGNATURE_VALUE, Asn1BitString.class) };
    }
    
    protected enum AttributeCertificateField implements EnumType
    {
        ACI_INFO, 
        SIGNATURE_ALGORITHM, 
        SIGNATURE_VALUE;
        
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
