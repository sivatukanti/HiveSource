// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.kdc;

import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.kerberos.kerb.type.KerberosString;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.kerberos.kerb.type.ticket.Tickets;
import org.apache.kerby.kerberos.kerb.type.base.EncryptedData;
import org.apache.kerby.kerberos.kerb.type.base.HostAddresses;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.kerby.kerberos.kerb.type.KrbIntegers;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import java.util.List;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.kerberos.kerb.type.ad.AuthorizationData;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class KdcReqBody extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    private AuthorizationData authorizationData;
    
    public KdcReqBody() {
        super(KdcReqBody.fieldInfos);
    }
    
    public KerberosTime getFrom() {
        return this.getFieldAs(KdcReqBodyField.FROM, KerberosTime.class);
    }
    
    public void setFrom(final KerberosTime from) {
        this.setFieldAs(KdcReqBodyField.FROM, from);
    }
    
    public KerberosTime getTill() {
        return this.getFieldAs(KdcReqBodyField.TILL, KerberosTime.class);
    }
    
    public void setTill(final KerberosTime till) {
        this.setFieldAs(KdcReqBodyField.TILL, till);
    }
    
    public KerberosTime getRtime() {
        return this.getFieldAs(KdcReqBodyField.RTIME, KerberosTime.class);
    }
    
    public void setRtime(final KerberosTime rtime) {
        this.setFieldAs(KdcReqBodyField.RTIME, rtime);
    }
    
    public int getNonce() {
        return this.getFieldAsInt(KdcReqBodyField.NONCE);
    }
    
    public void setNonce(final int nonce) {
        this.setFieldAsInt(KdcReqBodyField.NONCE, nonce);
    }
    
    public List<EncryptionType> getEtypes() {
        final KrbIntegers values = this.getFieldAs(KdcReqBodyField.ETYPE, KrbIntegers.class);
        if (values == null) {
            return Collections.emptyList();
        }
        final List<EncryptionType> results = new ArrayList<EncryptionType>();
        for (final Integer value : values.getValues()) {
            results.add(EncryptionType.fromValue(value));
        }
        return results;
    }
    
    public void setEtypes(final List<EncryptionType> etypes) {
        final List<Integer> values = new ArrayList<Integer>();
        for (final EncryptionType etype : etypes) {
            values.add(etype.getValue());
        }
        final KrbIntegers value = new KrbIntegers(values);
        this.setFieldAs(KdcReqBodyField.ETYPE, value);
    }
    
    public HostAddresses getAddresses() {
        return this.getFieldAs(KdcReqBodyField.ADDRESSES, HostAddresses.class);
    }
    
    public void setAddresses(final HostAddresses addresses) {
        this.setFieldAs(KdcReqBodyField.ADDRESSES, addresses);
    }
    
    public EncryptedData getEncryptedAuthorizationData() {
        return this.getFieldAs(KdcReqBodyField.ENC_AUTHORIZATION_DATA, EncryptedData.class);
    }
    
    public void setEncryptedAuthorizationData(final EncryptedData encAuthorizationData) {
        this.setFieldAs(KdcReqBodyField.ENC_AUTHORIZATION_DATA, encAuthorizationData);
    }
    
    public AuthorizationData getAuthorizationData() {
        return this.authorizationData;
    }
    
    public void setAuthorizationData(final AuthorizationData authorizationData) {
        this.authorizationData = authorizationData;
    }
    
    public Tickets getAdditionalTickets() {
        return this.getFieldAs(KdcReqBodyField.ADDITIONAL_TICKETS, Tickets.class);
    }
    
    public void setAdditionalTickets(final Tickets additionalTickets) {
        this.setFieldAs(KdcReqBodyField.ADDITIONAL_TICKETS, additionalTickets);
    }
    
    public KdcOptions getKdcOptions() {
        return this.getFieldAs(KdcReqBodyField.KDC_OPTIONS, KdcOptions.class);
    }
    
    public void setKdcOptions(final KdcOptions kdcOptions) {
        this.setFieldAs(KdcReqBodyField.KDC_OPTIONS, kdcOptions);
    }
    
    public PrincipalName getSname() {
        return this.getFieldAs(KdcReqBodyField.SNAME, PrincipalName.class);
    }
    
    public void setSname(final PrincipalName sname) {
        this.setFieldAs(KdcReqBodyField.SNAME, sname);
    }
    
    public PrincipalName getCname() {
        return this.getFieldAs(KdcReqBodyField.CNAME, PrincipalName.class);
    }
    
    public void setCname(final PrincipalName cname) {
        this.setFieldAs(KdcReqBodyField.CNAME, cname);
    }
    
    public String getRealm() {
        return this.getFieldAsString(KdcReqBodyField.REALM);
    }
    
    public void setRealm(final String realm) {
        this.setFieldAs(KdcReqBodyField.REALM, new KerberosString(realm));
    }
    
    static {
        KdcReqBody.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(KdcReqBodyField.KDC_OPTIONS, KdcOptions.class), new ExplicitField(KdcReqBodyField.CNAME, PrincipalName.class), new ExplicitField(KdcReqBodyField.REALM, KerberosString.class), new ExplicitField(KdcReqBodyField.SNAME, PrincipalName.class), new ExplicitField(KdcReqBodyField.FROM, KerberosTime.class), new ExplicitField(KdcReqBodyField.TILL, KerberosTime.class), new ExplicitField(KdcReqBodyField.RTIME, KerberosTime.class), new ExplicitField(KdcReqBodyField.NONCE, Asn1Integer.class), new ExplicitField(KdcReqBodyField.ETYPE, KrbIntegers.class), new ExplicitField(KdcReqBodyField.ADDRESSES, HostAddresses.class), new ExplicitField(KdcReqBodyField.ENC_AUTHORIZATION_DATA, AuthorizationData.class), new ExplicitField(KdcReqBodyField.ADDITIONAL_TICKETS, Tickets.class) };
    }
    
    protected enum KdcReqBodyField implements EnumType
    {
        KDC_OPTIONS, 
        CNAME, 
        REALM, 
        SNAME, 
        FROM, 
        TILL, 
        RTIME, 
        NONCE, 
        ETYPE, 
        ADDRESSES, 
        ENC_AUTHORIZATION_DATA, 
        ADDITIONAL_TICKETS;
        
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
