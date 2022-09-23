// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ad;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;
import org.slf4j.LoggerFactory;
import org.apache.kerby.asn1.Asn1Dumper;
import java.io.IOException;
import org.slf4j.Logger;

public class ADCamMac extends AuthorizationDataEntry
{
    private static final Logger LOG;
    private CamMac myCamMac;
    
    public ADCamMac() {
        super(AuthorizationType.AD_CAMMAC);
        this.myCamMac = new CamMac();
        this.myCamMac.outerEncodeable = this;
    }
    
    public ADCamMac(final byte[] encoded) throws IOException {
        this();
        this.myCamMac.decode(encoded);
    }
    
    public AuthorizationData getAuthorizationData() {
        return this.myCamMac.getAuthorizationData();
    }
    
    public void setAuthorizationData(final AuthorizationData authzData) {
        this.myCamMac.setAuthorizationData(authzData);
    }
    
    public CamMacVerifierMac getKdcVerifier() {
        return this.myCamMac.getKdcVerifier();
    }
    
    public void setKdcVerifier(final CamMacVerifierMac kdcVerifier) {
        this.myCamMac.setKdcVerifier(kdcVerifier);
    }
    
    public CamMacVerifierMac getSvcVerifier() {
        return this.myCamMac.getSvcVerifier();
    }
    
    public void setSvcVerifier(final CamMacVerifierMac svcVerifier) {
        this.myCamMac.setSvcVerifier(svcVerifier);
    }
    
    public CamMacOtherVerifiers getOtherVerifiers() {
        return this.myCamMac.getOtherVerifiers();
    }
    
    public void setOtherVerifiers(final CamMacOtherVerifiers otherVerifiers) {
        this.myCamMac.setOtherVerifiers(otherVerifiers);
    }
    
    @Override
    protected int encodingBodyLength() throws IOException {
        if (this.bodyLength == -1) {
            this.setAuthzData(this.myCamMac.encode());
            this.bodyLength = super.encodingBodyLength();
        }
        return this.bodyLength;
    }
    
    @Override
    public void dumpWith(final Asn1Dumper dumper, final int indents) {
        try {
            this.setAuthzData(this.myCamMac.encode());
        }
        catch (IOException e) {
            ADCamMac.LOG.error("Failed to set the AD_DATA field. " + e.toString());
        }
        super.dumpWith(dumper, indents);
        dumper.newLine();
        this.myCamMac.dumpWith(dumper, indents + 8);
    }
    
    static {
        LOG = LoggerFactory.getLogger(ADCamMac.class);
    }
    
    private static class CamMac extends KrbSequenceType
    {
        private static Asn1FieldInfo[] fieldInfos;
        
        CamMac() {
            super(CamMac.fieldInfos);
        }
        
        CamMac(final byte[] authzFields) {
            super(CamMac.fieldInfos);
            super.setFieldAsOctets(AuthorizationDataEntryField.AD_DATA, authzFields);
        }
        
        CamMac(final AuthorizationData authzData) {
            super(CamMac.fieldInfos);
            this.setFieldAs(CamMacField.CAMMAC_elements, authzData);
        }
        
        public AuthorizationData getAuthorizationData() {
            return this.getFieldAs(CamMacField.CAMMAC_elements, AuthorizationData.class);
        }
        
        public void setAuthorizationData(final AuthorizationData authzData) {
            this.setFieldAs(CamMacField.CAMMAC_elements, authzData);
            this.resetBodyLength();
        }
        
        public CamMacVerifierMac getKdcVerifier() {
            return this.getFieldAs(CamMacField.CAMMAC_kdc_verifier, CamMacVerifierMac.class);
        }
        
        public void setKdcVerifier(final CamMacVerifierMac kdcVerifier) {
            this.setFieldAs(CamMacField.CAMMAC_kdc_verifier, kdcVerifier);
            this.resetBodyLength();
        }
        
        public CamMacVerifierMac getSvcVerifier() {
            return this.getFieldAs(CamMacField.CAMMAC_svc_verifier, CamMacVerifierMac.class);
        }
        
        public void setSvcVerifier(final CamMacVerifierMac svcVerifier) {
            this.setFieldAs(CamMacField.CAMMAC_svc_verifier, svcVerifier);
            this.resetBodyLength();
        }
        
        public CamMacOtherVerifiers getOtherVerifiers() {
            return this.getFieldAs(CamMacField.CAMMAC_other_verifiers, CamMacOtherVerifiers.class);
        }
        
        public void setOtherVerifiers(final CamMacOtherVerifiers svcVerifier) {
            this.setFieldAs(CamMacField.CAMMAC_other_verifiers, svcVerifier);
            this.resetBodyLength();
        }
        
        static {
            CamMac.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(CamMacField.CAMMAC_elements, AuthorizationData.class), new ExplicitField(CamMacField.CAMMAC_kdc_verifier, CamMacVerifierMac.class), new ExplicitField(CamMacField.CAMMAC_svc_verifier, CamMacVerifierMac.class), new ExplicitField(CamMacField.CAMMAC_other_verifiers, CamMacOtherVerifiers.class) };
        }
        
        protected enum CamMacField implements EnumType
        {
            CAMMAC_elements, 
            CAMMAC_kdc_verifier, 
            CAMMAC_svc_verifier, 
            CAMMAC_other_verifiers;
            
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
