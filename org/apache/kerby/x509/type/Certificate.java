// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1BitString;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class Certificate extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public Certificate() {
        super(Certificate.fieldInfos);
    }
    
    public TBSCertificate getTBSCertificate() {
        return this.getFieldAs(CertificateField.TBS_CERTIFICATE, TBSCertificate.class);
    }
    
    public void setTbsCertificate(final TBSCertificate tbsCertificate) {
        this.setFieldAs(CertificateField.TBS_CERTIFICATE, tbsCertificate);
    }
    
    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.getFieldAs(CertificateField.SIGNATURE_ALGORITHM, AlgorithmIdentifier.class);
    }
    
    public void setSignatureAlgorithm(final AlgorithmIdentifier signatureAlgorithm) {
        this.setFieldAs(CertificateField.SIGNATURE_ALGORITHM, signatureAlgorithm);
    }
    
    public Asn1BitString getSignature() {
        return this.getFieldAs(CertificateField.SIGNATURE, Asn1BitString.class);
    }
    
    public void setSignature(final Asn1BitString signature) {
        this.setFieldAs(CertificateField.SIGNATURE, signature);
    }
    
    static {
        Certificate.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(CertificateField.TBS_CERTIFICATE, TBSCertificate.class), new Asn1FieldInfo(CertificateField.SIGNATURE_ALGORITHM, AlgorithmIdentifier.class), new Asn1FieldInfo(CertificateField.SIGNATURE, Asn1BitString.class) };
    }
    
    protected enum CertificateField implements EnumType
    {
        TBS_CERTIFICATE, 
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
