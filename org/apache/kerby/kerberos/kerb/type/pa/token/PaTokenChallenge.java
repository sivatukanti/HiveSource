// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.pa.token;

import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class PaTokenChallenge extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public PaTokenChallenge() {
        super(PaTokenChallenge.fieldInfos);
    }
    
    static {
        PaTokenChallenge.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(PaTokenChallengeField.TOKENINFOS, TokenInfos.class) };
    }
    
    protected enum PaTokenChallengeField implements EnumType
    {
        TOKENINFOS;
        
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
