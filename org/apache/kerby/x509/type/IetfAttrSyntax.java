// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class IetfAttrSyntax extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public IetfAttrSyntax() {
        super(IetfAttrSyntax.fieldInfos);
    }
    
    public GeneralNames getPolicyAuthority() {
        return this.getFieldAs(IetfAttrSyntaxField.POLICY_AUTHORITY, GeneralNames.class);
    }
    
    public void setPolicyAuthority(final GeneralNames policyAuthority) {
        this.setFieldAs(IetfAttrSyntaxField.POLICY_AUTHORITY, policyAuthority);
    }
    
    public IetfAttrSyntaxChoices getValues() {
        return this.getFieldAs(IetfAttrSyntaxField.VALUES, IetfAttrSyntaxChoices.class);
    }
    
    public void setValues(final IetfAttrSyntaxChoices values) {
        this.setFieldAs(IetfAttrSyntaxField.VALUES, values);
    }
    
    static {
        IetfAttrSyntax.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(IetfAttrSyntaxField.POLICY_AUTHORITY, GeneralNames.class), new Asn1FieldInfo(IetfAttrSyntaxField.VALUES, IetfAttrSyntaxChoices.class) };
    }
    
    protected enum IetfAttrSyntaxField implements EnumType
    {
        POLICY_AUTHORITY, 
        VALUES;
        
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
