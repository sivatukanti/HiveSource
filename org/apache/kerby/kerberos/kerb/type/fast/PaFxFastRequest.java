// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.fast;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1Choice;

public class PaFxFastRequest extends Asn1Choice
{
    static Asn1FieldInfo[] fieldInfos;
    
    public PaFxFastRequest() {
        super(PaFxFastRequest.fieldInfos);
    }
    
    public KrbFastArmoredReq getFastArmoredReq() {
        return this.getChoiceValueAs(PaFxFastRequestField.ARMORED_DATA, KrbFastArmoredReq.class);
    }
    
    public void setFastArmoredReq(final KrbFastArmoredReq fastArmoredReq) {
        this.setChoiceValue(PaFxFastRequestField.ARMORED_DATA, fastArmoredReq);
    }
    
    static {
        PaFxFastRequest.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(PaFxFastRequestField.ARMORED_DATA, KrbFastArmoredReq.class) };
    }
    
    protected enum PaFxFastRequestField implements EnumType
    {
        ARMORED_DATA;
        
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
