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

public class PaDataEntry extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public PaDataEntry() {
        super(PaDataEntry.fieldInfos);
    }
    
    public PaDataEntry(final PaDataType type, final byte[] paData) {
        super(PaDataEntry.fieldInfos);
        this.setPaDataType(type);
        this.setPaDataValue(paData);
    }
    
    public PaDataType getPaDataType() {
        final Integer value = this.getFieldAsInteger(PaDataEntryField.PADATA_TYPE);
        return PaDataType.fromValue(value);
    }
    
    public void setPaDataType(final PaDataType paDataType) {
        this.setFieldAsInt(PaDataEntryField.PADATA_TYPE, paDataType.getValue());
    }
    
    public byte[] getPaDataValue() {
        return this.getFieldAsOctets(PaDataEntryField.PADATA_VALUE);
    }
    
    public void setPaDataValue(final byte[] paDataValue) {
        this.setFieldAsOctets(PaDataEntryField.PADATA_VALUE, paDataValue);
    }
    
    static {
        PaDataEntry.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(PaDataEntryField.PADATA_TYPE, 1, Asn1Integer.class), new ExplicitField(PaDataEntryField.PADATA_VALUE, 2, Asn1OctetString.class) };
    }
    
    protected enum PaDataEntryField implements EnumType
    {
        PADATA_TYPE, 
        PADATA_VALUE;
        
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
