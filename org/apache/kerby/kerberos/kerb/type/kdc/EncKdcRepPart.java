// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.kdc;

import org.apache.kerby.kerberos.kerb.type.KerberosString;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.kerberos.kerb.type.base.HostAddresses;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.kerberos.kerb.type.ticket.TicketFlags;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.kerberos.kerb.type.base.LastReq;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbAppSequenceType;

public abstract class EncKdcRepPart extends KrbAppSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public EncKdcRepPart(final int tagNo) {
        super(tagNo, EncKdcRepPart.fieldInfos);
    }
    
    public EncryptionKey getKey() {
        return this.getFieldAs(EncKdcRepPartField.KEY, EncryptionKey.class);
    }
    
    public void setKey(final EncryptionKey key) {
        this.setFieldAs(EncKdcRepPartField.KEY, key);
    }
    
    public LastReq getLastReq() {
        return this.getFieldAs(EncKdcRepPartField.LAST_REQ, LastReq.class);
    }
    
    public void setLastReq(final LastReq lastReq) {
        this.setFieldAs(EncKdcRepPartField.LAST_REQ, lastReq);
    }
    
    public int getNonce() {
        return this.getFieldAsInt(EncKdcRepPartField.NONCE);
    }
    
    public void setNonce(final int nonce) {
        this.setFieldAsInt(EncKdcRepPartField.NONCE, nonce);
    }
    
    public KerberosTime getKeyExpiration() {
        return this.getFieldAsTime(EncKdcRepPartField.KEY_EXPIRATION);
    }
    
    public void setKeyExpiration(final KerberosTime keyExpiration) {
        this.setFieldAs(EncKdcRepPartField.KEY_EXPIRATION, keyExpiration);
    }
    
    public TicketFlags getFlags() {
        return this.getFieldAs(EncKdcRepPartField.FLAGS, TicketFlags.class);
    }
    
    public void setFlags(final TicketFlags flags) {
        this.setFieldAs(EncKdcRepPartField.FLAGS, flags);
    }
    
    public KerberosTime getAuthTime() {
        return this.getFieldAsTime(EncKdcRepPartField.AUTHTIME);
    }
    
    public void setAuthTime(final KerberosTime authTime) {
        this.setFieldAs(EncKdcRepPartField.AUTHTIME, authTime);
    }
    
    public KerberosTime getStartTime() {
        return this.getFieldAsTime(EncKdcRepPartField.STARTTIME);
    }
    
    public void setStartTime(final KerberosTime startTime) {
        this.setFieldAs(EncKdcRepPartField.STARTTIME, startTime);
    }
    
    public KerberosTime getEndTime() {
        return this.getFieldAsTime(EncKdcRepPartField.ENDTIME);
    }
    
    public void setEndTime(final KerberosTime endTime) {
        this.setFieldAs(EncKdcRepPartField.ENDTIME, endTime);
    }
    
    public KerberosTime getRenewTill() {
        return this.getFieldAsTime(EncKdcRepPartField.RENEW_TILL);
    }
    
    public void setRenewTill(final KerberosTime renewTill) {
        this.setFieldAs(EncKdcRepPartField.RENEW_TILL, renewTill);
    }
    
    public String getSrealm() {
        return this.getFieldAsString(EncKdcRepPartField.SREALM);
    }
    
    public void setSrealm(final String srealm) {
        this.setFieldAsString(EncKdcRepPartField.SREALM, srealm);
    }
    
    public PrincipalName getSname() {
        return this.getFieldAs(EncKdcRepPartField.SNAME, PrincipalName.class);
    }
    
    public void setSname(final PrincipalName sname) {
        this.setFieldAs(EncKdcRepPartField.SNAME, sname);
    }
    
    public HostAddresses getCaddr() {
        return this.getFieldAs(EncKdcRepPartField.CADDR, HostAddresses.class);
    }
    
    public void setCaddr(final HostAddresses caddr) {
        this.setFieldAs(EncKdcRepPartField.CADDR, caddr);
    }
    
    static {
        EncKdcRepPart.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(EncKdcRepPartField.KEY, EncryptionKey.class), new ExplicitField(EncKdcRepPartField.LAST_REQ, LastReq.class), new ExplicitField(EncKdcRepPartField.NONCE, Asn1Integer.class), new ExplicitField(EncKdcRepPartField.KEY_EXPIRATION, KerberosTime.class), new ExplicitField(EncKdcRepPartField.FLAGS, TicketFlags.class), new ExplicitField(EncKdcRepPartField.AUTHTIME, KerberosTime.class), new ExplicitField(EncKdcRepPartField.STARTTIME, KerberosTime.class), new ExplicitField(EncKdcRepPartField.ENDTIME, KerberosTime.class), new ExplicitField(EncKdcRepPartField.RENEW_TILL, KerberosTime.class), new ExplicitField(EncKdcRepPartField.SREALM, KerberosString.class), new ExplicitField(EncKdcRepPartField.SNAME, PrincipalName.class), new ExplicitField(EncKdcRepPartField.CADDR, HostAddresses.class) };
    }
    
    protected enum EncKdcRepPartField implements EnumType
    {
        KEY, 
        LAST_REQ, 
        NONCE, 
        KEY_EXPIRATION, 
        FLAGS, 
        AUTHTIME, 
        STARTTIME, 
        ENDTIME, 
        RENEW_TILL, 
        SREALM, 
        SNAME, 
        CADDR;
        
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
