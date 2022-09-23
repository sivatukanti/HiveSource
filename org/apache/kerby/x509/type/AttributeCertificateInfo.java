// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.asn1.type.Asn1BitString;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class AttributeCertificateInfo extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public AttributeCertificateInfo() {
        super(AttributeCertificateInfo.fieldInfos);
    }
    
    public int getVersion() {
        return this.getFieldAsInteger(ACInfoField.VERSION);
    }
    
    public void setVersion(final int version) {
        this.setFieldAsInt(ACInfoField.VERSION, version);
    }
    
    public Holder getHolder() {
        return this.getFieldAs(ACInfoField.HOLDER, Holder.class);
    }
    
    public void setHolder(final Holder holder) {
        this.setFieldAs(ACInfoField.HOLDER, holder);
    }
    
    public AttCertIssuer getIssuer() {
        return this.getFieldAs(ACInfoField.ISSUER, AttCertIssuer.class);
    }
    
    public void setIssuer(final AttCertIssuer attCertIssuer) {
        this.setFieldAs(ACInfoField.ISSUER, attCertIssuer);
    }
    
    public AlgorithmIdentifier getSignature() {
        return this.getFieldAs(ACInfoField.SIGNATURE, AlgorithmIdentifier.class);
    }
    
    public void setSignature(final AlgorithmIdentifier signature) {
        this.setFieldAs(ACInfoField.SIGNATURE, signature);
    }
    
    public CertificateSerialNumber getSerialNumber() {
        return this.getFieldAs(ACInfoField.SERIAL_NUMBER, CertificateSerialNumber.class);
    }
    
    public void setSerialNumber(final CertificateSerialNumber certificateSerialNumber) {
        this.setFieldAs(ACInfoField.SERIAL_NUMBER, certificateSerialNumber);
    }
    
    public AttCertValidityPeriod getAttrCertValidityPeriod() {
        return this.getFieldAs(ACInfoField.ATTR_CERT_VALIDITY_PERIOD, AttCertValidityPeriod.class);
    }
    
    public void setAttrCertValidityPeriod(final AttCertValidityPeriod attrCertValidityPeriod) {
        this.setFieldAs(ACInfoField.ATTR_CERT_VALIDITY_PERIOD, attrCertValidityPeriod);
    }
    
    public Attributes getAttributes() {
        return this.getFieldAs(ACInfoField.ATTRIBUTES, Attributes.class);
    }
    
    public void setAttributes(final Attributes attributes) {
        this.setFieldAs(ACInfoField.ATTRIBUTES, attributes);
    }
    
    public byte[] getIssuerUniqueID() {
        return this.getFieldAs(ACInfoField.ISSUER_UNIQUE_ID, Asn1BitString.class).getValue();
    }
    
    public void setIssuerUniqueId(final byte[] issuerUniqueId) {
        this.setFieldAs(ACInfoField.ISSUER_UNIQUE_ID, new Asn1BitString(issuerUniqueId));
    }
    
    public Extensions getExtensions() {
        return this.getFieldAs(ACInfoField.EXTENSIONS, Extensions.class);
    }
    
    public void setExtensions(final Extensions extensions) {
        this.setFieldAs(ACInfoField.EXTENSIONS, extensions);
    }
    
    static {
        AttributeCertificateInfo.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(ACInfoField.VERSION, Asn1Integer.class), new Asn1FieldInfo(ACInfoField.HOLDER, Holder.class), new Asn1FieldInfo(ACInfoField.ISSUER, AttCertIssuer.class), new Asn1FieldInfo(ACInfoField.SIGNATURE, AlgorithmIdentifier.class), new Asn1FieldInfo(ACInfoField.SERIAL_NUMBER, CertificateSerialNumber.class), new Asn1FieldInfo(ACInfoField.ATTR_CERT_VALIDITY_PERIOD, AttCertValidityPeriod.class), new Asn1FieldInfo(ACInfoField.ATTRIBUTES, Attributes.class), new Asn1FieldInfo(ACInfoField.ISSUER_UNIQUE_ID, Asn1BitString.class), new Asn1FieldInfo(ACInfoField.EXTENSIONS, Extensions.class) };
    }
    
    protected enum ACInfoField implements EnumType
    {
        VERSION, 
        HOLDER, 
        ISSUER, 
        SIGNATURE, 
        SERIAL_NUMBER, 
        ATTR_CERT_VALIDITY_PERIOD, 
        ATTRIBUTES, 
        ISSUER_UNIQUE_ID, 
        EXTENSIONS;
        
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
