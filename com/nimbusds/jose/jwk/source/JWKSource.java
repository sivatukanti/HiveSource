// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWK;
import java.util.List;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.proc.SecurityContext;

public interface JWKSource<C extends SecurityContext>
{
    List<JWK> get(final JWKSelector p0, final C p1) throws KeySourceException;
}
