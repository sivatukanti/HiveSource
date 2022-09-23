// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import org.apache.hadoop.security.token.TokenInfo;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;

@InterfaceStability.Evolving
@InterfaceAudience.LimitedPrivate({ "MapReduce", "HDFS" })
public abstract class SecurityInfo
{
    public abstract KerberosInfo getKerberosInfo(final Class<?> p0, final Configuration p1);
    
    public abstract TokenInfo getTokenInfo(final Class<?> p0, final Configuration p1);
}
