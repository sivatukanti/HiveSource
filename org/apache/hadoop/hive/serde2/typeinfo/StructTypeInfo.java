// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.typeinfo;

import java.util.Iterator;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

public final class StructTypeInfo extends TypeInfo implements Serializable
{
    private static final long serialVersionUID = 1L;
    private ArrayList<String> allStructFieldNames;
    private ArrayList<TypeInfo> allStructFieldTypeInfos;
    
    public StructTypeInfo() {
    }
    
    @Override
    public String getTypeName() {
        final StringBuilder sb = new StringBuilder();
        sb.append("struct<");
        for (int i = 0; i < this.allStructFieldNames.size(); ++i) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(this.allStructFieldNames.get(i));
            sb.append(":");
            sb.append(this.allStructFieldTypeInfos.get(i).getTypeName());
        }
        sb.append(">");
        return sb.toString();
    }
    
    public void setAllStructFieldNames(final ArrayList<String> allStructFieldNames) {
        this.allStructFieldNames = allStructFieldNames;
    }
    
    public void setAllStructFieldTypeInfos(final ArrayList<TypeInfo> allStructFieldTypeInfos) {
        this.allStructFieldTypeInfos = allStructFieldTypeInfos;
    }
    
    StructTypeInfo(final List<String> names, final List<TypeInfo> typeInfos) {
        this.allStructFieldNames = new ArrayList<String>(names);
        this.allStructFieldTypeInfos = new ArrayList<TypeInfo>(typeInfos);
    }
    
    @Override
    public ObjectInspector.Category getCategory() {
        return ObjectInspector.Category.STRUCT;
    }
    
    public ArrayList<String> getAllStructFieldNames() {
        return this.allStructFieldNames;
    }
    
    public ArrayList<TypeInfo> getAllStructFieldTypeInfos() {
        return this.allStructFieldTypeInfos;
    }
    
    public TypeInfo getStructFieldTypeInfo(final String field) {
        final String fieldLowerCase = field.toLowerCase();
        for (int i = 0; i < this.allStructFieldNames.size(); ++i) {
            if (fieldLowerCase.equalsIgnoreCase(this.allStructFieldNames.get(i))) {
                return this.allStructFieldTypeInfos.get(i);
            }
        }
        throw new RuntimeException("cannot find field " + field + "(lowercase form: " + fieldLowerCase + ") in " + this.allStructFieldNames);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof StructTypeInfo)) {
            return false;
        }
        final StructTypeInfo o = (StructTypeInfo)other;
        final Iterator<String> namesIterator = this.getAllStructFieldNames().iterator();
        final Iterator<String> otherNamesIterator = o.getAllStructFieldNames().iterator();
        while (namesIterator.hasNext() && otherNamesIterator.hasNext()) {
            if (!namesIterator.next().equalsIgnoreCase(otherNamesIterator.next())) {
                return false;
            }
        }
        return !namesIterator.hasNext() && !otherNamesIterator.hasNext() && o.getAllStructFieldTypeInfos().equals(this.getAllStructFieldTypeInfos());
    }
    
    @Override
    public int hashCode() {
        return this.allStructFieldNames.hashCode() ^ this.allStructFieldTypeInfos.hashCode();
    }
}
