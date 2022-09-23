// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1BitString;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class IssuerSerial extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public IssuerSerial() {
        super(IssuerSerial.fieldInfos);
    }
    
    public GeneralNames getIssuer() {
        return this.getFieldAs(IssuerSerialField.ISSUER, GeneralNames.class);
    }
    
    public void setIssuer(final GeneralNames issuer) {
        this.setFieldAs(IssuerSerialField.ISSUER, issuer);
    }
    
    public CertificateSerialNumber getSerial() {
        return this.getFieldAs(IssuerSerialField.SERIAL, CertificateSerialNumber.class);
    }
    
    public void setSerial(final CertificateSerialNumber serial) {
        this.setFieldAs(IssuerSerialField.SERIAL, serial);
    }
    
    public Asn1BitString getIssuerUID() {
        return this.getFieldAs(IssuerSerialField.ISSUER_UID, Asn1BitString.class);
    }
    
    public void setIssuerUID(final Asn1BitString issuerUID) {
        this.setFieldAs(IssuerSerialField.ISSUER_UID, issuerUID);
    }
    
    static {
        IssuerSerial.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(IssuerSerialField.ISSUER, GeneralNames.class), new Asn1FieldInfo(IssuerSerialField.SERIAL, CertificateSerialNumber.class), new Asn1FieldInfo(IssuerSerialField.ISSUER_UID, Asn1BitString.class) };
    }
    
    protected enum IssuerSerialField implements EnumType
    {
        ISSUER, 
        SERIAL, 
        ISSUER_UID;
        
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
