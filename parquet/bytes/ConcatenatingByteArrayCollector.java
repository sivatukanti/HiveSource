// 
// Decompiled by Procyon v0.5.36
// 

package parquet.bytes;

import java.util.Iterator;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConcatenatingByteArrayCollector extends BytesInput
{
    private final List<byte[]> slabs;
    private long size;
    
    public ConcatenatingByteArrayCollector() {
        this.slabs = new ArrayList<byte[]>();
        this.size = 0L;
    }
    
    public void collect(final BytesInput bytesInput) throws IOException {
        final byte[] bytes = bytesInput.toByteArray();
        this.slabs.add(bytes);
        this.size += bytes.length;
    }
    
    public void reset() {
        this.size = 0L;
        this.slabs.clear();
    }
    
    @Override
    public void writeAllTo(final OutputStream out) throws IOException {
        for (final byte[] slab : this.slabs) {
            out.write(slab);
        }
    }
    
    @Override
    public long size() {
        return this.size;
    }
    
    public String memUsageString(final String prefix) {
        return String.format("%s %s %d slabs, %,d bytes", prefix, this.getClass().getSimpleName(), this.slabs.size(), this.size);
    }
}
