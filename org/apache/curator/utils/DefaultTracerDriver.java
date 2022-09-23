// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.utils;

import java.util.concurrent.TimeUnit;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.curator.drivers.TracerDriver;

public class DefaultTracerDriver implements TracerDriver
{
    private final Logger log;
    
    public DefaultTracerDriver() {
        this.log = LoggerFactory.getLogger(this.getClass());
    }
    
    @Override
    public void addTrace(final String name, final long time, final TimeUnit unit) {
        if (this.log.isTraceEnabled()) {
            this.log.trace("Trace: " + name + " - " + TimeUnit.MILLISECONDS.convert(time, unit) + " ms");
        }
    }
    
    @Override
    public void addCount(final String name, final int increment) {
        if (this.log.isTraceEnabled()) {
            this.log.trace("Counter " + name + ": " + increment);
        }
    }
}
