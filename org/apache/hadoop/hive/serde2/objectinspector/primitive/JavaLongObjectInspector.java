// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class JavaLongObjectInspector extends AbstractPrimitiveJavaObjectInspector implements SettableLongObjectInspector
{
    JavaLongObjectInspector() {
        super(TypeInfoFactory.longTypeInfo);
    }
    
    @Override
    public Object getPrimitiveWritableObject(final Object o) {
        return (o == null) ? null : new LongWritable((long)o);
    }
    
    @Override
    public long get(final Object o) {
        return (long)o;
    }
    
    @Override
    public Object create(final long value) {
        return value;
    }
    
    @Override
    public Object set(final Object o, final long value) {
        return value;
    }
}
