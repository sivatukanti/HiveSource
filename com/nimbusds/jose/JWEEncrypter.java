// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

public interface JWEEncrypter extends JWEProvider
{
    JWECryptoParts encrypt(final JWEHeader p0, final byte[] p1) throws JOSEException;
}
