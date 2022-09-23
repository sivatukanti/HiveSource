// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1UtcTime;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.type.Asn1GeneralizedTime;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class AttCertValidityPeriod extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public AttCertValidityPeriod() {
        super(AttCertValidityPeriod.fieldInfos);
    }
    
    public Asn1GeneralizedTime getNotBeforeTime() {
        return this.getFieldAs(AttCertValidityPeriodField.NOT_BEFORE, Asn1GeneralizedTime.class);
    }
    
    public void setNotBeforeTime(final Asn1GeneralizedTime notBeforeTime) {
        this.setFieldAs(AttCertValidityPeriodField.NOT_BEFORE, notBeforeTime);
    }
    
    public Asn1GeneralizedTime getNotAfterTime() {
        return this.getFieldAs(AttCertValidityPeriodField.NOT_AFTER, Asn1GeneralizedTime.class);
    }
    
    public void setNotAfterTime(final Asn1GeneralizedTime notAfterTime) {
        this.setFieldAs(AttCertValidityPeriodField.NOT_AFTER, notAfterTime);
    }
    
    static {
        AttCertValidityPeriod.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(AttCertValidityPeriodField.NOT_BEFORE, Asn1UtcTime.class), new Asn1FieldInfo(AttCertValidityPeriodField.NOT_AFTER, Asn1UtcTime.class) };
    }
    
    protected enum AttCertValidityPeriodField implements EnumType
    {
        NOT_BEFORE, 
        NOT_AFTER;
        
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
