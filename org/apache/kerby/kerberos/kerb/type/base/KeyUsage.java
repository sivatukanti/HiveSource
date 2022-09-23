// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.base;

import org.apache.kerby.asn1.EnumType;

public enum KeyUsage implements EnumType
{
    UNKNOWN(-1), 
    NONE(0), 
    AS_REQ_PA_ENC_TS(1), 
    KDC_REP_TICKET(2), 
    AS_REP_ENCPART(3), 
    TGS_REQ_AD_SESSKEY(4), 
    TGS_REQ_AD_SUBKEY(5), 
    TGS_REQ_AUTH_CKSUM(6), 
    TGS_REQ_AUTH(7), 
    TGS_REP_ENCPART_SESSKEY(8), 
    TGS_REP_ENCPART_SUBKEY(9), 
    AP_REQ_AUTH_CKSUM(10), 
    AP_REQ_AUTH(11), 
    AP_REP_ENCPART(12), 
    KRB_PRIV_ENCPART(13), 
    KRB_CRED_ENCPART(14), 
    KRB_SAFE_CKSUM(15), 
    APP_DATA_ENCRYPT(16), 
    APP_DATA_CKSUM(17), 
    KRB_ERROR_CKSUM(18), 
    AD_KDCISSUED_CKSUM(19), 
    AD_MTE(20), 
    AD_ITE(21), 
    GSS_TOK_MIC(22), 
    GSS_TOK_WRAP_INTEG(23), 
    GSS_TOK_WRAP_PRIV(24), 
    PA_SAM_CHALLENGE_CKSUM(25), 
    PA_SAM_CHALLENGE_TRACKID(26), 
    PA_SAM_RESPONSE(27), 
    PA_S4U_X509_USER_REQUEST(26), 
    PA_S4U_X509_USER_REPLY(27), 
    PA_REFERRAL(26), 
    AD_SIGNEDPATH(-21), 
    IAKERB_FINISHED(42), 
    PA_PKINIT_KX(44), 
    PA_OTP_REQUEST(45), 
    FAST_REQ_CHKSUM(50), 
    FAST_ENC(51), 
    FAST_REP(52), 
    FAST_FINISHED(53), 
    ENC_CHALLENGE_CLIENT(54), 
    ENC_CHALLENGE_KDC(55), 
    AS_REQ(56), 
    PA_TOKEN(57), 
    AD_CAMMAC_VERIFIER_MAC(64);
    
    private int value;
    
    private KeyUsage(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    @Override
    public String getName() {
        return this.name();
    }
    
    public static KeyUsage fromValue(final Integer value) {
        if (value != null) {
            for (final EnumType e : values()) {
                if (e.getValue() == value) {
                    return (KeyUsage)e;
                }
            }
        }
        return KeyUsage.UNKNOWN;
    }
    
    public static final boolean isValid(final int usage) {
        return usage > -1;
    }
}
