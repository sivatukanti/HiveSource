// 
// Decompiled by Procyon v0.5.36
// 

package parquet.example.data.simple;

import parquet.io.api.RecordConsumer;

public class LongValue extends Primitive
{
    private final long value;
    
    public LongValue(final long value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
    
    @Override
    public long getLong() {
        return this.value;
    }
    
    @Override
    public void writeValue(final RecordConsumer recordConsumer) {
        recordConsumer.addLong(this.value);
    }
}
