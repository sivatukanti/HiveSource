// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.type.Asn1Boolean;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.type.Asn1ObjectIdentifier;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class Extension extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public Extension() {
        super(Extension.fieldInfos);
    }
    
    public Asn1ObjectIdentifier getExtnId() {
        return this.getFieldAs(ExtensionField.EXTN_ID, Asn1ObjectIdentifier.class);
    }
    
    public void setExtnId(final Asn1ObjectIdentifier extnId) {
        this.setFieldAs(ExtensionField.EXTN_ID, extnId);
    }
    
    public boolean getCritical() {
        return this.getFieldAs(ExtensionField.CRITICAL, Asn1Boolean.class).getValue();
    }
    
    public void setCritical(final boolean critical) {
        this.setFieldAs(ExtensionField.CRITICAL, new Asn1Boolean(critical));
    }
    
    public byte[] getExtnValue() {
        return this.getFieldAsOctets(ExtensionField.EXTN_VALUE);
    }
    
    public void setExtnValue(final byte[] value) {
        this.setFieldAsOctets(ExtensionField.EXTN_VALUE, value);
    }
    
    static {
        Extension.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(ExtensionField.EXTN_ID, Asn1ObjectIdentifier.class), new Asn1FieldInfo(ExtensionField.CRITICAL, Asn1Boolean.class), new Asn1FieldInfo(ExtensionField.EXTN_VALUE, Asn1OctetString.class) };
    }
    
    protected enum ExtensionField implements EnumType
    {
        EXTN_ID, 
        CRITICAL, 
        EXTN_VALUE;
        
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
