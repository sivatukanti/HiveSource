// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.HiveVarcharWritable;
import org.apache.hadoop.hive.serde2.typeinfo.BaseCharTypeInfo;
import org.apache.hadoop.hive.common.type.HiveBaseChar;
import org.apache.hadoop.hive.serde2.typeinfo.BaseCharUtils;
import org.apache.hadoop.hive.common.type.HiveVarchar;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.VarcharTypeInfo;

public class JavaHiveVarcharObjectInspector extends AbstractPrimitiveJavaObjectInspector implements SettableHiveVarcharObjectInspector
{
    public JavaHiveVarcharObjectInspector() {
    }
    
    public JavaHiveVarcharObjectInspector(final VarcharTypeInfo typeInfo) {
        super(typeInfo);
    }
    
    @Override
    public HiveVarchar getPrimitiveJavaObject(final Object o) {
        if (o == null) {
            return null;
        }
        final HiveVarchar value = (HiveVarchar)o;
        if (BaseCharUtils.doesPrimitiveMatchTypeParams(value, (BaseCharTypeInfo)this.typeInfo)) {
            return value;
        }
        return this.getPrimitiveWithParams(value);
    }
    
    @Override
    public HiveVarcharWritable getPrimitiveWritableObject(final Object o) {
        if (o == null) {
            return null;
        }
        return this.getWritableWithParams((HiveVarchar)o);
    }
    
    @Override
    public Object set(final Object o, final HiveVarchar value) {
        if (BaseCharUtils.doesPrimitiveMatchTypeParams(value, (BaseCharTypeInfo)this.typeInfo)) {
            return value;
        }
        return new HiveVarchar(value, this.getMaxLength());
    }
    
    @Override
    public Object set(final Object o, final String value) {
        return new HiveVarchar(value, this.getMaxLength());
    }
    
    @Override
    public Object create(final HiveVarchar value) {
        return new HiveVarchar(value, this.getMaxLength());
    }
    
    public int getMaxLength() {
        final VarcharTypeInfo ti = (VarcharTypeInfo)this.typeInfo;
        return ti.getLength();
    }
    
    private HiveVarchar getPrimitiveWithParams(final HiveVarchar val) {
        return new HiveVarchar(val, this.getMaxLength());
    }
    
    private HiveVarcharWritable getWritableWithParams(final HiveVarchar val) {
        final HiveVarcharWritable newValue = new HiveVarcharWritable();
        newValue.set(val, this.getMaxLength());
        return newValue;
    }
}
