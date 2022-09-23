// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.base;

import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.kerberos.kerb.type.KerberosString;
import org.apache.kerby.kerberos.kerb.KrbErrorCode;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.asn1.Asn1FieldInfo;

public class KrbError extends KrbMessage
{
    static Asn1FieldInfo[] fieldInfos;
    
    public KrbError() {
        super(KrbMessageType.KRB_ERROR, KrbError.fieldInfos);
    }
    
    public KerberosTime getCtime() {
        return this.getFieldAs(KrbErrorField.CTIME, KerberosTime.class);
    }
    
    public void setCtime(final KerberosTime ctime) {
        this.setFieldAs(KrbErrorField.CTIME, ctime);
    }
    
    public int getCusec() {
        return this.getFieldAsInt(KrbErrorField.CUSEC);
    }
    
    public void setCusec(final int cusec) {
        this.setFieldAsInt(KrbErrorField.CUSEC, cusec);
    }
    
    public KerberosTime getStime() {
        return this.getFieldAs(KrbErrorField.STIME, KerberosTime.class);
    }
    
    public void setStime(final KerberosTime stime) {
        this.setFieldAs(KrbErrorField.STIME, stime);
    }
    
    public int getSusec() {
        return this.getFieldAsInt(KrbErrorField.SUSEC);
    }
    
    public void setSusec(final int susec) {
        this.setFieldAsInt(KrbErrorField.SUSEC, susec);
    }
    
    public KrbErrorCode getErrorCode() {
        return KrbErrorCode.fromValue(this.getFieldAsInt(KrbErrorField.ERROR_CODE));
    }
    
    public void setErrorCode(final KrbErrorCode errorCode) {
        this.setFieldAsInt(KrbErrorField.ERROR_CODE, errorCode.getValue());
    }
    
    public String getCrealm() {
        return this.getFieldAsString(KrbErrorField.CREALM);
    }
    
    public void setCrealm(final String realm) {
        this.setFieldAs(KrbErrorField.CREALM, new Realm(realm));
    }
    
    public PrincipalName getCname() {
        return this.getFieldAs(KrbErrorField.CNAME, PrincipalName.class);
    }
    
    public void setCname(final PrincipalName cname) {
        this.setFieldAs(KrbErrorField.CNAME, cname);
    }
    
    public PrincipalName getSname() {
        return this.getFieldAs(KrbErrorField.SNAME, PrincipalName.class);
    }
    
    public void setSname(final PrincipalName sname) {
        this.setFieldAs(KrbErrorField.SNAME, sname);
    }
    
    public String getRealm() {
        return this.getFieldAsString(KrbErrorField.REALM);
    }
    
    public void setRealm(final String realm) {
        this.setFieldAs(KrbErrorField.REALM, new Realm(realm));
    }
    
    public String getEtext() {
        return this.getFieldAsString(KrbErrorField.ETEXT);
    }
    
    public void setEtext(final String text) {
        this.setFieldAs(KrbErrorField.ETEXT, new KerberosString(text));
    }
    
    public byte[] getEdata() {
        return this.getFieldAsOctetBytes(KrbErrorField.EDATA);
    }
    
    public void setEdata(final byte[] edata) {
        this.setFieldAsOctetBytes(KrbErrorField.EDATA, edata);
    }
    
    static {
        KrbError.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(KrbErrorField.PVNO, Asn1Integer.class), new ExplicitField(KrbErrorField.MSG_TYPE, Asn1Integer.class), new ExplicitField(KrbErrorField.CTIME, KerberosTime.class), new ExplicitField(KrbErrorField.CUSEC, Asn1Integer.class), new ExplicitField(KrbErrorField.STIME, KerberosTime.class), new ExplicitField(KrbErrorField.SUSEC, Asn1Integer.class), new ExplicitField(KrbErrorField.ERROR_CODE, Asn1Integer.class), new ExplicitField(KrbErrorField.CREALM, Realm.class), new ExplicitField(KrbErrorField.CNAME, PrincipalName.class), new ExplicitField(KrbErrorField.REALM, Realm.class), new ExplicitField(KrbErrorField.SNAME, PrincipalName.class), new ExplicitField(KrbErrorField.ETEXT, KerberosString.class), new ExplicitField(KrbErrorField.EDATA, Asn1OctetString.class) };
    }
    
    protected enum KrbErrorField implements EnumType
    {
        PVNO, 
        MSG_TYPE, 
        CTIME, 
        CUSEC, 
        STIME, 
        SUSEC, 
        ERROR_CODE, 
        CREALM, 
        CNAME, 
        REALM, 
        SNAME, 
        ETEXT, 
        EDATA;
        
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
