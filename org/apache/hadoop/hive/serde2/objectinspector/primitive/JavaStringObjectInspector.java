// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class JavaStringObjectInspector extends AbstractPrimitiveJavaObjectInspector implements SettableStringObjectInspector
{
    protected JavaStringObjectInspector() {
        super(TypeInfoFactory.stringTypeInfo);
    }
    
    @Override
    public Text getPrimitiveWritableObject(final Object o) {
        return (o == null) ? null : new Text(o.toString());
    }
    
    @Override
    public String getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : o.toString();
    }
    
    @Override
    public Object create(final Text value) {
        return (value == null) ? null : value.toString();
    }
    
    @Override
    public Object set(final Object o, final Text value) {
        return (value == null) ? null : value.toString();
    }
    
    @Override
    public Object create(final String value) {
        return value;
    }
    
    @Override
    public Object set(final Object o, final String value) {
        return value;
    }
}
