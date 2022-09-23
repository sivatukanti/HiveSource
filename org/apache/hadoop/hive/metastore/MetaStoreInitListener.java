// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configurable;

public abstract class MetaStoreInitListener implements Configurable
{
    private Configuration conf;
    
    public MetaStoreInitListener(final Configuration config) {
        this.conf = config;
    }
    
    public abstract void onInit(final MetaStoreInitContext p0) throws MetaException;
    
    @Override
    public Configuration getConf() {
        return this.conf;
    }
    
    @Override
    public void setConf(final Configuration config) {
        this.conf = config;
    }
}
