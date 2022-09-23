// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.annotations;

public interface ClassNameResolver
{
    boolean isExcluded(final String p0);
    
    boolean shouldOverride(final String p0);
}
