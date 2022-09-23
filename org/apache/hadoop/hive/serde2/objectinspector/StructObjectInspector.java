// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

import java.util.List;

public abstract class StructObjectInspector implements ObjectInspector
{
    public abstract List<? extends StructField> getAllStructFieldRefs();
    
    public abstract StructField getStructFieldRef(final String p0);
    
    public abstract Object getStructFieldData(final Object p0, final StructField p1);
    
    public abstract List<Object> getStructFieldsDataAsList(final Object p0);
    
    public boolean isSettable() {
        return false;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final List<? extends StructField> fields = this.getAllStructFieldRefs();
        sb.append(this.getClass().getName());
        sb.append("<");
        for (int i = 0; i < fields.size(); ++i) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(((StructField)fields.get(i)).getFieldObjectInspector().toString());
        }
        sb.append(">");
        return sb.toString();
    }
}
