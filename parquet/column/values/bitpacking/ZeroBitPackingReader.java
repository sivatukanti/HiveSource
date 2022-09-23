// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.bitpacking;

import java.io.IOException;

class ZeroBitPackingReader extends BitPacking.BitPackingReader
{
    @Override
    public int read() throws IOException {
        return 0;
    }
}
