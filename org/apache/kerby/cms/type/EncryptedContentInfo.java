// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.ImplicitField;
import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.type.Asn1ObjectIdentifier;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class EncryptedContentInfo extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public EncryptedContentInfo() {
        super(EncryptedContentInfo.fieldInfos);
    }
    
    public Asn1ObjectIdentifier getContentType() {
        return this.getFieldAs(ECInfoField.CONTENT_TYPE, Asn1ObjectIdentifier.class);
    }
    
    public void setContentType(final Asn1ObjectIdentifier contentType) {
        this.setFieldAs(ECInfoField.CONTENT_TYPE, contentType);
    }
    
    public ContentEncryptionAlgorithmIdentifier getContentEncryptionAlgorithmIdentifier() {
        return this.getFieldAs(ECInfoField.CONTENT_ENCRYPTION_ALGORITHM, ContentEncryptionAlgorithmIdentifier.class);
    }
    
    public void setContentEncryptionAlgorithmIdentifier(final ContentEncryptionAlgorithmIdentifier contentEncryptionAlgorithmIdentifier) {
        this.setFieldAs(ECInfoField.CONTENT_ENCRYPTION_ALGORITHM, contentEncryptionAlgorithmIdentifier);
    }
    
    public Asn1OctetString getEncryptedContent() {
        return this.getFieldAs(ECInfoField.ENCRYPTED_CONTENT, Asn1OctetString.class);
    }
    
    public void setEncryptedContent(final Asn1OctetString encryptedContent) {
        this.setFieldAs(ECInfoField.ENCRYPTED_CONTENT, encryptedContent);
    }
    
    static {
        EncryptedContentInfo.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(ECInfoField.CONTENT_TYPE, Asn1ObjectIdentifier.class), new Asn1FieldInfo(ECInfoField.CONTENT_ENCRYPTION_ALGORITHM, ContentEncryptionAlgorithmIdentifier.class), new ImplicitField(ECInfoField.ENCRYPTED_CONTENT, 0, Asn1OctetString.class) };
    }
    
    protected enum ECInfoField implements EnumType
    {
        CONTENT_TYPE, 
        CONTENT_ENCRYPTION_ALGORITHM, 
        ENCRYPTED_CONTENT;
        
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
