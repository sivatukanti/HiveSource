// 
// Decompiled by Procyon v0.5.36
// 

package parquet.example.data.simple.convert;

import parquet.io.api.Binary;
import parquet.io.api.PrimitiveConverter;

class SimplePrimitiveConverter extends PrimitiveConverter
{
    private final SimpleGroupConverter parent;
    private final int index;
    
    SimplePrimitiveConverter(final SimpleGroupConverter parent, final int index) {
        this.parent = parent;
        this.index = index;
    }
    
    @Override
    public void addBinary(final Binary value) {
        this.parent.getCurrentRecord().add(this.index, value);
    }
    
    @Override
    public void addBoolean(final boolean value) {
        this.parent.getCurrentRecord().add(this.index, value);
    }
    
    @Override
    public void addDouble(final double value) {
        this.parent.getCurrentRecord().add(this.index, value);
    }
    
    @Override
    public void addFloat(final float value) {
        this.parent.getCurrentRecord().add(this.index, value);
    }
    
    @Override
    public void addInt(final int value) {
        this.parent.getCurrentRecord().add(this.index, value);
    }
    
    @Override
    public void addLong(final long value) {
        this.parent.getCurrentRecord().add(this.index, value);
    }
}
