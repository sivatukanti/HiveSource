// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.base;

import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class KrbTokenBase extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public KrbTokenBase() {
        super(KrbTokenBase.fieldInfos);
    }
    
    public TokenFormat getTokenFormat() {
        final Integer value = this.getFieldAsInteger(KrbTokenField.TOKEN_FORMAT);
        return TokenFormat.fromValue(value);
    }
    
    public void setTokenFormat(final TokenFormat tokenFormat) {
        this.setFieldAsInt(KrbTokenField.TOKEN_FORMAT, tokenFormat.getValue());
    }
    
    public byte[] getTokenValue() {
        return this.getFieldAsOctets(KrbTokenField.TOKEN_VALUE);
    }
    
    public void setTokenValue(final byte[] tokenValue) {
        this.setFieldAsOctets(KrbTokenField.TOKEN_VALUE, tokenValue);
    }
    
    static {
        KrbTokenBase.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(KrbTokenField.TOKEN_FORMAT, Asn1Integer.class), new ExplicitField(KrbTokenField.TOKEN_VALUE, Asn1OctetString.class) };
    }
    
    protected enum KrbTokenField implements EnumType
    {
        TOKEN_FORMAT, 
        TOKEN_VALUE;
        
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
