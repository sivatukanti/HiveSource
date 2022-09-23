// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jwt.proc;

import com.nimbusds.jose.proc.JOSEProcessorConfiguration;
import com.nimbusds.jose.proc.SecurityContext;

public interface JWTProcessorConfiguration<C extends SecurityContext> extends JOSEProcessorConfiguration<C>
{
    JWTClaimsSetVerifier<C> getJWTClaimsSetVerifier();
    
    void setJWTClaimsSetVerifier(final JWTClaimsSetVerifier<C> p0);
    
    @Deprecated
    JWTClaimsVerifier getJWTClaimsVerifier();
    
    @Deprecated
    void setJWTClaimsVerifier(final JWTClaimsVerifier p0);
}
