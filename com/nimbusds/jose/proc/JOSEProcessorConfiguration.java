// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.proc;

public interface JOSEProcessorConfiguration<C extends SecurityContext>
{
    JWSKeySelector<C> getJWSKeySelector();
    
    void setJWSKeySelector(final JWSKeySelector<C> p0);
    
    JWEKeySelector<C> getJWEKeySelector();
    
    void setJWEKeySelector(final JWEKeySelector<C> p0);
    
    JWSVerifierFactory getJWSVerifierFactory();
    
    void setJWSVerifierFactory(final JWSVerifierFactory p0);
    
    JWEDecrypterFactory getJWEDecrypterFactory();
    
    void setJWEDecrypterFactory(final JWEDecrypterFactory p0);
}
