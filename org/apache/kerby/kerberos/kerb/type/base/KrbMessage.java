// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.base;

import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbAppSequenceType;

public abstract class KrbMessage extends KrbAppSequenceType
{
    private final int pvno = 5;
    
    protected KrbMessage(final KrbMessageType msgType, final Asn1FieldInfo[] fieldInfos) {
        super(msgType.getValue(), fieldInfos);
        this.setPvno(this.pvno);
        this.setMsgType(msgType);
    }
    
    public int getPvno() {
        return 5;
    }
    
    protected void setPvno(final int pvno) {
        this.setFieldAsInt(KrbMessageField.PVNO, pvno);
    }
    
    public KrbMessageType getMsgType() {
        final Integer value = this.getFieldAsInteger(KrbMessageField.MSG_TYPE);
        return KrbMessageType.fromValue(value);
    }
    
    public void setMsgType(final KrbMessageType msgType) {
        this.setFieldAsInt(KrbMessageField.MSG_TYPE, msgType.getValue());
    }
    
    protected enum KrbMessageField implements EnumType
    {
        PVNO, 
        MSG_TYPE;
        
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
