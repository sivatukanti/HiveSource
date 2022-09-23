// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class PolicyInformation extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public PolicyInformation() {
        super(PolicyInformation.fieldInfos);
    }
    
    public CertPolicyId getPolicyIdentifier() {
        return this.getFieldAs(PolicyInformationField.POLICY_IDENTIFIER, CertPolicyId.class);
    }
    
    public void setPolicyIdentifier(final CertPolicyId policyIdentifier) {
        this.setFieldAs(PolicyInformationField.POLICY_IDENTIFIER, policyIdentifier);
    }
    
    public PolicyQualifierInfos getPolicyQualifiers() {
        return this.getFieldAs(PolicyInformationField.POLICY_QUALIFIERS, PolicyQualifierInfos.class);
    }
    
    public void setPolicyQualifiers(final PolicyQualifierInfos policyQualifiers) {
        this.setFieldAs(PolicyInformationField.POLICY_QUALIFIERS, policyQualifiers);
    }
    
    static {
        PolicyInformation.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(PolicyInformationField.POLICY_IDENTIFIER, CertPolicyId.class), new Asn1FieldInfo(PolicyInformationField.POLICY_QUALIFIERS, PolicyQualifierInfos.class) };
    }
    
    protected enum PolicyInformationField implements EnumType
    {
        POLICY_IDENTIFIER, 
        POLICY_QUALIFIERS;
        
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
