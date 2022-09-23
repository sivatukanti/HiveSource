// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;

public interface PrimitiveObjectInspector extends ObjectInspector
{
    PrimitiveTypeInfo getTypeInfo();
    
    PrimitiveCategory getPrimitiveCategory();
    
    Class<?> getPrimitiveWritableClass();
    
    Object getPrimitiveWritableObject(final Object p0);
    
    Class<?> getJavaPrimitiveClass();
    
    Object getPrimitiveJavaObject(final Object p0);
    
    Object copyObject(final Object p0);
    
    boolean preferWritable();
    
    int precision();
    
    int scale();
    
    public enum PrimitiveCategory
    {
        VOID, 
        BOOLEAN, 
        BYTE, 
        SHORT, 
        INT, 
        LONG, 
        FLOAT, 
        DOUBLE, 
        STRING, 
        DATE, 
        TIMESTAMP, 
        BINARY, 
        DECIMAL, 
        VARCHAR, 
        CHAR, 
        INTERVAL_YEAR_MONTH, 
        INTERVAL_DAY_TIME, 
        UNKNOWN;
    }
}
