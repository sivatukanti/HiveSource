// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.locks;

import java.util.concurrent.Executor;

public interface Revocable<T>
{
    void makeRevocable(final RevocationListener<T> p0);
    
    void makeRevocable(final RevocationListener<T> p0, final Executor p1);
}
