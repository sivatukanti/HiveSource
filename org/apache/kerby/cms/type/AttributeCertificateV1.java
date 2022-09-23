// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.type.Asn1BitString;
import org.apache.kerby.x509.type.AlgorithmIdentifier;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.x509.type.AttributeCertificateInfo;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class AttributeCertificateV1 extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public AttributeCertificateV1() {
        super(AttributeCertificateV1.fieldInfos);
    }
    
    public AttributeCertificateInfo getAcinfo() {
        return this.getFieldAs(AttributeCertificateV1Field.ACI_INFO, AttributeCertificateInfo.class);
    }
    
    public void setAciInfo(final AttributeCertificateInfo aciInfo) {
        this.setFieldAs(AttributeCertificateV1Field.ACI_INFO, aciInfo);
    }
    
    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.getFieldAs(AttributeCertificateV1Field.SIGNATURE_ALGORITHM, AlgorithmIdentifier.class);
    }
    
    public void setSignatureAlgorithm(final AlgorithmIdentifier signatureAlgorithm) {
        this.setFieldAs(AttributeCertificateV1Field.SIGNATURE_ALGORITHM, signatureAlgorithm);
    }
    
    public Asn1BitString getSignatureValue() {
        return this.getFieldAs(AttributeCertificateV1Field.SIGNATURE, Asn1BitString.class);
    }
    
    public void setSignatureValue(final Asn1BitString signatureValue) {
        this.setFieldAs(AttributeCertificateV1Field.SIGNATURE, signatureValue);
    }
    
    static {
        AttributeCertificateV1.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(AttributeCertificateV1Field.ACI_INFO, AttributeCertificateInfoV1.class), new Asn1FieldInfo(AttributeCertificateV1Field.SIGNATURE_ALGORITHM, AlgorithmIdentifier.class), new Asn1FieldInfo(AttributeCertificateV1Field.SIGNATURE, Asn1BitString.class) };
    }
    
    protected enum AttributeCertificateV1Field implements EnumType
    {
        ACI_INFO, 
        SIGNATURE_ALGORITHM, 
        SIGNATURE;
        
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
