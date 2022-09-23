// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.base;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class LastReqEntry extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public LastReqEntry() {
        super(LastReqEntry.fieldInfos);
    }
    
    public LastReqType getLrType() {
        final Integer value = this.getFieldAsInteger(LastReqEntryField.LR_TYPE);
        return LastReqType.fromValue(value);
    }
    
    public void setLrType(final LastReqType lrType) {
        this.setFieldAsInt(LastReqEntryField.LR_TYPE, lrType.getValue());
    }
    
    public KerberosTime getLrValue() {
        return this.getFieldAs(LastReqEntryField.LR_VALUE, KerberosTime.class);
    }
    
    public void setLrValue(final KerberosTime lrValue) {
        this.setFieldAs(LastReqEntryField.LR_VALUE, lrValue);
    }
    
    static {
        LastReqEntry.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(LastReqEntryField.LR_TYPE, Asn1Integer.class), new ExplicitField(LastReqEntryField.LR_VALUE, KerberosTime.class) };
    }
    
    protected enum LastReqEntryField implements EnumType
    {
        LR_TYPE, 
        LR_VALUE;
        
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
