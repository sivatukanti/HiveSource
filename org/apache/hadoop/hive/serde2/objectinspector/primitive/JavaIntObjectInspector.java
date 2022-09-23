// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class JavaIntObjectInspector extends AbstractPrimitiveJavaObjectInspector implements SettableIntObjectInspector
{
    JavaIntObjectInspector() {
        super(TypeInfoFactory.intTypeInfo);
    }
    
    @Override
    public Object getPrimitiveWritableObject(final Object o) {
        return (o == null) ? null : new IntWritable((int)o);
    }
    
    @Override
    public int get(final Object o) {
        return (int)o;
    }
    
    @Override
    public Object create(final int value) {
        return value;
    }
    
    @Override
    public Object set(final Object o, final int value) {
        return value;
    }
}
