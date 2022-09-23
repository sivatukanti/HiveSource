// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.VoidObjectInspector;
import org.apache.hadoop.io.NullWritable;

public class LazyVoidObjectInspector extends AbstractPrimitiveLazyObjectInspector<NullWritable> implements VoidObjectInspector
{
    LazyVoidObjectInspector() {
        super(TypeInfoFactory.voidTypeInfo);
    }
    
    @Override
    public Object copyObject(final Object o) {
        return o;
    }
    
    @Override
    public Object getPrimitiveJavaObject(final Object o) {
        return null;
    }
}
