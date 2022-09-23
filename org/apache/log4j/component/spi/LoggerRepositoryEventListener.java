// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.component.spi;

import org.apache.log4j.spi.LoggerRepository;

public interface LoggerRepositoryEventListener
{
    void configurationResetEvent(final LoggerRepository p0);
    
    void configurationChangedEvent(final LoggerRepository p0);
    
    void shutdownEvent(final LoggerRepository p0);
}
