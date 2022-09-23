// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import java.util.AbstractMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configurable;

public abstract class MetaStoreEndFunctionListener implements Configurable
{
    private Configuration conf;
    
    public MetaStoreEndFunctionListener(final Configuration config) {
        this.conf = config;
    }
    
    public abstract void onEndFunction(final String p0, final MetaStoreEndFunctionContext p1);
    
    public void exportCounters(final AbstractMap<String, Long> counters) {
    }
    
    @Override
    public Configuration getConf() {
        return this.conf;
    }
    
    @Override
    public void setConf(final Configuration config) {
        this.conf = config;
    }
}
