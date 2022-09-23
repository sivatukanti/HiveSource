// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.ImplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class AuthorityKeyIdentifier extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public AuthorityKeyIdentifier() {
        super(AuthorityKeyIdentifier.fieldInfos);
    }
    
    public KeyIdentifier getKeyIdentifier() {
        return this.getFieldAs(AKIdentifierField.KEY_IDENTIFIER, KeyIdentifier.class);
    }
    
    public void setKeyIdentifier(final KeyIdentifier keyIdentifier) {
        this.setFieldAs(AKIdentifierField.KEY_IDENTIFIER, keyIdentifier);
    }
    
    public GeneralNames getAuthorityCertIssuer() {
        return this.getFieldAs(AKIdentifierField.AUTHORITY_CERT_ISSUER, GeneralNames.class);
    }
    
    public void setAuthorityCertIssuer(final GeneralNames authorityCertIssuer) {
        this.setFieldAs(AKIdentifierField.AUTHORITY_CERT_ISSUER, authorityCertIssuer);
    }
    
    public CertificateSerialNumber getAuthorityCertSerialNumber() {
        return this.getFieldAs(AKIdentifierField.AUTHORITY_CERT_SERIAL_NUMBER, CertificateSerialNumber.class);
    }
    
    public void setAuthorityCertSerialNumber(final CertificateSerialNumber authorityCertSerialNumber) {
        this.setFieldAs(AKIdentifierField.AUTHORITY_CERT_SERIAL_NUMBER, authorityCertSerialNumber);
    }
    
    static {
        AuthorityKeyIdentifier.fieldInfos = new Asn1FieldInfo[] { new ImplicitField(AKIdentifierField.KEY_IDENTIFIER, KeyIdentifier.class), new ImplicitField(AKIdentifierField.AUTHORITY_CERT_ISSUER, GeneralNames.class), new ImplicitField(AKIdentifierField.AUTHORITY_CERT_SERIAL_NUMBER, CertificateSerialNumber.class) };
    }
    
    protected enum AKIdentifierField implements EnumType
    {
        KEY_IDENTIFIER, 
        AUTHORITY_CERT_ISSUER, 
        AUTHORITY_CERT_SERIAL_NUMBER;
        
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
