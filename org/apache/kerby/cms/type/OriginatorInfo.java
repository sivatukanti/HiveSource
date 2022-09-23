// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.ImplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class OriginatorInfo extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public OriginatorInfo() {
        super(OriginatorInfo.fieldInfos);
    }
    
    public CertificateSet getCerts() {
        return this.getFieldAs(OriginatorInfoField.CERTS, CertificateSet.class);
    }
    
    public void setCerts(final CertificateSet certs) {
        this.setFieldAs(OriginatorInfoField.CERTS, certs);
    }
    
    public RevocationInfoChoices getCrls() {
        return this.getFieldAs(OriginatorInfoField.CRLS, RevocationInfoChoices.class);
    }
    
    public void setCrls(final RevocationInfoChoices crls) {
        this.setFieldAs(OriginatorInfoField.CRLS, crls);
    }
    
    static {
        OriginatorInfo.fieldInfos = new Asn1FieldInfo[] { new ImplicitField(OriginatorInfoField.CERTS, CertificateSet.class), new ImplicitField(OriginatorInfoField.CRLS, RevocationInfoChoices.class) };
    }
    
    protected enum OriginatorInfoField implements EnumType
    {
        CERTS, 
        CRLS;
        
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
