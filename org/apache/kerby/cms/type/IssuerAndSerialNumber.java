// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.x500.type.Name;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class IssuerAndSerialNumber extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public IssuerAndSerialNumber() {
        super(IssuerAndSerialNumber.fieldInfos);
    }
    
    public Name getIssuer() {
        return this.getFieldAs(IssuerAndSerialNumberField.ISSUER, Name.class);
    }
    
    public void setIssuer(final Name name) {
        this.setFieldAs(IssuerAndSerialNumberField.ISSUER, name);
    }
    
    public Asn1Integer getSerialNumber() {
        return this.getFieldAs(IssuerAndSerialNumberField.SERIAL_NUMBER, Asn1Integer.class);
    }
    
    public void setSerialNumber(final int serialNumber) {
        this.setFieldAsInt(IssuerAndSerialNumberField.SERIAL_NUMBER, serialNumber);
    }
    
    static {
        IssuerAndSerialNumber.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(IssuerAndSerialNumberField.ISSUER, Name.class), new Asn1FieldInfo(IssuerAndSerialNumberField.SERIAL_NUMBER, Asn1Integer.class) };
    }
    
    protected enum IssuerAndSerialNumberField implements EnumType
    {
        ISSUER, 
        SERIAL_NUMBER;
        
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
