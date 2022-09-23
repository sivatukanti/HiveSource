// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.lazy.LazyPrimitive;
import org.apache.hadoop.hive.serde2.typeinfo.BaseCharTypeInfo;
import org.apache.hadoop.hive.common.type.HiveBaseChar;
import org.apache.hadoop.hive.serde2.typeinfo.BaseCharUtils;
import org.apache.hadoop.hive.common.type.HiveVarchar;
import org.apache.hadoop.hive.serde2.lazy.LazyHiveVarchar;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.VarcharTypeInfo;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.HiveVarcharObjectInspector;
import org.apache.hadoop.hive.serde2.io.HiveVarcharWritable;

public class LazyHiveVarcharObjectInspector extends AbstractPrimitiveLazyObjectInspector<HiveVarcharWritable> implements HiveVarcharObjectInspector
{
    private boolean escaped;
    private byte escapeChar;
    
    public LazyHiveVarcharObjectInspector() {
    }
    
    public LazyHiveVarcharObjectInspector(final VarcharTypeInfo typeInfo) {
        this(typeInfo, false, (byte)0);
    }
    
    public LazyHiveVarcharObjectInspector(final VarcharTypeInfo typeInfo, final boolean escaped, final byte escapeChar) {
        super(typeInfo);
        this.escaped = escaped;
        this.escapeChar = escapeChar;
    }
    
    @Override
    public Object copyObject(final Object o) {
        if (o == null) {
            return null;
        }
        final LazyHiveVarchar ret = new LazyHiveVarchar(this);
        ret.setValue((LazyHiveVarchar)o);
        return ret;
    }
    
    @Override
    public HiveVarchar getPrimitiveJavaObject(final Object o) {
        if (o == null) {
            return null;
        }
        final HiveVarchar ret = ((LazyPrimitive<OI, HiveVarcharWritable>)o).getWritableObject().getHiveVarchar();
        if (!BaseCharUtils.doesPrimitiveMatchTypeParams(ret, (BaseCharTypeInfo)this.typeInfo)) {
            final HiveVarchar newValue = new HiveVarchar(ret, ((VarcharTypeInfo)this.typeInfo).getLength());
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
