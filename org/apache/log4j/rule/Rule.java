// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rule;

import java.beans.PropertyChangeListener;
import java.util.Map;
import org.apache.log4j.spi.LoggingEvent;

public interface Rule
{
    boolean evaluate(final LoggingEvent p0, final Map p1);
    
    void addPropertyChangeListener(final PropertyChangeListener p0);
    
    void removePropertyChangeListener(final PropertyChangeListener p0);
}
