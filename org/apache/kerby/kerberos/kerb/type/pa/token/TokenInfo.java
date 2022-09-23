// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.pa.token;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.type.Asn1Utf8String;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class TokenInfo extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public TokenInfo() {
        super(TokenInfo.fieldInfos);
    }
    
    public TokenFlags getFlags() {
        return this.getFieldAs(TokenInfoField.FLAGS, TokenFlags.class);
    }
    
    public void setFlags(final TokenFlags flags) {
        this.setFieldAs(TokenInfoField.FLAGS, flags);
    }
    
    public String getTokenVendor() {
        return this.getFieldAsString(TokenInfoField.TOKEN_VENDOR);
    }
    
    public void setTokenVendor(final String tokenVendor) {
        this.setFieldAs(TokenInfoField.TOKEN_VENDOR, new Asn1Utf8String(tokenVendor));
    }
    
    static {
        TokenInfo.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(TokenInfoField.FLAGS, Asn1OctetString.class), new ExplicitField(TokenInfoField.TOKEN_VENDOR, Asn1Utf8String.class) };
    }
    
    protected enum TokenInfoField implements EnumType
    {
        FLAGS, 
        TOKEN_VENDOR;
        
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
