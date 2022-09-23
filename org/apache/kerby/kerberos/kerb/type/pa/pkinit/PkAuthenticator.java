// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.pa.pkinit;

import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class PkAuthenticator extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public PkAuthenticator() {
        super(PkAuthenticator.fieldInfos);
    }
    
    public int getCusec() {
        return this.getFieldAsInt(PkAuthenticatorField.CUSEC);
    }
    
    public void setCusec(final int cusec) {
        this.setFieldAsInt(PkAuthenticatorField.CUSEC, cusec);
    }
    
    public KerberosTime getCtime() {
        return this.getFieldAsTime(PkAuthenticatorField.CTIME);
    }
    
    public void setCtime(final KerberosTime ctime) {
        this.setFieldAs(PkAuthenticatorField.CTIME, ctime);
    }
    
    public int getNonce() {
        return this.getFieldAsInt(PkAuthenticatorField.NONCE);
    }
    
    public void setNonce(final int nonce) {
        this.setFieldAsInt(PkAuthenticatorField.NONCE, nonce);
    }
    
    public byte[] getPaChecksum() {
        return this.getFieldAsOctets(PkAuthenticatorField.PA_CHECKSUM);
    }
    
    public void setPaChecksum(final byte[] paChecksum) {
        this.setFieldAsOctets(PkAuthenticatorField.PA_CHECKSUM, paChecksum);
    }
    
    static {
        PkAuthenticator.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(PkAuthenticatorField.CUSEC, Asn1Integer.class), new ExplicitField(PkAuthenticatorField.CTIME, KerberosTime.class), new ExplicitField(PkAuthenticatorField.NONCE, Asn1Integer.class), new ExplicitField(PkAuthenticatorField.PA_CHECKSUM, Asn1OctetString.class) };
    }
    
    protected enum PkAuthenticatorField implements EnumType
    {
        CUSEC, 
        CTIME, 
        NONCE, 
        PA_CHECKSUM;
        
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
