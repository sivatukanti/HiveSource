// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jwt.proc;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jose.proc.SecurityContext;

public interface JWTClaimsSetVerifier<C extends SecurityContext>
{
    void verify(final JWTClaimsSet p0, final C p1) throws BadJWTException;
}
