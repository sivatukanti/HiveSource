// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.HiveCharWritable;
import org.apache.hadoop.hive.serde2.typeinfo.BaseCharTypeInfo;
import org.apache.hadoop.hive.common.type.HiveBaseChar;
import org.apache.hadoop.hive.serde2.typeinfo.BaseCharUtils;
import org.apache.hadoop.hive.common.type.HiveChar;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.CharTypeInfo;

public class JavaHiveCharObjectInspector extends AbstractPrimitiveJavaObjectInspector implements SettableHiveCharObjectInspector
{
    public JavaHiveCharObjectInspector() {
    }
    
    public JavaHiveCharObjectInspector(final CharTypeInfo typeInfo) {
        super(typeInfo);
    }
    
    @Override
    public HiveChar getPrimitiveJavaObject(final Object o) {
        if (o == null) {
            return null;
        }
        final HiveChar value = (HiveChar)o;
        if (BaseCharUtils.doesPrimitiveMatchTypeParams(value, (BaseCharTypeInfo)this.typeInfo)) {
            return value;
        }
        return this.getPrimitiveWithParams(value);
    }
    
    @Override
    public HiveCharWritable getPrimitiveWritableObject(final Object o) {
        if (o == null) {
            return null;
        }
        return this.getWritableWithParams((HiveChar)o);
    }
    
    private HiveChar getPrimitiveWithParams(final HiveChar val) {
        final HiveChar hc = new HiveChar(val, this.getMaxLength());
        return hc;
    }
    
    private HiveCharWritable getWritableWithParams(final HiveChar val) {
        final HiveCharWritable hcw = new HiveCharWritable();
        hcw.set(val, this.getMaxLength());
        return hcw;
    }
    
    @Override
    public Object set(final Object o, final HiveChar value) {
        if (BaseCharUtils.doesPrimitiveMatchTypeParams(value, (BaseCharTypeInfo)this.typeInfo)) {
            return value;
        }
        return new HiveChar(value, this.getMaxLength());
    }
    
    @Override
    public Object set(final Object o, final String value) {
        return new HiveChar(value, this.getMaxLength());
    }
    
    @Override
    public Object create(final HiveChar value) {
        final HiveChar hc = new HiveChar(value, this.getMaxLength());
        return hc;
    }
    
    public int getMaxLength() {
        final CharTypeInfo ti = (CharTypeInfo)this.typeInfo;
        return ti.getLength();
    }
}
