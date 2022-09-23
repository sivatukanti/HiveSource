// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.pa.token;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.type.base.KrbTokenBase;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class PaTokenRequest extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public PaTokenRequest() {
        super(PaTokenRequest.fieldInfos);
    }
    
    public KrbTokenBase getToken() {
        return this.getFieldAs(PaTokenRequestField.TOKEN, KrbTokenBase.class);
    }
    
    public void setToken(final KrbTokenBase token) {
        this.setFieldAs(PaTokenRequestField.TOKEN, token);
    }
    
    public TokenInfo getTokenInfo() {
        return this.getFieldAs(PaTokenRequestField.TOKEN_INFO, TokenInfo.class);
    }
    
    public void setTokenInfo(final TokenInfo tokenInfo) {
        this.setFieldAs(PaTokenRequestField.TOKEN_INFO, tokenInfo);
    }
    
    static {
        PaTokenRequest.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(PaTokenRequestField.TOKEN_INFO, TokenInfo.class), new ExplicitField(PaTokenRequestField.TOKEN, KrbTokenBase.class) };
    }
    
    protected enum PaTokenRequestField implements EnumType
    {
        TOKEN_INFO, 
        TOKEN;
        
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
