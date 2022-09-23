// 
// Decompiled by Procyon v0.5.36
// 

package parquet.io;

public abstract class RecordReader<T>
{
    public abstract T read();
    
    public boolean shouldSkipCurrentRecord() {
        return false;
    }
}
