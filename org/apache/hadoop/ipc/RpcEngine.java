// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.SecretManager;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.IOException;
import org.apache.hadoop.io.retry.RetryPolicy;
import javax.net.SocketFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import java.net.InetSocketAddress;
import org.apache.hadoop.classification.InterfaceStability;

@InterfaceStability.Evolving
public interface RpcEngine
{
     <T> ProtocolProxy<T> getProxy(final Class<T> p0, final long p1, final InetSocketAddress p2, final UserGroupInformation p3, final Configuration p4, final SocketFactory p5, final int p6, final RetryPolicy p7) throws IOException;
    
     <T> ProtocolProxy<T> getProxy(final Class<T> p0, final long p1, final InetSocketAddress p2, final UserGroupInformation p3, final Configuration p4, final SocketFactory p5, final int p6, final RetryPolicy p7, final AtomicBoolean p8) throws IOException;
    
    RPC.Server getServer(final Class<?> p0, final Object p1, final String p2, final int p3, final int p4, final int p5, final int p6, final boolean p7, final Configuration p8, final SecretManager<? extends TokenIdentifier> p9, final String p10) throws IOException;
    
    ProtocolProxy<ProtocolMetaInfoPB> getProtocolMetaInfoProxy(final Client.ConnectionId p0, final Configuration p1, final SocketFactory p2) throws IOException;
}
