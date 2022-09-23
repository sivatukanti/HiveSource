// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.typeinfo;

public class VarcharTypeInfo extends BaseCharTypeInfo
{
    private static final long serialVersionUID = 1L;
    
    public VarcharTypeInfo() {
        super("varchar");
    }
    
    public VarcharTypeInfo(final int length) {
        super("varchar", length);
        BaseCharUtils.validateVarcharParameter(length);
    }
    
    @Override
    public String getTypeName() {
        return this.getQualifiedName();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == null || !(other instanceof VarcharTypeInfo)) {
            return false;
        }
        final VarcharTypeInfo pti = (VarcharTypeInfo)other;
        return this.getLength() == pti.getLength();
    }
    
    @Override
    public int hashCode() {
        return this.getLength();
    }
    
    @Override
    public String toString() {
        return this.getQualifiedName();
    }
}
