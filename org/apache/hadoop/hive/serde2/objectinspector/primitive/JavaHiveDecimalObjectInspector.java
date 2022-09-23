// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.typeinfo.HiveDecimalUtils;
import java.math.BigInteger;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.serde2.io.HiveDecimalWritable;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.DecimalTypeInfo;

public class JavaHiveDecimalObjectInspector extends AbstractPrimitiveJavaObjectInspector implements SettableHiveDecimalObjectInspector
{
    public JavaHiveDecimalObjectInspector() {
    }
    
    public JavaHiveDecimalObjectInspector(final DecimalTypeInfo typeInfo) {
        super(typeInfo);
    }
    
    @Override
    public HiveDecimalWritable getPrimitiveWritableObject(final Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof String) {
            final HiveDecimal dec = this.enforcePrecisionScale(HiveDecimal.create((String)o));
            return (dec == null) ? null : new HiveDecimalWritable(dec);
        }
        final HiveDecimal dec = this.enforcePrecisionScale((HiveDecimal)o);
        return (dec == null) ? null : new HiveDecimalWritable(dec);
    }
    
    @Override
    public HiveDecimal getPrimitiveJavaObject(final Object o) {
        return this.enforcePrecisionScale((HiveDecimal)o);
    }
    
    @Override
    public Object set(final Object o, final byte[] bytes, final int scale) {
        return this.enforcePrecisionScale(HiveDecimal.create(new BigInteger(bytes), scale));
    }
    
    @Override
    public Object set(final Object o, final HiveDecimal t) {
        return this.enforcePrecisionScale(t);
    }
    
    @Override
    public Object set(final Object o, final HiveDecimalWritable t) {
        return (t == null) ? null : this.enforcePrecisionScale(t.getHiveDecimal());
    }
    
    @Override
    public Object create(final byte[] bytes, final int scale) {
        return HiveDecimal.create(new BigInteger(bytes), scale);
    }
    
    @Override
    public Object create(final HiveDecimal t) {
        return t;
    }
    
    private HiveDecimal enforcePrecisionScale(final HiveDecimal dec) {
        return HiveDecimalUtils.enforcePrecisionScale(dec, (DecimalTypeInfo)this.typeInfo);
    }
}
