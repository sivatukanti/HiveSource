// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class NameConstraints extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public NameConstraints() {
        super(NameConstraints.fieldInfos);
    }
    
    public GeneralSubtrees getPermittedSubtrees() {
        return this.getFieldAs(NameConstraintsField.PERMITTED_SUBTREES, GeneralSubtrees.class);
    }
    
    public void setPermittedSubtrees(final GeneralSubtrees permittedSubtrees) {
        this.setFieldAs(NameConstraintsField.PERMITTED_SUBTREES, permittedSubtrees);
    }
    
    public GeneralSubtrees getExcludedSubtrees() {
        return this.getFieldAs(NameConstraintsField.EXCLUDED_SUBTREES, GeneralSubtrees.class);
    }
    
    public void setExcludedSubtrees(final GeneralSubtrees excludedSubtrees) {
        this.setFieldAs(NameConstraintsField.EXCLUDED_SUBTREES, excludedSubtrees);
    }
    
    static {
        NameConstraints.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(NameConstraintsField.PERMITTED_SUBTREES, GeneralSubtrees.class), new ExplicitField(NameConstraintsField.EXCLUDED_SUBTREES, GeneralSubtrees.class) };
    }
    
    protected enum NameConstraintsField implements EnumType
    {
        PERMITTED_SUBTREES, 
        EXCLUDED_SUBTREES;
        
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
