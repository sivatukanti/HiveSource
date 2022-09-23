// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.typeinfo;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import java.io.Serializable;

public final class ListTypeInfo extends TypeInfo implements Serializable
{
    private static final long serialVersionUID = 1L;
    private TypeInfo listElementTypeInfo;
    
    public ListTypeInfo() {
    }
    
    @Override
    public String getTypeName() {
        return "array<" + this.listElementTypeInfo.getTypeName() + ">";
    }
    
    public void setListElementTypeInfo(final TypeInfo listElementTypeInfo) {
        this.listElementTypeInfo = listElementTypeInfo;
    }
    
    ListTypeInfo(final TypeInfo elementTypeInfo) {
        this.listElementTypeInfo = elementTypeInfo;
    }
    
    @Override
    public ObjectInspector.Category getCategory() {
        return ObjectInspector.Category.LIST;
    }
    
    public TypeInfo getListElementTypeInfo() {
        return this.listElementTypeInfo;
    }
    
    @Override
    public boolean equals(final Object other) {
        return this == other || (other instanceof ListTypeInfo && this.getListElementTypeInfo().equals(((ListTypeInfo)other).getListElementTypeInfo()));
    }
    
    @Override
    public int hashCode() {
        return this.listElementTypeInfo.hashCode();
    }
}
