// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.proc;

import com.nimbusds.jose.KeySourceException;
import java.security.Key;
import java.util.List;
import com.nimbusds.jose.JWEHeader;

public interface JWEKeySelector<C extends SecurityContext>
{
    List<? extends Key> selectJWEKeys(final JWEHeader p0, final C p1) throws KeySourceException;
}
