// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.pa;

import org.apache.kerby.asn1.EnumType;

public enum PaDataType implements EnumType
{
    NONE(0), 
    TGS_REQ(1), 
    AP_REQ(1), 
    ENC_TIMESTAMP(2), 
    PW_SALT(3), 
    ENC_ENCKEY(4), 
    ENC_UNIX_TIME(5), 
    ENC_SANDIA_SECURID(6), 
    SESAME(7), 
    OSF_DCE(8), 
    CYBERSAFE_SECUREID(9), 
    AFS3_SALT(10), 
    ETYPE_INFO(11), 
    SAM_CHALLENGE(12), 
    SAM_RESPONSE(13), 
    PK_AS_REQ_OLD(14), 
    PK_AS_REP_OLD(15), 
    PK_AS_REQ(16), 
    PK_AS_REP(17), 
    ETYPE_INFO2(19), 
    USE_SPECIFIED_KVNO(20), 
    SVR_REFERRAL_INFO(20), 
    SAM_REDIRECT(21), 
    GET_FROM_TYPED_DATA(22), 
    REFERRAL(25), 
    SAM_CHALLENGE_2(30), 
    SAM_RESPONSE_2(31), 
    PAC_REQUEST(128), 
    FOR_USER(129), 
    S4U_X509_USER(130), 
    AS_CHECKSUM(132), 
    FX_COOKIE(133), 
    FX_FAST(136), 
    FX_ERROR(137), 
    ENCRYPTED_CHALLENGE(138), 
    OTP_CHALLENGE(141), 
    OTP_REQUEST(142), 
    OTP_PIN_CHANGE(144), 
    PKINIT_KX(147), 
    TOKEN_REQUEST(148), 
    ENCPADATA_REQ_ENC_PA_REP(149), 
    TOKEN_CHALLENGE(149);
    
    private final int value;
    
    private PaDataType(final int value) {
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
    
    public static PaDataType fromValue(final Integer value) {
        if (value != null) {
            for (final EnumType e : values()) {
                if (e.getValue() == value) {
                    return (PaDataType)e;
                }
            }
        }
        return PaDataType.NONE;
    }
}
