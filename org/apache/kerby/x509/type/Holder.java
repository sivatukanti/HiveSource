// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class Holder extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public Holder() {
        super(Holder.fieldInfos);
    }
    
    public IssuerSerial getBaseCertificateID() {
        return this.getFieldAs(HolderField.BASE_CERTIFICATE_ID, IssuerSerial.class);
    }
    
    public void setBaseCertificateId(final IssuerSerial baseCertificateId) {
        this.setFieldAs(HolderField.BASE_CERTIFICATE_ID, baseCertificateId);
    }
    
    public GeneralNames getEntityName() {
        return this.getFieldAs(HolderField.ENTITY_NAME, GeneralNames.class);
    }
    
    public void setEntityName(final GeneralNames entityName) {
        this.setFieldAs(HolderField.ENTITY_NAME, entityName);
    }
    
    public ObjectDigestInfo getObjectDigestInfo() {
        return this.getFieldAs(HolderField.OBJECT_DIGEST_INFO, ObjectDigestInfo.class);
    }
    
    public void setObjectDigestInfo(final ObjectDigestInfo objectDigestInfo) {
        this.setFieldAs(HolderField.OBJECT_DIGEST_INFO, objectDigestInfo);
    }
    
    static {
        Holder.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(HolderField.BASE_CERTIFICATE_ID, IssuerSerial.class), new ExplicitField(HolderField.ENTITY_NAME, GeneralNames.class), new ExplicitField(HolderField.OBJECT_DIGEST_INFO, ObjectDigestInfo.class) };
    }
    
    protected enum HolderField implements EnumType
    {
        BASE_CERTIFICATE_ID, 
        ENTITY_NAME, 
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
