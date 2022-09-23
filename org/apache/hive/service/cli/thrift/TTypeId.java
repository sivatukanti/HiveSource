// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import org.apache.thrift.TEnum;

public enum TTypeId implements TEnum
{
    BOOLEAN_TYPE(0), 
    TINYINT_TYPE(1), 
    SMALLINT_TYPE(2), 
    INT_TYPE(3), 
    BIGINT_TYPE(4), 
    FLOAT_TYPE(5), 
    DOUBLE_TYPE(6), 
    STRING_TYPE(7), 
    TIMESTAMP_TYPE(8), 
    BINARY_TYPE(9), 
    ARRAY_TYPE(10), 
    MAP_TYPE(11), 
    STRUCT_TYPE(12), 
    UNION_TYPE(13), 
    USER_DEFINED_TYPE(14), 
    DECIMAL_TYPE(15), 
    NULL_TYPE(16), 
    DATE_TYPE(17), 
    VARCHAR_TYPE(18), 
    CHAR_TYPE(19), 
    INTERVAL_YEAR_MONTH_TYPE(20), 
    INTERVAL_DAY_TIME_TYPE(21);
    
    private final int value;
    
    private TTypeId(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static TTypeId findByValue(final int value) {
        switch (value) {
            case 0: {
                return TTypeId.BOOLEAN_TYPE;
            }
            case 1: {
                return TTypeId.TINYINT_TYPE;
            }
            case 2: {
                return TTypeId.SMALLINT_TYPE;
            }
            case 3: {
                return TTypeId.INT_TYPE;
            }
            case 4: {
                return TTypeId.BIGINT_TYPE;
            }
            case 5: {
                return TTypeId.FLOAT_TYPE;
            }
            case 6: {
                return TTypeId.DOUBLE_TYPE;
            }
            case 7: {
                return TTypeId.STRING_TYPE;
            }
            case 8: {
                return TTypeId.TIMESTAMP_TYPE;
            }
            case 9: {
                return TTypeId.BINARY_TYPE;
            }
            case 10: {
                return TTypeId.ARRAY_TYPE;
            }
            case 11: {
                return TTypeId.MAP_TYPE;
            }
            case 12: {
                return TTypeId.STRUCT_TYPE;
            }
            case 13: {
                return TTypeId.UNION_TYPE;
            }
            case 14: {
                return TTypeId.USER_DEFINED_TYPE;
            }
            case 15: {
                return TTypeId.DECIMAL_TYPE;
            }
            case 16: {
                return TTypeId.NULL_TYPE;
            }
            case 17: {
                return TTypeId.DATE_TYPE;
            }
            case 18: {
                return TTypeId.VARCHAR_TYPE;
            }
            case 19: {
                return TTypeId.CHAR_TYPE;
            }
            case 20: {
                return TTypeId.INTERVAL_YEAR_MONTH_TYPE;
            }
            case 21: {
                return TTypeId.INTERVAL_DAY_TIME_TYPE;
            }
            default: {
                return null;
            }
        }
    }
}
