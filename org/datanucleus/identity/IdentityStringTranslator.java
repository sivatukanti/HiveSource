// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.identity;

import org.datanucleus.ExecutionContext;
import java.io.Serializable;

public interface IdentityStringTranslator extends Serializable
{
    Object getIdentity(final ExecutionContext p0, final String p1);
}
