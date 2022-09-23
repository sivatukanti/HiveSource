// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.x500.type.RelativeDistinguishedName;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1Choice;

public class DistributionPointName extends Asn1Choice
{
    static Asn1FieldInfo[] fieldInfos;
    
    public DistributionPointName() {
        super(DistributionPointName.fieldInfos);
    }
    
    public GeneralNames getFullName() {
        return this.getChoiceValueAs(DPNameField.FULL_NAME, GeneralNames.class);
    }
    
    public void setFullName(final GeneralNames fullName) {
        this.setChoiceValue(DPNameField.FULL_NAME, fullName);
    }
    
    public RelativeDistinguishedName getNameRelativeToCRLIssuer() {
        return this.getChoiceValueAs(DPNameField.NAME_RELATIVE_TO_CRL_ISSUER, RelativeDistinguishedName.class);
    }
    
    public void setNameRelativeToCrlIssuer(final RelativeDistinguishedName nameRelativeToCrlIssuer) {
        this.setChoiceValue(DPNameField.NAME_RELATIVE_TO_CRL_ISSUER, nameRelativeToCrlIssuer);
    }
    
    static {
        DistributionPointName.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(DPNameField.FULL_NAME, GeneralNames.class), new ExplicitField(DPNameField.NAME_RELATIVE_TO_CRL_ISSUER, RelativeDistinguishedName.class) };
    }
    
    protected enum DPNameField implements EnumType
    {
        FULL_NAME, 
        NAME_RELATIVE_TO_CRL_ISSUER;
        
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
