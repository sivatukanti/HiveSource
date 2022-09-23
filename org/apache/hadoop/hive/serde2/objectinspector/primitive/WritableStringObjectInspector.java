// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class WritableStringObjectInspector extends AbstractPrimitiveWritableObjectInspector implements SettableStringObjectInspector
{
    WritableStringObjectInspector() {
        super(TypeInfoFactory.stringTypeInfo);
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new Text((Text)o);
    }
    
    @Override
    public Text getPrimitiveWritableObject(final Object o) {
        return (o == null) ? null : ((Text)o);
    }
    
    @Override
    public String getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : ((Text)o).toString();
    }
    
    @Override
    public Object create(final Text value) {
        final Text r = new Text();
        if (value != null) {
            r.set(value);
        }
        return r;
    }
    
    @Override
    public Object create(final String value) {
        final Text r = new Text();
        if (value != null) {
            r.set(value);
        }
        return r;
    }
    
    @Override
    public Object set(final Object o, final Text value) {
        final Text r = (Text)o;
        if (value != null) {
            r.set(value);
        }
        return o;
    }
    
    @Override
    public Object set(final Object o, final String value) {
        final Text r = (Text)o;
        if (value != null) {
            r.set(value);
        }
        return o;
    }
}
