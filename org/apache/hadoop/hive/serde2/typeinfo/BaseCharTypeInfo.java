// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.typeinfo;

public abstract class BaseCharTypeInfo extends PrimitiveTypeInfo
{
    private static final long serialVersionUID = 1L;
    private int length;
    
    public BaseCharTypeInfo() {
    }
    
    public BaseCharTypeInfo(final String typeName) {
        super(typeName);
    }
    
    public BaseCharTypeInfo(final String typeName, final int length) {
        super(typeName);
        this.length = length;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public void setLength(final int length) {
        this.length = length;
    }
    
    @Override
    public String getQualifiedName() {
        return getQualifiedName(this.typeName, this.length);
    }
    
    public static String getQualifiedName(final String typeName, final int length) {
        final StringBuilder sb = new StringBuilder(typeName);
        sb.append("(");
        sb.append(length);
        sb.append(")");
        return sb.toString();
    }
    
    @Override
    public void setTypeName(final String typeName) {
    }
}
