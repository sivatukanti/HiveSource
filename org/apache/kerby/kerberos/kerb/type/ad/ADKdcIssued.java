// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ad;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;
import org.apache.kerby.asn1.Asn1Dumper;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.kerberos.kerb.type.base.Realm;
import org.apache.kerby.kerberos.kerb.type.base.CheckSum;
import java.io.IOException;

public class ADKdcIssued extends AuthorizationDataEntry
{
    private KdcIssued myKdcIssued;
    
    public ADKdcIssued() {
        super(AuthorizationType.AD_KDC_ISSUED);
        this.myKdcIssued = new KdcIssued();
        this.myKdcIssued.outerEncodeable = this;
    }
    
    public ADKdcIssued(final byte[] encoded) throws IOException {
        this();
        this.myKdcIssued.decode(encoded);
    }
    
    public CheckSum getCheckSum() {
        return this.myKdcIssued.getCheckSum();
    }
    
    public void setCheckSum(final CheckSum chkSum) {
        this.myKdcIssued.setCheckSum(chkSum);
    }
    
    public Realm getRealm() {
        return this.myKdcIssued.getRealm();
    }
    
    public void setRealm(final Realm realm) {
        this.myKdcIssued.setRealm(realm);
    }
    
    public PrincipalName getSname() {
        return this.myKdcIssued.getSname();
    }
    
    public void setSname(final PrincipalName sName) {
        this.myKdcIssued.setSname(sName);
    }
    
    public AuthorizationData getAuthorizationData() {
        return this.myKdcIssued.getAuthzData();
    }
    
    public void setAuthzData(final AuthorizationData authzData) {
        this.myKdcIssued.setAuthzData(authzData);
    }
    
    @Override
    protected int encodingBodyLength() throws IOException {
        if (this.bodyLength == -1) {
            this.setAuthzData(this.myKdcIssued.encode());
            this.bodyLength = super.encodingBodyLength();
        }
        return this.bodyLength;
    }
    
    @Override
    public void dumpWith(final Asn1Dumper dumper, final int indents) {
        super.dumpWith(dumper, indents);
        dumper.newLine();
        this.myKdcIssued.dumpWith(dumper, indents + 8);
    }
    
    private static class KdcIssued extends KrbSequenceType
    {
        private static Asn1FieldInfo[] fieldInfos;
        
        KdcIssued() {
            super(KdcIssued.fieldInfos);
        }
        
        public CheckSum getCheckSum() {
            return this.getFieldAs(KdcIssuedField.AD_CHECKSUM, CheckSum.class);
        }
        
        public void setCheckSum(final CheckSum chkSum) {
            this.setFieldAs(KdcIssuedField.AD_CHECKSUM, chkSum);
        }
        
        public Realm getRealm() {
            return this.getFieldAs(KdcIssuedField.I_REALM, Realm.class);
        }
        
        public void setRealm(final Realm realm) {
            this.setFieldAs(KdcIssuedField.I_REALM, realm);
        }
        
        public PrincipalName getSname() {
            return this.getFieldAs(KdcIssuedField.I_SNAME, PrincipalName.class);
        }
        
        public void setSname(final PrincipalName sName) {
            this.setFieldAs(KdcIssuedField.I_SNAME, sName);
        }
        
        public AuthorizationData getAuthzData() {
            return this.getFieldAs(KdcIssuedField.ELEMENTS, AuthorizationData.class);
        }
        
        public void setAuthzData(final AuthorizationData authzData) {
            this.setFieldAs(KdcIssuedField.ELEMENTS, authzData);
        }
        
        static {
            KdcIssued.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(KdcIssuedField.AD_CHECKSUM, CheckSum.class), new ExplicitField(KdcIssuedField.I_REALM, Realm.class), new ExplicitField(KdcIssuedField.I_SNAME, PrincipalName.class), new ExplicitField(KdcIssuedField.ELEMENTS, AuthorizationData.class) };
        }
        
        enum KdcIssuedField implements EnumType
        {
            AD_CHECKSUM, 
            I_REALM, 
            I_SNAME, 
            ELEMENTS;
            
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
}
