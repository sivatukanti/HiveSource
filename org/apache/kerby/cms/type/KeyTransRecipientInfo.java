// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class KeyTransRecipientInfo extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public KeyTransRecipientInfo() {
        super(KeyTransRecipientInfo.fieldInfos);
    }
    
    public CmsVersion getVersion() {
        return this.getFieldAs(KTRInfoField.VERSION, CmsVersion.class);
    }
    
    public void setVersion(final CmsVersion version) {
        this.setFieldAs(KTRInfoField.VERSION, version);
    }
    
    public RecipientIdentifier getRid() {
        return this.getFieldAs(KTRInfoField.RID, RecipientIdentifier.class);
    }
    
    public void setRid(final RecipientIdentifier rid) {
        this.setFieldAs(KTRInfoField.RID, rid);
    }
    
    public KeyEncryptionAlgorithmIdentifier getKeyEncryptionAlgorithmIdentifier() {
        return this.getFieldAs(KTRInfoField.KEY_ENCRYPTION_ALGORITHM, KeyEncryptionAlgorithmIdentifier.class);
    }
    
    public void setKeyEncryptionAlgorithmIdentifier(final KeyEncryptionAlgorithmIdentifier keyEncryptionAlgorithmIdentifier) {
        this.setFieldAs(KTRInfoField.KEY_ENCRYPTION_ALGORITHM, keyEncryptionAlgorithmIdentifier);
    }
    
    public EncryptedKey getEncryptedKey() {
        return this.getFieldAs(KTRInfoField.ENCRYPTED_KEY, EncryptedKey.class);
    }
    
    public void setEncryptedKey(final EncryptedKey encryptedKey) {
        this.setFieldAs(KTRInfoField.ENCRYPTED_KEY, encryptedKey);
    }
    
    static {
        KeyTransRecipientInfo.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(KTRInfoField.VERSION, CmsVersion.class), new Asn1FieldInfo(KTRInfoField.RID, RecipientIdentifier.class), new Asn1FieldInfo(KTRInfoField.KEY_ENCRYPTION_ALGORITHM, KeyEncryptionAlgorithmIdentifier.class), new Asn1FieldInfo(KTRInfoField.ENCRYPTED_KEY, EncryptedKey.class) };
    }
    
    protected enum KTRInfoField implements EnumType
    {
        VERSION, 
        RID, 
        KEY_ENCRYPTION_ALGORITHM, 
        ENCRYPTED_KEY;
        
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
