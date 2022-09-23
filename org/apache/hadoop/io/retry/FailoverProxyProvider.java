// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.retry;

import org.apache.hadoop.classification.InterfaceStability;
import java.io.Closeable;

@InterfaceStability.Evolving
public interface FailoverProxyProvider<T> extends Closeable
{
    ProxyInfo<T> getProxy();
    
    void performFailover(final T p0);
    
    Class<T> getInterface();
    
    public static class ProxyInfo<T>
    {
        public T proxy;
        public String proxyInfo;
        
        public ProxyInfo(final T proxy, final String proxyInfo) {
            this.proxy = proxy;
            this.proxyInfo = proxyInfo;
        }
        
        private String proxyName() {
            return (this.proxy != null) ? this.proxy.getClass().getSimpleName() : "UnknownProxy";
        }
        
        public String getString(final String methodName) {
            return this.proxyName() + "." + methodName + " over " + this.proxyInfo;
        }
        
        @Override
        public String toString() {
            return this.proxyName() + " over " + this.proxyInfo;
        }
    }
}
