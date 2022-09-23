// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.component.plugins;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Priority;
import org.apache.log4j.Level;
import org.apache.log4j.component.spi.Thresholdable;

public abstract class Receiver extends PluginSkeleton implements Thresholdable
{
    protected Level thresholdLevel;
    
    protected Receiver() {
    }
    
    public void setThreshold(final Level level) {
        final Level oldValue = this.thresholdLevel;
        this.firePropertyChange("threshold", oldValue, this.thresholdLevel = level);
    }
    
    public Level getThreshold() {
        return this.thresholdLevel;
    }
    
    public boolean isAsSevereAsThreshold(final Level level) {
        return this.thresholdLevel == null || level.isGreaterOrEqual(this.thresholdLevel);
    }
    
    public void doPost(final LoggingEvent event) {
        if (!this.isAsSevereAsThreshold(event.getLevel())) {
            return;
        }
        final Logger localLogger = this.getLoggerRepository().getLogger(event.getLoggerName());
        if (event.getLevel().isGreaterOrEqual(localLogger.getEffectiveLevel())) {
            localLogger.callAppenders(event);
        }
    }
}
