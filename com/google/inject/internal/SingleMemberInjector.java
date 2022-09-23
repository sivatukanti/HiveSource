// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.spi.InjectionPoint;

interface SingleMemberInjector
{
    void inject(final Errors p0, final InternalContext p1, final Object p2);
    
    InjectionPoint getInjectionPoint();
}
