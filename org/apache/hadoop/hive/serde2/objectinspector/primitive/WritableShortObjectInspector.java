// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.ShortWritable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class WritableShortObjectInspector extends AbstractPrimitiveWritableObjectInspector implements SettableShortObjectInspector
{
    WritableShortObjectInspector() {
        super(TypeInfoFactory.shortTypeInfo);
    }
    
    @Override
    public short get(final Object o) {
        return ((ShortWritable)o).get();
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new ShortWritable(((ShortWritable)o).get());
    }
    
    @Override
    public Object getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : Short.valueOf(((ShortWritable)o).get());
    }
    
    @Override
    public Object create(final short value) {
        return new ShortWritable(value);
    }
    
    @Override
    public Object set(final Object o, final short value) {
        ((ShortWritable)o).set(value);
        return o;
    }
}
