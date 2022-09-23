// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.objects;

import java.io.Serializable;

public abstract class AbstractObject2IntFunction<K> implements Object2IntFunction<K>, Serializable
{
    private static final long serialVersionUID = -4940583368468432370L;
    protected int defRetValue;
    
    protected AbstractObject2IntFunction() {
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
    public int put(final K key, final int value) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int removeInt(final Object key) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Integer get(final Object ok) {
        final Object k = ok;
        return this.containsKey(k) ? Integer.valueOf(this.getInt(k)) : null;
    }
    
    @Override
    public Integer put(final K ok, final Integer ov) {
        final K k = ok;
        final boolean containsKey = this.containsKey(k);
        final int v = this.put(k, (int)ov);
        return containsKey ? Integer.valueOf(v) : null;
    }
    
    @Override
    public Integer remove(final Object ok) {
        final Object k = ok;
        final boolean containsKey = this.containsKey(k);
        final int v = this.removeInt(k);
        return containsKey ? Integer.valueOf(v) : null;
    }
}
