// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.cglib.core;

import com.google.inject.internal.asm.$Label;

public interface $ObjectSwitchCallback
{
    void processCase(final Object p0, final $Label p1) throws Exception;
    
    void processDefault() throws Exception;
}
