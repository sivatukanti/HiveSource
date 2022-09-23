// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.serde2.typeinfo.BaseCharTypeInfo;
import org.apache.hadoop.hive.serde2.io.HiveBaseCharWritable;
import org.apache.hadoop.hive.serde2.typeinfo.BaseCharUtils;
import org.apache.hadoop.hive.common.type.HiveBaseChar;
import org.apache.hadoop.hive.serde2.io.HiveVarcharWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.common.type.HiveVarchar;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.VarcharTypeInfo;
import org.apache.commons.logging.Log;

public class WritableHiveVarcharObjectInspector extends AbstractPrimitiveWritableObjectInspector implements SettableHiveVarcharObjectInspector
{
    private static final Log LOG;
    
    public WritableHiveVarcharObjectInspector() {
    }
    
    public WritableHiveVarcharObjectInspector(final VarcharTypeInfo typeInfo) {
        super(typeInfo);
    }
    
    @Override
    public HiveVarchar getPrimitiveJavaObject(final Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Text) {
            final String str = ((Text)o).toString();
            return new HiveVarchar(str, ((VarcharTypeInfo)this.typeInfo).getLength());
        }
        final HiveVarcharWritable writable = (HiveVarcharWritable)o;
        if (this.doesWritableMatchTypeParams(writable)) {
            return writable.getHiveVarchar();
        }
        return this.getPrimitiveWithParams(writable);
    }
    
    @Override
    public HiveVarcharWritable getPrimitiveWritableObject(final Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Text) {
            final String str = ((Text)o).toString();
            final HiveVarcharWritable hcw = new HiveVarcharWritable();
            hcw.set(str, ((VarcharTypeInfo)this.typeInfo).getLength());
            return hcw;
        }
        final HiveVarcharWritable writable = (HiveVarcharWritable)o;
        if (this.doesWritableMatchTypeParams((HiveVarcharWritable)o)) {
            return writable;
        }
        return this.getWritableWithParams(writable);
    }
    
    private HiveVarchar getPrimitiveWithParams(final HiveVarcharWritable val) {
        final HiveVarchar hv = new HiveVarchar();
        hv.setValue(val.getHiveVarchar(), this.getMaxLength());
        return hv;
    }
    
    private HiveVarcharWritable getWritableWithParams(final HiveVarcharWritable val) {
        final HiveVarcharWritable newValue = new HiveVarcharWritable();
        newValue.set(val, this.getMaxLength());
        return newValue;
    }
    
    private boolean doesWritableMatchTypeParams(final HiveVarcharWritable writable) {
        return BaseCharUtils.doesWritableMatchTypeParams(writable, (BaseCharTypeInfo)this.typeInfo);
    }
    
    @Override
    public Object copyObject(final Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Text) {
            final String str = ((Text)o).toString();
            final HiveVarcharWritable hcw = new HiveVarcharWritable();
            hcw.set(str, ((VarcharTypeInfo)this.typeInfo).getLength());
            return hcw;
        }
        final HiveVarcharWritable writable = (HiveVarcharWritable)o;
        if (this.doesWritableMatchTypeParams((HiveVarcharWritable)o)) {
            return new HiveVarcharWritable(writable);
        }
        return this.getWritableWithParams(writable);
    }
    
    @Override
    public Object set(final Object o, final HiveVarchar value) {
        if (value == null) {
            return null;
        }
        final HiveVarcharWritable writable = (HiveVarcharWritable)o;
        writable.set(value, this.getMaxLength());
        return o;
    }
    
    @Override
    public Object set(final Object o, final String value) {
        if (value == null) {
            return null;
        }
        final HiveVarcharWritable writable = (HiveVarcharWritable)o;
        writable.set(value, this.getMaxLength());
        return o;
    }
    
    @Override
    public Object create(final HiveVarchar value) {
        final HiveVarcharWritable ret = new HiveVarcharWritable();
        ret.set(value, this.getMaxLength());
        return ret;
    }
    
    public int getMaxLength() {
        return ((VarcharTypeInfo)this.typeInfo).getLength();
    }
    
    static {
        LOG = LogFactory.getLog(WritableHiveVarcharObjectInspector.class);
    }
}
