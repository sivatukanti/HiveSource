// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2;

public class SerDeStats
{
    private long rawDataSize;
    private long rowCount;
    
    public SerDeStats() {
        this.rawDataSize = 0L;
        this.rowCount = 0L;
    }
    
    public long getRawDataSize() {
        return this.rawDataSize;
    }
    
    public void setRawDataSize(final long uSize) {
        this.rawDataSize = uSize;
    }
    
    public long getRowCount() {
        return this.rowCount;
    }
    
    public void setRowCount(final long rowCount) {
        this.rowCount = rowCount;
    }
}
