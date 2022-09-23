// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary.objectinspector;

import org.apache.hadoop.hive.serde2.lazybinary.LazyBinaryUnion;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import java.util.List;
import org.apache.hadoop.hive.serde2.objectinspector.StandardUnionObjectInspector;

public class LazyBinaryUnionObjectInspector extends StandardUnionObjectInspector
{
    protected LazyBinaryUnionObjectInspector() {
    }
    
    protected LazyBinaryUnionObjectInspector(final List<ObjectInspector> unionFieldObjectInspectors) {
        super(unionFieldObjectInspectors);
    }
    
    @Override
    public byte getTag(final Object o) {
        if (o == null) {
            return -1;
        }
        final LazyBinaryUnion lazyBinaryUnion = (LazyBinaryUnion)o;
        return lazyBinaryUnion.getTag();
    }
    
    @Override
    public Object getField(final Object o) {
        if (o == null) {
            return null;
        }
        final LazyBinaryUnion lazyBinaryUnion = (LazyBinaryUnion)o;
        return lazyBinaryUnion.getField();
    }
}
