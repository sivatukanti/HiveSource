// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ad;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.kerberos.kerb.type.base.CheckSum;
import org.apache.kerby.asn1.type.Asn1Integer;
import java.math.BigInteger;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class CamMacVerifierMac extends KrbSequenceType
{
    private static Asn1FieldInfo[] fieldInfos;
    
    public CamMacVerifierMac() {
        super(CamMacVerifierMac.fieldInfos);
    }
    
    public CamMacVerifierMac(final PrincipalName identifier) {
        super(CamMacVerifierMac.fieldInfos);
        this.setFieldAs(CamMacField.CAMMAC_identifier, identifier);
    }
    
    public PrincipalName getIdentifier() {
        return this.getFieldAs(CamMacField.CAMMAC_identifier, PrincipalName.class);
    }
    
    public void setIdentifier(final PrincipalName identifier) {
        this.setFieldAs(CamMacField.CAMMAC_identifier, identifier);
    }
    
    public int getKvno() {
        return this.getFieldAs(CamMacField.CAMMAC_kvno, Asn1Integer.class).getValue().intValue();
    }
    
    public void setKvno(final int kvno) {
        this.setFieldAs(CamMacField.CAMMAC_kvno, new Asn1Integer(Integer.valueOf(kvno)));
    }
    
    public int getEnctype() {
        return this.getFieldAs(CamMacField.CAMMAC_enctype, Asn1Integer.class).getValue().intValue();
    }
    
    public void setEnctype(final int encType) {
        this.setFieldAs(CamMacField.CAMMAC_enctype, new Asn1Integer(Integer.valueOf(encType)));
    }
    
    public CheckSum getMac() {
        return this.getFieldAs(CamMacField.CAMMAC_mac, CheckSum.class);
    }
    
    public void setMac(final CheckSum mac) {
        this.setFieldAs(CamMacField.CAMMAC_mac, mac);
    }
    
    static {
        CamMacVerifierMac.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(CamMacField.CAMMAC_identifier, PrincipalName.class), new ExplicitField(CamMacField.CAMMAC_kvno, Asn1Integer.class), new ExplicitField(CamMacField.CAMMAC_enctype, Asn1Integer.class), new ExplicitField(CamMacField.CAMMAC_mac, CheckSum.class) };
    }
    
    protected enum CamMacField implements EnumType
    {
        CAMMAC_identifier, 
        CAMMAC_kvno, 
        CAMMAC_enctype, 
        CAMMAC_mac;
        
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
