// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.shared.common.reference;

public interface JDBC40Translation
{
    public static final int FUNCTION_PARAMETER_UNKNOWN = 0;
    public static final int FUNCTION_PARAMETER_IN = 1;
    public static final int FUNCTION_PARAMETER_INOUT = 2;
    public static final int FUNCTION_PARAMETER_OUT = 3;
    public static final int FUNCTION_RETURN = 4;
    public static final int FUNCTION_COLUMN_RESULT = 5;
    public static final int FUNCTION_NO_NULLS = 0;
    public static final int FUNCTION_NULLABLE = 1;
    public static final int FUNCTION_NULLABLE_UNKNOWN = 2;
    public static final int FUNCTION_RESULT_UNKNOWN = 0;
    public static final int FUNCTION_NO_TABLE = 1;
    public static final int FUNCTION_RETURNS_TABLE = 2;
    public static final int NCHAR = -15;
    public static final int NVARCHAR = -9;
    public static final int LONGNVARCHAR = -16;
    public static final int NCLOB = 2011;
    public static final int ROWID = -8;
    public static final int REF_CURSOR = 2012;
    public static final int SQLXML = 2009;
}
