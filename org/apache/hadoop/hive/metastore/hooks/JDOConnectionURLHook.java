// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.hooks;

import org.apache.hadoop.conf.Configuration;

public interface JDOConnectionURLHook
{
    String getJdoConnectionUrl(final Configuration p0) throws Exception;
    
    void notifyBadConnectionUrl(final String p0);
}
