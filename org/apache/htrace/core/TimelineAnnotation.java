// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.core;

public class TimelineAnnotation
{
    private final long time;
    private final String msg;
    
    public TimelineAnnotation(final long time, final String msg) {
        this.time = time;
        this.msg = msg;
    }
    
    public long getTime() {
        return this.time;
    }
    
    public String getMessage() {
        return this.msg;
    }
    
    @Override
    public String toString() {
        return "@" + this.time + ": " + this.msg;
    }
}
