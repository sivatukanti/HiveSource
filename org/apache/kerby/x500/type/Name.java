// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x500.type;

import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1Choice;

public class Name extends Asn1Choice
{
    static Asn1FieldInfo[] fieldInfos;
    
    public Name() {
        super(Name.fieldInfos);
    }
    
    public RDNSequence getName() {
        return this.getChoiceValueAs(NameField.RDN_SEQUENCE, RDNSequence.class);
    }
    
    public void setName(final RDNSequence name) {
        this.setChoiceValue(NameField.RDN_SEQUENCE, name);
    }
    
    static {
        Name.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(NameField.RDN_SEQUENCE, RDNSequence.class) };
    }
    
    protected enum NameField implements EnumType
    {
        RDN_SEQUENCE;
        
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
