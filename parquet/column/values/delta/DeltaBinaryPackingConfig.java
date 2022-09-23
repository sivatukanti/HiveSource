// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.delta;

import parquet.bytes.BytesInput;
import java.io.IOException;
import parquet.bytes.BytesUtils;
import java.io.InputStream;
import parquet.Preconditions;

class DeltaBinaryPackingConfig
{
    final int blockSizeInValues;
    final int miniBlockNumInABlock;
    final int miniBlockSizeInValues;
    
    public DeltaBinaryPackingConfig(final int blockSizeInValues, final int miniBlockNumInABlock) {
        this.blockSizeInValues = blockSizeInValues;
        this.miniBlockNumInABlock = miniBlockNumInABlock;
        final double miniSize = blockSizeInValues / (double)miniBlockNumInABlock;
        Preconditions.checkArgument(miniSize % 8.0 == 0.0, "miniBlockSize must be multiple of 8, but it's " + miniSize);
        this.miniBlockSizeInValues = (int)miniSize;
    }
    
    public static DeltaBinaryPackingConfig readConfig(final InputStream in) throws IOException {
        return new DeltaBinaryPackingConfig(BytesUtils.readUnsignedVarInt(in), BytesUtils.readUnsignedVarInt(in));
    }
    
    public BytesInput toBytesInput() {
        return BytesInput.concat(BytesInput.fromUnsignedVarInt(this.blockSizeInValues), BytesInput.fromUnsignedVarInt(this.miniBlockNumInABlock));
    }
}
