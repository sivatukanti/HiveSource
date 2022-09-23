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
import org.apache.kerby.kerberos.kerb.type.KerberosStrings;
import java.io.IOException;

public class ADIntendedForApplicationClass extends AuthorizationDataEntry
{
    private IntendedForApplicationClass myIntForAppClass;
    
    public ADIntendedForApplicationClass() {
        super(AuthorizationType.AD_INTENDED_FOR_APPLICATION_CLASS);
        this.myIntForAppClass = new IntendedForApplicationClass();
        this.myIntForAppClass.outerEncodeable = this;
    }
    
    public ADIntendedForApplicationClass(final byte[] encoded) throws IOException {
        this();
        this.myIntForAppClass.decode(encoded);
    }
    
    public ADIntendedForApplicationClass(final KerberosStrings intendedAppClass) throws IOException {
        this();
        this.myIntForAppClass.setIntendedForApplicationClass(intendedAppClass);
    }
    
    public KerberosStrings getIntendedForApplicationClass() {
        return this.myIntForAppClass.getIntendedForApplicationClass();
    }
    
    public void setIntendedForApplicationClass(final KerberosStrings intendedAppClass) {
        this.myIntForAppClass.setIntendedForApplicationClass(intendedAppClass);
    }
    
    public AuthorizationData getAuthorizationData() {
        return this.myIntForAppClass.getAuthzData();
    }
    
    public void setAuthorizationData(final AuthorizationData authzData) {
        this.myIntForAppClass.setAuthzData(authzData);
    }
    
    @Override
    protected int encodingBodyLength() throws IOException {
        if (this.bodyLength == -1) {
            this.setAuthzData(this.myIntForAppClass.encode());
            this.bodyLength = super.encodingBodyLength();
        }
        return this.bodyLength;
    }
    
    @Override
    public void dumpWith(final Asn1Dumper dumper, final int indents) {
        super.dumpWith(dumper, indents);
        dumper.newLine();
        this.myIntForAppClass.dumpWith(dumper, indents + 8);
    }
    
    private static class IntendedForApplicationClass extends KrbSequenceType
    {
        private AuthorizationData authzData;
        private static Asn1FieldInfo[] fieldInfos;
        
        IntendedForApplicationClass() {
            super(IntendedForApplicationClass.fieldInfos);
        }
        
        IntendedForApplicationClass(final KerberosStrings intendedAppClass) {
            super(IntendedForApplicationClass.fieldInfos);
            this.setFieldAs(IntendedForApplicationClassField.IFAC_intendedAppClass, intendedAppClass);
        }
        
        public KerberosStrings getIntendedForApplicationClass() {
            return this.getFieldAs(IntendedForApplicationClassField.IFAC_intendedAppClass, KerberosStrings.class);
        }
        
        public void setIntendedForApplicationClass(final KerberosStrings intendedAppClass) {
            this.setFieldAs(IntendedForApplicationClassField.IFAC_intendedAppClass, intendedAppClass);
            this.resetBodyLength();
        }
        
        public AuthorizationData getAuthzData() {
            if (this.authzData == null) {
                this.authzData = this.getFieldAs(IntendedForApplicationClassField.IFAC_elements, AuthorizationData.class);
            }
            return this.authzData;
        }
        
        public void setAuthzData(final AuthorizationData authzData) {
            this.authzData = authzData;
            this.setFieldAs(IntendedForApplicationClassField.IFAC_elements, authzData);
            this.resetBodyLength();
        }
        
        static {
            IntendedForApplicationClass.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(IntendedForApplicationClassField.IFAC_intendedAppClass, KerberosStrings.class), new ExplicitField(IntendedForApplicationClassField.IFAC_elements, AuthorizationData.class) };
        }
        
        protected enum IntendedForApplicationClassField implements EnumType
        {
            IFAC_intendedAppClass, 
            IFAC_elements;
            
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
