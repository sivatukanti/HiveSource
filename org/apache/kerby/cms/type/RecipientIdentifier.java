// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.ImplicitField;
import org.apache.kerby.x509.type.SubjectKeyIdentifier;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1Choice;

public class RecipientIdentifier extends Asn1Choice
{
    static Asn1FieldInfo[] fieldInfos;
    
    public RecipientIdentifier() {
        super(RecipientIdentifier.fieldInfos);
    }
    
    public IssuerAndSerialNumber getIssuerAndSerialNumber() {
        return this.getChoiceValueAs(RecipientIdentifierField.ISSUER_AND_SERIAL_NUMBER, IssuerAndSerialNumber.class);
    }
    
    public void setIssuerAndSerialNumber(final IssuerAndSerialNumber issuerAndSerialNumber) {
        this.setChoiceValue(RecipientIdentifierField.ISSUER_AND_SERIAL_NUMBER, issuerAndSerialNumber);
    }
    
    public SubjectKeyIdentifier getSubjectKeyIdentifier() {
        return this.getChoiceValueAs(RecipientIdentifierField.SUBJECT_KEY_IDENTIFIER, SubjectKeyIdentifier.class);
    }
    
    public void setSubjectKeyIdentifier(final SubjectKeyIdentifier subjectKeyIdentifier) {
        this.setChoiceValue(RecipientIdentifierField.SUBJECT_KEY_IDENTIFIER, subjectKeyIdentifier);
    }
    
    static {
        RecipientIdentifier.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(RecipientIdentifierField.ISSUER_AND_SERIAL_NUMBER, IssuerAndSerialNumber.class), new ImplicitField(RecipientIdentifierField.SUBJECT_KEY_IDENTIFIER, 0, SubjectKeyIdentifier.class) };
    }
    
    protected enum RecipientIdentifierField implements EnumType
    {
        ISSUER_AND_SERIAL_NUMBER, 
        SUBJECT_KEY_IDENTIFIER;
        
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
