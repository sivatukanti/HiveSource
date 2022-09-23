// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.page;

public interface PageReader
{
    DictionaryPage readDictionaryPage();
    
    long getTotalValueCount();
    
    DataPage readPage();
}
