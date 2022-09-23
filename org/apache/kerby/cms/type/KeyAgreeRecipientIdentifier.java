// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.ImplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1Choice;

public class KeyAgreeRecipientIdentifier extends Asn1Choice
{
    static Asn1FieldInfo[] fieldInfos;
    
    public KeyAgreeRecipientIdentifier() {
        super(KeyAgreeRecipientIdentifier.fieldInfos);
    }
    
    public IssuerAndSerialNumber getIssuerAndSerialNumber() {
        return this.getChoiceValueAs(KeyAgreeRecipientIdentifierField.ISSUER_AND_SERIAL_NUMBER, IssuerAndSerialNumber.class);
    }
    
    public void setIssuerAndSerialNumber(final IssuerAndSerialNumber issuerAndSerialNumber) {
        this.setChoiceValue(KeyAgreeRecipientIdentifierField.ISSUER_AND_SERIAL_NUMBER, issuerAndSerialNumber);
    }
    
    public RecipientKeyIdentifier getRecipientKeyIdentifier() {
        return this.getChoiceValueAs(KeyAgreeRecipientIdentifierField.R_KEY_ID, RecipientKeyIdentifier.class);
    }
    
    public void setRecipientKeyIdentifier(final RecipientKeyIdentifier recipientKeyIdentifier) {
        this.setChoiceValue(KeyAgreeRecipientIdentifierField.R_KEY_ID, recipientKeyIdentifier);
    }
    
    static {
        KeyAgreeRecipientIdentifier.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(KeyAgreeRecipientIdentifierField.ISSUER_AND_SERIAL_NUMBER, IssuerAndSerialNumber.class), new ImplicitField(KeyAgreeRecipientIdentifierField.R_KEY_ID, 0, RecipientKeyIdentifier.class) };
    }
    
    protected enum KeyAgreeRecipientIdentifierField implements EnumType
    {
        ISSUER_AND_SERIAL_NUMBER, 
        R_KEY_ID;
        
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
