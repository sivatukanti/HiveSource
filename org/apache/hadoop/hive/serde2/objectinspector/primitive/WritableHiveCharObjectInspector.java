// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.typeinfo.BaseCharTypeInfo;
import org.apache.hadoop.hive.serde2.io.HiveBaseCharWritable;
import org.apache.hadoop.hive.serde2.typeinfo.BaseCharUtils;
import org.apache.hadoop.hive.common.type.HiveBaseChar;
import org.apache.hadoop.hive.serde2.io.HiveCharWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.common.type.HiveChar;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.CharTypeInfo;

public class WritableHiveCharObjectInspector extends AbstractPrimitiveWritableObjectInspector implements SettableHiveCharObjectInspector
{
    public WritableHiveCharObjectInspector() {
    }
    
    public WritableHiveCharObjectInspector(final CharTypeInfo typeInfo) {
        super(typeInfo);
    }
    
    @Override
    public HiveChar getPrimitiveJavaObject(final Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Text) {
            final String str = ((Text)o).toString();
            return new HiveChar(str, ((CharTypeInfo)this.typeInfo).getLength());
        }
        final HiveCharWritable writable = (HiveCharWritable)o;
        if (this.doesWritableMatchTypeParams(writable)) {
            return writable.getHiveChar();
        }
        return this.getPrimitiveWithParams(writable);
    }
    
    @Override
    public HiveCharWritable getPrimitiveWritableObject(final Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Text) {
            final String str = ((Text)o).toString();
            final HiveCharWritable hcw = new HiveCharWritable();
            hcw.set(str, ((CharTypeInfo)this.typeInfo).getLength());
            return hcw;
        }
        final HiveCharWritable writable = (HiveCharWritable)o;
        if (this.doesWritableMatchTypeParams((HiveCharWritable)o)) {
            return writable;
        }
        return this.getWritableWithParams(writable);
    }
    
    private HiveChar getPrimitiveWithParams(final HiveCharWritable val) {
        final HiveChar hv = new HiveChar();
        hv.setValue(val.getHiveChar(), this.getMaxLength());
        return hv;
    }
    
    private HiveCharWritable getWritableWithParams(final HiveCharWritable val) {
        final HiveCharWritable newValue = new HiveCharWritable();
        newValue.set(val, this.getMaxLength());
        return newValue;
    }
    
    private boolean doesWritableMatchTypeParams(final HiveCharWritable writable) {
        return BaseCharUtils.doesWritableMatchTypeParams(writable, (BaseCharTypeInfo)this.typeInfo);
    }
    
    @Override
    public Object copyObject(final Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Text) {
            final String str = ((Text)o).toString();
            final HiveCharWritable hcw = new HiveCharWritable();
            hcw.set(str, ((CharTypeInfo)this.typeInfo).getLength());
            return hcw;
        }
        final HiveCharWritable writable = (HiveCharWritable)o;
        if (this.doesWritableMatchTypeParams((HiveCharWritable)o)) {
            return new HiveCharWritable(writable);
        }
        return this.getWritableWithParams(writable);
    }
    
    @Override
    public Object set(final Object o, final HiveChar value) {
        if (value == null) {
            return null;
        }
        final HiveCharWritable writable = (HiveCharWritable)o;
        writable.set(value, this.getMaxLength());
        return o;
    }
    
    @Override
    public Object set(final Object o, final String value) {
        if (value == null) {
            return null;
        }
        final HiveCharWritable writable = (HiveCharWritable)o;
        writable.set(value, this.getMaxLength());
        return o;
    }
    
    @Override
    public Object create(final HiveChar value) {
        final HiveCharWritable ret = new HiveCharWritable();
        ret.set(value, this.getMaxLength());
        return ret;
    }
    
    public int getMaxLength() {
        return ((CharTypeInfo)this.typeInfo).getLength();
    }
}
