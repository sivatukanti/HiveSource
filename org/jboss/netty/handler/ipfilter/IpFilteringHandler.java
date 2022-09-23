// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.ipfilter;

public interface IpFilteringHandler
{
    void setIpFilterListener(final IpFilterListener p0);
    
    void removeIpFilterListener();
}
