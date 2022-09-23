// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.client;

import org.apache.commons.logging.LogFactory;
import java.util.Iterator;
import org.apache.hadoop.ipc.RPC;
import java.io.Closeable;
import org.apache.hadoop.io.retry.FailoverProxyProvider;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.Collection;
import org.apache.hadoop.yarn.conf.HAUtil;
import org.apache.hadoop.conf.Configuration;
import java.util.HashMap;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ConfiguredRMFailoverProxyProvider<T> implements RMFailoverProxyProvider<T>
{
    private static final Log LOG;
    private int currentProxyIndex;
    Map<String, T> proxies;
    private RMProxy<T> rmProxy;
    private Class<T> protocol;
    protected YarnConfiguration conf;
    protected String[] rmServiceIds;
    
    public ConfiguredRMFailoverProxyProvider() {
        this.currentProxyIndex = 0;
        this.proxies = new HashMap<String, T>();
    }
    
    @Override
    public void init(final Configuration configuration, final RMProxy<T> rmProxy, final Class<T> protocol) {
        this.rmProxy = rmProxy;
        this.protocol = protocol;
        this.rmProxy.checkAllowedProtocols(this.protocol);
        this.conf = new YarnConfiguration(configuration);
        final Collection<String> rmIds = HAUtil.getRMHAIds(this.conf);
        this.rmServiceIds = rmIds.toArray(new String[rmIds.size()]);
        this.conf.set("yarn.resourcemanager.ha.id", this.rmServiceIds[this.currentProxyIndex]);
        this.conf.setInt("ipc.client.connect.max.retries", this.conf.getInt("yarn.client.failover-retries", 0));
        this.conf.setInt("ipc.client.connect.max.retries.on.timeouts", this.conf.getInt("yarn.client.failover-retries-on-socket-timeouts", 0));
    }
    
    private T getProxyInternal() {
        try {
            final InetSocketAddress rmAddress = this.rmProxy.getRMAddress(this.conf, this.protocol);
            return RMProxy.getProxy(this.conf, this.protocol, rmAddress);
        }
        catch (IOException ioe) {
            ConfiguredRMFailoverProxyProvider.LOG.error("Unable to create proxy to the ResourceManager " + this.rmServiceIds[this.currentProxyIndex], ioe);
            return null;
        }
    }
    
    @Override
    public synchronized FailoverProxyProvider.ProxyInfo<T> getProxy() {
        final String rmId = this.rmServiceIds[this.currentProxyIndex];
        T current = this.proxies.get(rmId);
        if (current == null) {
            current = this.getProxyInternal();
            this.proxies.put(rmId, current);
        }
        return new FailoverProxyProvider.ProxyInfo<T>(current, rmId);
    }
    
    @Override
    public synchronized void performFailover(final T currentProxy) {
        this.currentProxyIndex = (this.currentProxyIndex + 1) % this.rmServiceIds.length;
        this.conf.set("yarn.resourcemanager.ha.id", this.rmServiceIds[this.currentProxyIndex]);
        ConfiguredRMFailoverProxyProvider.LOG.info("Failing over to " + this.rmServiceIds[this.currentProxyIndex]);
    }
    
    @Override
    public Class<T> getInterface() {
        return this.protocol;
    }
    
    @Override
    public synchronized void close() throws IOException {
        for (final T proxy : this.proxies.values()) {
            if (proxy instanceof Closeable) {
                ((Closeable)proxy).close();
            }
            else {
                RPC.stopProxy(proxy);
            }
        }
    }
    
    static {
        LOG = LogFactory.getLog(ConfiguredRMFailoverProxyProvider.class);
    }
}
