// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.util.concurrent.TimeUnit;
import java.io.Closeable;

public class StopWatch implements Closeable
{
    private final Timer timer;
    private boolean isStarted;
    private long startNanos;
    private long currentElapsedNanos;
    
    public StopWatch() {
        this(new Timer());
    }
    
    public StopWatch(final Timer timer) {
        this.timer = timer;
    }
    
    public boolean isRunning() {
        return this.isStarted;
    }
    
    public StopWatch start() {
        if (this.isStarted) {
            throw new IllegalStateException("StopWatch is already running");
        }
        this.isStarted = true;
        this.startNanos = this.timer.monotonicNowNanos();
        return this;
    }
    
    public StopWatch stop() {
        if (!this.isStarted) {
            throw new IllegalStateException("StopWatch is already stopped");
        }
        final long now = this.timer.monotonicNowNanos();
        this.isStarted = false;
        this.currentElapsedNanos += now - this.startNanos;
        return this;
    }
    
    public StopWatch reset() {
        this.currentElapsedNanos = 0L;
        this.isStarted = false;
        return this;
    }
    
    public long now(final TimeUnit timeUnit) {
        return timeUnit.convert(this.now(), TimeUnit.NANOSECONDS);
    }
    
    public long now() {
        return this.isStarted ? (this.timer.monotonicNowNanos() - this.startNanos + this.currentElapsedNanos) : this.currentElapsedNanos;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.now());
    }
    
    @Override
    public void close() {
        if (this.isStarted) {
            this.stop();
        }
    }
}
