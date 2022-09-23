// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.pa.pkinit;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.kerberos.kerb.type.base.Realm;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class Krb5PrincipalName extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public Krb5PrincipalName() {
        super(Krb5PrincipalName.fieldInfos);
    }
    
    public String getRelm() {
        return this.getFieldAsString(Krb5PrincipalNameField.REALM);
    }
    
    public void setRealm(final String realm) {
        this.setFieldAsString(Krb5PrincipalNameField.REALM, realm);
    }
    
    public PrincipalName getPrincipalName() {
        return this.getFieldAs(Krb5PrincipalNameField.PRINCIPAL_NAME, PrincipalName.class);
    }
    
    public void setPrincipalName(final PrincipalName principalName) {
        this.setFieldAs(Krb5PrincipalNameField.PRINCIPAL_NAME, principalName);
    }
    
    static {
        Krb5PrincipalName.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(Krb5PrincipalNameField.REALM, Realm.class), new ExplicitField(Krb5PrincipalNameField.PRINCIPAL_NAME, PrincipalName.class) };
    }
    
    protected enum Krb5PrincipalNameField implements EnumType
    {
        REALM, 
        PRINCIPAL_NAME;
        
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
