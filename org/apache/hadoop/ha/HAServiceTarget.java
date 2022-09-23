// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha;

import com.google.common.collect.Maps;
import java.util.Map;
import org.apache.hadoop.ha.protocolPB.ZKFCProtocolClientSideTranslatorPB;
import javax.net.SocketFactory;
import org.apache.hadoop.ha.protocolPB.HAServiceProtocolClientSideTranslatorPB;
import org.apache.hadoop.net.NetUtils;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import java.net.InetSocketAddress;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class HAServiceTarget
{
    private static final String HOST_SUBST_KEY = "host";
    private static final String PORT_SUBST_KEY = "port";
    private static final String ADDRESS_SUBST_KEY = "address";
    
    public abstract InetSocketAddress getAddress();
    
    public InetSocketAddress getHealthMonitorAddress() {
        return null;
    }
    
    public abstract InetSocketAddress getZKFCAddress();
    
    public abstract NodeFencer getFencer();
    
    public abstract void checkFencingConfigured() throws BadFencingConfigurationException;
    
    public HAServiceProtocol getProxy(final Configuration conf, final int timeoutMs) throws IOException {
        return this.getProxyForAddress(conf, timeoutMs, this.getAddress());
    }
    
    public HAServiceProtocol getHealthMonitorProxy(final Configuration conf, final int timeoutMs) throws IOException {
        InetSocketAddress addr = this.getHealthMonitorAddress();
        if (addr == null) {
            addr = this.getAddress();
        }
        return this.getProxyForAddress(conf, timeoutMs, addr);
    }
    
    private HAServiceProtocol getProxyForAddress(final Configuration conf, final int timeoutMs, final InetSocketAddress addr) throws IOException {
        final Configuration confCopy = new Configuration(conf);
        confCopy.setInt("ipc.client.connect.max.retries", 1);
        final SocketFactory factory = NetUtils.getDefaultSocketFactory(confCopy);
        return new HAServiceProtocolClientSideTranslatorPB(addr, confCopy, factory, timeoutMs);
    }
    
    public ZKFCProtocol getZKFCProxy(final Configuration conf, final int timeoutMs) throws IOException {
        final Configuration confCopy = new Configuration(conf);
        confCopy.setInt("ipc.client.connect.max.retries", 1);
        final SocketFactory factory = NetUtils.getDefaultSocketFactory(confCopy);
        return new ZKFCProtocolClientSideTranslatorPB(this.getZKFCAddress(), confCopy, factory, timeoutMs);
    }
    
    public final Map<String, String> getFencingParameters() {
        final Map<String, String> ret = (Map<String, String>)Maps.newHashMap();
        this.addFencingParameters(ret);
        return ret;
    }
    
    protected void addFencingParameters(final Map<String, String> ret) {
        ret.put("address", String.valueOf(this.getAddress()));
        ret.put("host", this.getAddress().getHostName());
        ret.put("port", String.valueOf(this.getAddress().getPort()));
    }
    
    public boolean isAutoFailoverEnabled() {
        return false;
    }
}
