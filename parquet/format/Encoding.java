// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format;

import parquet.org.apache.thrift.TEnum;

public enum Encoding implements TEnum
{
    PLAIN(0), 
    PLAIN_DICTIONARY(2), 
    RLE(3), 
    BIT_PACKED(4), 
    DELTA_BINARY_PACKED(5), 
    DELTA_LENGTH_BYTE_ARRAY(6), 
    DELTA_BYTE_ARRAY(7), 
    RLE_DICTIONARY(8);
    
    private final int value;
    
    private Encoding(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static Encoding findByValue(final int value) {
        switch (value) {
            case 0: {
                return Encoding.PLAIN;
            }
            case 2: {
                return Encoding.PLAIN_DICTIONARY;
            }
            case 3: {
                return Encoding.RLE;
            }
            case 4: {
                return Encoding.BIT_PACKED;
            }
            case 5: {
                return Encoding.DELTA_BINARY_PACKED;
            }
            case 6: {
                return Encoding.DELTA_LENGTH_BYTE_ARRAY;
            }
            case 7: {
                return Encoding.DELTA_BYTE_ARRAY;
            }
            case 8: {
                return Encoding.RLE_DICTIONARY;
            }
            default: {
                return null;
            }
        }
    }
}
