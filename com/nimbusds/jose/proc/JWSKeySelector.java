// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.proc;

import com.nimbusds.jose.KeySourceException;
import java.security.Key;
import java.util.List;
import com.nimbusds.jose.JWSHeader;

public interface JWSKeySelector<C extends SecurityContext>
{
    List<? extends Key> selectJWSKeys(final JWSHeader p0, final C p1) throws KeySourceException;
}
