// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.client;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.ipc.YarnRPC;
import java.security.PrivilegedAction;
import org.apache.hadoop.security.UserGroupInformation;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.hadoop.conf.Configuration;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class AHSProxy<T>
{
    private static final Log LOG;
    
    public static <T> T createAHSProxy(final Configuration conf, final Class<T> protocol, final InetSocketAddress ahsAddress) throws IOException {
        AHSProxy.LOG.info("Connecting to Application History server at " + ahsAddress);
        return (T)getProxy(conf, (Class<Object>)protocol, ahsAddress);
    }
    
    protected static <T> T getProxy(final Configuration conf, final Class<T> protocol, final InetSocketAddress rmAddress) throws IOException {
        return UserGroupInformation.getCurrentUser().doAs((PrivilegedAction<T>)new PrivilegedAction<T>() {
            @Override
            public T run() {
                return (T)YarnRPC.create(conf).getProxy(protocol, rmAddress, conf);
            }
        });
    }
    
    static {
        LOG = LogFactory.getLog(AHSProxy.class);
    }
}
