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

public class SignerIdentifier extends Asn1Choice
{
    static Asn1FieldInfo[] fieldInfos;
    
    public SignerIdentifier() {
        super(SignerIdentifier.fieldInfos);
    }
    
    public IssuerAndSerialNumber getIssuerAndSerialNumber() {
        return this.getChoiceValueAs(SignerIdentifierField.ISSUER_AND_SERIAL_NUMBER, IssuerAndSerialNumber.class);
    }
    
    public void setIssuerAndSerialNumber(final IssuerAndSerialNumber issuerAndSerialNumber) {
        this.setChoiceValue(SignerIdentifierField.ISSUER_AND_SERIAL_NUMBER, issuerAndSerialNumber);
    }
    
    public SubjectKeyIdentifier getSubjectKeyIdentifier() {
        return this.getChoiceValueAs(SignerIdentifierField.SUBJECT_KEY_IDENTIFIER, SubjectKeyIdentifier.class);
    }
    
    public void setSubjectKeyIdentifier(final SubjectKeyIdentifier subjectKeyIdentifier) {
        this.setChoiceValue(SignerIdentifierField.SUBJECT_KEY_IDENTIFIER, subjectKeyIdentifier);
    }
    
    static {
        SignerIdentifier.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(SignerIdentifierField.ISSUER_AND_SERIAL_NUMBER, IssuerAndSerialNumber.class), new ImplicitField(SignerIdentifierField.SUBJECT_KEY_IDENTIFIER, 0, SubjectKeyIdentifier.class) };
    }
    
    protected enum SignerIdentifierField implements EnumType
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
