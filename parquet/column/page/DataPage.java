// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.page;

public abstract class DataPage extends Page
{
    private final int valueCount;
    
    DataPage(final int compressedSize, final int uncompressedSize, final int valueCount) {
        super(compressedSize, uncompressedSize);
        this.valueCount = valueCount;
    }
    
    public int getValueCount() {
        return this.valueCount;
    }
    
    public abstract <T> T accept(final Visitor<T> p0);
    
    public interface Visitor<T>
    {
        T visit(final DataPageV1 p0);
        
        T visit(final DataPageV2 p0);
    }
}
