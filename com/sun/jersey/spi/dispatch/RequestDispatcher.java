// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.dispatch;

import com.sun.jersey.api.core.HttpContext;

public interface RequestDispatcher
{
    void dispatch(final Object p0, final HttpContext p1);
}
