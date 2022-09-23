// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.fast;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcReqBody;
import org.apache.kerby.kerberos.kerb.type.pa.PaData;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class KrbFastReq extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public KrbFastReq() {
        super(KrbFastReq.fieldInfos);
    }
    
    public FastOptions getFastOptions() {
        return this.getFieldAs(KrbFastReqField.FAST_OPTIONS, FastOptions.class);
    }
    
    public void setFastOptions(final FastOptions fastOptions) {
        this.setFieldAs(KrbFastReqField.FAST_OPTIONS, fastOptions);
    }
    
    public PaData getPaData() {
        return this.getFieldAs(KrbFastReqField.PADATA, PaData.class);
    }
    
    public void setPaData(final PaData paData) {
        this.setFieldAs(KrbFastReqField.PADATA, paData);
    }
    
    public KdcReqBody getKdcReqBody() {
        return this.getFieldAs(KrbFastReqField.REQ_BODY, KdcReqBody.class);
    }
    
    public void setKdcReqBody(final KdcReqBody kdcReqBody) {
        this.setFieldAs(KrbFastReqField.REQ_BODY, kdcReqBody);
    }
    
    static {
        KrbFastReq.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(KrbFastReqField.FAST_OPTIONS, FastOptions.class), new ExplicitField(KrbFastReqField.PADATA, PaData.class), new ExplicitField(KrbFastReqField.REQ_BODY, KdcReqBody.class) };
    }
    
    protected enum KrbFastReqField implements EnumType
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
