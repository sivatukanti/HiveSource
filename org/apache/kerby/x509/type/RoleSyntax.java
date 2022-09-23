// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class RoleSyntax extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public RoleSyntax() {
        super(RoleSyntax.fieldInfos);
    }
    
    public GeneralNames getRoleAuthority() {
        return this.getFieldAs(RoleSyntaxField.ROLE_AUTHORITY, GeneralNames.class);
    }
    
    public void setRoleAuthority(final GeneralNames roleAuthority) {
        this.setFieldAs(RoleSyntaxField.ROLE_AUTHORITY, roleAuthority);
    }
    
    public GeneralName getRoleName() {
        return this.getFieldAs(RoleSyntaxField.ROLE_NAME, GeneralName.class);
    }
    
    public void setRoleName(final GeneralName roleName) {
        this.setFieldAs(RoleSyntaxField.ROLE_NAME, roleName);
    }
    
    static {
        RoleSyntax.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(RoleSyntaxField.ROLE_AUTHORITY, GeneralNames.class), new ExplicitField(RoleSyntaxField.ROLE_NAME, GeneralName.class) };
    }
    
    protected enum RoleSyntaxField implements EnumType
    {
        ROLE_AUTHORITY, 
        ROLE_NAME;
        
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
