// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1Choice;

public class EDIPartyName extends Asn1Choice
{
    static Asn1FieldInfo[] fieldInfos;
    
    public EDIPartyName() {
        super(EDIPartyName.fieldInfos);
    }
    
    public DirectoryString getNameAssigner() {
        return this.getChoiceValueAs(EDIPartyNameField.NAME_ASSIGNER, DirectoryString.class);
    }
    
    public void setNameAssigner(final DirectoryString nameAssigner) {
        this.setChoiceValue(EDIPartyNameField.NAME_ASSIGNER, nameAssigner);
    }
    
    public DirectoryString getPartyName() {
        return this.getChoiceValueAs(EDIPartyNameField.PARTY_NAME, DirectoryString.class);
    }
    
    public void setPartyName(final DirectoryString partyName) {
        this.setChoiceValue(EDIPartyNameField.PARTY_NAME, partyName);
    }
    
    static {
        EDIPartyName.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(EDIPartyNameField.NAME_ASSIGNER, DirectoryString.class), new ExplicitField(EDIPartyNameField.PARTY_NAME, DirectoryString.class) };
    }
    
    protected enum EDIPartyNameField implements EnumType
    {
        NAME_ASSIGNER, 
        PARTY_NAME;
        
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
