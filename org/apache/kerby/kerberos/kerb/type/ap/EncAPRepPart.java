// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ap;

import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbAppSequenceType;

public class EncAPRepPart extends KrbAppSequenceType
{
    public static final int TAG = 27;
    static Asn1FieldInfo[] fieldInfos;
    
    public EncAPRepPart() {
        super(27, EncAPRepPart.fieldInfos);
    }
    
    public KerberosTime getCtime() {
        return this.getFieldAsTime(EncAPRepPartField.CTIME);
    }
    
    public void setCtime(final KerberosTime ctime) {
        this.setFieldAs(EncAPRepPartField.CTIME, ctime);
    }
    
    public int getCusec() {
        return this.getFieldAsInt(EncAPRepPartField.CUSEC);
    }
    
    public void setCusec(final int cusec) {
        this.setFieldAsInt(EncAPRepPartField.CUSEC, cusec);
    }
    
    public EncryptionKey getSubkey() {
        return this.getFieldAs(EncAPRepPartField.SUBKEY, EncryptionKey.class);
    }
    
    public void setSubkey(final EncryptionKey subkey) {
        this.setFieldAs(EncAPRepPartField.SUBKEY, subkey);
    }
    
    public int getSeqNumber() {
        return this.getFieldAsInt(EncAPRepPartField.SEQ_NUMBER);
    }
    
    public void setSeqNumber(final Integer seqNumber) {
        this.setFieldAsInt(EncAPRepPartField.SEQ_NUMBER, seqNumber);
    }
    
    static {
        EncAPRepPart.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(EncAPRepPartField.CTIME, KerberosTime.class), new ExplicitField(EncAPRepPartField.CUSEC, Asn1Integer.class), new ExplicitField(EncAPRepPartField.SUBKEY, EncryptionKey.class), new ExplicitField(EncAPRepPartField.SEQ_NUMBER, Asn1Integer.class) };
    }
    
    protected enum EncAPRepPartField implements EnumType
    {
        CTIME, 
        CUSEC, 
        SUBKEY, 
        SEQ_NUMBER;
        
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
