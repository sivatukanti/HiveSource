// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.logging;

import java.util.logging.Logger;

public class JdkLoggerFactory extends InternalLoggerFactory
{
    @Override
    public InternalLogger newInstance(final String name) {
        final Logger logger = Logger.getLogger(name);
        return new JdkLogger(logger, name);
    }
}
