// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.base;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Integer;
import java.util.Arrays;
import org.apache.kerby.asn1.type.Asn1Type;
import java.util.Collections;
import org.apache.kerby.kerberos.kerb.type.KerberosStrings;
import org.apache.kerby.asn1.EnumType;
import java.util.Iterator;
import java.util.List;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class PrincipalName extends KrbSequenceType
{
    private static Asn1FieldInfo[] fieldInfos;
    private String realm;
    
    public PrincipalName() {
        super(PrincipalName.fieldInfos);
    }
    
    public PrincipalName(final String nameString) {
        super(PrincipalName.fieldInfos);
        this.setNameType(NameType.NT_PRINCIPAL);
        this.fromNameString(nameString);
    }
    
    public PrincipalName(final String nameString, final NameType type) {
        super(PrincipalName.fieldInfos);
        this.fromNameString(nameString);
        this.setNameType(type);
    }
    
    public PrincipalName(final List<String> nameStrings, final NameType nameType) {
        super(PrincipalName.fieldInfos);
        this.setNameStrings(nameStrings);
        this.setNameType(nameType);
    }
    
    public static String extractRealm(final String principal) {
        final int pos = principal.indexOf(64);
        if (pos > 0) {
            return principal.substring(pos + 1);
        }
        throw new IllegalArgumentException("Not a valid principal, missing realm name");
    }
    
    public static String extractName(final String principal) {
        final int pos = principal.indexOf(64);
        if (pos < 0) {
            return principal;
        }
        return principal.substring(0, pos);
    }
    
    public static String makeSalt(final PrincipalName principalName) {
        final StringBuilder salt = new StringBuilder();
        if (principalName.getRealm() != null) {
            salt.append(principalName.getRealm());
        }
        final List<String> nameStrings = principalName.getNameStrings();
        for (final String ns : nameStrings) {
            salt.append(ns);
        }
        return salt.toString();
    }
    
    public NameType getNameType() {
        final Integer value = this.getFieldAsInteger(PrincipalNameField.NAME_TYPE);
        return NameType.fromValue(value);
    }
    
    public void setNameType(final NameType nameType) {
        this.setFieldAsInt(PrincipalNameField.NAME_TYPE, nameType.getValue());
    }
    
    public List<String> getNameStrings() {
        final KerberosStrings krbStrings = this.getFieldAs(PrincipalNameField.NAME_STRING, KerberosStrings.class);
        if (krbStrings != null) {
            return krbStrings.getAsStrings();
        }
        return Collections.emptyList();
    }
    
    public void setNameStrings(final List<String> nameStrings) {
        this.setFieldAs(PrincipalNameField.NAME_STRING, new KerberosStrings(nameStrings));
    }
    
    public String getRealm() {
        return this.realm;
    }
    
    public void setRealm(final String realm) {
        this.realm = realm;
    }
    
    public String getName() {
        return this.makeSingleName();
    }
    
    private String makeSingleName() {
        final List<String> names = this.getNameStrings();
        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (final String name : names) {
            if (isFirst) {
                isFirst = false;
            }
            else {
                sb.append('/');
            }
            sb.append(name);
        }
        if (this.realm != null && !this.realm.isEmpty()) {
            sb.append('@');
            sb.append(this.realm);
        }
        return sb.toString();
    }
    
    private void fromNameString(final String nameString) {
        if (nameString == null) {
            return;
        }
        final int realmPos = nameString.indexOf(64);
        String nameParts;
        if (realmPos != -1) {
            nameParts = nameString.substring(0, realmPos);
            this.realm = nameString.substring(realmPos + 1);
        }
        else {
            nameParts = nameString;
        }
        final String[] parts = nameParts.split("\\/");
        final List<String> nameStrings = Arrays.asList(parts);
        this.setNameStrings(nameStrings);
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PrincipalName)) {
            return false;
        }
        final PrincipalName otherPrincipal = (PrincipalName)other;
        return this.getNameType() == ((PrincipalName)other).getNameType() && this.getName().equals(otherPrincipal.getName());
    }
    
    @Override
    public String toString() {
        return this.getName();
    }
    
    static {
        PrincipalName.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(PrincipalNameField.NAME_TYPE, Asn1Integer.class), new ExplicitField(PrincipalNameField.NAME_STRING, KerberosStrings.class) };
    }
    
    protected enum PrincipalNameField implements EnumType
    {
        NAME_TYPE, 
        NAME_STRING;
        
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
