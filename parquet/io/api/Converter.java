// 
// Decompiled by Procyon v0.5.36
// 

package parquet.io.api;

public abstract class Converter
{
    public abstract boolean isPrimitive();
    
    public PrimitiveConverter asPrimitiveConverter() {
        throw new ClassCastException("Expected instance of primitive converter but got \"" + this.getClass().getName() + "\"");
    }
    
    public GroupConverter asGroupConverter() {
        throw new ClassCastException("Expected instance of group converter but got \"" + this.getClass().getName() + "\"");
    }
}
