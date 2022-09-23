// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.shims;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.Appender;

public class HiveEventCounter implements Appender, OptionHandler
{
    AppenderSkeleton hadoopEventCounter;
    
    public HiveEventCounter() {
        this.hadoopEventCounter = ShimLoader.getEventCounter();
    }
    
    @Override
    public void close() {
        this.hadoopEventCounter.close();
    }
    
    @Override
    public boolean requiresLayout() {
        return this.hadoopEventCounter.requiresLayout();
    }
    
    @Override
    public void addFilter(final Filter filter) {
        this.hadoopEventCounter.addFilter(filter);
    }
    
    @Override
    public void clearFilters() {
        this.hadoopEventCounter.clearFilters();
    }
    
    @Override
    public void doAppend(final LoggingEvent event) {
        this.hadoopEventCounter.doAppend(event);
    }
    
    @Override
    public ErrorHandler getErrorHandler() {
        return this.hadoopEventCounter.getErrorHandler();
    }
    
    @Override
    public Filter getFilter() {
        return this.hadoopEventCounter.getFilter();
    }
    
    @Override
    public Layout getLayout() {
        return this.hadoopEventCounter.getLayout();
    }
    
    @Override
    public String getName() {
        return this.hadoopEventCounter.getName();
    }
    
    @Override
    public void setErrorHandler(final ErrorHandler handler) {
        this.hadoopEventCounter.setErrorHandler(handler);
    }
    
    @Override
    public void setLayout(final Layout layout) {
        this.hadoopEventCounter.setLayout(layout);
    }
    
    @Override
    public void setName(final String name) {
        this.hadoopEventCounter.setName(name);
    }
    
    @Override
    public void activateOptions() {
        this.hadoopEventCounter.activateOptions();
    }
}
