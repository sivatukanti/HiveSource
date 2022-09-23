// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.http;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.AppenderSkeleton;

public class HttpRequestLogAppender extends AppenderSkeleton
{
    private String filename;
    private int retainDays;
    
    public void setRetainDays(final int retainDays) {
        this.retainDays = retainDays;
    }
    
    public int getRetainDays() {
        return this.retainDays;
    }
    
    public void setFilename(final String filename) {
        this.filename = filename;
    }
    
    public String getFilename() {
        return this.filename;
    }
    
    public void append(final LoggingEvent event) {
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public boolean requiresLayout() {
        return false;
    }
}
