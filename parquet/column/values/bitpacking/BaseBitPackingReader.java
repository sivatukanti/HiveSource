// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.bitpacking;

import parquet.bytes.BytesUtils;

abstract class BaseBitPackingReader extends BitPacking.BitPackingReader
{
    int alignToBytes(final int bitsCount) {
        return BytesUtils.paddedByteCountFromBits(bitsCount);
    }
}
