// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.client;

import java.net.InetSocketAddress;
import org.apache.yetus.audience.InterfaceAudience;

@InterfaceAudience.Public
public interface HostProvider
{
    int size();
    
    InetSocketAddress next(final long p0);
    
    void onConnected();
}
