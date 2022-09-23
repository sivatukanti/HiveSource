// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary.objectinspector;

import org.apache.hadoop.hive.serde2.lazybinary.LazyBinaryArray;
import java.util.List;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StandardListObjectInspector;

public class LazyBinaryListObjectInspector extends StandardListObjectInspector
{
    protected LazyBinaryListObjectInspector() {
    }
    
    protected LazyBinaryListObjectInspector(final ObjectInspector listElementObjectInspector) {
        super(listElementObjectInspector);
    }
    
    @Override
    public List<?> getList(final Object data) {
        if (data == null) {
            return null;
        }
        final LazyBinaryArray array = (LazyBinaryArray)data;
        return array.getList();
    }
    
    @Override
    public Object getListElement(final Object data, final int index) {
        if (data == null) {
            return null;
        }
        final LazyBinaryArray array = (LazyBinaryArray)data;
        return array.getListElementObject(index);
    }
    
    @Override
    public int getListLength(final Object data) {
        if (data == null) {
            return -1;
        }
        final LazyBinaryArray array = (LazyBinaryArray)data;
        return array.getListLength();
    }
}
