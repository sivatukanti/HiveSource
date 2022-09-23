// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.ImplicitField;
import org.apache.kerby.x509.type.GeneralNames;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.x509.type.IssuerSerial;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1Choice;

public class Subject extends Asn1Choice
{
    static Asn1FieldInfo[] fieldInfos;
    
    public Subject() {
        super(Subject.fieldInfos);
    }
    
    public IssuerSerial getBaseCertificateID() {
        return this.getChoiceValueAs(SubjectField.BASE_CERTIFICATE_ID, IssuerSerial.class);
    }
    
    public void setBaseCertificateID(final IssuerSerial baseCertificateID) {
        this.setChoiceValue(SubjectField.BASE_CERTIFICATE_ID, baseCertificateID);
    }
    
    public GeneralNames getSubjectName() {
        return this.getChoiceValueAs(SubjectField.SUBJECT_NAME, GeneralNames.class);
    }
    
    public void setSubjectName(final GeneralNames subjectName) {
        this.setChoiceValue(SubjectField.SUBJECT_NAME, subjectName);
    }
    
    static {
        Subject.fieldInfos = new Asn1FieldInfo[] { new ImplicitField(SubjectField.BASE_CERTIFICATE_ID, IssuerSerial.class), new ImplicitField(SubjectField.SUBJECT_NAME, GeneralNames.class) };
    }
    
    protected enum SubjectField implements EnumType
    {
        BASE_CERTIFICATE_ID, 
        SUBJECT_NAME;
        
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
