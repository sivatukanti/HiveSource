// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authorize;

import java.net.InetSocketAddress;
import java.util.HashSet;
import org.apache.hadoop.conf.Configuration;
import java.util.Collection;

public class ProxyServers
{
    public static final String CONF_HADOOP_PROXYSERVERS = "hadoop.proxyservers";
    private static volatile Collection<String> proxyServers;
    
    public static void refresh() {
        refresh(new Configuration());
    }
    
    public static void refresh(final Configuration conf) {
        final Collection<String> tempServers = new HashSet<String>();
        for (final String host : conf.getTrimmedStrings("hadoop.proxyservers")) {
            final InetSocketAddress addr = new InetSocketAddress(host, 0);
            if (!addr.isUnresolved()) {
                tempServers.add(addr.getAddress().getHostAddress());
            }
        }
        ProxyServers.proxyServers = tempServers;
    }
    
    public static boolean isProxyServer(final String remoteAddr) {
        if (ProxyServers.proxyServers == null) {
            refresh();
        }
        return ProxyServers.proxyServers.contains(remoteAddr);
    }
}
