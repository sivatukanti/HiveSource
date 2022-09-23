// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format;

import parquet.org.apache.thrift.TEnum;

public enum FieldRepetitionType implements TEnum
{
    REQUIRED(0), 
    OPTIONAL(1), 
    REPEATED(2);
    
    private final int value;
    
    private FieldRepetitionType(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static FieldRepetitionType findByValue(final int value) {
        switch (value) {
            case 0: {
                return FieldRepetitionType.REQUIRED;
            }
            case 1: {
                return FieldRepetitionType.OPTIONAL;
            }
            case 2: {
                return FieldRepetitionType.REPEATED;
            }
            default: {
                return null;
            }
        }
    }
}
