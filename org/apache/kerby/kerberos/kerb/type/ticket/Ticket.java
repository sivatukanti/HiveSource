// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ticket;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.kerberos.kerb.type.base.EncryptedData;
import org.apache.kerby.kerberos.kerb.type.KerberosString;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbAppSequenceType;

public class Ticket extends KrbAppSequenceType
{
    public static final int TKT_KVNO = 5;
    public static final int TAG = 1;
    static Asn1FieldInfo[] fieldInfos;
    private EncTicketPart encPart;
    
    public Ticket() {
        super(1, Ticket.fieldInfos);
        this.setTktKvno(5);
    }
    
    public int getTktvno() {
        return this.getFieldAsInt(TicketField.TKT_VNO);
    }
    
    public void setTktKvno(final int kvno) {
        this.setFieldAsInt(TicketField.TKT_VNO, kvno);
    }
    
    public PrincipalName getSname() {
        return this.getFieldAs(TicketField.SNAME, PrincipalName.class);
    }
    
    public void setSname(final PrincipalName sname) {
        this.setFieldAs(TicketField.SNAME, sname);
    }
    
    public String getRealm() {
        return this.getFieldAsString(TicketField.REALM);
    }
    
    public void setRealm(final String realm) {
        this.setFieldAs(TicketField.REALM, new KerberosString(realm));
    }
    
    public EncryptedData getEncryptedEncPart() {
        return this.getFieldAs(TicketField.ENC_PART, EncryptedData.class);
    }
    
    public void setEncryptedEncPart(final EncryptedData encryptedEncPart) {
        this.setFieldAs(TicketField.ENC_PART, encryptedEncPart);
    }
    
    public EncTicketPart getEncPart() {
        return this.encPart;
    }
    
    public void setEncPart(final EncTicketPart encPart) {
        this.encPart = encPart;
    }
    
    static {
        Ticket.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(TicketField.TKT_VNO, 0, Asn1Integer.class), new ExplicitField(TicketField.REALM, 1, KerberosString.class), new ExplicitField(TicketField.SNAME, 2, PrincipalName.class), new ExplicitField(TicketField.ENC_PART, 3, EncryptedData.class) };
    }
    
    protected enum TicketField implements EnumType
    {
        TKT_VNO, 
        REALM, 
        SNAME, 
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
