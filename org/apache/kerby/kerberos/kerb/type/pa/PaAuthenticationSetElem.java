// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.pa;

import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class PaAuthenticationSetElem extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public PaAuthenticationSetElem() {
        super(PaAuthenticationSetElem.fieldInfos);
    }
    
    public PaDataType getPaType() {
        final Integer value = this.getFieldAsInteger(PaAuthenticationSetElemField.PA_TYPE);
        return PaDataType.fromValue(value);
    }
    
    public void setPaType(final PaDataType paDataType) {
        this.setFieldAsInt(PaAuthenticationSetElemField.PA_TYPE, paDataType.getValue());
    }
    
    public byte[] getPaHint() {
        return this.getFieldAsOctets(PaAuthenticationSetElemField.PA_HINT);
    }
    
    public void setPaHint(final byte[] paHint) {
        this.setFieldAsOctets(PaAuthenticationSetElemField.PA_HINT, paHint);
    }
    
    public byte[] getPaValue() {
        return this.getFieldAsOctets(PaAuthenticationSetElemField.PA_VALUE);
    }
    
    public void setPaValue(final byte[] paDataValue) {
        this.setFieldAsOctets(PaAuthenticationSetElemField.PA_VALUE, paDataValue);
    }
    
    static {
        PaAuthenticationSetElem.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(PaAuthenticationSetElemField.PA_TYPE, Asn1Integer.class), new ExplicitField(PaAuthenticationSetElemField.PA_HINT, Asn1OctetString.class), new ExplicitField(PaAuthenticationSetElemField.PA_VALUE, Asn1OctetString.class) };
    }
    
    protected enum PaAuthenticationSetElemField implements EnumType
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
