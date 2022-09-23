// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.pa.otp;

import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.kerberos.kerb.type.pa.pkinit.AlgorithmIdentifiers;
import org.apache.kerby.kerberos.kerb.type.KerberosString;
import org.apache.kerby.asn1.type.Asn1Utf8String;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class OtpTokenInfo extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public OtpTokenInfo() {
        super(OtpTokenInfo.fieldInfos);
    }
    
    static {
        OtpTokenInfo.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(OtpTokenInfoField.FLAGS, Asn1OctetString.class), new ExplicitField(OtpTokenInfoField.OTP_VENDOR, Asn1Utf8String.class), new ExplicitField(OtpTokenInfoField.OTP_CHALLENGE, Asn1OctetString.class), new ExplicitField(OtpTokenInfoField.OTP_LENGTH, KerberosString.class), new ExplicitField(OtpTokenInfoField.OTP_FORMAT, Asn1OctetString.class), new ExplicitField(OtpTokenInfoField.OTP_TOKEN_ID, Asn1Utf8String.class), new ExplicitField(OtpTokenInfoField.OTP_ALG_ID, Asn1OctetString.class), new ExplicitField(OtpTokenInfoField.SUPPORTED_HASH_ALG, AlgorithmIdentifiers.class), new ExplicitField(OtpTokenInfoField.ITERATION_COUNT, Asn1Integer.class) };
    }
    
    protected enum OtpTokenInfoField implements EnumType
    {
        FLAGS, 
        OTP_VENDOR, 
        OTP_CHALLENGE, 
        OTP_LENGTH, 
        OTP_FORMAT, 
        OTP_TOKEN_ID, 
        OTP_ALG_ID, 
        SUPPORTED_HASH_ALG, 
        ITERATION_COUNT;
        
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
