// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class PolicyConstraints extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public PolicyConstraints() {
        super(PolicyConstraints.fieldInfos);
    }
    
    public Asn1Integer getRequireExplicitPolicy() {
        return this.getFieldAs(PolicyConstraintsField.REQUIRE_EXPLICIT_POLICY, Asn1Integer.class);
    }
    
    public void setRequireExplicitPolicy(final Asn1Integer requireExplicitPolicy) {
        this.setFieldAs(PolicyConstraintsField.REQUIRE_EXPLICIT_POLICY, requireExplicitPolicy);
    }
    
    public Asn1Integer getInhibitPolicyMapping() {
        return this.getFieldAs(PolicyConstraintsField.INHIBIT_POLICY_MAPPING, Asn1Integer.class);
    }
    
    public void setInhibitPolicyMapping(final Asn1Integer inhibitPolicyMapping) {
        this.setFieldAs(PolicyConstraintsField.INHIBIT_POLICY_MAPPING, inhibitPolicyMapping);
    }
    
    static {
        PolicyConstraints.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(PolicyConstraintsField.REQUIRE_EXPLICIT_POLICY, Asn1Integer.class), new ExplicitField(PolicyConstraintsField.INHIBIT_POLICY_MAPPING, Asn1Integer.class) };
    }
    
    protected enum PolicyConstraintsField implements EnumType
    {
        REQUIRE_EXPLICIT_POLICY, 
        INHIBIT_POLICY_MAPPING;
        
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
