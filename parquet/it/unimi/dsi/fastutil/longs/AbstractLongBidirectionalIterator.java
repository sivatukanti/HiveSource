// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.longs;

public abstract class AbstractLongBidirectionalIterator extends AbstractLongIterator implements LongBidirectionalIterator
{
    protected AbstractLongBidirectionalIterator() {
    }
    
    @Override
    public long previousLong() {
        return this.previous();
    }
    
    @Override
    public Long previous() {
        return this.previousLong();
    }
    
    @Override
    public int back(final int n) {
        int i = n;
        while (i-- != 0 && this.hasPrevious()) {
            this.previousLong();
        }
        return n - i - 1;
    }
}
