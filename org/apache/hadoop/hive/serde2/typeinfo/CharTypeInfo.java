// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.typeinfo;

public class CharTypeInfo extends BaseCharTypeInfo
{
    private static final long serialVersionUID = 1L;
    
    public CharTypeInfo() {
        super("char");
    }
    
    public CharTypeInfo(final int length) {
        super("char", length);
        BaseCharUtils.validateCharParameter(length);
    }
    
    @Override
    public String getTypeName() {
        return this.getQualifiedName();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == null || !(other instanceof CharTypeInfo)) {
            return false;
        }
        final CharTypeInfo pti = (CharTypeInfo)other;
        return this.typeName.equals(pti.typeName) && this.getLength() == pti.getLength();
    }
    
    @Override
    public int hashCode() {
        return this.getQualifiedName().hashCode();
    }
    
    @Override
    public String toString() {
        return this.getQualifiedName();
    }
}
