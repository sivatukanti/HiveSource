// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

import org.apache.hadoop.hive.serde2.columnar.ColumnarStructBase;
import java.util.List;
import org.apache.hadoop.hive.serde2.BaseStructObjectInspector;

class ColumnarStructObjectInspector extends BaseStructObjectInspector
{
    protected ColumnarStructObjectInspector() {
    }
    
    public ColumnarStructObjectInspector(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors) {
        super(structFieldNames, structFieldObjectInspectors);
    }
    
    public ColumnarStructObjectInspector(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors, final List<String> structFieldComments) {
        super(structFieldNames, structFieldObjectInspectors, structFieldComments);
    }
    
    @Override
    public Object getStructFieldData(final Object data, final StructField fieldRef) {
        if (data == null) {
            return null;
        }
        final ColumnarStructBase struct = (ColumnarStructBase)data;
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
        final ColumnarStructBase struct = (ColumnarStructBase)data;
        return struct.getFieldsAsList();
    }
}
