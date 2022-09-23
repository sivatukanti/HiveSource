// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class KEKRecipientInfo extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public KEKRecipientInfo() {
        super(KEKRecipientInfo.fieldInfos);
    }
    
    public CmsVersion getVersion() {
        return this.getFieldAs(KEKRecipientInfoField.VERSION, CmsVersion.class);
    }
    
    public void setVersion(final CmsVersion version) {
        this.setFieldAs(KEKRecipientInfoField.VERSION, version);
    }
    
    public KEKIdentifier getKEKIdentifier() {
        return this.getFieldAs(KEKRecipientInfoField.KE_KID, KEKIdentifier.class);
    }
    
    public void setKEKIdentifier(final KEKIdentifier kekIdentifier) {
        this.setFieldAs(KEKRecipientInfoField.KE_KID, kekIdentifier);
    }
    
    public KeyEncryptionAlgorithmIdentifier getKeyEncryptionAlgorithmIdentifier() {
        return this.getFieldAs(KEKRecipientInfoField.KEY_ENCRYPTION_ALGORITHM, KeyEncryptionAlgorithmIdentifier.class);
    }
    
    public void setKeyEncryptionAlgorithmIdentifier(final KeyEncryptionAlgorithmIdentifier keyEncryptionAlgorithmIdentifier) {
        this.setFieldAs(KEKRecipientInfoField.KEY_ENCRYPTION_ALGORITHM, keyEncryptionAlgorithmIdentifier);
    }
    
    public EncryptedKey getEncryptedKey() {
        return this.getFieldAs(KEKRecipientInfoField.ENCRYPTED_KEY, EncryptedKey.class);
    }
    
    public void setEncryptedKey(final EncryptedKey encryptedKey) {
        this.setFieldAs(KEKRecipientInfoField.ENCRYPTED_KEY, encryptedKey);
    }
    
    static {
        KEKRecipientInfo.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(KEKRecipientInfoField.VERSION, CmsVersion.class), new Asn1FieldInfo(KEKRecipientInfoField.KE_KID, KEKIdentifier.class), new Asn1FieldInfo(KEKRecipientInfoField.KEY_ENCRYPTION_ALGORITHM, KeyEncryptionAlgorithmIdentifier.class), new Asn1FieldInfo(KEKRecipientInfoField.ENCRYPTED_KEY, EncryptedKey.class) };
    }
    
    protected enum KEKRecipientInfoField implements EnumType
    {
        VERSION, 
        KE_KID, 
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
