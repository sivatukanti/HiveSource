// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.pa;

import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class PaEncTsEnc extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public PaEncTsEnc() {
        super(PaEncTsEnc.fieldInfos);
    }
    
    public KerberosTime getPaTimestamp() {
        return this.getFieldAsTime(PaEncTsEncField.PATIMESTAMP);
    }
    
    public void setPaTimestamp(final KerberosTime paTimestamp) {
        this.setFieldAs(PaEncTsEncField.PATIMESTAMP, paTimestamp);
    }
    
    public int getPaUsec() {
        return this.getFieldAsInt(PaEncTsEncField.PAUSEC);
    }
    
    public void setPaUsec(final int paUsec) {
        this.setFieldAsInt(PaEncTsEncField.PAUSEC, paUsec);
    }
    
    public KerberosTime getAllTime() {
        final KerberosTime paTimestamp = this.getPaTimestamp();
        return paTimestamp.extend(this.getPaUsec() / 1000);
    }
    
    static {
        PaEncTsEnc.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(PaEncTsEncField.PATIMESTAMP, KerberosTime.class), new ExplicitField(PaEncTsEncField.PAUSEC, Asn1Integer.class) };
    }
    
    protected enum PaEncTsEncField implements EnumType
    {
        PATIMESTAMP, 
        PAUSEC;
        
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
