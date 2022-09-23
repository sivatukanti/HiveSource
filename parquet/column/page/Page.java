// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.page;

public abstract class Page
{
    private final int compressedSize;
    private final int uncompressedSize;
    
    Page(final int compressedSize, final int uncompressedSize) {
        this.compressedSize = compressedSize;
        this.uncompressedSize = uncompressedSize;
    }
    
    public int getCompressedSize() {
        return this.compressedSize;
    }
    
    public int getUncompressedSize() {
        return this.uncompressedSize;
    }
}
