// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

import org.apache.hive.service.cli.thrift.TTypeId;

public enum Type
{
    NULL_TYPE("VOID", 0, TTypeId.NULL_TYPE), 
    BOOLEAN_TYPE("BOOLEAN", 16, TTypeId.BOOLEAN_TYPE), 
    TINYINT_TYPE("TINYINT", -6, TTypeId.TINYINT_TYPE), 
    SMALLINT_TYPE("SMALLINT", 5, TTypeId.SMALLINT_TYPE), 
    INT_TYPE("INT", 4, TTypeId.INT_TYPE), 
    BIGINT_TYPE("BIGINT", -5, TTypeId.BIGINT_TYPE), 
    FLOAT_TYPE("FLOAT", 6, TTypeId.FLOAT_TYPE), 
    DOUBLE_TYPE("DOUBLE", 8, TTypeId.DOUBLE_TYPE), 
    STRING_TYPE("STRING", 12, TTypeId.STRING_TYPE), 
    CHAR_TYPE("CHAR", 1, TTypeId.CHAR_TYPE, true, false, false), 
    VARCHAR_TYPE("VARCHAR", 12, TTypeId.VARCHAR_TYPE, true, false, false), 
    DATE_TYPE("DATE", 91, TTypeId.DATE_TYPE), 
    TIMESTAMP_TYPE("TIMESTAMP", 93, TTypeId.TIMESTAMP_TYPE), 
    INTERVAL_YEAR_MONTH_TYPE("INTERVAL_YEAR_MONTH", 1111, TTypeId.INTERVAL_YEAR_MONTH_TYPE), 
    INTERVAL_DAY_TIME_TYPE("INTERVAL_DAY_TIME", 1111, TTypeId.INTERVAL_DAY_TIME_TYPE), 
    BINARY_TYPE("BINARY", -2, TTypeId.BINARY_TYPE), 
    DECIMAL_TYPE("DECIMAL", 3, TTypeId.DECIMAL_TYPE, true, false, false), 
    ARRAY_TYPE("ARRAY", 2003, TTypeId.ARRAY_TYPE, true, true), 
    MAP_TYPE("MAP", 2000, TTypeId.MAP_TYPE, true, true), 
    STRUCT_TYPE("STRUCT", 2002, TTypeId.STRUCT_TYPE, true, false), 
    UNION_TYPE("UNIONTYPE", 1111, TTypeId.UNION_TYPE, true, false), 
    USER_DEFINED_TYPE("USER_DEFINED", 1111, TTypeId.USER_DEFINED_TYPE, true, false);
    
    private final String name;
    private final TTypeId tType;
    private final int javaSQLType;
    private final boolean isQualified;
    private final boolean isComplex;
    private final boolean isCollection;
    
    private Type(final String name, final int javaSQLType, final TTypeId tType, final boolean isQualified, final boolean isComplex, final boolean isCollection) {
        this.name = name;
        this.javaSQLType = javaSQLType;
        this.tType = tType;
        this.isQualified = isQualified;
        this.isComplex = isComplex;
        this.isCollection = isCollection;
    }
    
    private Type(final String name, final int javaSQLType, final TTypeId tType, final boolean isComplex, final boolean isCollection) {
        this(name, javaSQLType, tType, false, isComplex, isCollection);
    }
    
    private Type(final String name, final int javaSqlType, final TTypeId tType) {
        this(name, javaSqlType, tType, false, false, false);
    }
    
    public boolean isPrimitiveType() {
        return !this.isComplex;
    }
    
    public boolean isQualifiedType() {
        return this.isQualified;
    }
    
    public boolean isComplexType() {
        return this.isComplex;
    }
    
    public boolean isCollectionType() {
        return this.isCollection;
    }
    
    public static Type getType(final TTypeId tType) {
        for (final Type type : values()) {
            if (tType.equals(type.tType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unregonized Thrift TTypeId value: " + tType);
    }
    
    public static Type getType(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Invalid type name: null");
        }
        for (final Type type : values()) {
            if (name.equalsIgnoreCase(type.name)) {
                return type;
            }
            if ((type.isQualifiedType() || type.isComplexType()) && name.toUpperCase().startsWith(type.name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unrecognized type name: " + name);
    }
    
    public Integer getNumPrecRadix() {
        if (this.isNumericType()) {
            return 10;
        }
        return null;
    }
    
    public Integer getMaxPrecision() {
        switch (this) {
            case TINYINT_TYPE: {
                return 3;
            }
            case SMALLINT_TYPE: {
                return 5;
            }
            case INT_TYPE: {
                return 10;
            }
            case BIGINT_TYPE: {
                return 19;
            }
            case FLOAT_TYPE: {
                return 7;
            }
            case DOUBLE_TYPE: {
                return 15;
            }
            case DECIMAL_TYPE: {
                return 38;
            }
            default: {
                return null;
            }
        }
    }
    
    public boolean isNumericType() {
        switch (this) {
            case TINYINT_TYPE:
            case SMALLINT_TYPE:
            case INT_TYPE:
            case BIGINT_TYPE:
            case FLOAT_TYPE:
            case DOUBLE_TYPE:
            case DECIMAL_TYPE: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public String getLiteralPrefix() {
        return null;
    }
    
    public String getLiteralSuffix() {
        return null;
    }
    
    public Short getNullable() {
        return 1;
    }
    
    public Boolean isCaseSensitive() {
        switch (this) {
            case STRING_TYPE: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public String getCreateParams() {
        return null;
    }
    
    public Short getSearchable() {
        if (this.isPrimitiveType()) {
            return 3;
        }
        return 0;
    }
    
    public Boolean isUnsignedAttribute() {
        if (this.isNumericType()) {
            return false;
        }
        return true;
    }
    
    public Boolean isFixedPrecScale() {
        return false;
    }
    
    public Boolean isAutoIncrement() {
        return false;
    }
    
    public String getLocalizedName() {
        return null;
    }
    
    public Short getMinimumScale() {
        return 0;
    }
    
    public Short getMaximumScale() {
        return 0;
    }
    
    public TTypeId toTType() {
        return this.tType;
    }
    
    public int toJavaSQLType() {
        return this.javaSQLType;
    }
    
    public String getName() {
        return this.name;
    }
}
