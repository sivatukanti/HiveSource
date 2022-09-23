// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.client;

import org.apache.hadoop.ha.BadFencingConfigurationException;
import org.apache.hadoop.ha.NodeFencer;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.HAUtil;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import java.net.InetSocketAddress;
import org.apache.hadoop.ha.HAServiceTarget;

public class RMHAServiceTarget extends HAServiceTarget
{
    private final boolean autoFailoverEnabled;
    private final InetSocketAddress haAdminServiceAddress;
    
    public RMHAServiceTarget(final YarnConfiguration conf) throws IOException {
        this.autoFailoverEnabled = HAUtil.isAutomaticFailoverEnabled(conf);
        this.haAdminServiceAddress = conf.getSocketAddr("yarn.resourcemanager.admin.address", "0.0.0.0:8033", 8033);
    }
    
    @Override
    public InetSocketAddress getAddress() {
        return this.haAdminServiceAddress;
    }
    
    @Override
    public InetSocketAddress getZKFCAddress() {
        throw new UnsupportedOperationException("RMHAServiceTarget doesn't have a corresponding ZKFC address");
    }
    
    @Override
    public NodeFencer getFencer() {
        return null;
    }
    
    @Override
    public void checkFencingConfigured() throws BadFencingConfigurationException {
        throw new BadFencingConfigurationException("Fencer not configured");
    }
    
    @Override
    public boolean isAutoFailoverEnabled() {
        return this.autoFailoverEnabled;
    }
}
