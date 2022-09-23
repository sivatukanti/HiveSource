// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.txn;

import org.apache.hadoop.hive.metastore.api.CompactionType;

public class CompactionInfo implements Comparable<CompactionInfo>
{
    public long id;
    public String dbname;
    public String tableName;
    public String partName;
    public CompactionType type;
    public String runAs;
    public boolean tooManyAborts;
    private String fullPartitionName;
    private String fullTableName;
    
    public CompactionInfo(final String dbname, final String tableName, final String partName, final CompactionType type) {
        this.tooManyAborts = false;
        this.fullPartitionName = null;
        this.fullTableName = null;
        this.dbname = dbname;
        this.tableName = tableName;
        this.partName = partName;
        this.type = type;
    }
    
    CompactionInfo() {
        this.tooManyAborts = false;
        this.fullPartitionName = null;
        this.fullTableName = null;
    }
    
    public String getFullPartitionName() {
        if (this.fullPartitionName == null) {
            final StringBuilder buf = new StringBuilder(this.dbname);
            buf.append('.');
            buf.append(this.tableName);
            if (this.partName != null) {
                buf.append('.');
                buf.append(this.partName);
            }
            this.fullPartitionName = buf.toString();
        }
        return this.fullPartitionName;
    }
    
    public String getFullTableName() {
        if (this.fullTableName == null) {
            final StringBuilder buf = new StringBuilder(this.dbname);
            buf.append('.');
            buf.append(this.tableName);
            this.fullTableName = buf.toString();
        }
        return this.fullTableName;
    }
    
    public boolean isMajorCompaction() {
        return CompactionType.MAJOR == this.type;
    }
    
    @Override
    public int compareTo(final CompactionInfo o) {
        return this.getFullPartitionName().compareTo(o.getFullPartitionName());
    }
    
    @Override
    public String toString() {
        return "id:" + this.id + "," + "dbname:" + this.dbname + "," + "tableName:" + this.tableName + "," + "partName:" + this.partName + "," + "type:" + this.type + "," + "runAs:" + this.runAs + "," + "tooManyAborts:" + this.tooManyAborts;
    }
}
