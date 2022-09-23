// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.lazy.LazyPrimitive;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.hive.serde2.lazy.LazyString;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import org.apache.hadoop.io.Text;

public class LazyStringObjectInspector extends AbstractPrimitiveLazyObjectInspector<Text> implements StringObjectInspector
{
    private boolean escaped;
    private byte escapeChar;
    
    protected LazyStringObjectInspector() {
    }
    
    LazyStringObjectInspector(final boolean escaped, final byte escapeChar) {
        super(TypeInfoFactory.stringTypeInfo);
        this.escaped = escaped;
        this.escapeChar = escapeChar;
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new LazyString((LazyString)o);
    }
    
    @Override
    public Text getPrimitiveWritableObject(final Object o) {
        return (o == null) ? null : ((LazyPrimitive<OI, Text>)o).getWritableObject();
    }
    
    @Override
    public String getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : ((LazyPrimitive<OI, Text>)o).getWritableObject().toString();
    }
    
    public boolean isEscaped() {
        return this.escaped;
    }
    
    public byte getEscapeChar() {
        return this.escapeChar;
    }
}
