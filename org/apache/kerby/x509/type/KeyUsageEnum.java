// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.EnumType;

enum KeyUsageEnum implements EnumType
{
    DIGITAL_SIGNATURE, 
    NON_REPUDIATION, 
    KEY_ENCIPHERMENT, 
    DATA_ENCIPHERMENT, 
    KEY_AGREEMENT, 
    KEY_CERT_SIGN, 
    CRL_SIGN, 
    ENCIPHER_ONLY, 
    DECIPHER_ONLY;
    
    @Override
    public int getValue() {
        return this.ordinal();
    }
    
    @Override
    public String getName() {
        return this.name();
    }
}
