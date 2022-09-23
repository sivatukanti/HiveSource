// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ap;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.type.base.EncryptedData;
import org.apache.kerby.kerberos.kerb.type.base.KrbMessageType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.base.KrbMessage;

public class ApRep extends KrbMessage
{
    static Asn1FieldInfo[] fieldInfos;
    private EncAPRepPart encRepPart;
    
    public ApRep() {
        super(KrbMessageType.AP_REP, ApRep.fieldInfos);
    }
    
    public EncAPRepPart getEncRepPart() {
        return this.encRepPart;
    }
    
    public void setEncRepPart(final EncAPRepPart encRepPart) {
        this.encRepPart = encRepPart;
    }
    
    public EncryptedData getEncryptedEncPart() {
        return this.getFieldAs(ApRepField.ENC_PART, EncryptedData.class);
    }
    
    public void setEncryptedEncPart(final EncryptedData encPart) {
        this.setFieldAs(ApRepField.ENC_PART, encPart);
    }
    
    static {
        ApRep.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(ApRepField.PVNO, Asn1Integer.class), new ExplicitField(ApRepField.MSG_TYPE, Asn1Integer.class), new ExplicitField(ApRepField.ENC_PART, EncryptedData.class) };
    }
    
    protected enum ApRepField implements EnumType
    {
        PVNO, 
        MSG_TYPE, 
        ENC_PART;
        
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
