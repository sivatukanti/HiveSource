// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ad;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.type.base.KrbToken;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class AdToken extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public AdToken() {
        super(AdToken.fieldInfos);
    }
    
    public KrbToken getToken() {
        return this.getFieldAs(AdTokenField.TOKEN, KrbToken.class);
    }
    
    public void setToken(final KrbToken token) {
        this.setFieldAs(AdTokenField.TOKEN, token);
    }
    
    static {
        AdToken.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(AdTokenField.TOKEN, KrbToken.class) };
    }
    
    protected enum AdTokenField implements EnumType
    {
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
