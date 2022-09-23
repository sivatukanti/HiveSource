// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ad;

import org.apache.kerby.asn1.EnumType;
import org.slf4j.LoggerFactory;
import org.apache.kerby.asn1.Asn1Dumper;
import java.io.IOException;
import org.slf4j.Logger;

public class AuthorizationDataWrapper extends AuthorizationDataEntry
{
    private static final Logger LOG;
    private AuthorizationData authorizationData;
    
    public AuthorizationDataWrapper(final WrapperType type) {
        super(Enum.valueOf(AuthorizationType.class, type.name()));
    }
    
    public AuthorizationDataWrapper(final WrapperType type, final AuthorizationData authzData) throws IOException {
        super(Enum.valueOf(AuthorizationType.class, type.name()));
        this.authorizationData = authzData;
        if (authzData != null) {
            this.setAuthzData(authzData.encode());
        }
        else {
            this.setAuthzData(null);
        }
    }
    
    public AuthorizationData getAuthorizationData() throws IOException {
        AuthorizationData result;
        if (this.authorizationData != null) {
            result = this.authorizationData;
        }
        else {
            result = new AuthorizationData();
            result.decode(this.getAuthzData());
        }
        return result;
    }
    
    public void setAuthorizationData(final AuthorizationData authzData) throws IOException {
        this.setAuthzData(authzData.encode());
    }
    
    @Override
    public void dumpWith(final Asn1Dumper dumper, final int indents) {
        super.dumpWith(dumper, indents);
        dumper.newLine();
        try {
            this.getAuthorizationData().dumpWith(dumper, indents + 8);
        }
        catch (IOException e) {
            AuthorizationDataWrapper.LOG.error("Fail to get authorization data. " + e);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(AuthorizationDataWrapper.class);
    }
    
    public enum WrapperType implements EnumType
    {
        AD_IF_RELEVANT(AuthorizationType.AD_IF_RELEVANT.getValue()), 
        AD_MANDATORY_FOR_KDC(AuthorizationType.AD_MANDATORY_FOR_KDC.getValue());
        
        private final int value;
        
        private WrapperType(final int value) {
            this.value = value;
        }
        
        @Override
        public int getValue() {
            return this.value;
        }
        
        @Override
        public String getName() {
            return this.name();
        }
    }
}
