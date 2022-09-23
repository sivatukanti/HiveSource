// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class KeyAgreeRecipientInfo extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public KeyAgreeRecipientInfo() {
        super(KeyAgreeRecipientInfo.fieldInfos);
    }
    
    public CmsVersion getVersion() {
        return this.getFieldAs(KARInfoField.VERSION, CmsVersion.class);
    }
    
    public void setVersion(final CmsVersion version) {
        this.setFieldAs(KARInfoField.VERSION, version);
    }
    
    public OriginatorIdentifierOrKey getOriginator() {
        return this.getFieldAs(KARInfoField.ORIGINATOR, OriginatorIdentifierOrKey.class);
    }
    
    public void setOriginator(final OriginatorIdentifierOrKey originator) {
        this.setFieldAs(KARInfoField.ORIGINATOR, originator);
    }
    
    public Asn1OctetString getUkm() {
        return this.getFieldAs(KARInfoField.UKM, Asn1OctetString.class);
    }
    
    public void setUkm(final Asn1OctetString ukm) {
        this.setFieldAs(KARInfoField.UKM, ukm);
    }
    
    public KeyEncryptionAlgorithmIdentifier getKeyEncryptionAlgorithmIdentifier() {
        return this.getFieldAs(KARInfoField.KEY_ENCRYPTION_ALGORITHM, KeyEncryptionAlgorithmIdentifier.class);
    }
    
    public void setkeyEncryptionAlgorithmIdentifier(final KeyEncryptionAlgorithmIdentifier keyEncryptionAlgorithmIdentifier) {
        this.setFieldAs(KARInfoField.KEY_ENCRYPTION_ALGORITHM, keyEncryptionAlgorithmIdentifier);
    }
    
    public RecipientEncryptedKeys getRecipientEncryptedKeys() {
        return this.getFieldAs(KARInfoField.RECIPIENT_ENCRYPTED_KEY, RecipientEncryptedKeys.class);
    }
    
    public void setRecipientEncryptedKeys(final RecipientEncryptedKeys recipientEncryptedKeys) {
        this.setFieldAs(KARInfoField.RECIPIENT_ENCRYPTED_KEY, recipientEncryptedKeys);
    }
    
    static {
        KeyAgreeRecipientInfo.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(KARInfoField.VERSION, CmsVersion.class), new ExplicitField(KARInfoField.ORIGINATOR, 0, OriginatorIdentifierOrKey.class), new ExplicitField(KARInfoField.UKM, 1, Asn1OctetString.class), new Asn1FieldInfo(KARInfoField.KEY_ENCRYPTION_ALGORITHM, KeyEncryptionAlgorithmIdentifier.class), new Asn1FieldInfo(KARInfoField.RECIPIENT_ENCRYPTED_KEY, RecipientEncryptedKeys.class) };
    }
    
    protected enum KARInfoField implements EnumType
    {
        VERSION, 
        ORIGINATOR, 
        UKM, 
        KEY_ENCRYPTION_ALGORITHM, 
        RECIPIENT_ENCRYPTED_KEY;
        
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
