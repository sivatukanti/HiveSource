// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde;

import java.util.HashSet;
import java.util.Set;

public class serdeConstants
{
    public static final String SERIALIZATION_LIB = "serialization.lib";
    public static final String SERIALIZATION_CLASS = "serialization.class";
    public static final String SERIALIZATION_FORMAT = "serialization.format";
    public static final String SERIALIZATION_DDL = "serialization.ddl";
    public static final String SERIALIZATION_NULL_FORMAT = "serialization.null.format";
    public static final String SERIALIZATION_LAST_COLUMN_TAKES_REST = "serialization.last.column.takes.rest";
    public static final String SERIALIZATION_SORT_ORDER = "serialization.sort.order";
    public static final String SERIALIZATION_USE_JSON_OBJECTS = "serialization.use.json.object";
    public static final String SERIALIZATION_ENCODING = "serialization.encoding";
    public static final String FIELD_DELIM = "field.delim";
    public static final String COLLECTION_DELIM = "colelction.delim";
    public static final String LINE_DELIM = "line.delim";
    public static final String MAPKEY_DELIM = "mapkey.delim";
    public static final String QUOTE_CHAR = "quote.delim";
    public static final String ESCAPE_CHAR = "escape.delim";
    public static final String HEADER_COUNT = "skip.header.line.count";
    public static final String FOOTER_COUNT = "skip.footer.line.count";
    public static final String VOID_TYPE_NAME = "void";
    public static final String BOOLEAN_TYPE_NAME = "boolean";
    public static final String TINYINT_TYPE_NAME = "tinyint";
    public static final String SMALLINT_TYPE_NAME = "smallint";
    public static final String INT_TYPE_NAME = "int";
    public static final String BIGINT_TYPE_NAME = "bigint";
    public static final String FLOAT_TYPE_NAME = "float";
    public static final String DOUBLE_TYPE_NAME = "double";
    public static final String STRING_TYPE_NAME = "string";
    public static final String CHAR_TYPE_NAME = "char";
    public static final String VARCHAR_TYPE_NAME = "varchar";
    public static final String DATE_TYPE_NAME = "date";
    public static final String DATETIME_TYPE_NAME = "datetime";
    public static final String TIMESTAMP_TYPE_NAME = "timestamp";
    public static final String DECIMAL_TYPE_NAME = "decimal";
    public static final String BINARY_TYPE_NAME = "binary";
    public static final String INTERVAL_YEAR_MONTH_TYPE_NAME = "interval_year_month";
    public static final String INTERVAL_DAY_TIME_TYPE_NAME = "interval_day_time";
    public static final String LIST_TYPE_NAME = "array";
    public static final String MAP_TYPE_NAME = "map";
    public static final String STRUCT_TYPE_NAME = "struct";
    public static final String UNION_TYPE_NAME = "uniontype";
    public static final String LIST_COLUMNS = "columns";
    public static final String LIST_COLUMN_TYPES = "columns.types";
    public static final String TIMESTAMP_FORMATS = "timestamp.formats";
    public static final Set<String> PrimitiveTypes;
    public static final Set<String> CollectionTypes;
    public static final Set<String> IntegralTypes;
    
    static {
        (PrimitiveTypes = new HashSet<String>()).add("void");
        serdeConstants.PrimitiveTypes.add("boolean");
        serdeConstants.PrimitiveTypes.add("tinyint");
        serdeConstants.PrimitiveTypes.add("smallint");
        serdeConstants.PrimitiveTypes.add("int");
        serdeConstants.PrimitiveTypes.add("bigint");
        serdeConstants.PrimitiveTypes.add("float");
        serdeConstants.PrimitiveTypes.add("double");
        serdeConstants.PrimitiveTypes.add("string");
        serdeConstants.PrimitiveTypes.add("varchar");
        serdeConstants.PrimitiveTypes.add("char");
        serdeConstants.PrimitiveTypes.add("date");
        serdeConstants.PrimitiveTypes.add("datetime");
        serdeConstants.PrimitiveTypes.add("timestamp");
        serdeConstants.PrimitiveTypes.add("interval_year_month");
        serdeConstants.PrimitiveTypes.add("interval_day_time");
        serdeConstants.PrimitiveTypes.add("decimal");
        serdeConstants.PrimitiveTypes.add("binary");
        (CollectionTypes = new HashSet<String>()).add("array");
        serdeConstants.CollectionTypes.add("map");
        (IntegralTypes = new HashSet<String>()).add("tinyint");
        serdeConstants.IntegralTypes.add("smallint");
        serdeConstants.IntegralTypes.add("int");
        serdeConstants.IntegralTypes.add("bigint");
    }
}
