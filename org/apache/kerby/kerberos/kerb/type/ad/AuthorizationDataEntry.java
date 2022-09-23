// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ad;

import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.slf4j.Logger;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class AuthorizationDataEntry extends KrbSequenceType
{
    private static final Logger LOG;
    private static Asn1FieldInfo[] fieldInfos;
    
    public AuthorizationDataEntry() {
        super(AuthorizationDataEntry.fieldInfos);
    }
    
    public AuthorizationDataEntry(final AuthorizationType type) {
        super(AuthorizationDataEntry.fieldInfos);
        this.setAuthzType(type);
    }
    
    public AuthorizationDataEntry(final AuthorizationType type, final byte[] authzData) {
        super(AuthorizationDataEntry.fieldInfos);
        this.setAuthzType(type);
        this.setAuthzData(authzData);
    }
    
    public AuthorizationType getAuthzType() {
        final Integer value = this.getFieldAsInteger(AuthorizationDataEntryField.AD_TYPE);
        return AuthorizationType.fromValue(value);
    }
    
    public void setAuthzType(final AuthorizationType authzType) {
        this.setFieldAsInt(AuthorizationDataEntryField.AD_TYPE, authzType.getValue());
    }
    
    public byte[] getAuthzData() {
        return this.getFieldAsOctets(AuthorizationDataEntryField.AD_DATA);
    }
    
    public void setAuthzData(final byte[] authzData) {
        this.setFieldAsOctets(AuthorizationDataEntryField.AD_DATA, authzData);
    }
    
    public <T extends Asn1Type> T getAuthzDataAs(final Class<T> type) {
        T result = null;
        final byte[] authzBytes = this.getFieldAsOctets(AuthorizationDataEntryField.AD_DATA);
        if (authzBytes != null) {
            try {
                result = type.newInstance();
                result.decode(authzBytes);
            }
            catch (InstantiationException | IllegalAccessException | IOException ex2) {
                final Exception ex;
                final Exception e = ex;
                AuthorizationDataEntry.LOG.error("Failed to get the AD_DATA field. " + e.toString());
            }
        }
        return result;
    }
    
    public AuthorizationDataEntry clone() {
        return new AuthorizationDataEntry(this.getAuthzType(), this.getAuthzData().clone());
    }
    
    static {
        LOG = LoggerFactory.getLogger(AuthorizationDataEntry.class);
        AuthorizationDataEntry.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(AuthorizationDataEntryField.AD_TYPE, Asn1Integer.class), new ExplicitField(AuthorizationDataEntryField.AD_DATA, Asn1OctetString.class) };
    }
    
    protected enum AuthorizationDataEntryField implements EnumType
    {
        AD_TYPE, 
        AD_DATA;
        
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
