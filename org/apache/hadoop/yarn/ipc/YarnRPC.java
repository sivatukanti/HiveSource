// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.ipc;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.conf.Configuration;
import java.net.InetSocketAddress;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "MapReduce", "YARN" })
public abstract class YarnRPC
{
    private static final Log LOG;
    
    public abstract Object getProxy(final Class p0, final InetSocketAddress p1, final Configuration p2);
    
    public abstract void stopProxy(final Object p0, final Configuration p1);
    
    public abstract Server getServer(final Class p0, final Object p1, final InetSocketAddress p2, final Configuration p3, final SecretManager<? extends TokenIdentifier> p4, final int p5, final String p6);
    
    public Server getServer(final Class protocol, final Object instance, final InetSocketAddress addr, final Configuration conf, final SecretManager<? extends TokenIdentifier> secretManager, final int numHandlers) {
        return this.getServer(protocol, instance, addr, conf, secretManager, numHandlers, null);
    }
    
    public static YarnRPC create(final Configuration conf) {
        YarnRPC.LOG.debug("Creating YarnRPC for " + conf.get("yarn.ipc.rpc.class"));
        String clazzName = conf.get("yarn.ipc.rpc.class");
        if (clazzName == null) {
            clazzName = "org.apache.hadoop.yarn.ipc.HadoopYarnProtoRPC";
        }
        try {
            return (YarnRPC)Class.forName(clazzName).newInstance();
        }
        catch (Exception e) {
            throw new YarnRuntimeException(e);
        }
    }
    
    static {
        LOG = LogFactory.getLog(YarnRPC.class);
    }
}
