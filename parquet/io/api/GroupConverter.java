// 
// Decompiled by Procyon v0.5.36
// 

package parquet.io.api;

public abstract class GroupConverter extends Converter
{
    @Override
    public boolean isPrimitive() {
        return false;
    }
    
    @Override
    public GroupConverter asGroupConverter() {
        return this;
    }
    
    public abstract Converter getConverter(final int p0);
    
    public abstract void start();
    
    public abstract void end();
}
