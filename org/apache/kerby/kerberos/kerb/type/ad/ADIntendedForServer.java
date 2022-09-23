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
import java.io.IOException;

public class ADIntendedForServer extends AuthorizationDataEntry
{
    private IntForSrvr myIntForSrvr;
    
    public ADIntendedForServer() {
        super(AuthorizationType.AD_INTENDED_FOR_SERVER);
        this.myIntForSrvr = new IntForSrvr();
        this.myIntForSrvr.outerEncodeable = this;
    }
    
    public ADIntendedForServer(final byte[] encoded) throws IOException {
        this();
        this.myIntForSrvr.decode(encoded);
    }
    
    public ADIntendedForServer(final PrincipalList principals) throws IOException {
        this();
        this.myIntForSrvr.setIntendedServer(principals);
    }
    
    public PrincipalList getIntendedServer() {
        return this.myIntForSrvr.getIntendedServer();
    }
    
    public void setIntendedServer(final PrincipalList principals) {
        this.myIntForSrvr.setIntendedServer(principals);
    }
    
    public AuthorizationData getAuthorizationData() {
        return this.myIntForSrvr.getAuthzData();
    }
    
    public void setAuthorizationData(final AuthorizationData authzData) {
        this.myIntForSrvr.setAuthzData(authzData);
    }
    
    @Override
    protected int encodingBodyLength() throws IOException {
        if (this.bodyLength == -1) {
            this.setAuthzData(this.myIntForSrvr.encode());
            this.bodyLength = super.encodingBodyLength();
        }
        return this.bodyLength;
    }
    
    @Override
    public void dumpWith(final Asn1Dumper dumper, final int indents) {
        super.dumpWith(dumper, indents);
        dumper.newLine();
        this.myIntForSrvr.dumpWith(dumper, indents + 8);
    }
    
    private static class IntForSrvr extends KrbSequenceType
    {
        private AuthorizationData authzData;
        private static Asn1FieldInfo[] fieldInfos;
        
        IntForSrvr() {
            super(IntForSrvr.fieldInfos);
        }
        
        IntForSrvr(final PrincipalList principals) {
            super(IntForSrvr.fieldInfos);
            this.setFieldAs(IntForSrvrField.IFS_intendedServer, principals);
        }
        
        public PrincipalList getIntendedServer() {
            return this.getFieldAs(IntForSrvrField.IFS_intendedServer, PrincipalList.class);
        }
        
        public void setIntendedServer(final PrincipalList principals) {
            this.setFieldAs(IntForSrvrField.IFS_intendedServer, principals);
            this.resetBodyLength();
        }
        
        public AuthorizationData getAuthzData() {
            if (this.authzData == null) {
                this.authzData = this.getFieldAs(IntForSrvrField.IFS_elements, AuthorizationData.class);
            }
            return this.authzData;
        }
        
        public void setAuthzData(final AuthorizationData authzData) {
            this.authzData = authzData;
            this.setFieldAs(IntForSrvrField.IFS_elements, authzData);
            this.resetBodyLength();
        }
        
        static {
            IntForSrvr.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(IntForSrvrField.IFS_intendedServer, PrincipalList.class), new ExplicitField(IntForSrvrField.IFS_elements, AuthorizationData.class) };
        }
        
        protected enum IntForSrvrField implements EnumType
        {
            IFS_intendedServer, 
            IFS_elements;
            
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
