// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.ShortWritable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class JavaShortObjectInspector extends AbstractPrimitiveJavaObjectInspector implements SettableShortObjectInspector
{
    JavaShortObjectInspector() {
        super(TypeInfoFactory.shortTypeInfo);
    }
    
    @Override
    public Object getPrimitiveWritableObject(final Object o) {
        return (o == null) ? null : new ShortWritable((short)o);
    }
    
    @Override
    public short get(final Object o) {
        return (short)o;
    }
    
    @Override
    public Object create(final short value) {
        return value;
    }
    
    @Override
    public Object set(final Object o, final short value) {
        return value;
    }
}
