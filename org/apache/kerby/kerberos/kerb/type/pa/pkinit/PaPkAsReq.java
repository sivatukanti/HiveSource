// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.pa.pkinit;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.ImplicitField;
import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class PaPkAsReq extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public PaPkAsReq() {
        super(PaPkAsReq.fieldInfos);
    }
    
    public byte[] getSignedAuthPack() {
        return this.getFieldAsOctets(PaPkAsReqField.SIGNED_AUTH_PACK);
    }
    
    public void setSignedAuthPack(final byte[] signedAuthPack) {
        this.setFieldAsOctets(PaPkAsReqField.SIGNED_AUTH_PACK, signedAuthPack);
    }
    
    public TrustedCertifiers getTrustedCertifiers() {
        return this.getFieldAs(PaPkAsReqField.TRUSTED_CERTIFIERS, TrustedCertifiers.class);
    }
    
    public void setTrustedCertifiers(final TrustedCertifiers trustedCertifiers) {
        this.setFieldAs(PaPkAsReqField.TRUSTED_CERTIFIERS, trustedCertifiers);
    }
    
    public byte[] getKdcPkId() {
        return this.getFieldAsOctets(PaPkAsReqField.KDC_PKID);
    }
    
    public void setKdcPkId(final byte[] kdcPkId) {
        this.setFieldAsOctets(PaPkAsReqField.KDC_PKID, kdcPkId);
    }
    
    static {
        PaPkAsReq.fieldInfos = new Asn1FieldInfo[] { new ImplicitField(PaPkAsReqField.SIGNED_AUTH_PACK, Asn1OctetString.class), new ExplicitField(PaPkAsReqField.TRUSTED_CERTIFIERS, TrustedCertifiers.class), new ImplicitField(PaPkAsReqField.KDC_PKID, Asn1OctetString.class) };
    }
    
    protected enum PaPkAsReqField implements EnumType
    {
        SIGNED_AUTH_PACK, 
        TRUSTED_CERTIFIERS, 
        KDC_PKID;
        
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
