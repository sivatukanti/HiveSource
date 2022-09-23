// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.kdc;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.kerberos.kerb.type.base.EncryptedData;
import org.apache.kerby.kerberos.kerb.type.ticket.Ticket;
import org.apache.kerby.kerberos.kerb.type.KerberosString;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.type.pa.PaData;
import org.apache.kerby.kerberos.kerb.type.base.KrbMessageType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.base.KrbMessage;

public class KdcRep extends KrbMessage
{
    static Asn1FieldInfo[] fieldInfos;
    private EncKdcRepPart encPart;
    
    public KdcRep(final KrbMessageType msgType) {
        super(msgType, KdcRep.fieldInfos);
    }
    
    public PaData getPaData() {
        return this.getFieldAs(KdcRepField.PADATA, PaData.class);
    }
    
    public void setPaData(final PaData paData) {
        this.setFieldAs(KdcRepField.PADATA, paData);
    }
    
    public PrincipalName getCname() {
        return this.getFieldAs(KdcRepField.CNAME, PrincipalName.class);
    }
    
    public void setCname(final PrincipalName sname) {
        this.setFieldAs(KdcRepField.CNAME, sname);
    }
    
    public String getCrealm() {
        return this.getFieldAsString(KdcRepField.CREALM);
    }
    
    public void setCrealm(final String realm) {
        this.setFieldAs(KdcRepField.CREALM, new KerberosString(realm));
    }
    
    public Ticket getTicket() {
        return this.getFieldAs(KdcRepField.TICKET, Ticket.class);
    }
    
    public void setTicket(final Ticket ticket) {
        this.setFieldAs(KdcRepField.TICKET, ticket);
    }
    
    public EncryptedData getEncryptedEncPart() {
        return this.getFieldAs(KdcRepField.ENC_PART, EncryptedData.class);
    }
    
    public void setEncryptedEncPart(final EncryptedData encryptedEncPart) {
        this.setFieldAs(KdcRepField.ENC_PART, encryptedEncPart);
    }
    
    public EncKdcRepPart getEncPart() {
        return this.encPart;
    }
    
    public void setEncPart(final EncKdcRepPart encPart) {
        this.encPart = encPart;
    }
    
    static {
        KdcRep.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(KdcRepField.PVNO, Asn1Integer.class), new ExplicitField(KdcRepField.MSG_TYPE, Asn1Integer.class), new ExplicitField(KdcRepField.PADATA, PaData.class), new ExplicitField(KdcRepField.CREALM, KerberosString.class), new ExplicitField(KdcRepField.CNAME, PrincipalName.class), new ExplicitField(KdcRepField.TICKET, Ticket.class), new ExplicitField(KdcRepField.ENC_PART, EncryptedData.class) };
    }
    
    protected enum KdcRepField implements EnumType
    {
        PVNO, 
        MSG_TYPE, 
        PADATA, 
        CREALM, 
        CNAME, 
        TICKET, 
        ENC_PART;
        
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
