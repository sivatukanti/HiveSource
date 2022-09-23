// 
// Decompiled by Procyon v0.5.36
// 

package parquet.example.data.simple;

import parquet.io.api.RecordConsumer;

public class DoubleValue extends Primitive
{
    private final double value;
    
    public DoubleValue(final double value) {
        this.value = value;
    }
    
    @Override
    public double getDouble() {
        return this.value;
    }
    
    @Override
    public void writeValue(final RecordConsumer recordConsumer) {
        recordConsumer.addDouble(this.value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
