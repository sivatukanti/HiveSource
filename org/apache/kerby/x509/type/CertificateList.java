// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1BitString;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class CertificateList extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public CertificateList() {
        super(CertificateList.fieldInfos);
    }
    
    public TBSCertList getTBSCertList() {
        return this.getFieldAs(CertificateListField.TBS_CERT_LIST, TBSCertList.class);
    }
    
    public void setTBSCertList(final TBSCertList tbsCertList) {
        this.setFieldAs(CertificateListField.TBS_CERT_LIST, tbsCertList);
    }
    
    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.getFieldAs(CertificateListField.SIGNATURE_ALGORITHMS, AlgorithmIdentifier.class);
    }
    
    public void setSignatureAlgorithms(final AlgorithmIdentifier signatureAlgorithms) {
        this.setFieldAs(CertificateListField.SIGNATURE_ALGORITHMS, signatureAlgorithms);
    }
    
    public Asn1BitString getSignature() {
        return this.getFieldAs(CertificateListField.SIGNATURE_VALUE, Asn1BitString.class);
    }
    
    public void setSignatureValue(final Asn1BitString signatureValue) {
        this.setFieldAs(CertificateListField.SIGNATURE_VALUE, signatureValue);
    }
    
    static {
        CertificateList.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(CertificateListField.TBS_CERT_LIST, TBSCertList.class), new Asn1FieldInfo(CertificateListField.SIGNATURE_ALGORITHMS, AlgorithmIdentifier.class), new Asn1FieldInfo(CertificateListField.SIGNATURE_VALUE, Asn1BitString.class) };
    }
    
    protected enum CertificateListField implements EnumType
    {
        TBS_CERT_LIST, 
        SIGNATURE_ALGORITHMS, 
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
