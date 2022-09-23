// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class RevokedCertificate extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public RevokedCertificate() {
        super(RevokedCertificate.fieldInfos);
    }
    
    public CertificateSerialNumber getUserCertificate() {
        return this.getFieldAs(RevokedCertificateField.USER_CERTIFICATE, CertificateSerialNumber.class);
    }
    
    public void setUserCertificate(final CertificateSerialNumber userCertificate) {
        this.setFieldAs(RevokedCertificateField.USER_CERTIFICATE, userCertificate);
    }
    
    public Time getRevocationDate() {
        return this.getFieldAs(RevokedCertificateField.REVOCATION_DATA, Time.class);
    }
    
    public void setRevocationData(final Time revocationData) {
        this.setFieldAs(RevokedCertificateField.REVOCATION_DATA, revocationData);
    }
    
    public Extensions getCrlEntryExtensions() {
        return this.getFieldAs(RevokedCertificateField.CRL_ENTRY_EXTENSIONS, Extensions.class);
    }
    
    public void setCrlEntryExtensions(final Extensions crlEntryExtensions) {
        this.setFieldAs(RevokedCertificateField.CRL_ENTRY_EXTENSIONS, crlEntryExtensions);
    }
    
    static {
        RevokedCertificate.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(RevokedCertificateField.USER_CERTIFICATE, CertificateSerialNumber.class), new Asn1FieldInfo(RevokedCertificateField.REVOCATION_DATA, Time.class), new Asn1FieldInfo(RevokedCertificateField.CRL_ENTRY_EXTENSIONS, Extensions.class) };
    }
    
    protected enum RevokedCertificateField implements EnumType
    {
        USER_CERTIFICATE, 
        REVOCATION_DATA, 
        CRL_ENTRY_EXTENSIONS;
        
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
