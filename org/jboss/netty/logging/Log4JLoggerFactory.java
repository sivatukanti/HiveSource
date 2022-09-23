// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.logging;

import org.apache.log4j.Logger;

public class Log4JLoggerFactory extends InternalLoggerFactory
{
    @Override
    public InternalLogger newInstance(final String name) {
        final Logger logger = Logger.getLogger(name);
        return new Log4JLogger(logger);
    }
}
