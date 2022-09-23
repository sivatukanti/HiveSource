// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.type.base.EncryptedData;
import org.apache.kerby.kerberos.kerb.type.base.KrbMessageType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.base.KrbMessage;

public class KrbPriv extends KrbMessage
{
    static Asn1FieldInfo[] fieldInfos;
    private EncKrbPrivPart encPart;
    
    public KrbPriv() {
        super(KrbMessageType.KRB_PRIV, KrbPriv.fieldInfos);
    }
    
    public EncryptedData getEncryptedEncPart() {
        return this.getFieldAs(KrbPrivField.ENC_PART, EncryptedData.class);
    }
    
    public void setEncryptedEncPart(final EncryptedData encryptedEncPart) {
        this.setFieldAs(KrbPrivField.ENC_PART, encryptedEncPart);
    }
    
    public EncKrbPrivPart getEncPart() {
        return this.encPart;
    }
    
    public void setEncPart(final EncKrbPrivPart encPart) {
        this.encPart = encPart;
    }
    
    static {
        KrbPriv.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(KrbPrivField.PVNO, Asn1Integer.class), new ExplicitField(KrbPrivField.MSG_TYPE, Asn1Integer.class), new ExplicitField(KrbPrivField.ENC_PART, EncryptedData.class) };
    }
    
    protected enum KrbPrivField implements EnumType
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
