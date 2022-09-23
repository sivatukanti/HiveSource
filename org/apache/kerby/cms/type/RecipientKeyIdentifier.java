// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.type.Asn1GeneralizedTime;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.x509.type.SubjectKeyIdentifier;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class RecipientKeyIdentifier extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public RecipientKeyIdentifier() {
        super(RecipientKeyIdentifier.fieldInfos);
    }
    
    public SubjectKeyIdentifier getSubjectKeyIdentifier() {
        return this.getFieldAs(RecipientKeyIdentifierField.SUBJECT_KEY_IDENTIFIER, SubjectKeyIdentifier.class);
    }
    
    public void setKeyIdentifier(final SubjectKeyIdentifier subjectKeyIdentifier) {
        this.setFieldAs(RecipientKeyIdentifierField.SUBJECT_KEY_IDENTIFIER, subjectKeyIdentifier);
    }
    
    public Asn1GeneralizedTime getDate() {
        return this.getFieldAs(RecipientKeyIdentifierField.DATE, Asn1GeneralizedTime.class);
    }
    
    public void setDate(final Asn1GeneralizedTime date) {
        this.setFieldAs(RecipientKeyIdentifierField.DATE, date);
    }
    
    public OtherKeyAttribute getOther() {
        return this.getFieldAs(RecipientKeyIdentifierField.OTHER, OtherKeyAttribute.class);
    }
    
    public void setOther(final OtherKeyAttribute other) {
        this.setFieldAs(RecipientKeyIdentifierField.OTHER, other);
    }
    
    static {
        RecipientKeyIdentifier.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(RecipientKeyIdentifierField.SUBJECT_KEY_IDENTIFIER, SubjectKeyIdentifier.class), new Asn1FieldInfo(RecipientKeyIdentifierField.DATE, Asn1GeneralizedTime.class), new Asn1FieldInfo(RecipientKeyIdentifierField.OTHER, OtherKeyAttribute.class) };
    }
    
    protected enum RecipientKeyIdentifierField implements EnumType
    {
        SUBJECT_KEY_IDENTIFIER, 
        DATE, 
        OTHER;
        
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
