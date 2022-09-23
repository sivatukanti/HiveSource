// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.bitpacking;

public abstract class BytePacker
{
    private final int bitWidth;
    
    BytePacker(final int bitWidth) {
        this.bitWidth = bitWidth;
    }
    
    public final int getBitWidth() {
        return this.bitWidth;
    }
    
    public abstract void pack8Values(final int[] p0, final int p1, final byte[] p2, final int p3);
    
    public abstract void pack32Values(final int[] p0, final int p1, final byte[] p2, final int p3);
    
    public abstract void unpack8Values(final byte[] p0, final int p1, final int[] p2, final int p3);
    
    public abstract void unpack32Values(final byte[] p0, final int p1, final int[] p2, final int p3);
}
