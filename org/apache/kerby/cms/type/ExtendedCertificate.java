// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class ExtendedCertificate extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public ExtendedCertificate() {
        super(ExtendedCertificate.fieldInfos);
    }
    
    public ExtendedCertificateInfo getExtendedCertificateInfo() {
        return this.getFieldAs(ExtendedCertificateField.EXTENDED_CERTIFICATE_INFO, ExtendedCertificateInfo.class);
    }
    
    public void setExtendedCertificateInfo(final ExtendedCertificateInfo extendedCertificateInfo) {
        this.setFieldAs(ExtendedCertificateField.EXTENDED_CERTIFICATE_INFO, extendedCertificateInfo);
    }
    
    public SignatureAlgorithmIdentifier getSignatureAlgorithmIdentifier() {
        return this.getFieldAs(ExtendedCertificateField.SIGNATURE_ALGORITHMS, SignatureAlgorithmIdentifier.class);
    }
    
    public void setSignatureAlgorithmIdentifier(final SignatureAlgorithmIdentifier signatureAlgorithmIdentifier) {
        this.setFieldAs(ExtendedCertificateField.SIGNATURE_ALGORITHMS, signatureAlgorithmIdentifier);
    }
    
    public Signature getSignature() {
        return this.getFieldAs(ExtendedCertificateField.SIGNATURE, Signature.class);
    }
    
    public void setSignature(final Signature signature) {
        this.setFieldAs(ExtendedCertificateField.SIGNATURE, signature);
    }
    
    static {
        ExtendedCertificate.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(ExtendedCertificateField.EXTENDED_CERTIFICATE_INFO, ExtendedCertificateInfo.class), new Asn1FieldInfo(ExtendedCertificateField.SIGNATURE_ALGORITHMS, SignatureAlgorithmIdentifier.class), new Asn1FieldInfo(ExtendedCertificateField.SIGNATURE, Signature.class) };
    }
    
    protected enum ExtendedCertificateField implements EnumType
    {
        EXTENDED_CERTIFICATE_INFO, 
        SIGNATURE_ALGORITHMS, 
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
