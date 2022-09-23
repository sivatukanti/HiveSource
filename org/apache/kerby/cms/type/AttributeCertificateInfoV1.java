// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.x509.type.Extensions;
import org.apache.kerby.asn1.type.Asn1BitString;
import org.apache.kerby.x509.type.Attributes;
import org.apache.kerby.x509.type.AttCertValidityPeriod;
import org.apache.kerby.x509.type.CertificateSerialNumber;
import org.apache.kerby.x509.type.AlgorithmIdentifier;
import org.apache.kerby.x509.type.AttCertIssuer;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class AttributeCertificateInfoV1 extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public AttributeCertificateInfoV1() {
        super(AttributeCertificateInfoV1.fieldInfos);
    }
    
    public int getVersion() {
        return this.getFieldAsInteger(AttributeCertificateInfoV1Field.VERSION);
    }
    
    public void setVersion(final int version) {
        this.setFieldAsInt(AttributeCertificateInfoV1Field.VERSION, version);
    }
    
    public Subject getSubject() {
        return this.getFieldAs(AttributeCertificateInfoV1Field.SUBJECT, Subject.class);
    }
    
    public void setSubject(final Subject subject) {
        this.setFieldAs(AttributeCertificateInfoV1Field.SUBJECT, subject);
    }
    
    public AttCertIssuer getIssuer() {
        return this.getFieldAs(AttributeCertificateInfoV1Field.ISSUER, AttCertIssuer.class);
    }
    
    public void setIssuer(final AttCertIssuer attCertIssuer) {
        this.setFieldAs(AttributeCertificateInfoV1Field.ISSUER, attCertIssuer);
    }
    
    public AlgorithmIdentifier getSignature() {
        return this.getFieldAs(AttributeCertificateInfoV1Field.SIGNATURE, AlgorithmIdentifier.class);
    }
    
    public void setSignature(final AlgorithmIdentifier signature) {
        this.setFieldAs(AttributeCertificateInfoV1Field.SIGNATURE, signature);
    }
    
    public CertificateSerialNumber getSerialNumber() {
        return this.getFieldAs(AttributeCertificateInfoV1Field.SERIAL_NUMBER, CertificateSerialNumber.class);
    }
    
    public void setSerialNumber(final CertificateSerialNumber certificateSerialNumber) {
        this.setFieldAs(AttributeCertificateInfoV1Field.SERIAL_NUMBER, certificateSerialNumber);
    }
    
    public AttCertValidityPeriod getAttrCertValidityPeriod() {
        return this.getFieldAs(AttributeCertificateInfoV1Field.ATTR_CERT_VALIDITY_PERIOD, AttCertValidityPeriod.class);
    }
    
    public void setAttrCertValidityPeriod(final AttCertValidityPeriod attrCertValidityPeriod) {
        this.setFieldAs(AttributeCertificateInfoV1Field.ATTR_CERT_VALIDITY_PERIOD, attrCertValidityPeriod);
    }
    
    public Attributes getAttributes() {
        return this.getFieldAs(AttributeCertificateInfoV1Field.ATTRIBUTES, Attributes.class);
    }
    
    public void setAttributes(final Attributes attributes) {
        this.setFieldAs(AttributeCertificateInfoV1Field.ATTRIBUTES, attributes);
    }
    
    public byte[] getIssuerUniqueID() {
        return this.getFieldAs(AttributeCertificateInfoV1Field.ISSUER_UNIQUE_ID, Asn1BitString.class).getValue();
    }
    
    public void setIssuerUniqueId(final byte[] issuerUniqueId) {
        this.setFieldAs(AttributeCertificateInfoV1Field.ISSUER_UNIQUE_ID, new Asn1BitString(issuerUniqueId));
    }
    
    public Extensions getExtensions() {
        return this.getFieldAs(AttributeCertificateInfoV1Field.EXTENSIONS, Extensions.class);
    }
    
    public void setExtensions(final Extensions extensions) {
        this.setFieldAs(AttributeCertificateInfoV1Field.EXTENSIONS, extensions);
    }
    
    static {
        AttributeCertificateInfoV1.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(AttributeCertificateInfoV1Field.VERSION, Asn1Integer.class), new Asn1FieldInfo(AttributeCertificateInfoV1Field.SUBJECT, Subject.class), new Asn1FieldInfo(AttributeCertificateInfoV1Field.ISSUER, AttCertIssuer.class), new Asn1FieldInfo(AttributeCertificateInfoV1Field.SIGNATURE, AlgorithmIdentifier.class), new Asn1FieldInfo(AttributeCertificateInfoV1Field.SERIAL_NUMBER, CertificateSerialNumber.class), new Asn1FieldInfo(AttributeCertificateInfoV1Field.ATTR_CERT_VALIDITY_PERIOD, AttCertValidityPeriod.class), new Asn1FieldInfo(AttributeCertificateInfoV1Field.ATTRIBUTES, Attributes.class), new Asn1FieldInfo(AttributeCertificateInfoV1Field.ISSUER_UNIQUE_ID, Asn1BitString.class), new Asn1FieldInfo(AttributeCertificateInfoV1Field.EXTENSIONS, Extensions.class) };
    }
    
    protected enum AttributeCertificateInfoV1Field implements EnumType
    {
        VERSION, 
        SUBJECT, 
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
