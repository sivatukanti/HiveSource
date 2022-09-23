// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

interface ConstructionProxyFactory<T>
{
    ConstructionProxy<T> create() throws ErrorsException;
}
