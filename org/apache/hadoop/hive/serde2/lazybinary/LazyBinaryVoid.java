// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableVoidObjectInspector;

public class LazyBinaryVoid extends LazyBinaryPrimitive<WritableVoidObjectInspector, Writable>
{
    LazyBinaryVoid(final WritableVoidObjectInspector oi) {
        super(oi);
    }
    
    LazyBinaryVoid(final LazyBinaryVoid copy) {
        super(copy);
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
    }
}
