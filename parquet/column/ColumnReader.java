// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column;

import parquet.io.api.Binary;

public interface ColumnReader
{
    long getTotalValueCount();
    
    void consume();
    
    int getCurrentRepetitionLevel();
    
    int getCurrentDefinitionLevel();
    
    void writeCurrentValueToConverter();
    
    void skip();
    
    int getCurrentValueDictionaryID();
    
    int getInteger();
    
    boolean getBoolean();
    
    long getLong();
    
    Binary getBinary();
    
    float getFloat();
    
    double getDouble();
    
    ColumnDescriptor getDescriptor();
}
