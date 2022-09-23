// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.metadata;

import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class BlockMetaData
{
    private List<ColumnChunkMetaData> columns;
    private long rowCount;
    private long totalByteSize;
    private String path;
    
    public BlockMetaData() {
        this.columns = new ArrayList<ColumnChunkMetaData>();
    }
    
    public void setPath(final String path) {
        this.path = path;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public long getRowCount() {
        return this.rowCount;
    }
    
    public void setRowCount(final long rowCount) {
        this.rowCount = rowCount;
    }
    
    public long getTotalByteSize() {
        return this.totalByteSize;
    }
    
    public void setTotalByteSize(final long totalByteSize) {
        this.totalByteSize = totalByteSize;
    }
    
    public void addColumn(final ColumnChunkMetaData column) {
        this.columns.add(column);
    }
    
    public List<ColumnChunkMetaData> getColumns() {
        return Collections.unmodifiableList((List<? extends ColumnChunkMetaData>)this.columns);
    }
    
    public long getStartingPos() {
        return this.getColumns().get(0).getStartingPos();
    }
    
    @Override
    public String toString() {
        return "BlockMetaData{" + this.rowCount + ", " + this.totalByteSize + " " + this.columns + "}";
    }
    
    public long getCompressedSize() {
        long totalSize = 0L;
        for (final ColumnChunkMetaData col : this.getColumns()) {
            totalSize += col.getTotalSize();
        }
        return totalSize;
    }
}
