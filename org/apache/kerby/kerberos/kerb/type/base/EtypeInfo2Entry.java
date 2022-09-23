// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.base;

import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.kerberos.kerb.type.KerberosString;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class EtypeInfo2Entry extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public EtypeInfo2Entry() {
        super(EtypeInfo2Entry.fieldInfos);
    }
    
    public EncryptionType getEtype() {
        return EncryptionType.fromValue(this.getFieldAsInt(EtypeInfo2EntryField.ETYPE));
    }
    
    public void setEtype(final EncryptionType etype) {
        this.setField(EtypeInfo2EntryField.ETYPE, etype);
    }
    
    public String getSalt() {
        return this.getFieldAsString(EtypeInfo2EntryField.SALT);
    }
    
    public void setSalt(final String salt) {
        this.setFieldAsString(EtypeInfo2EntryField.SALT, salt);
    }
    
    public byte[] getS2kParams() {
        return this.getFieldAsOctets(EtypeInfo2EntryField.S2KPARAMS);
    }
    
    public void setS2kParams(final byte[] s2kParams) {
        this.setFieldAsOctets(EtypeInfo2EntryField.S2KPARAMS, s2kParams);
    }
    
    static {
        EtypeInfo2Entry.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(EtypeInfo2EntryField.ETYPE, Asn1Integer.class), new ExplicitField(EtypeInfo2EntryField.SALT, KerberosString.class), new ExplicitField(EtypeInfo2EntryField.S2KPARAMS, Asn1OctetString.class) };
    }
    
    protected enum EtypeInfo2EntryField implements EnumType
    {
        ETYPE, 
        SALT, 
        S2KPARAMS;
        
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
