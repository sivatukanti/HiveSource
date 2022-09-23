// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.client;

import org.apache.hadoop.io.retry.RetryPolicy;
import java.net.InetSocketAddress;
import org.apache.hadoop.yarn.ipc.YarnRPC;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class NMProxy extends ServerProxy
{
    public static <T> T createNMProxy(final Configuration conf, final Class<T> protocol, final UserGroupInformation ugi, final YarnRPC rpc, final InetSocketAddress serverAddress) {
        final RetryPolicy retryPolicy = ServerProxy.createRetryPolicy(conf, "yarn.client.nodemanager-connect.max-wait-ms", 900000L, "yarn.client.nodemanager-connect.retry-interval-ms", 10000L);
        return ServerProxy.createRetriableProxy(conf, protocol, ugi, rpc, serverAddress, retryPolicy);
    }
}
