// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.floats;

public abstract class AbstractFloatListIterator extends AbstractFloatBidirectionalIterator implements FloatListIterator
{
    protected AbstractFloatListIterator() {
    }
    
    @Override
    public void set(final Float ok) {
        this.set((float)ok);
    }
    
    @Override
    public void add(final Float ok) {
        this.add((float)ok);
    }
    
    @Override
    public void set(final float k) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void add(final float k) {
        throw new UnsupportedOperationException();
    }
}
