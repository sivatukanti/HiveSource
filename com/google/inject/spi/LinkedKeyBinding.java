// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import com.google.inject.Key;
import com.google.inject.Binding;

public interface LinkedKeyBinding<T> extends Binding<T>
{
    Key<? extends T> getLinkedKey();
}
