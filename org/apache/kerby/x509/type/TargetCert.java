// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class TargetCert extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public TargetCert() {
        super(TargetCert.fieldInfos);
    }
    
    public IssuerSerial getTargetCertificate() {
        return this.getFieldAs(TargetCertField.TARGET_CERTIFICATE, IssuerSerial.class);
    }
    
    public void setTargetCertificate(final IssuerSerial targetCertificate) {
        this.setFieldAs(TargetCertField.TARGET_CERTIFICATE, targetCertificate);
    }
    
    public GeneralName getTargetName() {
        return this.getFieldAs(TargetCertField.TARGET_NAME, GeneralName.class);
    }
    
    public void setTargetName(final GeneralName targetName) {
        this.setFieldAs(TargetCertField.TARGET_NAME, targetName);
    }
    
    public ObjectDigestInfo getCertDigestInfo() {
        return this.getFieldAs(TargetCertField.CERT_DIGEST_INFO, ObjectDigestInfo.class);
    }
    
    public void setCerttDigestInfo(final ObjectDigestInfo certDigestInfo) {
        this.setFieldAs(TargetCertField.CERT_DIGEST_INFO, certDigestInfo);
    }
    
    static {
        TargetCert.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(TargetCertField.TARGET_CERTIFICATE, IssuerSerial.class), new Asn1FieldInfo(TargetCertField.TARGET_NAME, GeneralName.class), new Asn1FieldInfo(TargetCertField.CERT_DIGEST_INFO, ObjectDigestInfo.class) };
    }
    
    protected enum TargetCertField implements EnumType
    {
        TARGET_CERTIFICATE, 
        TARGET_NAME, 
        CERT_DIGEST_INFO;
        
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
