// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.columnar;

import org.apache.hadoop.hive.serde2.lazybinary.LazyBinaryFactory;
import org.apache.hadoop.hive.serde2.lazy.LazyObjectBase;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import java.util.List;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;

public class LazyBinaryColumnarStruct extends ColumnarStructBase
{
    public LazyBinaryColumnarStruct(final ObjectInspector oi, final List<Integer> notSkippedColumnIDs) {
        super(oi, notSkippedColumnIDs);
    }
    
    @Override
    protected int getLength(final ObjectInspector objectInspector, final ByteArrayRef cachedByteArrayRef, final int start, final int length) {
        if (length == 0) {
            return -1;
        }
        final ObjectInspector.Category category = objectInspector.getCategory();
        if (category.equals(ObjectInspector.Category.PRIMITIVE)) {
            final PrimitiveObjectInspector.PrimitiveCategory primitiveCategory = ((PrimitiveObjectInspector)objectInspector).getPrimitiveCategory();
            if (primitiveCategory.equals(PrimitiveObjectInspector.PrimitiveCategory.STRING) && length == 1 && cachedByteArrayRef.getData()[start] == LazyBinaryColumnarSerDe.INVALID_UTF__SINGLE_BYTE[0]) {
                return 0;
            }
        }
        return length;
    }
    
    @Override
    protected LazyObjectBase createLazyObjectBase(final ObjectInspector objectInspector) {
        return LazyBinaryFactory.createLazyBinaryObject(objectInspector);
    }
}
