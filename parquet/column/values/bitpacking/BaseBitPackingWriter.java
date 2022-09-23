// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.bitpacking;

import java.io.IOException;
import java.io.OutputStream;

abstract class BaseBitPackingWriter extends BitPacking.BitPackingWriter
{
    void finish(final int numberOfBits, int buffer, final OutputStream out) throws IOException {
        final int padding = (numberOfBits % 8 == 0) ? 0 : (8 - numberOfBits % 8);
        buffer <<= padding;
        final int numberOfBytes = (numberOfBits + padding) / 8;
        for (int i = (numberOfBytes - 1) * 8; i >= 0; i -= 8) {
            out.write(buffer >>> i & 0xFF);
        }
    }
    
    void finish(final int numberOfBits, long buffer, final OutputStream out) throws IOException {
        final int padding = (numberOfBits % 8 == 0) ? 0 : (8 - numberOfBits % 8);
        buffer <<= padding;
        final int numberOfBytes = (numberOfBits + padding) / 8;
        for (int i = (numberOfBytes - 1) * 8; i >= 0; i -= 8) {
            out.write((int)(buffer >>> i) & 0xFF);
        }
    }
}
