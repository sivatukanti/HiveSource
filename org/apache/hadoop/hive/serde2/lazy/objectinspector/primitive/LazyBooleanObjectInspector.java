// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.lazy.LazyBoolean;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.BooleanObjectInspector;
import org.apache.hadoop.io.BooleanWritable;

public class LazyBooleanObjectInspector extends AbstractPrimitiveLazyObjectInspector<BooleanWritable> implements BooleanObjectInspector
{
    private boolean extendedLiteral;
    
    LazyBooleanObjectInspector() {
        super(TypeInfoFactory.booleanTypeInfo);
        this.extendedLiteral = false;
    }
    
    @Override
    public boolean get(final Object o) {
        return this.getPrimitiveWritableObject(o).get();
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new LazyBoolean((LazyBoolean)o);
    }
    
    @Override
    public Object getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : Boolean.valueOf(this.get(o));
    }
    
    public boolean isExtendedLiteral() {
        return this.extendedLiteral;
    }
    
    public void setExtendedLiteral(final boolean extendedLiteral) {
        this.extendedLiteral = extendedLiteral;
    }
}
