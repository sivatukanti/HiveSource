// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.api;

public interface ErrorListenerPathable<T> extends Pathable<T>
{
    Pathable<T> withUnhandledErrorListener(final UnhandledErrorListener p0);
}
