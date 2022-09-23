// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.pa.pkinit;

import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.type.Asn1BitString;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class KdcDhKeyInfo extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public KdcDhKeyInfo() {
        super(KdcDhKeyInfo.fieldInfos);
    }
    
    public Asn1BitString getSubjectPublicKey() {
        return this.getFieldAs(KdcDhKeyInfoField.SUBJECT_PUBLIC_KEY, Asn1BitString.class);
    }
    
    public void setSubjectPublicKey(final byte[] subjectPubKey) {
        this.setFieldAs(KdcDhKeyInfoField.SUBJECT_PUBLIC_KEY, new Asn1BitString(subjectPubKey));
    }
    
    public int getNonce() {
        return this.getFieldAsInt(KdcDhKeyInfoField.NONCE);
    }
    
    public void setNonce(final int nonce) {
        this.setFieldAsInt(KdcDhKeyInfoField.NONCE, nonce);
    }
    
    public KerberosTime getDHKeyExpiration() {
        return this.getFieldAsTime(KdcDhKeyInfoField.DH_KEY_EXPIRATION);
    }
    
    public void setDHKeyExpiration(final KerberosTime time) {
        this.setFieldAs(KdcDhKeyInfoField.DH_KEY_EXPIRATION, time);
    }
    
    static {
        KdcDhKeyInfo.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(KdcDhKeyInfoField.SUBJECT_PUBLIC_KEY, Asn1BitString.class), new ExplicitField(KdcDhKeyInfoField.NONCE, Asn1Integer.class), new ExplicitField(KdcDhKeyInfoField.DH_KEY_EXPIRATION, KerberosTime.class) };
    }
    
    protected enum KdcDhKeyInfoField implements EnumType
    {
        SUBJECT_PUBLIC_KEY, 
        NONCE, 
        DH_KEY_EXPIRATION;
        
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
