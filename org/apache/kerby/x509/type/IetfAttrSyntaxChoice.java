// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1ObjectIdentifier;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1Choice;

public class IetfAttrSyntaxChoice extends Asn1Choice
{
    static Asn1FieldInfo[] fieldInfos;
    
    public IetfAttrSyntaxChoice() {
        super(IetfAttrSyntaxChoice.fieldInfos);
    }
    
    public Asn1OctetString getOctets() {
        return this.getChoiceValueAs(IetfAttrSyntaxChoiceField.OCTETS, Asn1OctetString.class);
    }
    
    public void setOctets(final Asn1OctetString octets) {
        this.setChoiceValue(IetfAttrSyntaxChoiceField.OCTETS, octets);
    }
    
    public Asn1ObjectIdentifier getOid() {
        return this.getChoiceValueAs(IetfAttrSyntaxChoiceField.OID, Asn1ObjectIdentifier.class);
    }
    
    public void setOid(final Asn1ObjectIdentifier oid) {
        this.setChoiceValue(IetfAttrSyntaxChoiceField.OID, oid);
    }
    
    public Asn1ObjectIdentifier getUtf8() {
        return this.getChoiceValueAs(IetfAttrSyntaxChoiceField.UTF8, Asn1ObjectIdentifier.class);
    }
    
    public void setUtf8(final Asn1ObjectIdentifier utf8) {
        this.setChoiceValue(IetfAttrSyntaxChoiceField.UTF8, utf8);
    }
    
    static {
        IetfAttrSyntaxChoice.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(IetfAttrSyntaxChoiceField.OCTETS, Asn1OctetString.class), new Asn1FieldInfo(IetfAttrSyntaxChoiceField.OID, Asn1ObjectIdentifier.class), new Asn1FieldInfo(IetfAttrSyntaxChoiceField.UTF8, Asn1ObjectIdentifier.class) };
    }
    
    protected enum IetfAttrSyntaxChoiceField implements EnumType
    {
        OCTETS, 
        OID, 
        UTF8;
        
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
