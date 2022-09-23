// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authorize;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public abstract class PolicyProvider
{
    public static final String POLICY_PROVIDER_CONFIG = "hadoop.security.authorization.policyprovider";
    public static final PolicyProvider DEFAULT_POLICY_PROVIDER;
    
    public abstract Service[] getServices();
    
    static {
        DEFAULT_POLICY_PROVIDER = new PolicyProvider() {
            @Override
            public Service[] getServices() {
                return null;
            }
        };
    }
}
