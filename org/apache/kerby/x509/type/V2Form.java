// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class V2Form extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public V2Form() {
        super(V2Form.fieldInfos);
    }
    
    public GeneralNames getIssuerName() {
        return this.getFieldAs(V2FormField.ISSUER_NAME, GeneralNames.class);
    }
    
    public void setIssuerName(final GeneralNames issuerName) {
        this.setFieldAs(V2FormField.ISSUER_NAME, issuerName);
    }
    
    public IssuerSerial getBaseCertificateID() {
        return this.getFieldAs(V2FormField.BASE_CERTIFICATE_ID, IssuerSerial.class);
    }
    
    public void setBaseCertificateId(final IssuerSerial baseCertificateId) {
        this.setFieldAs(V2FormField.BASE_CERTIFICATE_ID, baseCertificateId);
    }
    
    public ObjectDigestInfo getObjectDigestInfo() {
        return this.getFieldAs(V2FormField.OBJECT_DIGEST_INFO, ObjectDigestInfo.class);
    }
    
    public void setObjectDigestInfo(final ObjectDigestInfo objectDigestInfo) {
        this.setFieldAs(V2FormField.OBJECT_DIGEST_INFO, objectDigestInfo);
    }
    
    static {
        V2Form.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(V2FormField.ISSUER_NAME, GeneralNames.class), new ExplicitField(V2FormField.BASE_CERTIFICATE_ID, 0, IssuerSerial.class), new ExplicitField(V2FormField.OBJECT_DIGEST_INFO, 1, ObjectDigestInfo.class) };
    }
    
    protected enum V2FormField implements EnumType
    {
        ISSUER_NAME, 
        BASE_CERTIFICATE_ID, 
        OBJECT_DIGEST_INFO;
        
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
