// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.drivers;

import java.util.concurrent.TimeUnit;

public abstract class AdvancedTracerDriver implements TracerDriver
{
    public abstract void addTrace(final OperationTrace p0);
    
    public abstract void addEvent(final EventTrace p0);
    
    @Deprecated
    @Override
    public final void addTrace(final String name, final long time, final TimeUnit unit) {
    }
    
    @Deprecated
    @Override
    public final void addCount(final String name, final int increment) {
    }
}
