// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter2.predicate;

public abstract class UserDefinedPredicate<T extends Comparable<T>>
{
    public abstract boolean keep(final T p0);
    
    public abstract boolean canDrop(final Statistics<T> p0);
    
    public abstract boolean inverseCanDrop(final Statistics<T> p0);
}
