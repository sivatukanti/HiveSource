// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4JLoggerFactory extends InternalLoggerFactory
{
    @Override
    public InternalLogger newInstance(final String name) {
        final Logger logger = LoggerFactory.getLogger(name);
        return new Slf4JLogger(logger);
    }
}
