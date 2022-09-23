// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class PolicyMapping extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public PolicyMapping() {
        super(PolicyMapping.fieldInfos);
    }
    
    public CertPolicyId getIssuerDomainPolicy() {
        return this.getFieldAs(PolicyMappingField.ISSUER_DOMAIN_POLICY, CertPolicyId.class);
    }
    
    public void setIssuerDomainPolicy(final CertPolicyId issuerDomainPolicy) {
        this.setFieldAs(PolicyMappingField.ISSUER_DOMAIN_POLICY, issuerDomainPolicy);
    }
    
    public CertPolicyId getSubjectDomainPolicy() {
        return this.getFieldAs(PolicyMappingField.SUBJECT_DOMAIN_POLICY, CertPolicyId.class);
    }
    
    public void setSubjectDomainPolicy(final CertPolicyId subjectDomainPolicy) {
        this.setFieldAs(PolicyMappingField.SUBJECT_DOMAIN_POLICY, subjectDomainPolicy);
    }
    
    static {
        PolicyMapping.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(PolicyMappingField.ISSUER_DOMAIN_POLICY, CertPolicyId.class), new Asn1FieldInfo(PolicyMappingField.SUBJECT_DOMAIN_POLICY, CertPolicyId.class) };
    }
    
    protected enum PolicyMappingField implements EnumType
    {
        ISSUER_DOMAIN_POLICY, 
        SUBJECT_DOMAIN_POLICY;
        
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
