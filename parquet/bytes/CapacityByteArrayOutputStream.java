// 
// Decompiled by Procyon v0.5.36
// 

package parquet.bytes;

import java.io.IOException;
import parquet.Preconditions;
import java.util.ArrayList;
import java.util.List;
import parquet.Log;
import java.io.OutputStream;

public class CapacityByteArrayOutputStream extends OutputStream
{
    private static final Log LOG;
    private static final byte[] EMPTY_SLAB;
    private int initialSlabSize;
    private final int maxCapacityHint;
    private final List<byte[]> slabs;
    private byte[] currentSlab;
    private int currentSlabIndex;
    private int bytesAllocated;
    private int bytesUsed;
    
    public static int initialSlabSizeHeuristic(final int minSlabSize, final int targetCapacity, final int targetNumSlabs) {
        return Math.max(minSlabSize, (int)(targetCapacity / Math.pow(2.0, targetNumSlabs)));
    }
    
    public static CapacityByteArrayOutputStream withTargetNumSlabs(final int minSlabSize, final int maxCapacityHint, final int targetNumSlabs) {
        return new CapacityByteArrayOutputStream(initialSlabSizeHeuristic(minSlabSize, maxCapacityHint, targetNumSlabs), maxCapacityHint);
    }
    
    @Deprecated
    public CapacityByteArrayOutputStream(final int initialSlabSize) {
        this(initialSlabSize, 1048576);
    }
    
    public CapacityByteArrayOutputStream(final int initialSlabSize, final int maxCapacityHint) {
        this.slabs = new ArrayList<byte[]>();
        this.bytesAllocated = 0;
        this.bytesUsed = 0;
        Preconditions.checkArgument(initialSlabSize > 0, "initialSlabSize must be > 0");
        Preconditions.checkArgument(maxCapacityHint > 0, "maxCapacityHint must be > 0");
        Preconditions.checkArgument(maxCapacityHint >= initialSlabSize, String.format("maxCapacityHint can't be less than initialSlabSize %d %d", initialSlabSize, maxCapacityHint));
        this.initialSlabSize = initialSlabSize;
        this.maxCapacityHint = maxCapacityHint;
        this.reset();
    }
    
    private void addSlab(final int minimumSize) {
        int nextSlabSize;
        if (this.bytesUsed == 0) {
            nextSlabSize = this.initialSlabSize;
        }
        else if (this.bytesUsed > this.maxCapacityHint / 5) {
            nextSlabSize = this.maxCapacityHint / 5;
        }
        else {
            nextSlabSize = this.bytesUsed;
        }
        if (nextSlabSize < minimumSize) {
            if (Log.DEBUG) {
                CapacityByteArrayOutputStream.LOG.debug(String.format("slab size %,d too small for value of size %,d. Bumping up slab size", nextSlabSize, minimumSize));
            }
            nextSlabSize = minimumSize;
        }
        if (Log.DEBUG) {
            CapacityByteArrayOutputStream.LOG.debug(String.format("used %d slabs, adding new slab of size %d", this.slabs.size(), nextSlabSize));
        }
        this.currentSlab = new byte[nextSlabSize];
        this.slabs.add(this.currentSlab);
        this.bytesAllocated += nextSlabSize;
        this.currentSlabIndex = 0;
    }
    
    @Override
    public void write(final int b) {
        if (this.currentSlabIndex == this.currentSlab.length) {
            this.addSlab(1);
        }
        this.currentSlab[this.currentSlabIndex] = (byte)b;
        ++this.currentSlabIndex;
        ++this.bytesUsed;
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) {
        if (off < 0 || off > b.length || len < 0 || off + len - b.length > 0) {
            throw new IndexOutOfBoundsException(String.format("Given byte array of size %d, with requested length(%d) and offset(%d)", b.length, len, off));
        }
        if (this.currentSlabIndex + len >= this.currentSlab.length) {
            final int length1 = this.currentSlab.length - this.currentSlabIndex;
            System.arraycopy(b, off, this.currentSlab, this.currentSlabIndex, length1);
            final int length2 = len - length1;
            this.addSlab(length2);
            System.arraycopy(b, off + length1, this.currentSlab, this.currentSlabIndex, length2);
            this.currentSlabIndex = length2;
        }
        else {
            System.arraycopy(b, off, this.currentSlab, this.currentSlabIndex, len);
            this.currentSlabIndex += len;
        }
        this.bytesUsed += len;
    }
    
    public void writeTo(final OutputStream out) throws IOException {
        for (int i = 0; i < this.slabs.size() - 1; ++i) {
            final byte[] slab = this.slabs.get(i);
            out.write(slab);
        }
        out.write(this.currentSlab, 0, this.currentSlabIndex);
    }
    
    public long size() {
        return this.bytesUsed;
    }
    
    public int getCapacity() {
        return this.bytesAllocated;
    }
    
    public void reset() {
        this.initialSlabSize = Math.max(this.bytesUsed / 7, this.initialSlabSize);
        if (Log.DEBUG) {
            CapacityByteArrayOutputStream.LOG.debug(String.format("initial slab of size %d", this.initialSlabSize));
        }
        this.slabs.clear();
        this.bytesAllocated = 0;
        this.bytesUsed = 0;
        this.currentSlab = CapacityByteArrayOutputStream.EMPTY_SLAB;
        this.currentSlabIndex = 0;
    }
    
    public long getCurrentIndex() {
        Preconditions.checkArgument(this.bytesUsed > 0, "This is an empty stream");
        return this.bytesUsed - 1;
    }
    
    public void setByte(final long index, final byte value) {
        Preconditions.checkArgument(index < this.bytesUsed, "Index: " + index + " is >= the current size of: " + this.bytesUsed);
        long seen = 0L;
        for (int i = 0; i < this.slabs.size(); ++i) {
            final byte[] slab = this.slabs.get(i);
            if (index < seen + slab.length) {
                slab[(int)(index - seen)] = value;
                break;
            }
            seen += slab.length;
        }
    }
    
    public String memUsageString(final String prefix) {
        return String.format("%s %s %d slabs, %,d bytes", prefix, this.getClass().getSimpleName(), this.slabs.size(), this.getCapacity());
    }
    
    int getSlabCount() {
        return this.slabs.size();
    }
    
    static {
        LOG = Log.getLog(CapacityByteArrayOutputStream.class);
        EMPTY_SLAB = new byte[0];
    }
}
