// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.fast;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.type.base.EncryptedData;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class KrbFastArmoredRep extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public KrbFastArmoredRep() {
        super(KrbFastArmoredRep.fieldInfos);
    }
    
    public EncryptedData getEncFastRep() {
        return this.getFieldAs(KrbFastArmoredRepField.ENC_FAST_REP, EncryptedData.class);
    }
    
    public void setEncFastRep(final EncryptedData encFastRep) {
        this.setFieldAs(KrbFastArmoredRepField.ENC_FAST_REP, encFastRep);
    }
    
    static {
        KrbFastArmoredRep.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(KrbFastArmoredRepField.ENC_FAST_REP, EncryptedData.class) };
    }
    
    protected enum KrbFastArmoredRepField implements EnumType
    {
        ENC_FAST_REP;
        
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
