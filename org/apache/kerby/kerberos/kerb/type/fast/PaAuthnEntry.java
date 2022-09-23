// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.fast;

import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class PaAuthnEntry extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public PaAuthnEntry() {
        super(PaAuthnEntry.fieldInfos);
    }
    
    public PaAuthnEntry(final PaDataType type, final byte[] paData) {
        this();
        this.setPaType(type);
        this.setPaValue(paData);
    }
    
    public PaDataType getPaType() {
        final Integer value = this.getFieldAsInteger(PaAuthnEntryField.PA_TYPE);
        return PaDataType.fromValue(value);
    }
    
    public void setPaType(final PaDataType paDataType) {
        this.setFieldAsInt(PaAuthnEntryField.PA_TYPE, paDataType.getValue());
    }
    
    public byte[] getPaHint() {
        return this.getFieldAsOctets(PaAuthnEntryField.PA_HINT);
    }
    
    public void setPaHint(final byte[] paHint) {
        this.setFieldAsOctets(PaAuthnEntryField.PA_HINT, paHint);
    }
    
    public byte[] getPaValue() {
        return this.getFieldAsOctets(PaAuthnEntryField.PA_VALUE);
    }
    
    public void setPaValue(final byte[] paValue) {
        this.setFieldAsOctets(PaAuthnEntryField.PA_VALUE, paValue);
    }
    
    static {
        PaAuthnEntry.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(PaAuthnEntryField.PA_TYPE, Asn1Integer.class), new ExplicitField(PaAuthnEntryField.PA_HINT, Asn1OctetString.class), new ExplicitField(PaAuthnEntryField.PA_VALUE, Asn1OctetString.class) };
    }
    
    protected enum PaAuthnEntryField implements EnumType
    {
        PA_TYPE, 
        PA_HINT, 
        PA_VALUE;
        
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
