// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class PasswordRecipientInfo extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public PasswordRecipientInfo() {
        super(PasswordRecipientInfo.fieldInfos);
    }
    
    public CmsVersion getVersion() {
        return this.getFieldAs(PwdRInfoField.VERSION, CmsVersion.class);
    }
    
    public void setVersion(final CmsVersion version) {
        this.setFieldAs(PwdRInfoField.VERSION, version);
    }
    
    public KeyDerivationAlgorithmIdentifier getKeyDerivationAlgorithmIdentifier() {
        return this.getFieldAs(PwdRInfoField.KEY_DERIVATION_ALGORIGHM, KeyDerivationAlgorithmIdentifier.class);
    }
    
    public void setKeyDerivationAlgorithmIdentifier(final KeyDerivationAlgorithmIdentifier keyDerivationAlgorithmIdentifier) {
        this.setFieldAs(PwdRInfoField.KEY_DERIVATION_ALGORIGHM, keyDerivationAlgorithmIdentifier);
    }
    
    public KeyEncryptionAlgorithmIdentifier getKeyEncryptionAlgorithmIdentifier() {
        return this.getFieldAs(PwdRInfoField.KEY_ENCRYPTION_ALGORITHMS, KeyEncryptionAlgorithmIdentifier.class);
    }
    
    public void setKeyEncryptionAlgorithmIdentifier(final KeyEncryptionAlgorithmIdentifier keyEncryptionAlgorithmIdentifier) {
        this.setFieldAs(PwdRInfoField.KEY_ENCRYPTION_ALGORITHMS, keyEncryptionAlgorithmIdentifier);
    }
    
    public EncryptedKey getEncryptedKey() {
        return this.getFieldAs(PwdRInfoField.ENCRYPTED_KEY, EncryptedKey.class);
    }
    
    public void setEncryptedKey(final EncryptedKey encryptedKey) {
        this.setFieldAs(PwdRInfoField.ENCRYPTED_KEY, encryptedKey);
    }
    
    static {
        PasswordRecipientInfo.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(PwdRInfoField.VERSION, CmsVersion.class), new ExplicitField(PwdRInfoField.KEY_DERIVATION_ALGORIGHM, 0, KeyDerivationAlgorithmIdentifier.class), new Asn1FieldInfo(PwdRInfoField.KEY_ENCRYPTION_ALGORITHMS, KeyEncryptionAlgorithmIdentifier.class), new Asn1FieldInfo(PwdRInfoField.ENCRYPTED_KEY, EncryptedKey.class) };
    }
    
    protected enum PwdRInfoField implements EnumType
    {
        VERSION, 
        KEY_DERIVATION_ALGORIGHM, 
        KEY_ENCRYPTION_ALGORITHMS, 
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
