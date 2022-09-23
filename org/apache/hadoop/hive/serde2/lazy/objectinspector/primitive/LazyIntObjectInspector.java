// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.lazy.LazyInteger;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.IntObjectInspector;
import org.apache.hadoop.io.IntWritable;

public class LazyIntObjectInspector extends AbstractPrimitiveLazyObjectInspector<IntWritable> implements IntObjectInspector
{
    LazyIntObjectInspector() {
        super(TypeInfoFactory.intTypeInfo);
    }
    
    @Override
    public int get(final Object o) {
        return this.getPrimitiveWritableObject(o).get();
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new LazyInteger((LazyInteger)o);
    }
    
    @Override
    public Object getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : Integer.valueOf(this.get(o));
    }
}
