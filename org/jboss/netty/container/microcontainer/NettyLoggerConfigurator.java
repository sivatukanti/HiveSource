// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.container.microcontainer;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.JBossLoggerFactory;

public class NettyLoggerConfigurator
{
    public NettyLoggerConfigurator() {
        InternalLoggerFactory.setDefaultFactory(new JBossLoggerFactory());
    }
}
