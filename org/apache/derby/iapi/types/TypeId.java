// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.catalog.types.DecimalTypeIdImpl;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.catalog.types.TypeDescriptorImpl;
import org.apache.derby.catalog.TypeDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.types.UserDefinedTypeIdImpl;
import org.apache.derby.catalog.types.BaseTypeIdImpl;

public final class TypeId
{
    public static final int LONGINT_PRECISION = 19;
    public static final int LONGINT_SCALE = 0;
    public static final int LONGINT_MAXWIDTH = 8;
    public static final int INT_PRECISION = 10;
    public static final int INT_SCALE = 0;
    public static final int INT_MAXWIDTH = 4;
    public static final int SMALLINT_PRECISION = 5;
    public static final int SMALLINT_SCALE = 0;
    public static final int SMALLINT_MAXWIDTH = 2;
    public static final int TINYINT_PRECISION = 3;
    public static final int TINYINT_SCALE = 0;
    public static final int TINYINT_MAXWIDTH = 1;
    public static final int DOUBLE_PRECISION = 52;
    public static final int DOUBLE_PRECISION_IN_DIGITS = 15;
    public static final int DOUBLE_SCALE = 0;
    public static final int DOUBLE_MAXWIDTH = 8;
    public static final int REAL_PRECISION = 23;
    public static final int REAL_PRECISION_IN_DIGITS = 7;
    public static final int REAL_SCALE = 0;
    public static final int REAL_MAXWIDTH = 4;
    public static final int DECIMAL_PRECISION = 31;
    public static final int DECIMAL_SCALE = 31;
    public static final int DECIMAL_MAXWIDTH = 31;
    public static final int BOOLEAN_MAXWIDTH = 1;
    public static final int CHAR_MAXWIDTH = 254;
    public static final int VARCHAR_MAXWIDTH = 32672;
    public static final int LONGVARCHAR_MAXWIDTH = 32700;
    public static final int BIT_MAXWIDTH = 254;
    public static final int VARBIT_MAXWIDTH = 32672;
    public static final int LONGVARBIT_MAXWIDTH = 32700;
    public static final int BLOB_MAXWIDTH = Integer.MAX_VALUE;
    public static final int CLOB_MAXWIDTH = Integer.MAX_VALUE;
    public static final int XML_MAXWIDTH = Integer.MAX_VALUE;
    public static final int DATE_MAXWIDTH = 10;
    public static final int TIME_MAXWIDTH = 8;
    public static final int TIMESTAMP_MAXWIDTH = 29;
    public static final int TIME_SCALE = 0;
    public static final int TIMESTAMP_SCALE = 9;
    public static final String BIT_NAME = "CHAR () FOR BIT DATA";
    public static final String VARBIT_NAME = "VARCHAR () FOR BIT DATA";
    public static final String LONGVARBIT_NAME = "LONG VARCHAR FOR BIT DATA";
    public static final String TINYINT_NAME = "TINYINT";
    public static final String SMALLINT_NAME = "SMALLINT";
    public static final String INTEGER_NAME = "INTEGER";
    public static final String LONGINT_NAME = "BIGINT";
    public static final String FLOAT_NAME = "FLOAT";
    public static final String REAL_NAME = "REAL";
    public static final String DOUBLE_NAME = "DOUBLE";
    public static final String NUMERIC_NAME = "NUMERIC";
    public static final String DECIMAL_NAME = "DECIMAL";
    public static final String CHAR_NAME = "CHAR";
    public static final String VARCHAR_NAME = "VARCHAR";
    public static final String LONGVARCHAR_NAME = "LONG VARCHAR";
    public static final String DATE_NAME = "DATE";
    public static final String TIME_NAME = "TIME";
    public static final String TIMESTAMP_NAME = "TIMESTAMP";
    public static final String BINARY_NAME = "BINARY";
    public static final String VARBINARY_NAME = "VARBINARY";
    public static final String LONGVARBINARY_NAME = "LONGVARBINARY";
    public static final String BOOLEAN_NAME = "BOOLEAN";
    public static final String REF_NAME = "REF";
    public static final String REF_CURSOR = "REF CURSOR";
    public static final String NATIONAL_CHAR_NAME = "NATIONAL CHAR";
    public static final String NATIONAL_VARCHAR_NAME = "NATIONAL CHAR VARYING";
    public static final String NATIONAL_LONGVARCHAR_NAME = "LONG NVARCHAR";
    public static final String BLOB_NAME = "BLOB";
    public static final String CLOB_NAME = "CLOB";
    public static final String NCLOB_NAME = "NCLOB";
    public static final String XML_NAME = "XML";
    public static final String ARRAY_NAME = "ARRAY";
    public static final String STRUCT_NAME = "STRUCT";
    public static final String DATALINK_NAME = "DATALINK";
    public static final String ROWID_NAME = "ROWID";
    public static final String SQLXML_NAME = "SQLXML";
    public static final int USER_PRECEDENCE = 1000;
    public static final int XML_PRECEDENCE = 180;
    public static final int BLOB_PRECEDENCE = 170;
    public static final int LONGVARBIT_PRECEDENCE = 160;
    public static final int VARBIT_PRECEDENCE = 150;
    public static final int BIT_PRECEDENCE = 140;
    public static final int BOOLEAN_PRECEDENCE = 130;
    public static final int TIME_PRECEDENCE = 120;
    public static final int TIMESTAMP_PRECEDENCE = 110;
    public static final int DATE_PRECEDENCE = 100;
    public static final int DOUBLE_PRECEDENCE = 90;
    public static final int REAL_PRECEDENCE = 80;
    public static final int DECIMAL_PRECEDENCE = 70;
    public static final int NUMERIC_PRECEDENCE = 69;
    public static final int LONGINT_PRECEDENCE = 60;
    public static final int INT_PRECEDENCE = 50;
    public static final int SMALLINT_PRECEDENCE = 40;
    public static final int TINYINT_PRECEDENCE = 30;
    public static final int REF_PRECEDENCE = 25;
    public static final int CLOB_PRECEDENCE = 14;
    public static final int LONGVARCHAR_PRECEDENCE = 12;
    public static final int VARCHAR_PRECEDENCE = 10;
    public static final int CHAR_PRECEDENCE = 0;
    public static final TypeId BOOLEAN_ID;
    public static final TypeId SMALLINT_ID;
    public static final TypeId INTEGER_ID;
    public static final TypeId CHAR_ID;
    private static final TypeId TINYINT_ID;
    private static final TypeId BIGINT_ID;
    private static final TypeId REAL_ID;
    private static final TypeId DOUBLE_ID;
    private static final TypeId DECIMAL_ID;
    private static final TypeId NUMERIC_ID;
    private static final TypeId VARCHAR_ID;
    private static final TypeId DATE_ID;
    private static final TypeId TIME_ID;
    private static final TypeId TIMESTAMP_ID;
    private static final TypeId BIT_ID;
    private static final TypeId VARBIT_ID;
    private static final TypeId REF_ID;
    private static final TypeId LONGVARCHAR_ID;
    private static final TypeId LONGVARBIT_ID;
    private static final TypeId BLOB_ID;
    private static final TypeId CLOB_ID;
    private static final TypeId XML_ID;
    private static final TypeId[] ALL_BUILTIN_TYPE_IDS;
    static DataValueDescriptor decimalImplementation;
    private BaseTypeIdImpl baseTypeId;
    private int formatId;
    private boolean isBitTypeId;
    private boolean isLOBTypeId;
    private boolean isBooleanTypeId;
    private boolean isConcatableTypeId;
    private boolean isDecimalTypeId;
    private boolean isLongConcatableTypeId;
    private boolean isNumericTypeId;
    private boolean isRefTypeId;
    private boolean isStringTypeId;
    private boolean isFloatingPointTypeId;
    private boolean isRealTypeId;
    private boolean isDateTimeTimeStampTypeId;
    private boolean isUserDefinedTypeId;
    private int maxPrecision;
    private int maxScale;
    private int typePrecedence;
    private String javaTypeName;
    private int maxMaxWidth;
    
    private static TypeId create(final int n, final int n2) {
        return new TypeId(n, new BaseTypeIdImpl(n2));
    }
    
    public static TypeId[] getAllBuiltinTypeIds() {
        final int length = TypeId.ALL_BUILTIN_TYPE_IDS.length;
        final TypeId[] array = new TypeId[length];
        for (int i = 0; i < length; ++i) {
            array[i] = TypeId.ALL_BUILTIN_TYPE_IDS[i];
        }
        return array;
    }
    
    public static TypeId getBuiltInTypeId(final int n) {
        switch (n) {
            case -6: {
                return TypeId.TINYINT_ID;
            }
            case 5: {
                return TypeId.SMALLINT_ID;
            }
            case 4: {
                return TypeId.INTEGER_ID;
            }
            case -5: {
                return TypeId.BIGINT_ID;
            }
            case 7: {
                return TypeId.REAL_ID;
            }
            case 6:
            case 8: {
                return TypeId.DOUBLE_ID;
            }
            case 3: {
                return TypeId.DECIMAL_ID;
            }
            case 2: {
                return TypeId.NUMERIC_ID;
            }
            case 1: {
                return TypeId.CHAR_ID;
            }
            case 12: {
                return TypeId.VARCHAR_ID;
            }
            case 91: {
                return TypeId.DATE_ID;
            }
            case 92: {
                return TypeId.TIME_ID;
            }
            case 93: {
                return TypeId.TIMESTAMP_ID;
            }
            case -7:
            case 16: {
                return TypeId.BOOLEAN_ID;
            }
            case -2: {
                return TypeId.BIT_ID;
            }
            case -3: {
                return TypeId.VARBIT_ID;
            }
            case -4: {
                return TypeId.LONGVARBIT_ID;
            }
            case -1: {
                return TypeId.LONGVARCHAR_ID;
            }
            case 2004: {
                return TypeId.BLOB_ID;
            }
            case 2005: {
                return TypeId.CLOB_ID;
            }
            case 2009: {
                return TypeId.XML_ID;
            }
            default: {
                return null;
            }
        }
    }
    
    public static TypeId getUserDefinedTypeId(final String s) throws StandardException {
        return new TypeId(267, new UserDefinedTypeIdImpl(s));
    }
    
    public static TypeId getUserDefinedTypeId(final String s, final String s2, final String s3) throws StandardException {
        return new TypeId(267, new UserDefinedTypeIdImpl(s, s2, s3));
    }
    
    public static TypeId getSQLTypeForJavaType(final String s) throws StandardException {
        if (s.equals("java.lang.Boolean") || s.equals("boolean")) {
            return TypeId.BOOLEAN_ID;
        }
        if (s.equals("byte[]")) {
            return TypeId.VARBIT_ID;
        }
        if (s.equals("java.lang.String")) {
            return TypeId.VARCHAR_ID;
        }
        if (s.equals("java.lang.Integer") || s.equals("int")) {
            return TypeId.INTEGER_ID;
        }
        if (s.equals("byte")) {
            return TypeId.TINYINT_ID;
        }
        if (s.equals("short")) {
            return TypeId.SMALLINT_ID;
        }
        if (s.equals("java.lang.Long") || s.equals("long")) {
            return TypeId.BIGINT_ID;
        }
        if (s.equals("java.lang.Float") || s.equals("float")) {
            return TypeId.REAL_ID;
        }
        if (s.equals("java.lang.Double") || s.equals("double")) {
            return TypeId.DOUBLE_ID;
        }
        if (s.equals("java.math.BigDecimal")) {
            return TypeId.DECIMAL_ID;
        }
        if (s.equals("java.sql.Date")) {
            return TypeId.DATE_ID;
        }
        if (s.equals("java.sql.Time")) {
            return TypeId.TIME_ID;
        }
        if (s.equals("java.sql.Timestamp")) {
            return TypeId.TIMESTAMP_ID;
        }
        if (s.equals("java.sql.Blob")) {
            return TypeId.BLOB_ID;
        }
        if (s.equals("java.sql.Clob")) {
            return TypeId.CLOB_ID;
        }
        if (s.equals("org.apache.derby.iapi.types.XML")) {
            return TypeId.XML_ID;
        }
        if (s.equals("char")) {
            return null;
        }
        return getUserDefinedTypeId(s);
    }
    
    public static TypeId getBuiltInTypeId(final String s) {
        if (s.equals("BOOLEAN")) {
            return TypeId.BOOLEAN_ID;
        }
        if (s.equals("CHAR")) {
            return TypeId.CHAR_ID;
        }
        if (s.equals("DATE")) {
            return TypeId.DATE_ID;
        }
        if (s.equals("DOUBLE")) {
            return TypeId.DOUBLE_ID;
        }
        if (s.equals("FLOAT")) {
            return TypeId.DOUBLE_ID;
        }
        if (s.equals("INTEGER")) {
            return TypeId.INTEGER_ID;
        }
        if (s.equals("BIGINT")) {
            return TypeId.BIGINT_ID;
        }
        if (s.equals("REAL")) {
            return TypeId.REAL_ID;
        }
        if (s.equals("SMALLINT")) {
            return TypeId.SMALLINT_ID;
        }
        if (s.equals("TIME")) {
            return TypeId.TIME_ID;
        }
        if (s.equals("TIMESTAMP")) {
            return TypeId.TIMESTAMP_ID;
        }
        if (s.equals("VARCHAR")) {
            return TypeId.VARCHAR_ID;
        }
        if (s.equals("CHAR () FOR BIT DATA")) {
            return TypeId.BIT_ID;
        }
        if (s.equals("VARCHAR () FOR BIT DATA")) {
            return TypeId.VARBIT_ID;
        }
        if (s.equals("TINYINT")) {
            return TypeId.TINYINT_ID;
        }
        if (s.equals("DECIMAL")) {
            return TypeId.DECIMAL_ID;
        }
        if (s.equals("NUMERIC")) {
            return TypeId.NUMERIC_ID;
        }
        if (s.equals("LONG VARCHAR")) {
            return TypeId.LONGVARCHAR_ID;
        }
        if (s.equals("LONG VARCHAR FOR BIT DATA")) {
            return TypeId.LONGVARBIT_ID;
        }
        if (s.equals("BLOB")) {
            return TypeId.BLOB_ID;
        }
        if (s.equals("CLOB")) {
            return TypeId.CLOB_ID;
        }
        if (s.equals("XML")) {
            return TypeId.XML_ID;
        }
        if (s.equals("REF")) {
            return TypeId.REF_ID;
        }
        return null;
    }
    
    public static TypeId getTypeId(final TypeDescriptor typeDescriptor) {
        final TypeDescriptorImpl typeDescriptorImpl = (TypeDescriptorImpl)typeDescriptor;
        final int jdbcTypeId = typeDescriptor.getJDBCTypeId();
        final TypeId builtInTypeId = getBuiltInTypeId(jdbcTypeId);
        if (builtInTypeId != null) {
            return builtInTypeId;
        }
        if (jdbcTypeId == 2000) {
            return new TypeId(267, typeDescriptorImpl.getTypeId());
        }
        if (typeDescriptorImpl.isRowMultiSet()) {
            return new TypeId(469, typeDescriptorImpl.getTypeId());
        }
        return null;
    }
    
    public TypeId(final int formatId, final BaseTypeIdImpl baseTypeId) {
        this.formatId = formatId;
        this.baseTypeId = baseTypeId;
        this.setTypeIdSpecificInstanceVariables();
    }
    
    public boolean equals(final Object o) {
        return o instanceof TypeId && this.getSQLTypeName().equals(((TypeId)o).getSQLTypeName());
    }
    
    public int hashCode() {
        return this.getSQLTypeName().hashCode();
    }
    
    private void setTypeIdSpecificInstanceVariables() {
        switch (this.formatId) {
            case 27: {
                this.typePrecedence = 140;
                this.javaTypeName = "byte[]";
                this.maxMaxWidth = 254;
                this.isBitTypeId = true;
                this.isConcatableTypeId = true;
                break;
            }
            case 4: {
                this.maxPrecision = 1;
                this.typePrecedence = 130;
                this.javaTypeName = "java.lang.Boolean";
                this.maxMaxWidth = 1;
                this.isBooleanTypeId = true;
                break;
            }
            case 5: {
                this.typePrecedence = 0;
                this.javaTypeName = "java.lang.String";
                this.maxMaxWidth = 254;
                this.isStringTypeId = true;
                this.isConcatableTypeId = true;
                break;
            }
            case 40: {
                this.typePrecedence = 100;
                this.javaTypeName = "java.sql.Date";
                this.maxMaxWidth = 10;
                this.maxPrecision = 10;
                this.isDateTimeTimeStampTypeId = true;
                break;
            }
            case 197: {
                this.maxPrecision = 31;
                this.maxScale = 31;
                this.typePrecedence = 70;
                this.javaTypeName = "java.math.BigDecimal";
                this.maxMaxWidth = 31;
                this.isDecimalTypeId = true;
                this.isNumericTypeId = true;
                break;
            }
            case 6: {
                this.maxPrecision = 52;
                this.maxScale = 0;
                this.typePrecedence = 90;
                this.javaTypeName = "java.lang.Double";
                this.maxMaxWidth = 8;
                this.isNumericTypeId = true;
                this.isFloatingPointTypeId = true;
                break;
            }
            case 7: {
                this.maxPrecision = 10;
                this.maxScale = 0;
                this.typePrecedence = 50;
                this.javaTypeName = "java.lang.Integer";
                this.maxMaxWidth = 4;
                this.isNumericTypeId = true;
                break;
            }
            case 11: {
                this.maxPrecision = 19;
                this.maxScale = 0;
                this.typePrecedence = 60;
                this.javaTypeName = "java.lang.Long";
                this.maxMaxWidth = 8;
                this.isNumericTypeId = true;
                break;
            }
            case 232: {
                this.typePrecedence = 160;
                this.javaTypeName = "byte[]";
                this.maxMaxWidth = 32700;
                this.isBitTypeId = true;
                this.isConcatableTypeId = true;
                this.isLongConcatableTypeId = true;
                break;
            }
            case 230: {
                this.typePrecedence = 12;
                this.javaTypeName = "java.lang.String";
                this.maxMaxWidth = 32700;
                this.isStringTypeId = true;
                this.isConcatableTypeId = true;
                this.isLongConcatableTypeId = true;
                break;
            }
            case 8: {
                this.maxPrecision = 23;
                this.maxScale = 0;
                this.typePrecedence = 80;
                this.javaTypeName = "java.lang.Float";
                this.maxMaxWidth = 4;
                this.isNumericTypeId = true;
                this.isRealTypeId = true;
                this.isFloatingPointTypeId = true;
                break;
            }
            case 9: {
                this.typePrecedence = 25;
                this.javaTypeName = "java.sql.Ref";
                this.isRefTypeId = true;
                break;
            }
            case 10: {
                this.maxPrecision = 5;
                this.maxScale = 0;
                this.typePrecedence = 40;
                this.javaTypeName = "java.lang.Integer";
                this.maxMaxWidth = 2;
                this.isNumericTypeId = true;
                break;
            }
            case 35: {
                this.typePrecedence = 120;
                this.javaTypeName = "java.sql.Time";
                this.maxScale = 0;
                this.maxMaxWidth = 8;
                this.maxPrecision = 8;
                this.isDateTimeTimeStampTypeId = true;
                break;
            }
            case 36: {
                this.typePrecedence = 110;
                this.javaTypeName = "java.sql.Timestamp";
                this.maxScale = 9;
                this.maxMaxWidth = 29;
                this.maxPrecision = 29;
                this.isDateTimeTimeStampTypeId = true;
                break;
            }
            case 195: {
                this.maxPrecision = 3;
                this.maxScale = 0;
                this.typePrecedence = 30;
                this.javaTypeName = "java.lang.Integer";
                this.maxMaxWidth = 1;
                this.isNumericTypeId = true;
                break;
            }
            case 267: {
                if (this.baseTypeId != null) {
                    this.setUserTypeIdInfo();
                }
                else {
                    this.typePrecedence = 1000;
                }
                this.maxMaxWidth = -1;
                this.isUserDefinedTypeId = true;
                break;
            }
            case 29: {
                this.typePrecedence = 150;
                this.javaTypeName = "byte[]";
                this.maxMaxWidth = 32672;
                this.isBitTypeId = true;
                this.isConcatableTypeId = true;
                break;
            }
            case 440: {
                this.typePrecedence = 170;
                this.javaTypeName = "java.sql.Blob";
                this.maxMaxWidth = Integer.MAX_VALUE;
                this.isBitTypeId = true;
                this.isConcatableTypeId = true;
                this.isLongConcatableTypeId = true;
                this.isLOBTypeId = true;
                break;
            }
            case 13: {
                this.typePrecedence = 10;
                this.javaTypeName = "java.lang.String";
                this.maxMaxWidth = 32672;
                this.isStringTypeId = true;
                this.isConcatableTypeId = true;
                break;
            }
            case 444: {
                this.typePrecedence = 14;
                this.javaTypeName = "java.sql.Clob";
                this.maxMaxWidth = Integer.MAX_VALUE;
                this.isStringTypeId = true;
                this.isConcatableTypeId = true;
                this.isLongConcatableTypeId = true;
                this.isLOBTypeId = true;
                break;
            }
            case 456: {
                this.typePrecedence = 180;
                this.javaTypeName = "org.apache.derby.iapi.types.XML";
                this.maxMaxWidth = Integer.MAX_VALUE;
                this.isLongConcatableTypeId = true;
                break;
            }
        }
    }
    
    public final int getJDBCTypeId() {
        return this.baseTypeId.getJDBCTypeId();
    }
    
    public String getSQLTypeName() {
        return this.baseTypeId.getSQLTypeName();
    }
    
    public final boolean userType() {
        return this.baseTypeId.userType();
    }
    
    public int getMaximumPrecision() {
        return this.maxPrecision;
    }
    
    public int getMaximumScale() {
        return this.maxScale;
    }
    
    private void setUserTypeIdInfo() {
        final UserDefinedTypeIdImpl userDefinedTypeIdImpl = (UserDefinedTypeIdImpl)this.baseTypeId;
        this.typePrecedence = 1000;
        this.javaTypeName = userDefinedTypeIdImpl.getClassName();
    }
    
    public boolean isStringTypeId() {
        return this.isStringTypeId;
    }
    
    public boolean isDateTimeTimeStampTypeId() {
        return this.isDateTimeTimeStampTypeId;
    }
    
    public boolean isRealTypeId() {
        return this.isRealTypeId;
    }
    
    public boolean isFloatingPointTypeId() {
        return this.isFloatingPointTypeId;
    }
    
    public boolean isDoubleTypeId() {
        return this.isFloatingPointTypeId && !this.isRealTypeId;
    }
    
    public boolean isFixedStringTypeId() {
        return this.formatId == 5;
    }
    
    public boolean isClobTypeId() {
        return this.formatId == 444;
    }
    
    public boolean isBlobTypeId() {
        return this.formatId == 440;
    }
    
    public boolean isLongVarcharTypeId() {
        return this.formatId == 230;
    }
    
    public boolean isLongVarbinaryTypeId() {
        return this.formatId == 232;
    }
    
    public boolean isDateTimeTimeStampTypeID() {
        return this.formatId == 40 || this.formatId == 35 || this.formatId == 36;
    }
    
    public boolean isTimestampId() {
        return this.formatId == 36;
    }
    
    public boolean isXMLTypeId() {
        return this.formatId == 456;
    }
    
    public boolean orderable(final ClassFactory classFactory) {
        switch (this.formatId) {
            case 230:
            case 232:
            case 440:
            case 444:
            case 456: {
                return false;
            }
            case 267: {
                return false;
            }
            default: {
                return true;
            }
        }
    }
    
    public int typePrecedence() {
        return this.typePrecedence;
    }
    
    public String getCorrespondingJavaTypeName() {
        return this.javaTypeName;
    }
    
    public String getResultSetMetaDataTypeName() {
        if (TypeId.BLOB_ID != null && TypeId.BLOB_ID.equals(this)) {
            return "java.sql.Blob";
        }
        if (TypeId.CLOB_ID != null && TypeId.CLOB_ID.equals(this)) {
            return "java.sql.Clob";
        }
        return this.getCorrespondingJavaTypeName();
    }
    
    public int getMaximumMaximumWidth() {
        return this.maxMaxWidth;
    }
    
    public String toParsableString(final DataTypeDescriptor dataTypeDescriptor) {
        return this.baseTypeId.toParsableString(dataTypeDescriptor.getCatalogType());
    }
    
    public boolean isNumericTypeId() {
        return this.isNumericTypeId;
    }
    
    public boolean isDecimalTypeId() {
        return this.isDecimalTypeId;
    }
    
    public boolean isBooleanTypeId() {
        return this.isBooleanTypeId;
    }
    
    public boolean isRefTypeId() {
        return this.isRefTypeId;
    }
    
    public boolean isConcatableTypeId() {
        return this.isConcatableTypeId;
    }
    
    public boolean isBitTypeId() {
        return this.isBitTypeId;
    }
    
    public boolean isLOBTypeId() {
        return this.isLOBTypeId;
    }
    
    public boolean isLongConcatableTypeId() {
        return this.isLongConcatableTypeId;
    }
    
    public boolean isUserDefinedTypeId() {
        return this.isUserDefinedTypeId;
    }
    
    public int getTypeFormatId() {
        return this.formatId;
    }
    
    public DataValueDescriptor getNull() {
        switch (this.formatId) {
            case 27: {
                return new SQLBit();
            }
            case 4: {
                return new SQLBoolean();
            }
            case 5: {
                return new SQLChar();
            }
            case 197: {
                return TypeId.decimalImplementation.getNewNull();
            }
            case 6: {
                return new SQLDouble();
            }
            case 7: {
                return new SQLInteger();
            }
            case 11: {
                return new SQLLongint();
            }
            case 232: {
                return new SQLLongVarbit();
            }
            case 440: {
                return new SQLBlob();
            }
            case 444: {
                return new SQLClob();
            }
            case 230: {
                return new SQLLongvarchar();
            }
            case 8: {
                return new SQLReal();
            }
            case 9: {
                return new SQLRef();
            }
            case 10: {
                return new SQLSmallint();
            }
            case 195: {
                return new SQLTinyint();
            }
            case 40: {
                return new SQLDate();
            }
            case 35: {
                return new SQLTime();
            }
            case 36: {
                return new SQLTimestamp();
            }
            case 267: {
                return new UserType();
            }
            case 29: {
                return new SQLVarbit();
            }
            case 13: {
                return new SQLVarchar();
            }
            case 456: {
                return new XML();
            }
            default: {
                return null;
            }
        }
    }
    
    public boolean streamStorable() {
        return this.isStringTypeId() || this.isBitTypeId();
    }
    
    public int getApproximateLengthInBytes(final DataTypeDescriptor dataTypeDescriptor) {
        switch (this.formatId) {
            case 27: {
                return (int)Math.ceil(dataTypeDescriptor.getMaximumWidth() / 8.0);
            }
            case 5: {
                return 2 * dataTypeDescriptor.getMaximumWidth() + 2;
            }
            case 197: {
                if (dataTypeDescriptor.getPrecision() == Integer.MAX_VALUE) {
                    return 200;
                }
                return 8 + (int)Math.ceil(dataTypeDescriptor.getPrecision() / 2.0);
            }
            case 232:
            case 440:
            case 444:
            case 456: {
                return 10240;
            }
            case 9: {
                return 16;
            }
            case 267: {
                return 200;
            }
            case 29: {
                if (dataTypeDescriptor.getMaximumWidth() == Integer.MAX_VALUE) {
                    return 200;
                }
                return (int)Math.ceil(dataTypeDescriptor.getMaximumWidth() / 8.0);
            }
            case 13:
            case 230: {
                if (dataTypeDescriptor.getMaximumWidth() == Integer.MAX_VALUE) {
                    return 200;
                }
                return dataTypeDescriptor.getMaximumWidth() * 2 + 2;
            }
            case 40: {
                return 18;
            }
            case 35: {
                return 16;
            }
            case 36: {
                return 29;
            }
            default: {
                return dataTypeDescriptor.getMaximumWidth();
            }
        }
    }
    
    public BaseTypeIdImpl getBaseTypeId() {
        return this.baseTypeId;
    }
    
    public int getPrecision(final DataTypeDescriptor dataTypeDescriptor, final DataTypeDescriptor dataTypeDescriptor2) {
        long n = this.getScale(dataTypeDescriptor, dataTypeDescriptor2) + Math.max(dataTypeDescriptor.getPrecision() - (long)dataTypeDescriptor.getScale(), dataTypeDescriptor2.getPrecision() - (long)dataTypeDescriptor2.getScale());
        if (n > 2147483647L) {
            n = 2147483647L;
        }
        return (int)n;
    }
    
    public int getScale(final DataTypeDescriptor dataTypeDescriptor, final DataTypeDescriptor dataTypeDescriptor2) {
        return Math.max(dataTypeDescriptor.getScale(), dataTypeDescriptor2.getScale());
    }
    
    public boolean variableLength() {
        switch (this.formatId) {
            case 5:
            case 13:
            case 27:
            case 29:
            case 197:
            case 440:
            case 444: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    static {
        BOOLEAN_ID = create(4, 16);
        SMALLINT_ID = create(10, 22);
        INTEGER_ID = create(7, 19);
        CHAR_ID = create(5, 17);
        TINYINT_ID = create(195, 196);
        BIGINT_ID = create(11, 23);
        REAL_ID = create(8, 20);
        DOUBLE_ID = create(6, 18);
        DECIMAL_ID = new TypeId(197, new DecimalTypeIdImpl(false));
        NUMERIC_ID = new TypeId(197, new DecimalTypeIdImpl(true));
        VARCHAR_ID = create(13, 25);
        DATE_ID = create(40, 32);
        TIME_ID = create(35, 33);
        TIMESTAMP_ID = create(36, 34);
        BIT_ID = create(27, 28);
        VARBIT_ID = create(29, 30);
        REF_ID = create(9, 21);
        LONGVARCHAR_ID = create(230, 231);
        LONGVARBIT_ID = create(232, 233);
        BLOB_ID = create(440, 442);
        CLOB_ID = create(444, 446);
        XML_ID = create(456, 457);
        ALL_BUILTIN_TYPE_IDS = new TypeId[] { TypeId.BOOLEAN_ID, TypeId.SMALLINT_ID, TypeId.INTEGER_ID, TypeId.CHAR_ID, TypeId.TINYINT_ID, TypeId.BIGINT_ID, TypeId.REAL_ID, TypeId.DOUBLE_ID, TypeId.DECIMAL_ID, TypeId.NUMERIC_ID, TypeId.VARCHAR_ID, TypeId.DATE_ID, TypeId.TIME_ID, TypeId.TIMESTAMP_ID, TypeId.BIT_ID, TypeId.VARBIT_ID, TypeId.REF_ID, TypeId.LONGVARCHAR_ID, TypeId.LONGVARBIT_ID, TypeId.BLOB_ID, TypeId.CLOB_ID, TypeId.XML_ID };
    }
}
