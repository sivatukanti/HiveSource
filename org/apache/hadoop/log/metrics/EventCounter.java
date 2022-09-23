// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.log.metrics;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.log4j.AppenderSkeleton;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class EventCounter extends AppenderSkeleton
{
    private static final int FATAL = 0;
    private static final int ERROR = 1;
    private static final int WARN = 2;
    private static final int INFO = 3;
    private static EventCounts counts;
    
    @InterfaceAudience.Private
    public static long getFatal() {
        return EventCounter.counts.get(0);
    }
    
    @InterfaceAudience.Private
    public static long getError() {
        return EventCounter.counts.get(1);
    }
    
    @InterfaceAudience.Private
    public static long getWarn() {
        return EventCounter.counts.get(2);
    }
    
    @InterfaceAudience.Private
    public static long getInfo() {
        return EventCounter.counts.get(3);
    }
    
    public void append(final LoggingEvent event) {
        final Level level = event.getLevel();
        if (level.equals(Level.INFO)) {
            EventCounter.counts.incr(3);
        }
        else if (level.equals(Level.WARN)) {
            EventCounter.counts.incr(2);
        }
        else if (level.equals(Level.ERROR)) {
            EventCounter.counts.incr(1);
        }
        else if (level.equals(Level.FATAL)) {
            EventCounter.counts.incr(0);
        }
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public boolean requiresLayout() {
        return false;
    }
    
    static {
        EventCounter.counts = new EventCounts();
    }
    
    private static class EventCounts
    {
        private final long[] counts;
        
        private EventCounts() {
            this.counts = new long[] { 0L, 0L, 0L, 0L };
        }
        
        private synchronized void incr(final int i) {
            final long[] counts = this.counts;
            ++counts[i];
        }
        
        private synchronized long get(final int i) {
            return this.counts[i];
        }
    }
}
