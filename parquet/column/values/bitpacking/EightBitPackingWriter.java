// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.bitpacking;

import java.io.IOException;
import java.io.OutputStream;

class EightBitPackingWriter extends BitPacking.BitPackingWriter
{
    private OutputStream out;
    
    public EightBitPackingWriter(final OutputStream out) {
        this.out = out;
    }
    
    @Override
    public void write(final int val) throws IOException {
        this.out.write(val);
    }
    
    @Override
    public void finish() throws IOException {
        this.out = null;
    }
}
