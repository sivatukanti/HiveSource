// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommonsLoggerFactory extends InternalLoggerFactory
{
    @Override
    public InternalLogger newInstance(final String name) {
        final Log logger = LogFactory.getLog(name);
        return new CommonsLogger(logger, name);
    }
}
