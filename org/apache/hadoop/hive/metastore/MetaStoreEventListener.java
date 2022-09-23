// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.hadoop.hive.metastore.events.InsertEvent;
import org.apache.hadoop.hive.metastore.events.AlterIndexEvent;
import org.apache.hadoop.hive.metastore.events.DropIndexEvent;
import org.apache.hadoop.hive.metastore.events.AddIndexEvent;
import org.apache.hadoop.hive.metastore.events.LoadPartitionDoneEvent;
import org.apache.hadoop.hive.metastore.events.DropDatabaseEvent;
import org.apache.hadoop.hive.metastore.events.CreateDatabaseEvent;
import org.apache.hadoop.hive.metastore.events.AlterPartitionEvent;
import org.apache.hadoop.hive.metastore.events.DropPartitionEvent;
import org.apache.hadoop.hive.metastore.events.AddPartitionEvent;
import org.apache.hadoop.hive.metastore.events.AlterTableEvent;
import org.apache.hadoop.hive.metastore.events.DropTableEvent;
import org.apache.hadoop.hive.metastore.events.CreateTableEvent;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.events.ConfigChangeEvent;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configurable;

public abstract class MetaStoreEventListener implements Configurable
{
    private Configuration conf;
    
    public MetaStoreEventListener(final Configuration config) {
        this.conf = config;
    }
    
    public void onConfigChange(final ConfigChangeEvent tableEvent) throws MetaException {
    }
    
    public void onCreateTable(final CreateTableEvent tableEvent) throws MetaException {
    }
    
    public void onDropTable(final DropTableEvent tableEvent) throws MetaException {
    }
    
    public void onAlterTable(final AlterTableEvent tableEvent) throws MetaException {
    }
    
    public void onAddPartition(final AddPartitionEvent partitionEvent) throws MetaException {
    }
    
    public void onDropPartition(final DropPartitionEvent partitionEvent) throws MetaException {
    }
    
    public void onAlterPartition(final AlterPartitionEvent partitionEvent) throws MetaException {
    }
    
    public void onCreateDatabase(final CreateDatabaseEvent dbEvent) throws MetaException {
    }
    
    public void onDropDatabase(final DropDatabaseEvent dbEvent) throws MetaException {
    }
    
    public void onLoadPartitionDone(final LoadPartitionDoneEvent partSetDoneEvent) throws MetaException {
    }
    
    public void onAddIndex(final AddIndexEvent indexEvent) throws MetaException {
    }
    
    public void onDropIndex(final DropIndexEvent indexEvent) throws MetaException {
    }
    
    public void onAlterIndex(final AlterIndexEvent indexEvent) throws MetaException {
    }
    
    public void onInsert(final InsertEvent insertEvent) throws MetaException {
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
