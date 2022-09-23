// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jwt;

public interface JWTClaimsSetTransformer<T>
{
    T transform(final JWTClaimsSet p0);
}
