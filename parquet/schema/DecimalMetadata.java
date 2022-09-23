// 
// Decompiled by Procyon v0.5.36
// 

package parquet.schema;

public class DecimalMetadata
{
    private final int precision;
    private final int scale;
    
    public DecimalMetadata(final int precision, final int scale) {
        this.precision = precision;
        this.scale = scale;
    }
    
    public int getPrecision() {
        return this.precision;
    }
    
    public int getScale() {
        return this.scale;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final DecimalMetadata that = (DecimalMetadata)o;
        return this.precision == that.precision && this.scale == that.scale;
    }
    
    @Override
    public int hashCode() {
        int result = this.precision;
        result = 31 * result + this.scale;
        return result;
    }
}
