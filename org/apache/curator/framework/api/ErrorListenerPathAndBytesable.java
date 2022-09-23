// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.api;

public interface ErrorListenerPathAndBytesable<T> extends PathAndBytesable<T>
{
    PathAndBytesable<T> withUnhandledErrorListener(final UnhandledErrorListener p0);
}
