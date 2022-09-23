// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyVoidObjectInspector;

public class LazyVoid extends LazyPrimitive<LazyVoidObjectInspector, NullWritable>
{
    LazyVoid(final LazyVoidObjectInspector lazyVoidObjectInspector) {
        super(lazyVoidObjectInspector);
    }
    
    LazyVoid(final LazyPrimitive<LazyVoidObjectInspector, NullWritable> copy) {
        super(copy);
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
    }
}
