// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.pa.otp;

import org.apache.kerby.kerberos.kerb.type.KerberosString;
import org.apache.kerby.asn1.type.Asn1Utf8String;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class PaOtpChallenge extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public PaOtpChallenge() {
        super(PaOtpChallenge.fieldInfos);
    }
    
    static {
        PaOtpChallenge.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(PaOtpChallengeField.NONCE, Asn1OctetString.class), new ExplicitField(PaOtpChallengeField.OTP_SERVICE, Asn1Utf8String.class), new ExplicitField(PaOtpChallengeField.OTP_TOKEN_INFO, Asn1OctetString.class), new ExplicitField(PaOtpChallengeField.SALT, KerberosString.class), new ExplicitField(PaOtpChallengeField.S2KPARAMS, Asn1OctetString.class) };
    }
    
    protected enum PaOtpChallengeField implements EnumType
    {
        NONCE, 
        OTP_SERVICE, 
        OTP_TOKEN_INFO, 
        SALT, 
        S2KPARAMS;
        
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
