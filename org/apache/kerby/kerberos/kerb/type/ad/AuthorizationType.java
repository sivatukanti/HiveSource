// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ad;

import org.apache.kerby.asn1.EnumType;

public enum AuthorizationType implements EnumType
{
    NONE(0), 
    AD_IF_RELEVANT(1), 
    AD_INTENDED_FOR_SERVER(2), 
    AD_INTENDED_FOR_APPLICATION_CLASS(3), 
    AD_KDC_ISSUED(4), 
    AD_AND_OR(5), 
    AD_MANDATORY_TICKET_EXTENSIONS(6), 
    AD_IN_TICKET_EXTENSIONS(7), 
    AD_MANDATORY_FOR_KDC(8), 
    AD_INITIAL_VERIFIED_CAS(9), 
    OSF_DCE(64), 
    SESAME(65), 
    AD_OSF_DCE_PKI_CERTID(66), 
    AD_CAMMAC(96), 
    AD_WIN2K_PAC(128), 
    AD_ETYPE_NEGOTIATION(129), 
    AD_TOKEN(256), 
    AD_AUTHENTICAION_INDICATOR(-1);
    
    private final int value;
    
    private AuthorizationType(final int value) {
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
    
    public static AuthorizationType fromValue(final Integer value) {
        if (value != null) {
            for (final EnumType e : values()) {
                if (e.getValue() == value) {
                    return (AuthorizationType)e;
                }
            }
        }
        return AuthorizationType.NONE;
    }
}
