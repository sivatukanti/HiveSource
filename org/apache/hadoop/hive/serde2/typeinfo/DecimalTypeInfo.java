// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.typeinfo;

public class DecimalTypeInfo extends PrimitiveTypeInfo
{
    private static final long serialVersionUID = 1L;
    private int precision;
    private int scale;
    
    public DecimalTypeInfo() {
        super("decimal");
    }
    
    public DecimalTypeInfo(final int precision, final int scale) {
        super("decimal");
        HiveDecimalUtils.validateParameter(precision, scale);
        this.precision = precision;
        this.scale = scale;
    }
    
    @Override
    public String getTypeName() {
        return this.getQualifiedName();
    }
    
    @Override
    public void setTypeName(final String typeName) {
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == null || !(other instanceof DecimalTypeInfo)) {
            return false;
        }
        final DecimalTypeInfo dti = (DecimalTypeInfo)other;
        return this.precision() == dti.precision() && this.scale() == dti.scale();
    }
    
    @Override
    public int hashCode() {
        return 31 * (17 + this.precision) + this.scale;
    }
    
    @Override
    public String toString() {
        return this.getQualifiedName();
    }
    
    @Override
    public String getQualifiedName() {
        return getQualifiedName(this.precision, this.scale);
    }
    
    public static String getQualifiedName(final int precision, final int scale) {
        final StringBuilder sb = new StringBuilder("decimal");
        sb.append("(");
        sb.append(precision);
        sb.append(",");
        sb.append(scale);
        sb.append(")");
        return sb.toString();
    }
    
    public int precision() {
        return this.precision;
    }
    
    public int scale() {
        return this.scale;
    }
    
    @Override
    public boolean accept(final TypeInfo other) {
        if (other == null || !(other instanceof DecimalTypeInfo)) {
            return false;
        }
        final DecimalTypeInfo dti = (DecimalTypeInfo)other;
        return this.precision() - this.scale() >= dti.precision() - dti.scale();
    }
    
    public int getPrecision() {
        return this.precision;
    }
    
    public void setPrecision(final int precision) {
        this.precision = precision;
    }
    
    public int getScale() {
        return this.scale;
    }
    
    public void setScale(final int scale) {
        this.scale = scale;
    }
}
