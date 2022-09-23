// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.base;

import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class EtypeInfoEntry extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public EtypeInfoEntry() {
        super(EtypeInfoEntry.fieldInfos);
    }
    
    public EncryptionType getEtype() {
        return EncryptionType.fromValue(this.getFieldAsInt(EtypeInfoEntryField.ETYPE));
    }
    
    public void setEtype(final EncryptionType etype) {
        this.setField(EtypeInfoEntryField.ETYPE, etype);
    }
    
    public byte[] getSalt() {
        return this.getFieldAsOctets(EtypeInfoEntryField.SALT);
    }
    
    public void setSalt(final byte[] salt) {
        this.setFieldAsOctets(EtypeInfoEntryField.SALT, salt);
    }
    
    static {
        EtypeInfoEntry.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(EtypeInfoEntryField.ETYPE, Asn1Integer.class), new ExplicitField(EtypeInfoEntryField.SALT, Asn1OctetString.class) };
    }
    
    protected enum EtypeInfoEntryField implements EnumType
    {
        ETYPE, 
        SALT;
        
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
