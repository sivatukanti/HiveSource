// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.factories;

import org.apache.hadoop.conf.Configuration;
import java.net.InetSocketAddress;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "MapReduce", "YARN" })
public interface RpcClientFactory
{
    Object getClient(final Class<?> p0, final long p1, final InetSocketAddress p2, final Configuration p3);
    
    void stopClient(final Object p0);
}
