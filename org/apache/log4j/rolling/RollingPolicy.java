// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rolling;

import org.apache.log4j.spi.OptionHandler;

public interface RollingPolicy extends OptionHandler
{
    RolloverDescription initialize(final String p0, final boolean p1) throws SecurityException;
    
    RolloverDescription rollover(final String p0) throws SecurityException;
}
