// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.typeinfo.HiveDecimalUtils;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.serde2.io.HiveDecimalWritable;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.DecimalTypeInfo;

public class WritableHiveDecimalObjectInspector extends AbstractPrimitiveWritableObjectInspector implements SettableHiveDecimalObjectInspector
{
    public WritableHiveDecimalObjectInspector() {
    }
    
    public WritableHiveDecimalObjectInspector(final DecimalTypeInfo typeInfo) {
        super(typeInfo);
    }
    
    @Override
    public HiveDecimalWritable getPrimitiveWritableObject(final Object o) {
        if (o == null) {
            return null;
        }
        return this.enforcePrecisionScale((HiveDecimalWritable)o);
    }
    
    @Override
    public HiveDecimal getPrimitiveJavaObject(final Object o) {
        if (o == null) {
            return null;
        }
        return this.enforcePrecisionScale(((HiveDecimalWritable)o).getHiveDecimal());
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new HiveDecimalWritable((HiveDecimalWritable)o);
    }
    
    @Override
    public Object set(final Object o, final byte[] bytes, final int scale) {
        final HiveDecimalWritable writable = (HiveDecimalWritable)this.create(bytes, scale);
        if (writable != null) {
            ((HiveDecimalWritable)o).set(writable);
            return o;
        }
        return null;
    }
    
    @Override
    public Object set(final Object o, final HiveDecimal t) {
        final HiveDecimal dec = this.enforcePrecisionScale(t);
        if (dec != null) {
            ((HiveDecimalWritable)o).set(dec);
            return o;
        }
        return null;
    }
    
    @Override
    public Object set(final Object o, final HiveDecimalWritable t) {
        final HiveDecimalWritable writable = this.enforcePrecisionScale(t);
        if (writable == null) {
            return null;
        }
        ((HiveDecimalWritable)o).set(writable);
        return o;
    }
    
    @Override
    public Object create(final byte[] bytes, final int scale) {
        return new HiveDecimalWritable(bytes, scale);
    }
    
    @Override
    public Object create(final HiveDecimal t) {
        return (t == null) ? null : new HiveDecimalWritable(t);
    }
    
    private HiveDecimal enforcePrecisionScale(final HiveDecimal dec) {
        return HiveDecimalUtils.enforcePrecisionScale(dec, (DecimalTypeInfo)this.typeInfo);
    }
    
    private HiveDecimalWritable enforcePrecisionScale(final HiveDecimalWritable writable) {
        return HiveDecimalUtils.enforcePrecisionScale(writable, (DecimalTypeInfo)this.typeInfo);
    }
}
