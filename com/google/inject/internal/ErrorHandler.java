// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.spi.Message;

interface ErrorHandler
{
    void handle(final Object p0, final Errors p1);
    
    void handle(final Message p0);
}
