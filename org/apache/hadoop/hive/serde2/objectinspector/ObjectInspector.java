// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

public interface ObjectInspector extends Cloneable
{
    String getTypeName();
    
    Category getCategory();
    
    public enum Category
    {
        PRIMITIVE, 
        LIST, 
        MAP, 
        STRUCT, 
        UNION;
    }
}
