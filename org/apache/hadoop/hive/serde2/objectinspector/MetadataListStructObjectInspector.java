// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

import org.apache.hadoop.hive.serde2.ColumnSet;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MetadataListStructObjectInspector extends StandardStructObjectInspector
{
    static ConcurrentHashMap<List<List<String>>, MetadataListStructObjectInspector> cached;
    
    public static MetadataListStructObjectInspector getInstance(final List<String> columnNames) {
        final ArrayList<List<String>> key = new ArrayList<List<String>>(1);
        key.add(columnNames);
        MetadataListStructObjectInspector result = MetadataListStructObjectInspector.cached.get(columnNames);
        if (result == null) {
            result = new MetadataListStructObjectInspector(columnNames);
            final MetadataListStructObjectInspector prev = MetadataListStructObjectInspector.cached.putIfAbsent(key, result);
            if (prev != null) {
                result = prev;
            }
        }
        return result;
    }
    
    public static MetadataListStructObjectInspector getInstance(final List<String> columnNames, final List<String> columnComments) {
        final ArrayList<List<String>> key = new ArrayList<List<String>>(2);
        Collections.addAll((Collection<? super List>)key, (List[])new List[] { columnNames, columnComments });
        MetadataListStructObjectInspector result = MetadataListStructObjectInspector.cached.get(key);
        if (result == null) {
            result = new MetadataListStructObjectInspector(columnNames, columnComments);
            final MetadataListStructObjectInspector prev = MetadataListStructObjectInspector.cached.putIfAbsent(key, result);
            if (prev != null) {
                result = prev;
            }
        }
        return result;
    }
    
    static ArrayList<ObjectInspector> getFieldObjectInspectors(final int fields) {
        final ArrayList<ObjectInspector> r = new ArrayList<ObjectInspector>(fields);
        for (int i = 0; i < fields; ++i) {
            r.add(PrimitiveObjectInspectorFactory.getPrimitiveJavaObjectInspector(PrimitiveObjectInspector.PrimitiveCategory.STRING));
        }
        return r;
    }
    
    protected MetadataListStructObjectInspector() {
    }
    
    MetadataListStructObjectInspector(final List<String> columnNames) {
        super(columnNames, getFieldObjectInspectors(columnNames.size()));
    }
    
    public MetadataListStructObjectInspector(final List<String> columnNames, final List<String> columnComments) {
        super(columnNames, getFieldObjectInspectors(columnNames.size()), columnComments);
    }
    
    @Override
    public Object getStructFieldData(Object data, final StructField fieldRef) {
        if (data instanceof ColumnSet) {
            data = ((ColumnSet)data).col;
        }
        return super.getStructFieldData(data, fieldRef);
    }
    
    @Override
    public List<Object> getStructFieldsDataAsList(Object data) {
        if (data instanceof ColumnSet) {
            data = ((ColumnSet)data).col;
        }
        return super.getStructFieldsDataAsList(data);
    }
    
    static {
        MetadataListStructObjectInspector.cached = new ConcurrentHashMap<List<List<String>>, MetadataListStructObjectInspector>();
    }
}
