// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1;

public enum UniversalTag
{
    UNKNOWN(-1), 
    CHOICE(-2), 
    ANY(-3), 
    EOC(0), 
    BOOLEAN(1), 
    INTEGER(2), 
    BIT_STRING(3), 
    OCTET_STRING(4), 
    NULL(5), 
    OBJECT_IDENTIFIER(6), 
    OBJECT_DESCRIPTOR(7), 
    EXTERNAL(8), 
    REAL(9), 
    ENUMERATED(10), 
    EMBEDDED_PDV(11), 
    UTF8_STRING(12), 
    RELATIVE_OID(13), 
    RESERVED_14(14), 
    RESERVED_15(15), 
    SEQUENCE(16), 
    SEQUENCE_OF(16), 
    SET(17), 
    SET_OF(17), 
    NUMERIC_STRING(18), 
    PRINTABLE_STRING(19), 
    T61_STRING(20), 
    VIDEOTEX_STRING(21), 
    IA5_STRING(22), 
    UTC_TIME(23), 
    GENERALIZED_TIME(24), 
    GRAPHIC_STRING(25), 
    VISIBLE_STRING(26), 
    GENERAL_STRING(27), 
    UNIVERSAL_STRING(28), 
    CHARACTER_STRING(29), 
    BMP_STRING(30), 
    RESERVED_31(31);
    
    private int value;
    
    private UniversalTag(final int value) {
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public static UniversalTag fromValue(final int value) {
        switch (value) {
            case -2: {
                return UniversalTag.CHOICE;
            }
            case 0: {
                return UniversalTag.EOC;
            }
            case 1: {
                return UniversalTag.BOOLEAN;
            }
            case 2: {
                return UniversalTag.INTEGER;
            }
            case 3: {
                return UniversalTag.BIT_STRING;
            }
            case 4: {
                return UniversalTag.OCTET_STRING;
            }
            case 5: {
                return UniversalTag.NULL;
            }
            case 6: {
                return UniversalTag.OBJECT_IDENTIFIER;
            }
            case 7: {
                return UniversalTag.OBJECT_DESCRIPTOR;
            }
            case 8: {
                return UniversalTag.EXTERNAL;
            }
            case 9: {
                return UniversalTag.REAL;
            }
            case 10: {
                return UniversalTag.ENUMERATED;
            }
            case 11: {
                return UniversalTag.EMBEDDED_PDV;
            }
            case 12: {
                return UniversalTag.UTF8_STRING;
            }
            case 13: {
                return UniversalTag.RELATIVE_OID;
            }
            case 14: {
                return UniversalTag.RESERVED_14;
            }
            case 15: {
                return UniversalTag.RESERVED_15;
            }
            case 16: {
                return UniversalTag.SEQUENCE;
            }
            case 17: {
                return UniversalTag.SET;
            }
            case 18: {
                return UniversalTag.NUMERIC_STRING;
            }
            case 19: {
                return UniversalTag.PRINTABLE_STRING;
            }
            case 20: {
                return UniversalTag.T61_STRING;
            }
            case 21: {
                return UniversalTag.VIDEOTEX_STRING;
            }
            case 22: {
                return UniversalTag.IA5_STRING;
            }
            case 23: {
                return UniversalTag.UTC_TIME;
            }
            case 24: {
                return UniversalTag.GENERALIZED_TIME;
            }
            case 25: {
                return UniversalTag.GRAPHIC_STRING;
            }
            case 26: {
                return UniversalTag.VISIBLE_STRING;
            }
            case 27: {
                return UniversalTag.GENERAL_STRING;
            }
            case 28: {
                return UniversalTag.UNIVERSAL_STRING;
            }
            case 29: {
                return UniversalTag.CHARACTER_STRING;
            }
            case 30: {
                return UniversalTag.BMP_STRING;
            }
            case 31: {
                return UniversalTag.RESERVED_31;
            }
            default: {
                return UniversalTag.UNKNOWN;
            }
        }
    }
    
    public String toStr() {
        String typeStr = this.toString();
        typeStr = typeStr.replace('_', ' ');
        return typeStr.toLowerCase();
    }
}
