// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.hive.serde2.typeinfo.DecimalTypeInfo;
import org.apache.hadoop.hive.serde2.io.HiveDecimalWritable;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableHiveDecimalObjectInspector;

public class LazyBinaryHiveDecimal extends LazyBinaryPrimitive<WritableHiveDecimalObjectInspector, HiveDecimalWritable>
{
    private int precision;
    private int scale;
    
    LazyBinaryHiveDecimal(final WritableHiveDecimalObjectInspector oi) {
        super(oi);
        final DecimalTypeInfo typeInfo = (DecimalTypeInfo)oi.getTypeInfo();
        this.precision = typeInfo.precision();
        this.scale = typeInfo.scale();
        this.data = (T)new HiveDecimalWritable();
    }
    
    LazyBinaryHiveDecimal(final LazyBinaryHiveDecimal copy) {
        super(copy);
        this.data = (T)new HiveDecimalWritable((HiveDecimalWritable)copy.data);
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        ((HiveDecimalWritable)this.data).setFromBytes(bytes.getData(), start, length);
        final HiveDecimal dec = ((HiveDecimalWritable)this.data).getHiveDecimal(this.precision, this.scale);
        this.data = (T)((dec == null) ? null : new HiveDecimalWritable(dec));
    }
}
