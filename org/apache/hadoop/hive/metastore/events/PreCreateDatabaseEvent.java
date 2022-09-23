// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.events;

import org.apache.hadoop.hive.metastore.HiveMetaStore;
import org.apache.hadoop.hive.metastore.api.Database;

public class PreCreateDatabaseEvent extends PreEventContext
{
    private final Database db;
    
    public PreCreateDatabaseEvent(final Database db, final HiveMetaStore.HMSHandler handler) {
        super(PreEventType.CREATE_DATABASE, handler);
        this.db = db;
    }
    
    public Database getDatabase() {
        return this.db;
    }
}
