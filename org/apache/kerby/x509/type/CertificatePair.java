// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class CertificatePair extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public CertificatePair() {
        super(CertificatePair.fieldInfos);
    }
    
    public Certificate getForward() {
        return this.getFieldAs(CertificatePairField.FORWARD, Certificate.class);
    }
    
    public void setForward(final Certificate forward) {
        this.setFieldAs(CertificatePairField.FORWARD, forward);
    }
    
    public Certificate getReverse() {
        return this.getFieldAs(CertificatePairField.REVERSE, Certificate.class);
    }
    
    public void setReverse(final Certificate reverse) {
        this.setFieldAs(CertificatePairField.REVERSE, reverse);
    }
    
    static {
        CertificatePair.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(CertificatePairField.FORWARD, Certificate.class), new ExplicitField(CertificatePairField.REVERSE, Certificate.class) };
    }
    
    protected enum CertificatePairField implements EnumType
    {
        FORWARD, 
        REVERSE;
        
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
