// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.metadata;

import java.util.concurrent.ConcurrentHashMap;

public class Canonicalizer<T>
{
    private ConcurrentHashMap<T, T> canonicals;
    
    public Canonicalizer() {
        this.canonicals = new ConcurrentHashMap<T, T>();
    }
    
    public final T canonicalize(T value) {
        T canonical = this.canonicals.get(value);
        if (canonical == null) {
            value = this.toCanonical(value);
            final T existing = this.canonicals.putIfAbsent(value, value);
            if (existing == null) {
                canonical = value;
            }
            else {
                canonical = existing;
            }
        }
        return canonical;
    }
    
    protected T toCanonical(final T value) {
        return value;
    }
}
