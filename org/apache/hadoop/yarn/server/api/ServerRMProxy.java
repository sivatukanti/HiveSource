// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api;

import org.apache.commons.logging.LogFactory;
import com.google.common.base.Preconditions;
import org.apache.hadoop.classification.InterfaceAudience;
import java.net.InetSocketAddress;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.commons.logging.Log;
import org.apache.hadoop.yarn.client.RMProxy;

public class ServerRMProxy<T> extends RMProxy<T>
{
    private static final Log LOG;
    private static final ServerRMProxy INSTANCE;
    
    private ServerRMProxy() {
    }
    
    public static <T> T createRMProxy(final Configuration configuration, final Class<T> protocol) throws IOException {
        return RMProxy.createRMProxy(configuration, protocol, ServerRMProxy.INSTANCE);
    }
    
    @InterfaceAudience.Private
    @Override
    protected InetSocketAddress getRMAddress(final YarnConfiguration conf, final Class<?> protocol) {
        if (protocol == ResourceTracker.class) {
            return conf.getSocketAddr("yarn.resourcemanager.resource-tracker.address", "0.0.0.0:8031", 8031);
        }
        final String message = "Unsupported protocol found when creating the proxy connection to ResourceManager: " + ((protocol != null) ? protocol.getClass().getName() : "null");
        ServerRMProxy.LOG.error(message);
        throw new IllegalStateException(message);
    }
    
    @InterfaceAudience.Private
    @Override
    protected void checkAllowedProtocols(final Class<?> protocol) {
        Preconditions.checkArgument(protocol.isAssignableFrom(ResourceTracker.class), (Object)"ResourceManager does not support this protocol");
    }
    
    static {
        LOG = LogFactory.getLog(ServerRMProxy.class);
        INSTANCE = new ServerRMProxy();
    }
}
