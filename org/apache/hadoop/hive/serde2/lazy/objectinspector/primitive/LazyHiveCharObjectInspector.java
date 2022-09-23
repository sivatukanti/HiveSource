// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.lazy.LazyPrimitive;
import org.apache.hadoop.hive.serde2.typeinfo.BaseCharTypeInfo;
import org.apache.hadoop.hive.common.type.HiveBaseChar;
import org.apache.hadoop.hive.serde2.typeinfo.BaseCharUtils;
import org.apache.hadoop.hive.common.type.HiveChar;
import org.apache.hadoop.hive.serde2.lazy.LazyHiveChar;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.CharTypeInfo;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.HiveCharObjectInspector;
import org.apache.hadoop.hive.serde2.io.HiveCharWritable;

public class LazyHiveCharObjectInspector extends AbstractPrimitiveLazyObjectInspector<HiveCharWritable> implements HiveCharObjectInspector
{
    private boolean escaped;
    private byte escapeChar;
    
    public LazyHiveCharObjectInspector() {
    }
    
    public LazyHiveCharObjectInspector(final CharTypeInfo typeInfo) {
        this(typeInfo, false, (byte)0);
    }
    
    public LazyHiveCharObjectInspector(final CharTypeInfo typeInfo, final boolean escaped, final byte escapeChar) {
        super(typeInfo);
        this.escaped = escaped;
        this.escapeChar = escapeChar;
    }
    
    @Override
    public Object copyObject(final Object o) {
        if (o == null) {
            return null;
        }
        final LazyHiveChar ret = new LazyHiveChar(this);
        ret.setValue((LazyHiveChar)o);
        return ret;
    }
    
    @Override
    public HiveChar getPrimitiveJavaObject(final Object o) {
        if (o == null) {
            return null;
        }
        final HiveChar ret = ((LazyPrimitive<OI, HiveCharWritable>)o).getWritableObject().getHiveChar();
        if (!BaseCharUtils.doesPrimitiveMatchTypeParams(ret, (BaseCharTypeInfo)this.typeInfo)) {
            final HiveChar newValue = new HiveChar(ret, ((CharTypeInfo)this.typeInfo).getLength());
            return newValue;
        }
        return ret;
    }
    
    public boolean isEscaped() {
        return this.escaped;
    }
    
    public byte getEscapeChar() {
        return this.escapeChar;
    }
    
    @Override
    public String toString() {
        return this.getTypeName();
    }
}
