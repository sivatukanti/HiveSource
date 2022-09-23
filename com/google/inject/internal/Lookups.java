// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.Provider;
import com.google.inject.Key;

interface Lookups
{
     <T> Provider<T> getProvider(final Key<T> p0);
    
     <T> MembersInjector<T> getMembersInjector(final TypeLiteral<T> p0);
}
