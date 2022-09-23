// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rolling;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Appender;
import org.apache.log4j.spi.OptionHandler;

public interface TriggeringPolicy extends OptionHandler
{
    boolean isTriggeringEvent(final Appender p0, final LoggingEvent p1, final String p2, final long p3);
}
