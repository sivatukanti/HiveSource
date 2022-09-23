// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.floats;

public abstract class AbstractFloatIterator implements FloatIterator
{
    protected AbstractFloatIterator() {
    }
    
    @Override
    public float nextFloat() {
        return this.next();
    }
    
    @Override
    public Float next() {
        return this.nextFloat();
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int skip(final int n) {
        int i = n;
        while (i-- != 0 && this.hasNext()) {
            this.nextFloat();
        }
        return n - i - 1;
    }
}
