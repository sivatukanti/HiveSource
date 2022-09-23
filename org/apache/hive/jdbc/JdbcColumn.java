// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.jdbc;

import java.sql.SQLException;
import org.apache.hadoop.hive.common.type.HiveIntervalDayTime;
import org.apache.hadoop.hive.common.type.HiveIntervalYearMonth;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.sql.Date;
import org.apache.hive.service.cli.Type;

public class JdbcColumn
{
    private final String columnName;
    private final String tableName;
    private final String tableCatalog;
    private final String type;
    private final String comment;
    private final int ordinalPos;
    
    JdbcColumn(final String columnName, final String tableName, final String tableCatalog, final String type, final String comment, final int ordinalPos) {
        this.columnName = columnName;
        this.tableName = tableName;
        this.tableCatalog = tableCatalog;
        this.type = type;
        this.comment = comment;
        this.ordinalPos = ordinalPos;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public String getTableCatalog() {
        return this.tableCatalog;
    }
    
    public String getType() {
        return this.type;
    }
    
    static String columnClassName(final Type hiveType, final JdbcColumnAttributes columnAttributes) throws SQLException {
        final int columnType = hiveTypeToSqlType(hiveType);
        switch (columnType) {
            case 0: {
                return "null";
            }
            case 16: {
                return Boolean.class.getName();
            }
            case 1:
            case 12: {
                return String.class.getName();
            }
            case -6: {
                return Byte.class.getName();
            }
            case 5: {
                return Short.class.getName();
            }
            case 4: {
                return Integer.class.getName();
            }
            case -5: {
                return Long.class.getName();
            }
            case 91: {
                return Date.class.getName();
            }
            case 6: {
                return Float.class.getName();
            }
            case 8: {
                return Double.class.getName();
            }
            case 93: {
                return Timestamp.class.getName();
            }
            case 3: {
                return BigInteger.class.getName();
            }
            case -2: {
                return byte[].class.getName();
            }
            case 1111:
            case 2000: {
                switch (hiveType) {
                    case INTERVAL_YEAR_MONTH_TYPE: {
                        return HiveIntervalYearMonth.class.getName();
                    }
                    case INTERVAL_DAY_TIME_TYPE: {
                        return HiveIntervalDayTime.class.getName();
                    }
                    default: {
                        return String.class.getName();
                    }
                }
                break;
            }
            case 2002:
            case 2003: {
                return String.class.getName();
            }
            default: {
                throw new SQLException("Invalid column type: " + columnType);
            }
        }
    }
    
    static Type typeStringToHiveType(final String type) throws SQLException {
        if ("string".equalsIgnoreCase(type)) {
            return Type.STRING_TYPE;
        }
        if ("varchar".equalsIgnoreCase(type)) {
            return Type.VARCHAR_TYPE;
        }
        if ("char".equalsIgnoreCase(type)) {
            return Type.CHAR_TYPE;
        }
        if ("float".equalsIgnoreCase(type)) {
            return Type.FLOAT_TYPE;
        }
        if ("double".equalsIgnoreCase(type)) {
            return Type.DOUBLE_TYPE;
        }
        if ("boolean".equalsIgnoreCase(type)) {
            return Type.BOOLEAN_TYPE;
        }
        if ("tinyint".equalsIgnoreCase(type)) {
            return Type.TINYINT_TYPE;
        }
        if ("smallint".equalsIgnoreCase(type)) {
            return Type.SMALLINT_TYPE;
        }
        if ("int".equalsIgnoreCase(type)) {
            return Type.INT_TYPE;
        }
        if ("bigint".equalsIgnoreCase(type)) {
            return Type.BIGINT_TYPE;
        }
        if ("date".equalsIgnoreCase(type)) {
            return Type.DATE_TYPE;
        }
        if ("timestamp".equalsIgnoreCase(type)) {
            return Type.TIMESTAMP_TYPE;
        }
        if ("interval_year_month".equalsIgnoreCase(type)) {
            return Type.INTERVAL_YEAR_MONTH_TYPE;
        }
        if ("interval_day_time".equalsIgnoreCase(type)) {
            return Type.INTERVAL_DAY_TIME_TYPE;
        }
        if ("decimal".equalsIgnoreCase(type)) {
            return Type.DECIMAL_TYPE;
        }
        if ("binary".equalsIgnoreCase(type)) {
            return Type.BINARY_TYPE;
        }
        if ("map".equalsIgnoreCase(type)) {
            return Type.MAP_TYPE;
        }
        if ("array".equalsIgnoreCase(type)) {
            return Type.ARRAY_TYPE;
        }
        if ("struct".equalsIgnoreCase(type)) {
            return Type.STRUCT_TYPE;
        }
        throw new SQLException("Unrecognized column type: " + type);
    }
    
    public static int hiveTypeToSqlType(final Type hiveType) throws SQLException {
        return hiveType.toJavaSQLType();
    }
    
    public static int hiveTypeToSqlType(final String type) throws SQLException {
        if ("void".equalsIgnoreCase(type) || "null".equalsIgnoreCase(type)) {
            return 0;
        }
        return hiveTypeToSqlType(typeStringToHiveType(type));
    }
    
    static String getColumnTypeName(final String type) throws SQLException {
        if ("string".equalsIgnoreCase(type)) {
            return "string";
        }
        if ("varchar".equalsIgnoreCase(type)) {
            return "varchar";
        }
        if ("char".equalsIgnoreCase(type)) {
            return "char";
        }
        if ("float".equalsIgnoreCase(type)) {
            return "float";
        }
        if ("double".equalsIgnoreCase(type)) {
            return "double";
        }
        if ("boolean".equalsIgnoreCase(type)) {
            return "boolean";
        }
        if ("tinyint".equalsIgnoreCase(type)) {
            return "tinyint";
        }
        if ("smallint".equalsIgnoreCase(type)) {
            return "smallint";
        }
        if ("int".equalsIgnoreCase(type)) {
            return "int";
        }
        if ("bigint".equalsIgnoreCase(type)) {
            return "bigint";
        }
        if ("timestamp".equalsIgnoreCase(type)) {
            return "timestamp";
        }
        if ("date".equalsIgnoreCase(type)) {
            return "date";
        }
        if ("interval_year_month".equalsIgnoreCase(type)) {
            return "interval_year_month";
        }
        if ("interval_day_time".equalsIgnoreCase(type)) {
            return "interval_day_time";
        }
        if ("decimal".equalsIgnoreCase(type)) {
            return "decimal";
        }
        if ("binary".equalsIgnoreCase(type)) {
            return "binary";
        }
        if ("void".equalsIgnoreCase(type) || "null".equalsIgnoreCase(type)) {
            return "void";
        }
        if (type.equalsIgnoreCase("map")) {
            return "map";
        }
        if (type.equalsIgnoreCase("array")) {
            return "array";
        }
        if (type.equalsIgnoreCase("struct")) {
            return "struct";
        }
        throw new SQLException("Unrecognized column type: " + type);
    }
    
    static int columnDisplaySize(final Type hiveType, final JdbcColumnAttributes columnAttributes) throws SQLException {
        final int columnType = hiveTypeToSqlType(hiveType);
        switch (columnType) {
            case 16: {
                return columnPrecision(hiveType, columnAttributes);
            }
            case 1:
            case 12: {
                return columnPrecision(hiveType, columnAttributes);
            }
            case -2: {
                return Integer.MAX_VALUE;
            }
            case -6:
            case -5:
            case 4:
            case 5: {
                return columnPrecision(hiveType, columnAttributes) + 1;
            }
            case 91: {
                return 10;
            }
            case 93: {
                return columnPrecision(hiveType, columnAttributes);
            }
            case 6: {
                return 24;
            }
            case 8: {
                return 25;
            }
            case 3: {
                return columnPrecision(hiveType, columnAttributes) + 2;
            }
            case 1111:
            case 2000: {
                return columnPrecision(hiveType, columnAttributes);
            }
            case 2002:
            case 2003: {
                return Integer.MAX_VALUE;
            }
            default: {
                throw new SQLException("Invalid column type: " + columnType);
            }
        }
    }
    
    static int columnPrecision(final Type hiveType, final JdbcColumnAttributes columnAttributes) throws SQLException {
        final int columnType = hiveTypeToSqlType(hiveType);
        switch (columnType) {
            case 16: {
                return 1;
            }
            case 1:
            case 12: {
                if (columnAttributes != null) {
                    return columnAttributes.precision;
                }
                return Integer.MAX_VALUE;
            }
            case -2: {
                return Integer.MAX_VALUE;
            }
            case -6: {
                return 3;
            }
            case 5: {
                return 5;
            }
            case 4: {
                return 10;
            }
            case -5: {
                return 19;
            }
            case 6: {
                return 7;
            }
            case 8: {
                return 15;
            }
            case 91: {
                return 10;
            }
            case 93: {
                return 29;
            }
            case 3: {
                return columnAttributes.precision;
            }
            case 1111:
            case 2000: {
                switch (hiveType) {
                    case INTERVAL_YEAR_MONTH_TYPE: {
                        return 11;
                    }
                    case INTERVAL_DAY_TIME_TYPE: {
                        return 29;
                    }
                    default: {
                        return Integer.MAX_VALUE;
                    }
                }
                break;
            }
            case 2002:
            case 2003: {
                return Integer.MAX_VALUE;
            }
            default: {
                throw new SQLException("Invalid column type: " + columnType);
            }
        }
    }
    
    static int columnScale(final Type hiveType, final JdbcColumnAttributes columnAttributes) throws SQLException {
        final int columnType = hiveTypeToSqlType(hiveType);
        switch (columnType) {
            case -6:
            case -5:
            case -2:
            case 1:
            case 4:
            case 5:
            case 12:
            case 16:
            case 91: {
                return 0;
            }
            case 6: {
                return 7;
            }
            case 8: {
                return 15;
            }
            case 93: {
                return 9;
            }
            case 3: {
                return columnAttributes.scale;
            }
            case 1111:
            case 2000:
            case 2002:
            case 2003: {
                return 0;
            }
            default: {
                throw new SQLException("Invalid column type: " + columnType);
            }
        }
    }
    
    public Integer getNumPrecRadix() {
        if (this.type.equalsIgnoreCase("tinyint")) {
            return 10;
        }
        if (this.type.equalsIgnoreCase("smallint")) {
            return 10;
        }
        if (this.type.equalsIgnoreCase("int")) {
            return 10;
        }
        if (this.type.equalsIgnoreCase("bigint")) {
            return 10;
        }
        if (this.type.equalsIgnoreCase("float")) {
            return 10;
        }
        if (this.type.equalsIgnoreCase("double")) {
            return 10;
        }
        if (this.type.equalsIgnoreCase("decimal")) {
            return 10;
        }
        return null;
    }
    
    public String getComment() {
        return this.comment;
    }
    
    public int getOrdinalPos() {
        return this.ordinalPos;
    }
}
