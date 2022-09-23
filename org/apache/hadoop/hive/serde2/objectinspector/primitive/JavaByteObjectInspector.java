// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.ByteWritable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class JavaByteObjectInspector extends AbstractPrimitiveJavaObjectInspector implements SettableByteObjectInspector
{
    JavaByteObjectInspector() {
        super(TypeInfoFactory.byteTypeInfo);
    }
    
    @Override
    public Object getPrimitiveWritableObject(final Object o) {
        return (o == null) ? null : new ByteWritable((byte)o);
    }
    
    @Override
    public byte get(final Object o) {
        return (byte)o;
    }
    
    @Override
    public Object create(final byte value) {
        return value;
    }
    
    @Override
    public Object set(final Object o, final byte value) {
        return value;
    }
}
