// 
// Decompiled by Procyon v0.5.36
// 

package parquet.io.api;

public abstract class RecordMaterializer<T>
{
    public abstract T getCurrentRecord();
    
    public void skipCurrentRecord() {
    }
    
    public abstract GroupConverter getRootConverter();
}
