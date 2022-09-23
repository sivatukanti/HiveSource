// 
// Decompiled by Procyon v0.5.36
// 

package parquet.example.data.simple;

import parquet.io.api.RecordConsumer;

public class BooleanValue extends Primitive
{
    private final boolean bool;
    
    public BooleanValue(final boolean bool) {
        this.bool = bool;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.bool);
    }
    
    @Override
    public boolean getBoolean() {
        return this.bool;
    }
    
    @Override
    public void writeValue(final RecordConsumer recordConsumer) {
        recordConsumer.addBoolean(this.bool);
    }
}
