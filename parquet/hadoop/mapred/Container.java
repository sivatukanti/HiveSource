// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.mapred;

public class Container<T>
{
    T object;
    
    public void set(final T object) {
        this.object = object;
    }
    
    public T get() {
        return this.object;
    }
}
