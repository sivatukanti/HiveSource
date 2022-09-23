// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.jwk;

import java.security.KeyPair;
import java.security.PrivateKey;
import com.nimbusds.jose.JOSEException;
import java.security.PublicKey;

public interface AssymetricJWK
{
    PublicKey toPublicKey() throws JOSEException;
    
    PrivateKey toPrivateKey() throws JOSEException;
    
    KeyPair toKeyPair() throws JOSEException;
}
