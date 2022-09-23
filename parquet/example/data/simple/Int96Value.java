// 
// Decompiled by Procyon v0.5.36
// 

package parquet.example.data.simple;

import parquet.io.api.RecordConsumer;
import parquet.io.api.Binary;

public class Int96Value extends Primitive
{
    private final Binary value;
    
    public Int96Value(final Binary value) {
        this.value = value;
    }
    
    @Override
    public Binary getInt96() {
        return this.value;
    }
    
    @Override
    public void writeValue(final RecordConsumer recordConsumer) {
        recordConsumer.addBinary(this.value);
    }
    
    @Override
    public String toString() {
        return "Int96Value{" + String.valueOf(this.value) + "}";
    }
}
