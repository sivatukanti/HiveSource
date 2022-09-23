// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

public interface PayloadTransformer<T>
{
    T transform(final Payload p0);
}
