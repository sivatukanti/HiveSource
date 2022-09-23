// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.client;

import org.apache.commons.logging.LogFactory;
import com.google.common.base.Joiner;
import org.apache.hadoop.security.SecurityUtil;
import java.util.ArrayList;
import org.apache.hadoop.yarn.conf.HAUtil;
import org.apache.hadoop.io.Text;
import com.google.common.base.Preconditions;
import org.apache.hadoop.yarn.api.ApplicationMasterProtocol;
import org.apache.hadoop.yarn.server.api.ResourceManagerAdministrationProtocol;
import org.apache.hadoop.yarn.api.ApplicationClientProtocol;
import java.net.InetSocketAddress;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import java.util.Iterator;
import org.apache.hadoop.yarn.security.AMRMTokenIdentifier;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.UserGroupInformation;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class ClientRMProxy<T> extends RMProxy<T>
{
    private static final Log LOG;
    private static final ClientRMProxy INSTANCE;
    
    private ClientRMProxy() {
    }
    
    public static <T> T createRMProxy(final Configuration configuration, final Class<T> protocol) throws IOException {
        return RMProxy.createRMProxy(configuration, protocol, ClientRMProxy.INSTANCE);
    }
    
    private static void setAMRMTokenService(final Configuration conf) throws IOException {
        for (final Token<? extends TokenIdentifier> token : UserGroupInformation.getCurrentUser().getTokens()) {
            if (token.getKind().equals(AMRMTokenIdentifier.KIND_NAME)) {
                token.setService(getAMRMTokenService(conf));
            }
        }
    }
    
    @InterfaceAudience.Private
    @Override
    protected InetSocketAddress getRMAddress(final YarnConfiguration conf, final Class<?> protocol) throws IOException {
        if (protocol == ApplicationClientProtocol.class) {
            return conf.getSocketAddr("yarn.resourcemanager.address", "0.0.0.0:8032", 8032);
        }
        if (protocol == ResourceManagerAdministrationProtocol.class) {
            return conf.getSocketAddr("yarn.resourcemanager.admin.address", "0.0.0.0:8033", 8033);
        }
        if (protocol == ApplicationMasterProtocol.class) {
            setAMRMTokenService(conf);
            return conf.getSocketAddr("yarn.resourcemanager.scheduler.address", "0.0.0.0:8030", 8030);
        }
        final String message = "Unsupported protocol found when creating the proxy connection to ResourceManager: " + ((protocol != null) ? protocol.getClass().getName() : "null");
        ClientRMProxy.LOG.error(message);
        throw new IllegalStateException(message);
    }
    
    @InterfaceAudience.Private
    @Override
    protected void checkAllowedProtocols(final Class<?> protocol) {
        Preconditions.checkArgument(protocol.isAssignableFrom(ClientRMProtocols.class), (Object)"RM does not support this client protocol");
    }
    
    @InterfaceStability.Unstable
    public static Text getRMDelegationTokenService(final Configuration conf) {
        return getTokenService(conf, "yarn.resourcemanager.address", "0.0.0.0:8032", 8032);
    }
    
    @InterfaceStability.Unstable
    public static Text getAMRMTokenService(final Configuration conf) {
        return getTokenService(conf, "yarn.resourcemanager.scheduler.address", "0.0.0.0:8030", 8030);
    }
    
    @InterfaceStability.Unstable
    public static Text getTokenService(final Configuration conf, final String address, final String defaultAddr, final int defaultPort) {
        if (HAUtil.isHAEnabled(conf)) {
            final ArrayList<String> services = new ArrayList<String>();
            final YarnConfiguration yarnConf = new YarnConfiguration(conf);
            for (final String rmId : HAUtil.getRMHAIds(conf)) {
                yarnConf.set("yarn.resourcemanager.ha.id", rmId);
                services.add(SecurityUtil.buildTokenService(yarnConf.getSocketAddr(address, defaultAddr, defaultPort)).toString());
            }
            return new Text(Joiner.on(',').join(services));
        }
        return SecurityUtil.buildTokenService(conf.getSocketAddr(address, defaultAddr, defaultPort));
    }
    
    static {
        LOG = LogFactory.getLog(ClientRMProxy.class);
        INSTANCE = new ClientRMProxy();
    }
    
    private interface ClientRMProtocols extends ApplicationClientProtocol, ApplicationMasterProtocol, ResourceManagerAdministrationProtocol
    {
    }
}
