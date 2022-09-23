// 
// Decompiled by Procyon v0.5.36
// 

package parquet.example.data.simple;

import parquet.io.api.RecordConsumer;
import parquet.io.api.Binary;

public abstract class Primitive
{
    public String getString() {
        throw new UnsupportedOperationException();
    }
    
    public int getInteger() {
        throw new UnsupportedOperationException();
    }
    
    public long getLong() {
        throw new UnsupportedOperationException();
    }
    
    public boolean getBoolean() {
        throw new UnsupportedOperationException();
    }
    
    public Binary getBinary() {
        throw new UnsupportedOperationException();
    }
    
    public Binary getInt96() {
        throw new UnsupportedOperationException();
    }
    
    public float getFloat() {
        throw new UnsupportedOperationException();
    }
    
    public double getDouble() {
        throw new UnsupportedOperationException();
    }
    
    public abstract void writeValue(final RecordConsumer p0);
}
