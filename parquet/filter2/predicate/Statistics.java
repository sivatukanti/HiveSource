// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter2.predicate;

import parquet.Preconditions;

public class Statistics<T>
{
    private final T min;
    private final T max;
    
    public Statistics(final T min, final T max) {
        this.min = Preconditions.checkNotNull(min, "min");
        this.max = Preconditions.checkNotNull(max, "max");
    }
    
    public T getMin() {
        return this.min;
    }
    
    public T getMax() {
        return this.max;
    }
}
