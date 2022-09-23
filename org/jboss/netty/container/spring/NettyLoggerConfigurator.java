// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.container.spring;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.CommonsLoggerFactory;

public class NettyLoggerConfigurator
{
    public NettyLoggerConfigurator() {
        InternalLoggerFactory.setDefaultFactory(new CommonsLoggerFactory());
    }
}
