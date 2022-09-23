// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.logging;

import org.jboss.logging.Logger;

public class JBossLoggerFactory extends InternalLoggerFactory
{
    @Override
    public InternalLogger newInstance(final String name) {
        final Logger logger = Logger.getLogger(name);
        return new JBossLogger(logger);
    }
}
