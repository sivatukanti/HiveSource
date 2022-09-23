// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

import com.nimbusds.jose.util.Base64URL;

public interface JWSVerifier extends JWSProvider
{
    boolean verify(final JWSHeader p0, final byte[] p1, final Base64URL p2) throws JOSEException;
}
