// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.longs;

import java.io.Serializable;

public abstract class AbstractLong2IntFunction implements Long2IntFunction, Serializable
{
    private static final long serialVersionUID = -4940583368468432370L;
    protected int defRetValue;
    
    protected AbstractLong2IntFunction() {
    }
    
    @Override
    public void defaultReturnValue(final int rv) {
        this.defRetValue = rv;
    }
    
    @Override
    public int defaultReturnValue() {
        return this.defRetValue;
    }
    
    @Override
    public int put(final long key, final int value) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int remove(final long key) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean containsKey(final Object ok) {
        return this.containsKey((long)ok);
    }
    
    @Override
    public Integer get(final Object ok) {
        final long k = (long)ok;
        return this.containsKey(k) ? Integer.valueOf(this.get(k)) : null;
    }
    
    @Override
    public Integer put(final Long ok, final Integer ov) {
        final long k = ok;
        final boolean containsKey = this.containsKey(k);
        final int v = this.put(k, (int)ov);
        return containsKey ? Integer.valueOf(v) : null;
    }
    
    @Override
    public Integer remove(final Object ok) {
        final long k = (long)ok;
        final boolean containsKey = this.containsKey(k);
        final int v = this.remove(k);
        return containsKey ? Integer.valueOf(v) : null;
    }
}
