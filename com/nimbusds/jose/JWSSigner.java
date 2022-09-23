// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

import com.nimbusds.jose.util.Base64URL;

public interface JWSSigner extends JWSProvider
{
    Base64URL sign(final JWSHeader p0, final byte[] p1) throws JOSEException;
}
