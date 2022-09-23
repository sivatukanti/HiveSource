// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.typeinfo;

import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import java.io.Serializable;

public class PrimitiveTypeInfo extends TypeInfo implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected String typeName;
    
    public PrimitiveTypeInfo() {
    }
    
    PrimitiveTypeInfo(final String typeName) {
        this.typeName = typeName;
    }
    
    @Override
    public ObjectInspector.Category getCategory() {
        return ObjectInspector.Category.PRIMITIVE;
    }
    
    public PrimitiveObjectInspector.PrimitiveCategory getPrimitiveCategory() {
        return this.getPrimitiveTypeEntry().primitiveCategory;
    }
    
    public Class<?> getPrimitiveWritableClass() {
        return this.getPrimitiveTypeEntry().primitiveWritableClass;
    }
    
    public Class<?> getPrimitiveJavaClass() {
        return this.getPrimitiveTypeEntry().primitiveJavaClass;
    }
    
    public void setTypeName(final String typeName) {
        this.typeName = typeName;
    }
    
    @Override
    public String getTypeName() {
        return this.typeName;
    }
    
    public PrimitiveObjectInspectorUtils.PrimitiveTypeEntry getPrimitiveTypeEntry() {
        return PrimitiveObjectInspectorUtils.getTypeEntryFromTypeName(this.typeName);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == null || !(other instanceof PrimitiveTypeInfo)) {
            return false;
        }
        final PrimitiveTypeInfo pti = (PrimitiveTypeInfo)other;
        return this.typeName.equals(pti.typeName);
    }
    
    @Override
    public int hashCode() {
        return this.typeName.hashCode();
    }
    
    @Override
    public String toString() {
        return this.typeName;
    }
}
