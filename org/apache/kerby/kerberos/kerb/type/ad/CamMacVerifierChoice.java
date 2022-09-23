// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ad;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1Choice;

public class CamMacVerifierChoice extends Asn1Choice
{
    private static Asn1FieldInfo[] fieldInfos;
    
    public CamMacVerifierChoice() {
        super(CamMacVerifierChoice.fieldInfos);
    }
    
    public void setChoice(final EnumType type, final Asn1Type choice) {
        this.setChoiceValue(type, choice);
    }
    
    static {
        CamMacVerifierChoice.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(VerifierChoice.CAMMAC_verifierMac, CamMacVerifierMac.class) };
    }
    
    protected enum VerifierChoice implements EnumType
    {
        CAMMAC_verifierMac;
        
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
