// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.component.spi;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

public interface LoggerEventListener
{
    void appenderAddedEvent(final Logger p0, final Appender p1);
    
    void appenderRemovedEvent(final Logger p0, final Appender p1);
    
    void levelChangedEvent(final Logger p0);
}
