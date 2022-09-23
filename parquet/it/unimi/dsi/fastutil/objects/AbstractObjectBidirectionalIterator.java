// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.objects;

public abstract class AbstractObjectBidirectionalIterator<K> extends AbstractObjectIterator<K> implements ObjectBidirectionalIterator<K>
{
    protected AbstractObjectBidirectionalIterator() {
    }
    
    @Override
    public int back(final int n) {
        int i = n;
        while (i-- != 0 && this.hasPrevious()) {
            this.previous();
        }
        return n - i - 1;
    }
}
