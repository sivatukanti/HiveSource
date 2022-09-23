// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1Any;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class PolicyQualifierInfo extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public PolicyQualifierInfo() {
        super(PolicyQualifierInfo.fieldInfos);
    }
    
    public PolicyQualifierId getPolicyQualifierId() {
        return this.getFieldAs(PolicyQualifierInfoField.POLICY_QUALIFIER_ID, PolicyQualifierId.class);
    }
    
    public void setPolicyQualifierId(final PolicyQualifierId policyQualifierId) {
        this.setFieldAs(PolicyQualifierInfoField.POLICY_QUALIFIER_ID, policyQualifierId);
    }
    
    public <T extends Asn1Type> T getQualifierAs(final Class<T> t) {
        return this.getFieldAsAny(PolicyQualifierInfoField.QUALIFIER, t);
    }
    
    public void setQualifier(final Asn1Type qualifier) {
        this.setFieldAsAny(PolicyQualifierInfoField.QUALIFIER, qualifier);
    }
    
    static {
        PolicyQualifierInfo.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(PolicyQualifierInfoField.POLICY_QUALIFIER_ID, PolicyQualifierId.class), new Asn1FieldInfo(PolicyQualifierInfoField.QUALIFIER, Asn1Any.class) };
    }
    
    protected enum PolicyQualifierInfoField implements EnumType
    {
        POLICY_QUALIFIER_ID, 
        QUALIFIER;
        
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
