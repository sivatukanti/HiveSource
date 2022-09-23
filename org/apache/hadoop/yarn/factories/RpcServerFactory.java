// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.factories;

import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.conf.Configuration;
import java.net.InetSocketAddress;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "MapReduce", "YARN" })
public interface RpcServerFactory
{
    Server getServer(final Class<?> p0, final Object p1, final InetSocketAddress p2, final Configuration p3, final SecretManager<? extends TokenIdentifier> p4, final int p5, final String p6);
}
