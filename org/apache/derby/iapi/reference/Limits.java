// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.reference;

public interface Limits
{
    public static final int DB2_MAX_TRIGGER_RECURSION = 16;
    public static final int DB2_MAX_INDEXES_ON_TABLE = 32767;
    public static final int DB2_MAX_COLUMNS_IN_TABLE = 1012;
    public static final int DB2_MAX_COLUMNS_IN_VIEW = 5000;
    public static final int DB2_MAX_ELEMENTS_IN_SELECT_LIST = 1012;
    public static final int DB2_MAX_ELEMENTS_IN_GROUP_BY = 32677;
    public static final int DB2_MAX_ELEMENTS_IN_ORDER_BY = 1012;
    public static final int DB2_JCC_MAX_EXCEPTION_PARAM_LENGTH = 2400;
    public static final int MAX_IDENTIFIER_LENGTH = 128;
    public static final int DB2_CHAR_MAXWIDTH = 254;
    public static final int DB2_VARCHAR_MAXWIDTH = 32672;
    public static final int DB2_LOB_MAXWIDTH = Integer.MAX_VALUE;
    public static final int DB2_LONGVARCHAR_MAXWIDTH = 32700;
    public static final int DB2_CONCAT_VARCHAR_LENGTH = 4000;
    public static final int DB2_MAX_FLOATINGPOINT_LITERAL_LENGTH = 30;
    public static final int DB2_MAX_CHARACTER_LITERAL_LENGTH = 32672;
    public static final int DB2_MAX_HEX_LITERAL_LENGTH = 16336;
    public static final int DB2_MIN_COL_LENGTH_FOR_CURRENT_USER = 8;
    public static final int DB2_MIN_COL_LENGTH_FOR_CURRENT_SCHEMA = 128;
    public static final int DB2_MIN_PAGE_SIZE = 4096;
    public static final int DB2_MAX_PAGE_SIZE = 32768;
    public static final int DB2_MAX_DECIMAL_PRECISION_SCALE = 31;
    public static final int DB2_DEFAULT_DECIMAL_PRECISION = 5;
    public static final int DB2_DEFAULT_DECIMAL_SCALE = 0;
    public static final float DB2_SMALLEST_REAL = -3.402E38f;
    public static final float DB2_LARGEST_REAL = 3.402E38f;
    public static final float DB2_SMALLEST_POSITIVE_REAL = 1.175E-37f;
    public static final float DB2_LARGEST_NEGATIVE_REAL = -1.175E-37f;
    public static final double DB2_SMALLEST_DOUBLE = -1.79769E308;
    public static final double DB2_LARGEST_DOUBLE = 1.79769E308;
    public static final double DB2_SMALLEST_POSITIVE_DOUBLE = 2.225E-307;
    public static final double DB2_LARGEST_NEGATIVE_DOUBLE = -2.225E-307;
    public static final int MAX_BLOB_RETURN_LEN = 32672;
    public static final int MAX_CLOB_RETURN_LEN = 10890;
}
