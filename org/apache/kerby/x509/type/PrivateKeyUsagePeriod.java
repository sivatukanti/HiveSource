// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.type.Asn1GeneralizedTime;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class PrivateKeyUsagePeriod extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public PrivateKeyUsagePeriod() {
        super(PrivateKeyUsagePeriod.fieldInfos);
    }
    
    public Asn1GeneralizedTime getNotBeforeTime() {
        return this.getFieldAs(PrivateKeyUsagePeriodField.NOT_BEFORE, Asn1GeneralizedTime.class);
    }
    
    public void setNotBeforeTime(final Asn1GeneralizedTime notBeforeTime) {
        this.setFieldAs(PrivateKeyUsagePeriodField.NOT_BEFORE, notBeforeTime);
    }
    
    public Asn1GeneralizedTime getNotAfterTime() {
        return this.getFieldAs(PrivateKeyUsagePeriodField.NOT_AFTER, Asn1GeneralizedTime.class);
    }
    
    public void setNotAfterTime(final Asn1GeneralizedTime notAfterTime) {
        this.setFieldAs(PrivateKeyUsagePeriodField.NOT_AFTER, notAfterTime);
    }
    
    static {
        PrivateKeyUsagePeriod.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(PrivateKeyUsagePeriodField.NOT_BEFORE, Asn1GeneralizedTime.class), new ExplicitField(PrivateKeyUsagePeriodField.NOT_AFTER, Asn1GeneralizedTime.class) };
    }
    
    protected enum PrivateKeyUsagePeriodField implements EnumType
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
