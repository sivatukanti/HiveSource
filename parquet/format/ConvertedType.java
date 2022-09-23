// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format;

import parquet.org.apache.thrift.TEnum;

public enum ConvertedType implements TEnum
{
    UTF8(0), 
    MAP(1), 
    MAP_KEY_VALUE(2), 
    LIST(3), 
    ENUM(4), 
    DECIMAL(5), 
    DATE(6), 
    TIME_MILLIS(7), 
    TIMESTAMP_MILLIS(9), 
    UINT_8(11), 
    UINT_16(12), 
    UINT_32(13), 
    UINT_64(14), 
    INT_8(15), 
    INT_16(16), 
    INT_32(17), 
    INT_64(18), 
    JSON(19), 
    BSON(20), 
    INTERVAL(21);
    
    private final int value;
    
    private ConvertedType(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static ConvertedType findByValue(final int value) {
        switch (value) {
            case 0: {
                return ConvertedType.UTF8;
            }
            case 1: {
                return ConvertedType.MAP;
            }
            case 2: {
                return ConvertedType.MAP_KEY_VALUE;
            }
            case 3: {
                return ConvertedType.LIST;
            }
            case 4: {
                return ConvertedType.ENUM;
            }
            case 5: {
                return ConvertedType.DECIMAL;
            }
            case 6: {
                return ConvertedType.DATE;
            }
            case 7: {
                return ConvertedType.TIME_MILLIS;
            }
            case 9: {
                return ConvertedType.TIMESTAMP_MILLIS;
            }
            case 11: {
                return ConvertedType.UINT_8;
            }
            case 12: {
                return ConvertedType.UINT_16;
            }
            case 13: {
                return ConvertedType.UINT_32;
            }
            case 14: {
                return ConvertedType.UINT_64;
            }
            case 15: {
                return ConvertedType.INT_8;
            }
            case 16: {
                return ConvertedType.INT_16;
            }
            case 17: {
                return ConvertedType.INT_32;
            }
            case 18: {
                return ConvertedType.INT_64;
            }
            case 19: {
                return ConvertedType.JSON;
            }
            case 20: {
                return ConvertedType.BSON;
            }
            case 21: {
                return ConvertedType.INTERVAL;
            }
            default: {
                return null;
            }
        }
    }
}
