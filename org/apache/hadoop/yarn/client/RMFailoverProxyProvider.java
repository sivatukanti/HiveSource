// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.client;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.io.retry.FailoverProxyProvider;

@InterfaceAudience.Private
public interface RMFailoverProxyProvider<T> extends FailoverProxyProvider<T>
{
    void init(final Configuration p0, final RMProxy<T> p1, final Class<T> p2);
}
