// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class JavaVoidObjectInspector extends AbstractPrimitiveJavaObjectInspector implements VoidObjectInspector
{
    JavaVoidObjectInspector() {
        super(TypeInfoFactory.voidTypeInfo);
    }
    
    @Override
    public Object getPrimitiveWritableObject(final Object o) {
        return NullWritable.get();
    }
}
