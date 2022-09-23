// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.serde2.typeinfo.DecimalTypeInfo;
import org.apache.hadoop.hive.serde2.io.HiveDecimalWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class WritableConstantHiveDecimalObjectInspector extends WritableHiveDecimalObjectInspector implements ConstantObjectInspector
{
    private HiveDecimalWritable value;
    
    protected WritableConstantHiveDecimalObjectInspector() {
    }
    
    WritableConstantHiveDecimalObjectInspector(final DecimalTypeInfo typeInfo, final HiveDecimalWritable value) {
        super(typeInfo);
        this.value = value;
    }
    
    @Override
    public HiveDecimalWritable getWritableConstantValue() {
        final DecimalTypeInfo decTypeInfo = (DecimalTypeInfo)this.typeInfo;
        final HiveDecimal dec = (this.value == null) ? null : this.value.getHiveDecimal(decTypeInfo.precision(), decTypeInfo.scale());
        if (dec == null) {
            return null;
        }
        return new HiveDecimalWritable(dec);
    }
    
    @Override
    public int precision() {
        if (this.value == null) {
            return super.precision();
        }
        return this.value.getHiveDecimal().precision();
    }
    
    @Override
    public int scale() {
        if (this.value == null) {
            return super.scale();
        }
        return this.value.getHiveDecimal().scale();
    }
}
