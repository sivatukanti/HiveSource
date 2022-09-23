// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.hadoop.hive.metastore.api.InvalidOperationException;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.events.PreEventContext;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configurable;

public abstract class MetaStorePreEventListener implements Configurable
{
    private Configuration conf;
    
    public MetaStorePreEventListener(final Configuration config) {
        this.conf = config;
    }
    
    public abstract void onEvent(final PreEventContext p0) throws MetaException, NoSuchObjectException, InvalidOperationException;
    
    @Override
    public Configuration getConf() {
        return this.conf;
    }
    
    @Override
    public void setConf(final Configuration config) {
        this.conf = config;
    }
}
