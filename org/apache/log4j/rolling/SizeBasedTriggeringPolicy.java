// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rolling;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Appender;
import org.apache.log4j.spi.OptionHandler;

public final class SizeBasedTriggeringPolicy implements TriggeringPolicy, OptionHandler
{
    private long maxFileSize;
    
    public SizeBasedTriggeringPolicy() {
        this.maxFileSize = 10485760L;
    }
    
    public SizeBasedTriggeringPolicy(final long maxFileSize) {
        this.maxFileSize = 10485760L;
        this.maxFileSize = maxFileSize;
    }
    
    public boolean isTriggeringEvent(final Appender appender, final LoggingEvent event, final String file, final long fileLength) {
        return fileLength >= this.maxFileSize;
    }
    
    public long getMaxFileSize() {
        return this.maxFileSize;
    }
    
    public void setMaxFileSize(final long l) {
        this.maxFileSize = l;
    }
    
    public void activateOptions() {
    }
}
