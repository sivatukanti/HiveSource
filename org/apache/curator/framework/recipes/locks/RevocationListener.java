// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.locks;

public interface RevocationListener<T>
{
    void revocationRequested(final T p0);
}
