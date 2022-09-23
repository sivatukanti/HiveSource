// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.operation;

public class TableTypeMappingFactory
{
    private static TableTypeMapping hiveTableTypeMapping;
    private static TableTypeMapping classicTableTypeMapping;
    
    public static TableTypeMapping getTableTypeMapping(final String mappingType) {
        if (TableTypeMappings.CLASSIC.toString().equalsIgnoreCase(mappingType)) {
            return TableTypeMappingFactory.classicTableTypeMapping;
        }
        return TableTypeMappingFactory.hiveTableTypeMapping;
    }
    
    static {
        TableTypeMappingFactory.hiveTableTypeMapping = new HiveTableTypeMapping();
        TableTypeMappingFactory.classicTableTypeMapping = new ClassicTableTypeMapping();
    }
    
    public enum TableTypeMappings
    {
        HIVE, 
        CLASSIC;
    }
}
