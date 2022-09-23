// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.EnumType;

enum CRLReasonEnum implements EnumType
{
    UNSPECIFIED, 
    KEY_COMPROMISE, 
    CA_COMPROMISE, 
    AFFILIATION_CHANGED, 
    SUPERSEDED, 
    CESSATION_OF_OPERATION, 
    CERTIFICATE_HOLD, 
    REMOVE_FROM_CRL, 
    PRIVILEGE_WITH_DRAWN, 
    AA_COMPROMISE;
    
    @Override
    public int getValue() {
        return this.ordinal();
    }
    
    @Override
    public String getName() {
        return this.name();
    }
}
