// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ap;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.kerberos.kerb.type.base.EncryptedData;
import org.apache.kerby.kerberos.kerb.type.ticket.Ticket;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.type.base.KrbMessageType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.base.KrbMessage;

public class ApReq extends KrbMessage
{
    static Asn1FieldInfo[] fieldInfos;
    private Authenticator authenticator;
    
    public ApReq() {
        super(KrbMessageType.AP_REQ, ApReq.fieldInfos);
    }
    
    public ApOptions getApOptions() {
        return this.getFieldAs(ApReqField.AP_OPTIONS, ApOptions.class);
    }
    
    public void setApOptions(final ApOptions apOptions) {
        this.setFieldAs(ApReqField.AP_OPTIONS, apOptions);
    }
    
    public Ticket getTicket() {
        return this.getFieldAs(ApReqField.TICKET, Ticket.class);
    }
    
    public void setTicket(final Ticket ticket) {
        this.setFieldAs(ApReqField.TICKET, ticket);
    }
    
    public Authenticator getAuthenticator() {
        return this.authenticator;
    }
    
    public void setAuthenticator(final Authenticator authenticator) {
        this.authenticator = authenticator;
    }
    
    public EncryptedData getEncryptedAuthenticator() {
        return this.getFieldAs(ApReqField.AUTHENTICATOR, EncryptedData.class);
    }
    
    public void setEncryptedAuthenticator(final EncryptedData encryptedAuthenticator) {
        this.setFieldAs(ApReqField.AUTHENTICATOR, encryptedAuthenticator);
    }
    
    static {
        ApReq.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(ApReqField.PVNO, Asn1Integer.class), new ExplicitField(ApReqField.MSG_TYPE, Asn1Integer.class), new ExplicitField(ApReqField.AP_OPTIONS, ApOptions.class), new ExplicitField(ApReqField.TICKET, Ticket.class), new ExplicitField(ApReqField.AUTHENTICATOR, EncryptedData.class) };
    }
    
    protected enum ApReqField implements EnumType
    {
        PVNO, 
        MSG_TYPE, 
        AP_OPTIONS, 
        TICKET, 
        AUTHENTICATOR;
        
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
