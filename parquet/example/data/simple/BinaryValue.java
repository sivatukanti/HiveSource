// 
// Decompiled by Procyon v0.5.36
// 

package parquet.example.data.simple;

import parquet.io.api.RecordConsumer;
import parquet.io.api.Binary;

public class BinaryValue extends Primitive
{
    private final Binary binary;
    
    public BinaryValue(final Binary binary) {
        this.binary = binary;
    }
    
    @Override
    public Binary getBinary() {
        return this.binary;
    }
    
    @Override
    public String getString() {
        return this.binary.toStringUsingUTF8();
    }
    
    @Override
    public void writeValue(final RecordConsumer recordConsumer) {
        recordConsumer.addBinary(this.binary);
    }
    
    @Override
    public String toString() {
        return this.getString();
    }
}
