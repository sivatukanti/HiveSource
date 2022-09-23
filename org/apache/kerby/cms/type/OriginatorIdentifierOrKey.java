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

public class OriginatorIdentifierOrKey extends Asn1Choice
{
    static Asn1FieldInfo[] fieldInfos;
    
    public OriginatorIdentifierOrKey() {
        super(OriginatorIdentifierOrKey.fieldInfos);
    }
    
    public IssuerAndSerialNumber getIssuerAndSerialNumber() {
        return this.getChoiceValueAs(OriginatorIdentifierOrKeyField.ISSUER_AND_SERIAL_NUMBER, IssuerAndSerialNumber.class);
    }
    
    public void setIssuerAndSerialNumber(final IssuerAndSerialNumber issuerAndSerialNumber) {
        this.setChoiceValue(OriginatorIdentifierOrKeyField.ISSUER_AND_SERIAL_NUMBER, issuerAndSerialNumber);
    }
    
    public SubjectKeyIdentifier getSubjectKeyIdentifier() {
        return this.getChoiceValueAs(OriginatorIdentifierOrKeyField.SUBJECT_KEY_IDENTIFIER, SubjectKeyIdentifier.class);
    }
    
    public void setSubjectKeyIdentifier(final SubjectKeyIdentifier subjectKeyIdentifier) {
        this.setChoiceValue(OriginatorIdentifierOrKeyField.SUBJECT_KEY_IDENTIFIER, subjectKeyIdentifier);
    }
    
    public OriginatorPublicKey getOriginatorPublicKey() {
        return this.getChoiceValueAs(OriginatorIdentifierOrKeyField.ORIGINATOR_KEY, OriginatorPublicKey.class);
    }
    
    public void setOriginatorPublicKey(final OriginatorPublicKey originatorPublicKey) {
        this.setChoiceValue(OriginatorIdentifierOrKeyField.ORIGINATOR_KEY, originatorPublicKey);
    }
    
    static {
        OriginatorIdentifierOrKey.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(OriginatorIdentifierOrKeyField.ISSUER_AND_SERIAL_NUMBER, IssuerAndSerialNumber.class), new ImplicitField(OriginatorIdentifierOrKeyField.SUBJECT_KEY_IDENTIFIER, 0, SubjectKeyIdentifier.class), new ImplicitField(OriginatorIdentifierOrKeyField.ORIGINATOR_KEY, 1, OriginatorPublicKey.class) };
    }
    
    protected enum OriginatorIdentifierOrKeyField implements EnumType
    {
        ISSUER_AND_SERIAL_NUMBER, 
        SUBJECT_KEY_IDENTIFIER, 
        ORIGINATOR_KEY;
        
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
