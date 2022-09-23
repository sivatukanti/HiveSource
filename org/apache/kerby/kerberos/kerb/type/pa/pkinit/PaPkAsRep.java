// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.pa.pkinit;

import org.apache.kerby.asn1.ImplicitField;
import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1Choice;

public class PaPkAsRep extends Asn1Choice
{
    static Asn1FieldInfo[] fieldInfos;
    
    public PaPkAsRep() {
        super(PaPkAsRep.fieldInfos);
    }
    
    public DhRepInfo getDHRepInfo() {
        return this.getChoiceValueAs(PaPkAsRepField.DH_INFO, DhRepInfo.class);
    }
    
    public void setDHRepInfo(final DhRepInfo dhRepInfo) {
        this.setChoiceValue(PaPkAsRepField.DH_INFO, dhRepInfo);
    }
    
    public byte[] getEncKeyPack() {
        return this.getChoiceValueAsOctets(PaPkAsRepField.ENCKEY_PACK);
    }
    
    public void setEncKeyPack(final byte[] encKeyPack) {
        this.setChoiceValueAsOctets(PaPkAsRepField.ENCKEY_PACK, encKeyPack);
    }
    
    static {
        PaPkAsRep.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(PaPkAsRepField.DH_INFO, DhRepInfo.class), new ImplicitField(PaPkAsRepField.ENCKEY_PACK, Asn1OctetString.class) };
    }
    
    protected enum PaPkAsRepField implements EnumType
    {
        DH_INFO, 
        ENCKEY_PACK;
        
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
