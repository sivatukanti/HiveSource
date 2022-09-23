// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.ImplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class SignerInfo extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public SignerInfo() {
        super(SignerInfo.fieldInfos);
    }
    
    public int getCmsVersion() {
        return this.getFieldAsInteger(SignerInfoField.CMS_VERSION);
    }
    
    public void setCmsVersion(final int version) {
        this.setFieldAsInt(SignerInfoField.CMS_VERSION, version);
    }
    
    public SignerIdentifier getSignerIdentifier() {
        return this.getFieldAs(SignerInfoField.SID, SignerIdentifier.class);
    }
    
    public void setSignerIdentifier(final SignerIdentifier signerIdentifier) {
        this.setFieldAs(SignerInfoField.SID, signerIdentifier);
    }
    
    public DigestAlgorithmIdentifier getDigestAlgorithmIdentifier() {
        return this.getFieldAs(SignerInfoField.DIGEST_ALGORITHM, DigestAlgorithmIdentifier.class);
    }
    
    public void setDigestAlgorithmIdentifier(final DigestAlgorithmIdentifier digestAlgorithmIdentifier) {
        this.setFieldAs(SignerInfoField.DIGEST_ALGORITHM, digestAlgorithmIdentifier);
    }
    
    public SignedAttributes getSignedAttributes() {
        return this.getFieldAs(SignerInfoField.SIGNED_ATTRS, SignedAttributes.class);
    }
    
    public void setSignedAttributes(final SignedAttributes signedAttributes) {
        this.setFieldAs(SignerInfoField.SIGNED_ATTRS, signedAttributes);
    }
    
    public SignatureAlgorithmIdentifier getSignatureAlgorithmIdentifier() {
        return this.getFieldAs(SignerInfoField.SIGNATURE_ALGORITHMS, SignatureAlgorithmIdentifier.class);
    }
    
    public void setSignatureAlgorithmIdentifier(final SignatureAlgorithmIdentifier signatureAlgorithmIdentifier) {
        this.setFieldAs(SignerInfoField.SIGNATURE_ALGORITHMS, signatureAlgorithmIdentifier);
    }
    
    public SignatureValue getSignatureValue() {
        return this.getFieldAs(SignerInfoField.SIGNATURE, SignatureValue.class);
    }
    
    public void setSignatureValue(final SignatureValue signatureValue) {
        this.setFieldAs(SignerInfoField.SIGNATURE, signatureValue);
    }
    
    public UnsignedAttributes getUnsignedAttributes() {
        return this.getFieldAs(SignerInfoField.UNSIGNED_ATTRS, UnsignedAttributes.class);
    }
    
    public void setUnsignedAttributes(final UnsignedAttributes unsignedAttributes) {
        this.setFieldAs(SignerInfoField.UNSIGNED_ATTRS, unsignedAttributes);
    }
    
    static {
        SignerInfo.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(SignerInfoField.CMS_VERSION, CmsVersion.class), new Asn1FieldInfo(SignerInfoField.SID, SignerIdentifier.class), new Asn1FieldInfo(SignerInfoField.DIGEST_ALGORITHM, DigestAlgorithmIdentifier.class), new ImplicitField(SignerInfoField.SIGNED_ATTRS, 0, SignedAttributes.class), new Asn1FieldInfo(SignerInfoField.SIGNATURE_ALGORITHMS, SignatureAlgorithmIdentifier.class), new Asn1FieldInfo(SignerInfoField.SIGNATURE, SignatureValue.class), new ImplicitField(SignerInfoField.UNSIGNED_ATTRS, 1, UnsignedAttributes.class) };
    }
    
    protected enum SignerInfoField implements EnumType
    {
        CMS_VERSION, 
        SID, 
        DIGEST_ALGORITHM, 
        SIGNED_ATTRS, 
        SIGNATURE_ALGORITHMS, 
        SIGNATURE, 
        UNSIGNED_ATTRS;
        
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
