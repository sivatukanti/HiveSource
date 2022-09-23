// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.common;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.type.base.NameType;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;

public class KrbUtil
{
    public static final String ANONYMOUS_PRINCIPAL = "ANONYMOUS@WELLKNOWN:ANONYMOUS";
    public static final String KRB5_WELLKNOWN_NAMESTR = "WELLKNOWN";
    public static final String KRB5_ANONYMOUS_PRINCSTR = "ANONYMOUS";
    public static final String KRB5_ANONYMOUS_REALMSTR = "WELLKNOWN:ANONYMOUS";
    
    public static PrincipalName makeTgsPrincipal(final String realm) {
        final String nameString = "krbtgt/" + realm + "@" + realm;
        return new PrincipalName(nameString, NameType.NT_SRV_INST);
    }
    
    public static PrincipalName makeKadminPrincipal(final String realm) {
        final String nameString = "kadmin/" + realm + "@" + realm;
        return new PrincipalName(nameString, NameType.NT_PRINCIPAL);
    }
    
    public static PrincipalName makeKadminPrincipal(final String principal, final String realm) {
        final String nameString = principal + "@" + realm;
        return new PrincipalName(nameString, NameType.NT_PRINCIPAL);
    }
    
    public static boolean pricipalCompareIgnoreRealm(final PrincipalName princ1, final PrincipalName princ2) throws KrbException {
        if (princ1 != null && princ2 != null) {
            princ1.setRealm(null);
            princ2.setRealm(null);
            return princ1.getName().equals(princ2.getName());
        }
        throw new KrbException("principal can't be null.");
    }
    
    public static PrincipalName makeAnonymousPrincipal() {
        final PrincipalName principalName = new PrincipalName("WELLKNOWN/ANONYMOUS");
        principalName.setRealm("WELLKNOWN:ANONYMOUS");
        principalName.setNameType(NameType.NT_WELLKNOWN);
        return principalName;
    }
}
