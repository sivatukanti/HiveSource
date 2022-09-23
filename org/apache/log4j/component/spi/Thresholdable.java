// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.component.spi;

import org.apache.log4j.Level;

public interface Thresholdable
{
    void setThreshold(final Level p0);
    
    Level getThreshold();
    
    boolean isAsSevereAsThreshold(final Level p0);
}
