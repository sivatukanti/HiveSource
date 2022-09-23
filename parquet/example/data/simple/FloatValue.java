// 
// Decompiled by Procyon v0.5.36
// 

package parquet.example.data.simple;

import parquet.io.api.RecordConsumer;

public class FloatValue extends Primitive
{
    private final float value;
    
    public FloatValue(final float value) {
        this.value = value;
    }
    
    @Override
    public float getFloat() {
        return this.value;
    }
    
    @Override
    public void writeValue(final RecordConsumer recordConsumer) {
        recordConsumer.addFloat(this.value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
