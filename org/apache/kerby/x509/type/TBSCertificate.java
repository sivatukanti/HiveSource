// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.ImplicitField;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.asn1.type.Asn1BitString;
import org.apache.kerby.x500.type.Name;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class TBSCertificate extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public TBSCertificate() {
        super(TBSCertificate.fieldInfos);
    }
    
    public int getVersion() {
        return this.getFieldAsInteger(TBSCertificateField.VERSION);
    }
    
    public void setVersion(final int version) {
        this.setFieldAsInt(TBSCertificateField.VERSION, version);
    }
    
    public CertificateSerialNumber getSerialNumber() {
        return this.getFieldAs(TBSCertificateField.SERIAL_NUMBER, CertificateSerialNumber.class);
    }
    
    public void setSerialNumber(final CertificateSerialNumber certificateSerialNumber) {
        this.setFieldAs(TBSCertificateField.SERIAL_NUMBER, certificateSerialNumber);
    }
    
    public AlgorithmIdentifier getSignature() {
        return this.getFieldAs(TBSCertificateField.SIGNATURE, AlgorithmIdentifier.class);
    }
    
    public void setSignature(final AlgorithmIdentifier signature) {
        this.setFieldAs(TBSCertificateField.SIGNATURE, signature);
    }
    
    public Name getIssuer() {
        return this.getFieldAs(TBSCertificateField.ISSUER, Name.class);
    }
    
    public void setIssuer(final Name attCertIssuer) {
        this.setFieldAs(TBSCertificateField.ISSUER, attCertIssuer);
    }
    
    public AttCertValidityPeriod getValidity() {
        return this.getFieldAs(TBSCertificateField.VALIDITY, AttCertValidityPeriod.class);
    }
    
    public void setValidity(final AttCertValidityPeriod validity) {
        this.setFieldAs(TBSCertificateField.VALIDITY, validity);
    }
    
    public Name getSubject() {
        return this.getFieldAs(TBSCertificateField.SUBJECT, Name.class);
    }
    
    public void setSubject(final Name subject) {
        this.setFieldAs(TBSCertificateField.SUBJECT, subject);
    }
    
    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.getFieldAs(TBSCertificateField.SUBJECT_PUBLIC_KEY_INFO, SubjectPublicKeyInfo.class);
    }
    
    public void setSubjectPublicKeyInfo(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        this.setFieldAs(TBSCertificateField.SUBJECT_PUBLIC_KEY_INFO, subjectPublicKeyInfo);
    }
    
    public byte[] getIssuerUniqueID() {
        return this.getFieldAs(TBSCertificateField.ISSUER_UNIQUE_ID, Asn1BitString.class).getValue();
    }
    
    public void setIssuerUniqueId(final byte[] issuerUniqueId) {
        this.setFieldAs(TBSCertificateField.ISSUER_UNIQUE_ID, new Asn1BitString(issuerUniqueId));
    }
    
    public byte[] getSubjectUniqueId() {
        return this.getFieldAs(TBSCertificateField.ISSUER_UNIQUE_ID, Asn1BitString.class).getValue();
    }
    
    public void setSubjectUniqueId(final byte[] issuerUniqueId) {
        this.setFieldAs(TBSCertificateField.SUBJECT_UNIQUE_ID, new Asn1BitString(issuerUniqueId));
    }
    
    public Extensions getExtensions() {
        return this.getFieldAs(TBSCertificateField.EXTENSIONS, Extensions.class);
    }
    
    public void setExtensions(final Extensions extensions) {
        this.setFieldAs(TBSCertificateField.EXTENSIONS, extensions);
    }
    
    static {
        TBSCertificate.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(TBSCertificateField.VERSION, Asn1Integer.class), new Asn1FieldInfo(TBSCertificateField.SERIAL_NUMBER, CertificateSerialNumber.class), new Asn1FieldInfo(TBSCertificateField.SIGNATURE, AlgorithmIdentifier.class), new Asn1FieldInfo(TBSCertificateField.ISSUER, Name.class), new Asn1FieldInfo(TBSCertificateField.VALIDITY, AttCertValidityPeriod.class), new Asn1FieldInfo(TBSCertificateField.SUBJECT, Name.class), new Asn1FieldInfo(TBSCertificateField.SUBJECT_PUBLIC_KEY_INFO, SubjectPublicKeyInfo.class), new ImplicitField(TBSCertificateField.ISSUER_UNIQUE_ID, 1, Asn1BitString.class), new ImplicitField(TBSCertificateField.SUBJECT_UNIQUE_ID, 2, Asn1BitString.class), new ExplicitField(TBSCertificateField.EXTENSIONS, 3, Extensions.class) };
    }
    
    protected enum TBSCertificateField implements EnumType
    {
        VERSION, 
        SERIAL_NUMBER, 
        SIGNATURE, 
        ISSUER, 
        VALIDITY, 
        SUBJECT, 
        SUBJECT_PUBLIC_KEY_INFO, 
        ISSUER_UNIQUE_ID, 
        SUBJECT_UNIQUE_ID, 
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
