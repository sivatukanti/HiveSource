// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.typeinfo;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import java.io.Serializable;

public final class MapTypeInfo extends TypeInfo implements Serializable
{
    private static final long serialVersionUID = 1L;
    private TypeInfo mapKeyTypeInfo;
    private TypeInfo mapValueTypeInfo;
    
    public MapTypeInfo() {
    }
    
    @Override
    public String getTypeName() {
        return "map<" + this.mapKeyTypeInfo.getTypeName() + "," + this.mapValueTypeInfo.getTypeName() + ">";
    }
    
    public void setMapKeyTypeInfo(final TypeInfo mapKeyTypeInfo) {
        this.mapKeyTypeInfo = mapKeyTypeInfo;
    }
    
    public void setMapValueTypeInfo(final TypeInfo mapValueTypeInfo) {
        this.mapValueTypeInfo = mapValueTypeInfo;
    }
    
    MapTypeInfo(final TypeInfo keyTypeInfo, final TypeInfo valueTypeInfo) {
        this.mapKeyTypeInfo = keyTypeInfo;
        this.mapValueTypeInfo = valueTypeInfo;
    }
    
    @Override
    public ObjectInspector.Category getCategory() {
        return ObjectInspector.Category.MAP;
    }
    
    public TypeInfo getMapKeyTypeInfo() {
        return this.mapKeyTypeInfo;
    }
    
    public TypeInfo getMapValueTypeInfo() {
        return this.mapValueTypeInfo;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MapTypeInfo)) {
            return false;
        }
        final MapTypeInfo o = (MapTypeInfo)other;
        return o.getMapKeyTypeInfo().equals(this.getMapKeyTypeInfo()) && o.getMapValueTypeInfo().equals(this.getMapValueTypeInfo());
    }
    
    @Override
    public int hashCode() {
        return this.mapKeyTypeInfo.hashCode() ^ this.mapValueTypeInfo.hashCode();
    }
}
