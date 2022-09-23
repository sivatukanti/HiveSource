// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.local;

import java.util.concurrent.ConcurrentHashMap;
import org.jboss.netty.channel.Channel;
import java.util.concurrent.ConcurrentMap;

final class LocalChannelRegistry
{
    private static final ConcurrentMap<LocalAddress, Channel> map;
    
    static boolean isRegistered(final LocalAddress address) {
        return LocalChannelRegistry.map.containsKey(address);
    }
    
    static Channel getChannel(final LocalAddress address) {
        return LocalChannelRegistry.map.get(address);
    }
    
    static boolean register(final LocalAddress address, final Channel channel) {
        return LocalChannelRegistry.map.putIfAbsent(address, channel) == null;
    }
    
    static boolean unregister(final LocalAddress address) {
        return LocalChannelRegistry.map.remove(address) != null;
    }
    
    private LocalChannelRegistry() {
    }
    
    static {
        map = new ConcurrentHashMap<LocalAddress, Channel>();
    }
}
