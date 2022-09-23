// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class RecipientEncryptedKey extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public RecipientEncryptedKey() {
        super(RecipientEncryptedKey.fieldInfos);
    }
    
    public KeyAgreeRecipientIdentifier getRid() {
        return this.getFieldAs(RecipientEncryptedKeyField.RID, KeyAgreeRecipientIdentifier.class);
    }
    
    public void setRid(final KeyAgreeRecipientIdentifier rid) {
        this.setFieldAs(RecipientEncryptedKeyField.RID, rid);
    }
    
    public EncryptedKey getEncryptedKey() {
        return this.getFieldAs(RecipientEncryptedKeyField.ENCRYPTED_KEY, EncryptedKey.class);
    }
    
    public void setEncryptedKey(final EncryptedKey encryptedKey) {
        this.setFieldAs(RecipientEncryptedKeyField.ENCRYPTED_KEY, encryptedKey);
    }
    
    static {
        RecipientEncryptedKey.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(RecipientEncryptedKeyField.RID, KeyAgreeRecipientIdentifier.class), new Asn1FieldInfo(RecipientEncryptedKeyField.ENCRYPTED_KEY, EncryptedKey.class) };
    }
    
    protected enum RecipientEncryptedKeyField implements EnumType
    {
        RID, 
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
