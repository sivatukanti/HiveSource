// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ticket;

import org.apache.kerby.kerberos.kerb.type.KerberosString;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.kerberos.kerb.type.ad.AuthorizationData;
import org.apache.kerby.kerberos.kerb.type.base.HostAddresses;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.kerberos.kerb.type.base.TransitedEncoding;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbAppSequenceType;

public class EncTicketPart extends KrbAppSequenceType
{
    public static final int TAG = 3;
    static Asn1FieldInfo[] fieldInfos;
    
    public EncTicketPart() {
        super(3, EncTicketPart.fieldInfos);
    }
    
    public TicketFlags getFlags() {
        return this.getFieldAs(EncTicketPartField.FLAGS, TicketFlags.class);
    }
    
    public void setFlags(final TicketFlags flags) {
        this.setFieldAs(EncTicketPartField.FLAGS, flags);
    }
    
    public EncryptionKey getKey() {
        return this.getFieldAs(EncTicketPartField.KEY, EncryptionKey.class);
    }
    
    public void setKey(final EncryptionKey key) {
        this.setFieldAs(EncTicketPartField.KEY, key);
    }
    
    public String getCrealm() {
        return this.getFieldAsString(EncTicketPartField.CREALM);
    }
    
    public void setCrealm(final String crealm) {
        this.setFieldAsString(EncTicketPartField.CREALM, crealm);
    }
    
    public PrincipalName getCname() {
        return this.getFieldAs(EncTicketPartField.CNAME, PrincipalName.class);
    }
    
    public void setCname(final PrincipalName cname) {
        this.setFieldAs(EncTicketPartField.CNAME, cname);
    }
    
    public TransitedEncoding getTransited() {
        return this.getFieldAs(EncTicketPartField.TRANSITED, TransitedEncoding.class);
    }
    
    public void setTransited(final TransitedEncoding transited) {
        this.setFieldAs(EncTicketPartField.TRANSITED, transited);
    }
    
    public KerberosTime getAuthTime() {
        return this.getFieldAs(EncTicketPartField.AUTHTIME, KerberosTime.class);
    }
    
    public void setAuthTime(final KerberosTime authTime) {
        this.setFieldAs(EncTicketPartField.AUTHTIME, authTime);
    }
    
    public KerberosTime getStartTime() {
        return this.getFieldAs(EncTicketPartField.STARTTIME, KerberosTime.class);
    }
    
    public void setStartTime(final KerberosTime startTime) {
        this.setFieldAs(EncTicketPartField.STARTTIME, startTime);
    }
    
    public KerberosTime getEndTime() {
        return this.getFieldAs(EncTicketPartField.ENDTIME, KerberosTime.class);
    }
    
    public void setEndTime(final KerberosTime endTime) {
        this.setFieldAs(EncTicketPartField.ENDTIME, endTime);
    }
    
    public KerberosTime getRenewtill() {
        return this.getFieldAs(EncTicketPartField.RENEW_TILL, KerberosTime.class);
    }
    
    public void setRenewtill(final KerberosTime renewtill) {
        this.setFieldAs(EncTicketPartField.RENEW_TILL, renewtill);
    }
    
    public HostAddresses getClientAddresses() {
        return this.getFieldAs(EncTicketPartField.CADDR, HostAddresses.class);
    }
    
    public void setClientAddresses(final HostAddresses clientAddresses) {
        this.setFieldAs(EncTicketPartField.CADDR, clientAddresses);
    }
    
    public AuthorizationData getAuthorizationData() {
        return this.getFieldAs(EncTicketPartField.AUTHORIZATION_DATA, AuthorizationData.class);
    }
    
    public void setAuthorizationData(final AuthorizationData authorizationData) {
        this.setFieldAs(EncTicketPartField.AUTHORIZATION_DATA, authorizationData);
    }
    
    static {
        EncTicketPart.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(EncTicketPartField.FLAGS, TicketFlags.class), new ExplicitField(EncTicketPartField.KEY, EncryptionKey.class), new ExplicitField(EncTicketPartField.CREALM, KerberosString.class), new ExplicitField(EncTicketPartField.CNAME, PrincipalName.class), new ExplicitField(EncTicketPartField.TRANSITED, TransitedEncoding.class), new ExplicitField(EncTicketPartField.AUTHTIME, KerberosTime.class), new ExplicitField(EncTicketPartField.STARTTIME, KerberosTime.class), new ExplicitField(EncTicketPartField.ENDTIME, KerberosTime.class), new ExplicitField(EncTicketPartField.RENEW_TILL, KerberosTime.class), new ExplicitField(EncTicketPartField.CADDR, HostAddresses.class), new ExplicitField(EncTicketPartField.AUTHORIZATION_DATA, AuthorizationData.class) };
    }
    
    protected enum EncTicketPartField implements EnumType
    {
        FLAGS, 
        KEY, 
        CREALM, 
        CNAME, 
        TRANSITED, 
        AUTHTIME, 
        STARTTIME, 
        ENDTIME, 
        RENEW_TILL, 
        CADDR, 
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
