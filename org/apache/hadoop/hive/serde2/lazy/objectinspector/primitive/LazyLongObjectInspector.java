// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.lazy.LazyLong;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.LongObjectInspector;
import org.apache.hadoop.io.LongWritable;

public class LazyLongObjectInspector extends AbstractPrimitiveLazyObjectInspector<LongWritable> implements LongObjectInspector
{
    LazyLongObjectInspector() {
        super(TypeInfoFactory.longTypeInfo);
    }
    
    @Override
    public long get(final Object o) {
        return this.getPrimitiveWritableObject(o).get();
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new LazyLong((LazyLong)o);
    }
    
    @Override
    public Object getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : Long.valueOf(this.get(o));
    }
}
