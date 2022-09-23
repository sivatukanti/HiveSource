// 
// Decompiled by Procyon v0.5.36
// 

package parquet.example.data.simple;

import parquet.io.api.RecordConsumer;

public class IntegerValue extends Primitive
{
    private final int value;
    
    public IntegerValue(final int value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
    
    @Override
    public int getInteger() {
        return this.value;
    }
    
    @Override
    public void writeValue(final RecordConsumer recordConsumer) {
        recordConsumer.addInteger(this.value);
    }
}
