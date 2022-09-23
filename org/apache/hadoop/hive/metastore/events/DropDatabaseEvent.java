// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.events;

import org.apache.hadoop.hive.metastore.HiveMetaStore;
import org.apache.hadoop.hive.metastore.api.Database;

public class DropDatabaseEvent extends ListenerEvent
{
    private final Database db;
    
    public DropDatabaseEvent(final Database db, final boolean status, final HiveMetaStore.HMSHandler handler) {
        super(status, handler);
        this.db = db;
    }
    
    public Database getDatabase() {
        return this.db;
    }
}
