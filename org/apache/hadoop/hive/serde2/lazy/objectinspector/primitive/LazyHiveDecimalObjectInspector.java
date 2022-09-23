// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.typeinfo.HiveDecimalUtils;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.serde2.lazy.LazyHiveDecimal;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.DecimalTypeInfo;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.HiveDecimalObjectInspector;
import org.apache.hadoop.hive.serde2.io.HiveDecimalWritable;

public class LazyHiveDecimalObjectInspector extends AbstractPrimitiveLazyObjectInspector<HiveDecimalWritable> implements HiveDecimalObjectInspector
{
    protected LazyHiveDecimalObjectInspector(final DecimalTypeInfo typeInfo) {
        super(typeInfo);
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new LazyHiveDecimal((LazyHiveDecimal)o);
    }
    
    @Override
    public HiveDecimal getPrimitiveJavaObject(final Object o) {
        if (o == null) {
            return null;
        }
        final HiveDecimal dec = ((LazyHiveDecimal)o).getWritableObject().getHiveDecimal();
        return HiveDecimalUtils.enforcePrecisionScale(dec, (DecimalTypeInfo)this.typeInfo);
    }
}
