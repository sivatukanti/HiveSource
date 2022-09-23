// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.typeinfo.HiveDecimalUtils;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;

public abstract class AbstractPrimitiveObjectInspector implements PrimitiveObjectInspector
{
    protected PrimitiveTypeInfo typeInfo;
    
    protected AbstractPrimitiveObjectInspector() {
    }
    
    protected AbstractPrimitiveObjectInspector(final PrimitiveTypeInfo typeInfo) {
        this.typeInfo = typeInfo;
    }
    
    @Override
    public Class<?> getJavaPrimitiveClass() {
        return this.typeInfo.getPrimitiveJavaClass();
    }
    
    @Override
    public PrimitiveCategory getPrimitiveCategory() {
        return this.typeInfo.getPrimitiveCategory();
    }
    
    @Override
    public Class<?> getPrimitiveWritableClass() {
        return this.typeInfo.getPrimitiveWritableClass();
    }
    
    @Override
    public ObjectInspector.Category getCategory() {
        return ObjectInspector.Category.PRIMITIVE;
    }
    
    @Override
    public String getTypeName() {
        return this.typeInfo.getTypeName();
    }
    
    @Override
    public PrimitiveTypeInfo getTypeInfo() {
        return this.typeInfo;
    }
    
    @Override
    public int precision() {
        return HiveDecimalUtils.getPrecisionForType(this.typeInfo);
    }
    
    @Override
    public int scale() {
        return HiveDecimalUtils.getScaleForType(this.typeInfo);
    }
}
