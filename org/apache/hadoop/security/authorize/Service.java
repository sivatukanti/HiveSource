// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authorize;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class Service
{
    private String key;
    private Class<?> protocol;
    
    public Service(final String key, final Class<?> protocol) {
        this.key = key;
        this.protocol = protocol;
    }
    
    public String getServiceKey() {
        return this.key;
    }
    
    public Class<?> getProtocol() {
        return this.protocol;
    }
}
