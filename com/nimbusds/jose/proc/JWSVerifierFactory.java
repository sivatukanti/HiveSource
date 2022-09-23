// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.proc;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import java.security.Key;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSProvider;

public interface JWSVerifierFactory extends JWSProvider
{
    JWSVerifier createJWSVerifier(final JWSHeader p0, final Key p1) throws JOSEException;
}
