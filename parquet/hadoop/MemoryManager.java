// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop;

import java.util.Iterator;
import parquet.ParquetRuntimeException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import parquet.Log;

public class MemoryManager
{
    private static final Log LOG;
    static final float DEFAULT_MEMORY_POOL_RATIO = 0.95f;
    static final long DEFAULT_MIN_MEMORY_ALLOCATION = 1048576L;
    private final float memoryPoolRatio;
    private final long totalMemoryPool;
    private final long minMemoryAllocation;
    private final Map<InternalParquetRecordWriter, Long> writerList;
    
    public MemoryManager(final float ratio, final long minAllocation) {
        this.writerList = new HashMap<InternalParquetRecordWriter, Long>();
        this.checkRatio(ratio);
        this.memoryPoolRatio = ratio;
        this.minMemoryAllocation = minAllocation;
        this.totalMemoryPool = Math.round(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() * ratio);
        MemoryManager.LOG.debug(String.format("Allocated total memory pool is: %,d", this.totalMemoryPool));
    }
    
    private void checkRatio(final float ratio) {
        if (ratio <= 0.0f || ratio > 1.0f) {
            throw new IllegalArgumentException("The configured memory pool ratio " + ratio + " is " + "not between 0 and 1.");
        }
    }
    
    synchronized void addWriter(final InternalParquetRecordWriter writer, final Long allocation) {
        final Long oldValue = this.writerList.get(writer);
        if (oldValue == null) {
            this.writerList.put(writer, allocation);
            this.updateAllocation();
            return;
        }
        throw new IllegalArgumentException("[BUG] The Parquet Memory Manager should not add an instance of InternalParquetRecordWriter more than once. The Manager already contains the writer: " + writer);
    }
    
    synchronized void removeWriter(final InternalParquetRecordWriter writer) {
        if (this.writerList.containsKey(writer)) {
            this.writerList.remove(writer);
        }
        if (!this.writerList.isEmpty()) {
            this.updateAllocation();
        }
    }
    
    private void updateAllocation() {
        long totalAllocations = 0L;
        for (final Long allocation : this.writerList.values()) {
            totalAllocations += allocation;
        }
        double scale;
        if (totalAllocations <= this.totalMemoryPool) {
            scale = 1.0;
        }
        else {
            scale = this.totalMemoryPool / (double)totalAllocations;
            MemoryManager.LOG.warn(String.format("Total allocation exceeds %.2f%% (%,d bytes) of heap memory\nScaling row group sizes to %.2f%% for %d writers", 100.0f * this.memoryPoolRatio, this.totalMemoryPool, 100.0 * scale, this.writerList.size()));
        }
        int maxColCount = 0;
        for (final InternalParquetRecordWriter w : this.writerList.keySet()) {
            maxColCount = Math.max(w.getSchema().getColumns().size(), maxColCount);
        }
        for (final Map.Entry<InternalParquetRecordWriter, Long> entry : this.writerList.entrySet()) {
            final long newSize = (long)Math.floor(entry.getValue() * scale);
            if (scale < 1.0 && this.minMemoryAllocation > 0L && newSize < this.minMemoryAllocation) {
                throw new ParquetRuntimeException(String.format("New Memory allocation %d bytes is smaller than the minimum allocation size of %d bytes.", newSize, this.minMemoryAllocation)) {};
            }
            entry.getKey().setRowGroupSizeThreshold(newSize);
            MemoryManager.LOG.debug(String.format("Adjust block size from %,d to %,d for writer: %s", entry.getValue(), newSize, entry.getKey()));
        }
    }
    
    long getTotalMemoryPool() {
        return this.totalMemoryPool;
    }
    
    Map<InternalParquetRecordWriter, Long> getWriterList() {
        return this.writerList;
    }
    
    float getMemoryPoolRatio() {
        return this.memoryPoolRatio;
    }
    
    static {
        LOG = Log.getLog(MemoryManager.class);
    }
}
