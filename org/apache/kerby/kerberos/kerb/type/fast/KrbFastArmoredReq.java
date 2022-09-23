// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.fast;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.kerberos.kerb.type.base.EncryptedData;
import org.apache.kerby.kerberos.kerb.type.base.CheckSum;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class KrbFastArmoredReq extends KrbSequenceType
{
    private KrbFastReq fastReq;
    static Asn1FieldInfo[] fieldInfos;
    
    public KrbFastArmoredReq() {
        super(KrbFastArmoredReq.fieldInfos);
    }
    
    public KrbFastArmor getArmor() {
        return this.getFieldAs(KrbFastArmoredReqField.ARMOR, KrbFastArmor.class);
    }
    
    public void setArmor(final KrbFastArmor armor) {
        this.setFieldAs(KrbFastArmoredReqField.ARMOR, armor);
    }
    
    public CheckSum getReqChecksum() {
        return this.getFieldAs(KrbFastArmoredReqField.REQ_CHECKSUM, CheckSum.class);
    }
    
    public void setReqChecksum(final CheckSum checkSum) {
        this.setFieldAs(KrbFastArmoredReqField.REQ_CHECKSUM, checkSum);
    }
    
    public KrbFastReq getFastReq() {
        return this.fastReq;
    }
    
    public void setFastReq(final KrbFastReq fastReq) {
        this.fastReq = fastReq;
    }
    
    public EncryptedData getEncryptedFastReq() {
        return this.getFieldAs(KrbFastArmoredReqField.ENC_FAST_REQ, EncryptedData.class);
    }
    
    public void setEncryptedFastReq(final EncryptedData encFastReq) {
        this.setFieldAs(KrbFastArmoredReqField.ENC_FAST_REQ, encFastReq);
    }
    
    static {
        KrbFastArmoredReq.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(KrbFastArmoredReqField.ARMOR, KrbFastArmor.class), new ExplicitField(KrbFastArmoredReqField.REQ_CHECKSUM, CheckSum.class), new ExplicitField(KrbFastArmoredReqField.ENC_FAST_REQ, EncryptedData.class) };
    }
    
    protected enum KrbFastArmoredReqField implements EnumType
    {
        ARMOR, 
        REQ_CHECKSUM, 
        ENC_FAST_REQ;
        
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
