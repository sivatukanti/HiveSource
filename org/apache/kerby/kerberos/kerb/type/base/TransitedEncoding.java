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

public class TransitedEncoding extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public TransitedEncoding() {
        super(TransitedEncoding.fieldInfos);
    }
    
    public TransitedEncodingType getTrType() {
        final Integer value = this.getFieldAsInteger(TransitedEncodingField.TR_TYPE);
        return TransitedEncodingType.fromValue(value);
    }
    
    public void setTrType(final TransitedEncodingType trType) {
        this.setField(TransitedEncodingField.TR_TYPE, trType);
    }
    
    public byte[] getContents() {
        return this.getFieldAsOctets(TransitedEncodingField.CONTENTS);
    }
    
    public void setContents(final byte[] contents) {
        this.setFieldAsOctets(TransitedEncodingField.CONTENTS, contents);
    }
    
    static {
        TransitedEncoding.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(TransitedEncodingField.TR_TYPE, Asn1Integer.class), new ExplicitField(TransitedEncodingField.CONTENTS, Asn1OctetString.class) };
    }
    
    protected enum TransitedEncodingField implements EnumType
    {
        TR_TYPE, 
        CONTENTS;
        
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
