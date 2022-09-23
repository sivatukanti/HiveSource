// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.ImplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class SignedData extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public SignedData() {
        super(SignedData.fieldInfos);
    }
    
    public int getVersion() {
        return this.getFieldAsInteger(SignedDataField.CMS_VERSION);
    }
    
    public void setVersion(final int version) {
        this.setFieldAsInt(SignedDataField.CMS_VERSION, version);
    }
    
    public DigestAlgorithmIdentifiers getDigestAlgorithms() {
        return this.getFieldAs(SignedDataField.DIGEST_ALGORITHMS, DigestAlgorithmIdentifiers.class);
    }
    
    public void setDigestAlgorithms(final DigestAlgorithmIdentifiers digestAlgorithms) {
        this.setFieldAs(SignedDataField.DIGEST_ALGORITHMS, digestAlgorithms);
    }
    
    public EncapsulatedContentInfo getEncapContentInfo() {
        return this.getFieldAs(SignedDataField.ENCAP_CONTENT_INFO, EncapsulatedContentInfo.class);
    }
    
    public void setEncapContentInfo(final EncapsulatedContentInfo contentInfo) {
        this.setFieldAs(SignedDataField.ENCAP_CONTENT_INFO, contentInfo);
    }
    
    public CertificateSet getCertificates() {
        return this.getFieldAs(SignedDataField.CERTIFICATES, CertificateSet.class);
    }
    
    public void setCertificates(final CertificateSet certificates) {
        this.setFieldAs(SignedDataField.CERTIFICATES, certificates);
    }
    
    public RevocationInfoChoices getCrls() {
        return this.getFieldAs(SignedDataField.CRLS, RevocationInfoChoices.class);
    }
    
    public void setCrls(final RevocationInfoChoices crls) {
        this.setFieldAs(SignedDataField.CRLS, crls);
    }
    
    public SignerInfos getSignerInfos() {
        return this.getFieldAs(SignedDataField.SIGNER_INFOS, SignerInfos.class);
    }
    
    public void setSignerInfos(final SignerInfos signerInfos) {
        this.setFieldAs(SignedDataField.SIGNER_INFOS, signerInfos);
    }
    
    public boolean isSigned() {
        return this.getSignerInfos().getElements().size() != 0;
    }
    
    static {
        SignedData.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(SignedDataField.CMS_VERSION, CmsVersion.class), new Asn1FieldInfo(SignedDataField.DIGEST_ALGORITHMS, DigestAlgorithmIdentifiers.class), new Asn1FieldInfo(SignedDataField.ENCAP_CONTENT_INFO, EncapsulatedContentInfo.class), new ImplicitField(SignedDataField.CERTIFICATES, 0, CertificateSet.class), new ImplicitField(SignedDataField.CRLS, 1, RevocationInfoChoices.class), new Asn1FieldInfo(SignedDataField.SIGNER_INFOS, SignerInfos.class) };
    }
    
    protected enum SignedDataField implements EnumType
    {
        CMS_VERSION, 
        DIGEST_ALGORITHMS, 
        ENCAP_CONTENT_INFO, 
        CERTIFICATES, 
        CRLS, 
        SIGNER_INFOS;
        
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
