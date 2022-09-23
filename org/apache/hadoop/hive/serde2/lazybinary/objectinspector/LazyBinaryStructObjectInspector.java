// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary.objectinspector;

import org.apache.hadoop.hive.serde2.lazybinary.LazyBinaryStruct;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import java.util.List;
import org.apache.hadoop.hive.serde2.objectinspector.StandardStructObjectInspector;

public class LazyBinaryStructObjectInspector extends StandardStructObjectInspector
{
    protected LazyBinaryStructObjectInspector() {
    }
    
    protected LazyBinaryStructObjectInspector(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors) {
        super(structFieldNames, structFieldObjectInspectors);
    }
    
    protected LazyBinaryStructObjectInspector(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors, final List<String> structFieldComments) {
        super(structFieldNames, structFieldObjectInspectors, structFieldComments);
    }
    
    protected LazyBinaryStructObjectInspector(final List<StructField> fields) {
        super(fields);
    }
    
    @Override
    public Object getStructFieldData(final Object data, final StructField fieldRef) {
        if (data == null) {
            return null;
        }
        final LazyBinaryStruct struct = (LazyBinaryStruct)data;
        final MyField f = (MyField)fieldRef;
        final int fieldID = f.getFieldID();
        assert fieldID >= 0 && fieldID < this.fields.size();
        return struct.getField(fieldID);
    }
    
    @Override
    public List<Object> getStructFieldsDataAsList(final Object data) {
        if (data == null) {
            return null;
        }
        final LazyBinaryStruct struct = (LazyBinaryStruct)data;
        return struct.getFieldsAsList();
    }
    
    public StructField getStructFieldRef(final int index) {
        return this.fields.get(index);
    }
}
