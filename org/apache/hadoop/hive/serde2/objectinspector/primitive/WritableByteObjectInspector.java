// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.ByteWritable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class WritableByteObjectInspector extends AbstractPrimitiveWritableObjectInspector implements SettableByteObjectInspector
{
    public WritableByteObjectInspector() {
        super(TypeInfoFactory.byteTypeInfo);
    }
    
    @Override
    public byte get(final Object o) {
        return ((ByteWritable)o).get();
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new org.apache.hadoop.hive.serde2.io.ByteWritable(this.get(o));
    }
    
    @Override
    public Object getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : Byte.valueOf(this.get(o));
    }
    
    @Override
    public Object create(final byte value) {
        return new org.apache.hadoop.hive.serde2.io.ByteWritable(value);
    }
    
    @Override
    public Object set(final Object o, final byte value) {
        ((ByteWritable)o).set(value);
        return o;
    }
}
