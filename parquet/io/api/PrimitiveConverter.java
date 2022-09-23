// 
// Decompiled by Procyon v0.5.36
// 

package parquet.io.api;

import parquet.column.Dictionary;

public abstract class PrimitiveConverter extends Converter
{
    @Override
    public boolean isPrimitive() {
        return true;
    }
    
    @Override
    public PrimitiveConverter asPrimitiveConverter() {
        return this;
    }
    
    public boolean hasDictionarySupport() {
        return false;
    }
    
    public void setDictionary(final Dictionary dictionary) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
    
    public void addValueFromDictionary(final int dictionaryId) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
    
    public void addBinary(final Binary value) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
    
    public void addBoolean(final boolean value) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
    
    public void addDouble(final double value) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
    
    public void addFloat(final float value) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
    
    public void addInt(final int value) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
    
    public void addLong(final long value) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
}
