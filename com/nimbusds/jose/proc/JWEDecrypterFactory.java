// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.proc;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import java.security.Key;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEProvider;

public interface JWEDecrypterFactory extends JWEProvider
{
    JWEDecrypter createJWEDecrypter(final JWEHeader p0, final Key p1) throws JOSEException;
}
