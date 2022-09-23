// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.hadoop.hive.metastore.api.LockLevel;
import org.apache.hadoop.hive.metastore.api.LockType;
import org.apache.hadoop.hive.metastore.api.LockComponent;

public class LockComponentBuilder
{
    private LockComponent component;
    private boolean tableNameSet;
    private boolean partNameSet;
    
    public LockComponentBuilder() {
        this.component = new LockComponent();
        final boolean b = false;
        this.partNameSet = b;
        this.tableNameSet = b;
    }
    
    public LockComponentBuilder setExclusive() {
        this.component.setType(LockType.EXCLUSIVE);
        return this;
    }
    
    public LockComponentBuilder setSemiShared() {
        this.component.setType(LockType.SHARED_WRITE);
        return this;
    }
    
    public LockComponentBuilder setShared() {
        this.component.setType(LockType.SHARED_READ);
        return this;
    }
    
    public LockComponentBuilder setDbName(final String dbName) {
        this.component.setDbname(dbName);
        return this;
    }
    
    public LockComponentBuilder setTableName(final String tableName) {
        this.component.setTablename(tableName);
        this.tableNameSet = true;
        return this;
    }
    
    public LockComponentBuilder setPartitionName(final String partitionName) {
        this.component.setPartitionname(partitionName);
        this.partNameSet = true;
        return this;
    }
    
    public LockComponent build() {
        LockLevel level = LockLevel.DB;
        if (this.tableNameSet) {
            level = LockLevel.TABLE;
        }
        if (this.partNameSet) {
            level = LockLevel.PARTITION;
        }
        this.component.setLevel(level);
        return this.component;
    }
}
