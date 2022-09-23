// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

public class PartitionDropOptions
{
    public boolean deleteData;
    public boolean ignoreProtection;
    public boolean ifExists;
    public boolean returnResults;
    public boolean purgeData;
    
    public PartitionDropOptions() {
        this.deleteData = true;
        this.ignoreProtection = false;
        this.ifExists = false;
        this.returnResults = true;
        this.purgeData = false;
    }
    
    public static PartitionDropOptions instance() {
        return new PartitionDropOptions();
    }
    
    public PartitionDropOptions deleteData(final boolean deleteData) {
        this.deleteData = deleteData;
        return this;
    }
    
    public PartitionDropOptions ignoreProtection(final boolean ignoreProtection) {
        this.ignoreProtection = ignoreProtection;
        return this;
    }
    
    public PartitionDropOptions ifExists(final boolean ifExists) {
        this.ifExists = ifExists;
        return this;
    }
    
    public PartitionDropOptions returnResults(final boolean returnResults) {
        this.returnResults = returnResults;
        return this;
    }
    
    public PartitionDropOptions purgeData(final boolean purgeData) {
        this.purgeData = purgeData;
        return this;
    }
}
