// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.local;

import org.jboss.netty.channel.ServerChannel;

public interface LocalServerChannel extends ServerChannel
{
    LocalAddress getLocalAddress();
    
    LocalAddress getRemoteAddress();
}
