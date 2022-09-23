// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

interface ContextualCallable<T>
{
    T call(final InternalContext p0) throws ErrorsException;
}
