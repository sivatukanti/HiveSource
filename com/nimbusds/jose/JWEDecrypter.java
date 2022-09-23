// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

import com.nimbusds.jose.util.Base64URL;

public interface JWEDecrypter extends JWEProvider
{
    byte[] decrypt(final JWEHeader p0, final Base64URL p1, final Base64URL p2, final Base64URL p3, final Base64URL p4) throws JOSEException;
}
