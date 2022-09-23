// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog;

public interface AliasInfo
{
    public static final char ALIAS_TYPE_UDT_AS_CHAR = 'A';
    public static final char ALIAS_TYPE_AGGREGATE_AS_CHAR = 'G';
    public static final char ALIAS_TYPE_PROCEDURE_AS_CHAR = 'P';
    public static final char ALIAS_TYPE_FUNCTION_AS_CHAR = 'F';
    public static final char ALIAS_TYPE_SYNONYM_AS_CHAR = 'S';
    public static final String ALIAS_TYPE_UDT_AS_STRING = "A";
    public static final String ALIAS_TYPE_AGGREGATE_AS_STRING = "G";
    public static final String ALIAS_TYPE_PROCEDURE_AS_STRING = "P";
    public static final String ALIAS_TYPE_FUNCTION_AS_STRING = "F";
    public static final String ALIAS_TYPE_SYNONYM_AS_STRING = "S";
    public static final char ALIAS_NAME_SPACE_UDT_AS_CHAR = 'A';
    public static final char ALIAS_NAME_SPACE_AGGREGATE_AS_CHAR = 'G';
    public static final char ALIAS_NAME_SPACE_PROCEDURE_AS_CHAR = 'P';
    public static final char ALIAS_NAME_SPACE_FUNCTION_AS_CHAR = 'F';
    public static final char ALIAS_NAME_SPACE_SYNONYM_AS_CHAR = 'S';
    public static final String ALIAS_NAME_SPACE_UDT_AS_STRING = "A";
    public static final String ALIAS_NAME_SPACE_AGGREGATE_AS_STRING = "G";
    public static final String ALIAS_NAME_SPACE_PROCEDURE_AS_STRING = "P";
    public static final String ALIAS_NAME_SPACE_FUNCTION_AS_STRING = "F";
    public static final String ALIAS_NAME_SPACE_SYNONYM_AS_STRING = "S";
    
    String getMethodName();
    
    boolean isTableFunction();
}
