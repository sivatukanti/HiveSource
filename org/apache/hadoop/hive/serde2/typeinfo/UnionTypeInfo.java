// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.typeinfo;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class UnionTypeInfo extends TypeInfo implements Serializable
{
    private static final long serialVersionUID = 1L;
    private List<TypeInfo> allUnionObjectTypeInfos;
    
    public UnionTypeInfo() {
    }
    
    @Override
    public String getTypeName() {
        final StringBuilder sb = new StringBuilder();
        sb.append("uniontype<");
        for (int i = 0; i < this.allUnionObjectTypeInfos.size(); ++i) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(this.allUnionObjectTypeInfos.get(i).getTypeName());
        }
        sb.append(">");
        return sb.toString();
    }
    
    public void setAllUnionObjectTypeInfos(final List<TypeInfo> allUnionObjectTypeInfos) {
        this.allUnionObjectTypeInfos = allUnionObjectTypeInfos;
    }
    
    UnionTypeInfo(final List<TypeInfo> typeInfos) {
        (this.allUnionObjectTypeInfos = new ArrayList<TypeInfo>()).addAll(typeInfos);
    }
    
    @Override
    public ObjectInspector.Category getCategory() {
        return ObjectInspector.Category.UNION;
    }
    
    public List<TypeInfo> getAllUnionObjectTypeInfos() {
        return this.allUnionObjectTypeInfos;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UnionTypeInfo)) {
            return false;
        }
        final UnionTypeInfo o = (UnionTypeInfo)other;
        return o.getAllUnionObjectTypeInfos().equals(this.getAllUnionObjectTypeInfos());
    }
    
    @Override
    public int hashCode() {
        return this.allUnionObjectTypeInfos.hashCode();
    }
}
