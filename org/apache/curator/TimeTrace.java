// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator;

import java.util.concurrent.TimeUnit;
import org.apache.curator.drivers.TracerDriver;

public class TimeTrace
{
    private final String name;
    private final TracerDriver driver;
    private final long startTimeNanos;
    
    public TimeTrace(final String name, final TracerDriver driver) {
        this.startTimeNanos = System.nanoTime();
        this.name = name;
        this.driver = driver;
    }
    
    public void commit() {
        final long elapsed = System.nanoTime() - this.startTimeNanos;
        this.driver.addTrace(this.name, elapsed, TimeUnit.NANOSECONDS);
    }
}
