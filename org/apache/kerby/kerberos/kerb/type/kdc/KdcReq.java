// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.kdc;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataEntry;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.type.pa.PaData;
import org.apache.kerby.kerberos.kerb.type.base.KrbMessageType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.base.KrbMessage;

public class KdcReq extends KrbMessage
{
    static Asn1FieldInfo[] fieldInfos;
    
    public KdcReq(final KrbMessageType msgType) {
        super(msgType, KdcReq.fieldInfos);
    }
    
    public PaData getPaData() {
        return this.getFieldAs(KdcReqField.PADATA, PaData.class);
    }
    
    public void setPaData(final PaData paData) {
        this.setFieldAs(KdcReqField.PADATA, paData);
    }
    
    public void addPaData(final PaDataEntry paDataEntry) {
        if (this.getPaData() == null) {
            this.setPaData(new PaData());
        }
        this.getPaData().addElement(paDataEntry);
    }
    
    public KdcReqBody getReqBody() {
        return this.getFieldAs(KdcReqField.REQ_BODY, KdcReqBody.class);
    }
    
    public void setReqBody(final KdcReqBody reqBody) {
        this.setFieldAs(KdcReqField.REQ_BODY, reqBody);
    }
    
    static {
        KdcReq.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(KdcReqField.PVNO, 1, Asn1Integer.class), new ExplicitField(KdcReqField.MSG_TYPE, 2, Asn1Integer.class), new ExplicitField(KdcReqField.PADATA, 3, PaData.class), new ExplicitField(KdcReqField.REQ_BODY, 4, KdcReqBody.class) };
    }
    
    protected enum KdcReqField implements EnumType
    {
        PVNO, 
        MSG_TYPE, 
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
