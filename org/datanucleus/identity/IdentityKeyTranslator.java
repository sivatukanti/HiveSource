// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.identity;

import org.datanucleus.ExecutionContext;
import java.io.Serializable;

public interface IdentityKeyTranslator extends Serializable
{
    Object getKey(final ExecutionContext p0, final Class p1, final Object p2);
}
