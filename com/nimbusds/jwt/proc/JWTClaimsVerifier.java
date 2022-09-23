// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jwt.proc;

import com.nimbusds.jwt.JWTClaimsSet;

@Deprecated
public interface JWTClaimsVerifier
{
    @Deprecated
    void verify(final JWTClaimsSet p0) throws BadJWTException;
}
