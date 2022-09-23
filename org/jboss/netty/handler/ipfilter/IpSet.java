// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.ipfilter;

import java.net.InetAddress;

public interface IpSet
{
    boolean contains(final InetAddress p0);
}
