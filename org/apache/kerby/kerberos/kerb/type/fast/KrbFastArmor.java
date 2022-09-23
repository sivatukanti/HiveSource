// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.fast;

import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class KrbFastArmor extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public KrbFastArmor() {
        super(KrbFastArmor.fieldInfos);
    }
    
    public ArmorType getArmorType() {
        final Integer value = this.getFieldAsInteger(KrbFastArmorField.ARMOR_TYPE);
        return ArmorType.fromValue(value);
    }
    
    public void setArmorType(final ArmorType armorType) {
        this.setFieldAsInt(KrbFastArmorField.ARMOR_TYPE, armorType.getValue());
    }
    
    public byte[] getArmorValue() {
        return this.getFieldAsOctets(KrbFastArmorField.ARMOR_VALUE);
    }
    
    public void setArmorValue(final byte[] armorValue) {
        this.setFieldAsOctets(KrbFastArmorField.ARMOR_VALUE, armorValue);
    }
    
    static {
        KrbFastArmor.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(KrbFastArmorField.ARMOR_TYPE, Asn1Integer.class), new ExplicitField(KrbFastArmorField.ARMOR_VALUE, Asn1OctetString.class) };
    }
    
    protected enum KrbFastArmorField implements EnumType
    {
        ARMOR_TYPE, 
        ARMOR_VALUE;
        
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
