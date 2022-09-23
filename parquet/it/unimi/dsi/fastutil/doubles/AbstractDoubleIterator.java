// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.doubles;

public abstract class AbstractDoubleIterator implements DoubleIterator
{
    protected AbstractDoubleIterator() {
    }
    
    @Override
    public double nextDouble() {
        return this.next();
    }
    
    @Override
    public Double next() {
        return this.nextDouble();
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int skip(final int n) {
        int i = n;
        while (i-- != 0 && this.hasNext()) {
            this.nextDouble();
        }
        return n - i - 1;
    }
}
