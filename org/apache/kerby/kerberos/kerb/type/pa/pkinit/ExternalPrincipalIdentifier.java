// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.pa.pkinit;

import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.ImplicitField;
import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class ExternalPrincipalIdentifier extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public ExternalPrincipalIdentifier() {
        super(ExternalPrincipalIdentifier.fieldInfos);
    }
    
    public byte[] getSubjectName() {
        return this.getFieldAsOctets(ExternalPrincipalIdentifierField.SUBJECT_NAME);
    }
    
    public void setSubjectName(final byte[] subjectName) {
        this.setFieldAsOctets(ExternalPrincipalIdentifierField.SUBJECT_NAME, subjectName);
    }
    
    public byte[] getIssuerSerialNumber() {
        return this.getFieldAsOctets(ExternalPrincipalIdentifierField.ISSUER_AND_SERIAL_NUMBER);
    }
    
    public void setIssuerSerialNumber(final byte[] issuerSerialNumber) {
        this.setFieldAsOctets(ExternalPrincipalIdentifierField.ISSUER_AND_SERIAL_NUMBER, issuerSerialNumber);
    }
    
    public byte[] getSubjectKeyIdentifier() {
        return this.getFieldAsOctets(ExternalPrincipalIdentifierField.SUBJECT_KEY_IDENTIFIER);
    }
    
    public void setSubjectKeyIdentifier(final byte[] subjectKeyIdentifier) {
        this.setFieldAsOctets(ExternalPrincipalIdentifierField.SUBJECT_KEY_IDENTIFIER, subjectKeyIdentifier);
    }
    
    static {
        ExternalPrincipalIdentifier.fieldInfos = new Asn1FieldInfo[] { new ImplicitField(ExternalPrincipalIdentifierField.SUBJECT_NAME, Asn1OctetString.class), new ImplicitField(ExternalPrincipalIdentifierField.ISSUER_AND_SERIAL_NUMBER, Asn1OctetString.class), new ImplicitField(ExternalPrincipalIdentifierField.SUBJECT_KEY_IDENTIFIER, Asn1OctetString.class) };
    }
    
    protected enum ExternalPrincipalIdentifierField implements EnumType
    {
        SUBJECT_NAME, 
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
