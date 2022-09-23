// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.component.plugins;

import java.beans.PropertyChangeListener;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.OptionHandler;

public interface Plugin extends OptionHandler
{
    String getName();
    
    void setName(final String p0);
    
    LoggerRepository getLoggerRepository();
    
    void setLoggerRepository(final LoggerRepository p0);
    
    void addPropertyChangeListener(final String p0, final PropertyChangeListener p1);
    
    void addPropertyChangeListener(final PropertyChangeListener p0);
    
    void removePropertyChangeListener(final PropertyChangeListener p0);
    
    void removePropertyChangeListener(final String p0, final PropertyChangeListener p1);
    
    boolean isActive();
    
    boolean isEquivalent(final Plugin p0);
    
    void shutdown();
}
