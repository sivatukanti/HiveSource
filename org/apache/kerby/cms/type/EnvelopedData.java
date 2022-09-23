// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.ImplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class EnvelopedData extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public EnvelopedData() {
        super(EnvelopedData.fieldInfos);
    }
    
    public CmsVersion getCmsVersion() {
        return this.getFieldAs(EnvelopedDataField.CMS_VERSION, CmsVersion.class);
    }
    
    public void setCmsVersion(final CmsVersion cmsVersion) {
        this.setFieldAs(EnvelopedDataField.CMS_VERSION, cmsVersion);
    }
    
    public OriginatorInfo getOriginatorInfo() {
        return this.getFieldAs(EnvelopedDataField.ORIGINATOR_INFO, OriginatorInfo.class);
    }
    
    public void setOriginatorInfo(final OriginatorInfo originatorInfo) {
        this.setFieldAs(EnvelopedDataField.ORIGINATOR_INFO, originatorInfo);
    }
    
    public RecipientInfos getRecipientInfos() {
        return this.getFieldAs(EnvelopedDataField.RECIPIENT_INFOS, RecipientInfos.class);
    }
    
    public void setRecipientInfos(final RecipientInfos recipientInfos) {
        this.setFieldAs(EnvelopedDataField.RECIPIENT_INFOS, recipientInfos);
    }
    
    public EncryptedContentInfo getEncryptedContentInfo() {
        return this.getFieldAs(EnvelopedDataField.ENCRYPTED_CONTENT_INFO, EncryptedContentInfo.class);
    }
    
    public void setEncryptedContentInfo(final EncryptedContentInfo encryptedContentInfo) {
        this.setFieldAs(EnvelopedDataField.ENCRYPTED_CONTENT_INFO, encryptedContentInfo);
    }
    
    public UnprotectedAttributes getUnprotectedAttributes() {
        return this.getFieldAs(EnvelopedDataField.UNPROTECTED_ATTRS, UnprotectedAttributes.class);
    }
    
    public void setUnprotectedAttributes(final UnprotectedAttributes unprotectedAttributes) {
        this.setFieldAs(EnvelopedDataField.UNPROTECTED_ATTRS, unprotectedAttributes);
    }
    
    static {
        EnvelopedData.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(EnvelopedDataField.CMS_VERSION, CmsVersion.class), new ImplicitField(EnvelopedDataField.ORIGINATOR_INFO, 0, OriginatorInfo.class), new Asn1FieldInfo(EnvelopedDataField.RECIPIENT_INFOS, RecipientInfos.class), new Asn1FieldInfo(EnvelopedDataField.ENCRYPTED_CONTENT_INFO, EncryptedContentInfo.class), new ImplicitField(EnvelopedDataField.UNPROTECTED_ATTRS, 1, UnprotectedAttributes.class) };
    }
    
    protected enum EnvelopedDataField implements EnumType
    {
        CMS_VERSION, 
        ORIGINATOR_INFO, 
        RECIPIENT_INFOS, 
        ENCRYPTED_CONTENT_INFO, 
        UNPROTECTED_ATTRS;
        
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
