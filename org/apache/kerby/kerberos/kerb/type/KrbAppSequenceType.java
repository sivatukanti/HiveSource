// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type;

import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1TaggingSequence;

public abstract class KrbAppSequenceType extends Asn1TaggingSequence
{
    public KrbAppSequenceType(final int tagNo, final Asn1FieldInfo[] fieldInfos) {
        super(tagNo, fieldInfos, true, false);
    }
    
    protected int getFieldAsInt(final EnumType index) {
        final Integer value = this.getFieldAsInteger(index);
        if (value != null) {
            return value;
        }
        return -1;
    }
    
    protected void setFieldAsString(final EnumType index, final String value) {
        this.setFieldAs(index, new KerberosString(value));
    }
    
    protected KerberosTime getFieldAsTime(final EnumType index) {
        return this.getFieldAs(index, KerberosTime.class);
    }
    
    protected void setFieldAsTime(final EnumType index, final long value) {
        this.setFieldAs(index, new KerberosTime(value));
    }
    
    protected void setField(final EnumType index, final EnumType krbEnum) {
        this.setFieldAsInt(index, krbEnum.getValue());
    }
}
