// 
// Decompiled by Procyon v0.5.36
// 

package parquet.encoding;

import parquet.encoding.bitpacking.ByteBasedBitPackingGenerator;
import parquet.encoding.bitpacking.IntBasedBitPackingGenerator;

public class Generator
{
    public static void main(final String[] args) throws Exception {
        IntBasedBitPackingGenerator.main(args);
        ByteBasedBitPackingGenerator.main(args);
    }
}
