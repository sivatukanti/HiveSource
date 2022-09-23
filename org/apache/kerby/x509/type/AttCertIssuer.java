// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1Choice;

public class AttCertIssuer extends Asn1Choice
{
    static Asn1FieldInfo[] fieldInfos;
    
    public AttCertIssuer() {
        super(AttCertIssuer.fieldInfos);
    }
    
    public GeneralNames getV1Form() {
        return this.getChoiceValueAs(AttCertIssuerField.V1_FORM, GeneralNames.class);
    }
    
    public void setV1Form(final GeneralNames v1Form) {
        this.setChoiceValue(AttCertIssuerField.V1_FORM, v1Form);
    }
    
    public V2Form getV2Form() {
        return this.getChoiceValueAs(AttCertIssuerField.V2_FORM, V2Form.class);
    }
    
    public void setV2Form(final V2Form v2Form) {
        this.setChoiceValue(AttCertIssuerField.V2_FORM, v2Form);
    }
    
    static {
        AttCertIssuer.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(AttCertIssuerField.V1_FORM, GeneralNames.class), new ExplicitField(AttCertIssuerField.V2_FORM, 0, V2Form.class) };
    }
    
    protected enum AttCertIssuerField implements EnumType
    {
        V1_FORM, 
        V2_FORM;
        
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
