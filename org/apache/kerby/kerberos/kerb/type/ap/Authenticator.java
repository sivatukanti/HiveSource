// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ap;

import org.apache.kerby.kerberos.kerb.type.KerberosString;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.kerberos.kerb.type.ad.AuthorizationData;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.kerberos.kerb.type.base.CheckSum;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbAppSequenceType;

public class Authenticator extends KrbAppSequenceType
{
    public static final int TAG = 2;
    static Asn1FieldInfo[] fieldInfos;
    
    public Authenticator() {
        super(2, Authenticator.fieldInfos);
        this.setAuthenticatorVno(5);
    }
    
    public int getAuthenticatorVno() {
        return this.getFieldAsInt(AuthenticatorField.AUTHENTICATOR_VNO);
    }
    
    public void setAuthenticatorVno(final int authenticatorVno) {
        this.setFieldAsInt(AuthenticatorField.AUTHENTICATOR_VNO, authenticatorVno);
    }
    
    public String getCrealm() {
        return this.getFieldAsString(AuthenticatorField.CREALM);
    }
    
    public void setCrealm(final String crealm) {
        this.setFieldAsString(AuthenticatorField.CREALM, crealm);
    }
    
    public PrincipalName getCname() {
        return this.getFieldAs(AuthenticatorField.CNAME, PrincipalName.class);
    }
    
    public void setCname(final PrincipalName cname) {
        this.setFieldAs(AuthenticatorField.CNAME, cname);
    }
    
    public CheckSum getCksum() {
        return this.getFieldAs(AuthenticatorField.CKSUM, CheckSum.class);
    }
    
    public void setCksum(final CheckSum cksum) {
        this.setFieldAs(AuthenticatorField.CKSUM, cksum);
    }
    
    public int getCusec() {
        return this.getFieldAsInt(AuthenticatorField.CUSEC);
    }
    
    public void setCusec(final int cusec) {
        this.setFieldAsInt(AuthenticatorField.CUSEC, cusec);
    }
    
    public KerberosTime getCtime() {
        return this.getFieldAsTime(AuthenticatorField.CTIME);
    }
    
    public void setCtime(final KerberosTime ctime) {
        this.setFieldAs(AuthenticatorField.CTIME, ctime);
    }
    
    public EncryptionKey getSubKey() {
        return this.getFieldAs(AuthenticatorField.SUBKEY, EncryptionKey.class);
    }
    
    public void setSubKey(final EncryptionKey subKey) {
        this.setFieldAs(AuthenticatorField.SUBKEY, subKey);
    }
    
    public int getSeqNumber() {
        return this.getFieldAsInt(AuthenticatorField.SEQ_NUMBER);
    }
    
    public void setSeqNumber(final Integer seqNumber) {
        this.setFieldAsInt(AuthenticatorField.SEQ_NUMBER, seqNumber);
    }
    
    public AuthorizationData getAuthorizationData() {
        return this.getFieldAs(AuthenticatorField.AUTHORIZATION_DATA, AuthorizationData.class);
    }
    
    public void setAuthorizationData(final AuthorizationData authorizationData) {
        this.setFieldAs(AuthenticatorField.AUTHORIZATION_DATA, authorizationData);
    }
    
    static {
        Authenticator.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(AuthenticatorField.AUTHENTICATOR_VNO, Asn1Integer.class), new ExplicitField(AuthenticatorField.CREALM, KerberosString.class), new ExplicitField(AuthenticatorField.CNAME, PrincipalName.class), new ExplicitField(AuthenticatorField.CKSUM, CheckSum.class), new ExplicitField(AuthenticatorField.CUSEC, Asn1Integer.class), new ExplicitField(AuthenticatorField.CTIME, KerberosTime.class), new ExplicitField(AuthenticatorField.SUBKEY, EncryptionKey.class), new ExplicitField(AuthenticatorField.SEQ_NUMBER, Asn1Integer.class), new ExplicitField(AuthenticatorField.AUTHORIZATION_DATA, AuthorizationData.class) };
    }
    
    protected enum AuthenticatorField implements EnumType
    {
        AUTHENTICATOR_VNO, 
        CREALM, 
        CNAME, 
        CKSUM, 
        CUSEC, 
        CTIME, 
        SUBKEY, 
        SEQ_NUMBER, 
        AUTHORIZATION_DATA;
        
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
