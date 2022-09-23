// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.fast;

import org.apache.kerby.kerberos.kerb.type.pa.PaData;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.kerberos.kerb.type.base.EncryptedData;
import org.apache.kerby.kerberos.kerb.type.base.CheckSum;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class KrbFastFinished extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public KrbFastFinished() {
        super(KrbFastFinished.fieldInfos);
    }
    
    public KrbFastArmor getArmor() {
        return this.getFieldAs(KrbFastFinishedField.FAST_OPTIONS, KrbFastArmor.class);
    }
    
    public void setArmor(final KrbFastArmor armor) {
        this.setFieldAs(KrbFastFinishedField.FAST_OPTIONS, armor);
    }
    
    public CheckSum getReqChecksum() {
        return this.getFieldAs(KrbFastFinishedField.PADATA, CheckSum.class);
    }
    
    public void setReqChecksum(final CheckSum checkSum) {
        this.setFieldAs(KrbFastFinishedField.PADATA, checkSum);
    }
    
    public EncryptedData getEncFastReq() {
        return this.getFieldAs(KrbFastFinishedField.REQ_BODY, EncryptedData.class);
    }
    
    public void setEncFastReq(final EncryptedData encFastReq) {
        this.setFieldAs(KrbFastFinishedField.REQ_BODY, encFastReq);
    }
    
    static {
        KrbFastFinished.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(KrbFastFinishedField.FAST_OPTIONS, KrbFastArmor.class), new ExplicitField(KrbFastFinishedField.PADATA, PaData.class), new ExplicitField(KrbFastFinishedField.REQ_BODY, EncryptedData.class) };
    }
    
    protected enum KrbFastFinishedField implements EnumType
    {
        FAST_OPTIONS, 
        PADATA, 
        REQ_BODY;
        
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
