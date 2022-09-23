// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.typeinfo;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import java.io.Serializable;

public abstract class TypeInfo implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    protected TypeInfo() {
    }
    
    public abstract ObjectInspector.Category getCategory();
    
    public abstract String getTypeName();
    
    public String getQualifiedName() {
        return this.getTypeName();
    }
    
    @Override
    public String toString() {
        return this.getTypeName();
    }
    
    @Override
    public abstract boolean equals(final Object p0);
    
    @Override
    public abstract int hashCode();
    
    public boolean accept(final TypeInfo other) {
        return this.equals(other);
    }
}
