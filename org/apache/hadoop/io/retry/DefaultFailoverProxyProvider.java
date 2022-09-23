// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.retry;

import java.io.IOException;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.classification.InterfaceStability;

@InterfaceStability.Evolving
public class DefaultFailoverProxyProvider<T> implements FailoverProxyProvider<T>
{
    private T proxy;
    private Class<T> iface;
    
    public DefaultFailoverProxyProvider(final Class<T> iface, final T proxy) {
        this.proxy = proxy;
        this.iface = iface;
    }
    
    @Override
    public Class<T> getInterface() {
        return this.iface;
    }
    
    @Override
    public ProxyInfo<T> getProxy() {
        return new ProxyInfo<T>(this.proxy, null);
    }
    
    @Override
    public void performFailover(final T currentProxy) {
    }
    
    @Override
    public void close() throws IOException {
        RPC.stopProxy(this.proxy);
    }
}
