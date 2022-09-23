// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class ExtendedCertificateInfo extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public ExtendedCertificateInfo() {
        super(ExtendedCertificateInfo.fieldInfos);
    }
    
    public CmsVersion getCmsVersion() {
        return this.getFieldAs(ExtendedCertificateInfoField.CMS_VERSION, CmsVersion.class);
    }
    
    public void setCmsVersion(final CmsVersion version) {
        this.setFieldAs(ExtendedCertificateInfoField.CMS_VERSION, version);
    }
    
    public SignatureAlgorithmIdentifier getCertificate() {
        return this.getFieldAs(ExtendedCertificateInfoField.CERTIFICATE, SignatureAlgorithmIdentifier.class);
    }
    
    public void setCertificate(final SignatureAlgorithmIdentifier signatureAlgorithmIdentifier) {
        this.setFieldAs(ExtendedCertificateInfoField.CERTIFICATE, signatureAlgorithmIdentifier);
    }
    
    public Signature getAttributes() {
        return this.getFieldAs(ExtendedCertificateInfoField.ATTRIBUTES, Signature.class);
    }
    
    public void setAttributes(final Signature signature) {
        this.setFieldAs(ExtendedCertificateInfoField.ATTRIBUTES, signature);
    }
    
    static {
        ExtendedCertificateInfo.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(ExtendedCertificateInfoField.CMS_VERSION, CmsVersion.class), new Asn1FieldInfo(ExtendedCertificateInfoField.CERTIFICATE, SignatureAlgorithmIdentifier.class), new Asn1FieldInfo(ExtendedCertificateInfoField.ATTRIBUTES, Signature.class) };
    }
    
    protected enum ExtendedCertificateInfoField implements EnumType
    {
        CMS_VERSION, 
        CERTIFICATE, 
        ATTRIBUTES;
        
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
