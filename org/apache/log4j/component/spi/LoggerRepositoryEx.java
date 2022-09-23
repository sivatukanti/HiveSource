// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.component.spi;

import org.apache.log4j.spi.LoggerFactory;
import java.util.List;
import java.util.Map;
import org.apache.log4j.component.scheduler.Scheduler;
import org.apache.log4j.component.plugins.PluginRegistry;
import org.apache.log4j.Logger;
import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.spi.LoggerRepository;

public interface LoggerRepositoryEx extends LoggerRepository
{
    void addLoggerRepositoryEventListener(final LoggerRepositoryEventListener p0);
    
    void removeLoggerRepositoryEventListener(final LoggerRepositoryEventListener p0);
    
    void addLoggerEventListener(final LoggerEventListener p0);
    
    void removeLoggerEventListener(final LoggerEventListener p0);
    
    String getName();
    
    void setName(final String p0);
    
    boolean isPristine();
    
    void setPristine(final boolean p0);
    
    void fireRemoveAppenderEvent(final Category p0, final Appender p1);
    
    void fireLevelChangedEvent(final Logger p0);
    
    void fireConfigurationChangedEvent();
    
    PluginRegistry getPluginRegistry();
    
    Scheduler getScheduler();
    
    Map getProperties();
    
    String getProperty(final String p0);
    
    void setProperty(final String p0, final String p1);
    
    List getErrorList();
    
    void addErrorItem(final ErrorItem p0);
    
    Object getObject(final String p0);
    
    void putObject(final String p0, final Object p1);
    
    void setLoggerFactory(final LoggerFactory p0);
    
    LoggerFactory getLoggerFactory();
}
