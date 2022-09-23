// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.hadoop.hive.metastore.api.MetaException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.hive.conf.HiveConf;

public interface MetaStoreThread
{
    void setHiveConf(final HiveConf p0);
    
    void setThreadId(final int p0);
    
    void init(final AtomicBoolean p0, final AtomicBoolean p1) throws MetaException;
    
    void start();
}
