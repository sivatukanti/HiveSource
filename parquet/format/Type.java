// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format;

import parquet.org.apache.thrift.TEnum;

public enum Type implements TEnum
{
    BOOLEAN(0), 
    INT32(1), 
    INT64(2), 
    INT96(3), 
    FLOAT(4), 
    DOUBLE(5), 
    BYTE_ARRAY(6), 
    FIXED_LEN_BYTE_ARRAY(7);
    
    private final int value;
    
    private Type(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static Type findByValue(final int value) {
        switch (value) {
            case 0: {
                return Type.BOOLEAN;
            }
            case 1: {
                return Type.INT32;
            }
            case 2: {
                return Type.INT64;
            }
            case 3: {
                return Type.INT96;
            }
            case 4: {
                return Type.FLOAT;
            }
            case 5: {
                return Type.DOUBLE;
            }
            case 6: {
                return Type.BYTE_ARRAY;
            }
            case 7: {
                return Type.FIXED_LEN_BYTE_ARRAY;
            }
            default: {
                return null;
            }
        }
    }
}
