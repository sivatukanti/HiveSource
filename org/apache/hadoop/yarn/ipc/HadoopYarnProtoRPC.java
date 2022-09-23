// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.ipc;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.yarn.factory.providers.RpcFactoryProvider;
import org.apache.hadoop.conf.Configuration;
import java.net.InetSocketAddress;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "MapReduce", "YARN" })
public class HadoopYarnProtoRPC extends YarnRPC
{
    private static final Log LOG;
    
    @Override
    public Object getProxy(final Class protocol, final InetSocketAddress addr, final Configuration conf) {
        HadoopYarnProtoRPC.LOG.debug("Creating a HadoopYarnProtoRpc proxy for protocol " + protocol);
        return RpcFactoryProvider.getClientFactory(conf).getClient(protocol, 1L, addr, conf);
    }
    
    @Override
    public void stopProxy(final Object proxy, final Configuration conf) {
        RpcFactoryProvider.getClientFactory(conf).stopClient(proxy);
    }
    
    @Override
    public Server getServer(final Class protocol, final Object instance, final InetSocketAddress addr, final Configuration conf, final SecretManager<? extends TokenIdentifier> secretManager, final int numHandlers, final String portRangeConfig) {
        HadoopYarnProtoRPC.LOG.debug("Creating a HadoopYarnProtoRpc server for protocol " + protocol + " with " + numHandlers + " handlers");
        return RpcFactoryProvider.getServerFactory(conf).getServer(protocol, instance, addr, conf, secretManager, numHandlers, portRangeConfig);
    }
    
    static {
        LOG = LogFactory.getLog(HadoopYarnProtoRPC.class);
    }
}
