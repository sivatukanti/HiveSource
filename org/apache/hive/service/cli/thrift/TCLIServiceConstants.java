// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TCLIServiceConstants
{
    public static final Set<TTypeId> PRIMITIVE_TYPES;
    public static final Set<TTypeId> COMPLEX_TYPES;
    public static final Set<TTypeId> COLLECTION_TYPES;
    public static final Map<TTypeId, String> TYPE_NAMES;
    public static final String CHARACTER_MAXIMUM_LENGTH = "characterMaximumLength";
    public static final String PRECISION = "precision";
    public static final String SCALE = "scale";
    
    static {
        (PRIMITIVE_TYPES = new HashSet<TTypeId>()).add(TTypeId.BOOLEAN_TYPE);
        TCLIServiceConstants.PRIMITIVE_TYPES.add(TTypeId.TINYINT_TYPE);
        TCLIServiceConstants.PRIMITIVE_TYPES.add(TTypeId.SMALLINT_TYPE);
        TCLIServiceConstants.PRIMITIVE_TYPES.add(TTypeId.INT_TYPE);
        TCLIServiceConstants.PRIMITIVE_TYPES.add(TTypeId.BIGINT_TYPE);
        TCLIServiceConstants.PRIMITIVE_TYPES.add(TTypeId.FLOAT_TYPE);
        TCLIServiceConstants.PRIMITIVE_TYPES.add(TTypeId.DOUBLE_TYPE);
        TCLIServiceConstants.PRIMITIVE_TYPES.add(TTypeId.STRING_TYPE);
        TCLIServiceConstants.PRIMITIVE_TYPES.add(TTypeId.TIMESTAMP_TYPE);
        TCLIServiceConstants.PRIMITIVE_TYPES.add(TTypeId.BINARY_TYPE);
        TCLIServiceConstants.PRIMITIVE_TYPES.add(TTypeId.DECIMAL_TYPE);
        TCLIServiceConstants.PRIMITIVE_TYPES.add(TTypeId.NULL_TYPE);
        TCLIServiceConstants.PRIMITIVE_TYPES.add(TTypeId.DATE_TYPE);
        TCLIServiceConstants.PRIMITIVE_TYPES.add(TTypeId.VARCHAR_TYPE);
        TCLIServiceConstants.PRIMITIVE_TYPES.add(TTypeId.CHAR_TYPE);
        TCLIServiceConstants.PRIMITIVE_TYPES.add(TTypeId.INTERVAL_YEAR_MONTH_TYPE);
        TCLIServiceConstants.PRIMITIVE_TYPES.add(TTypeId.INTERVAL_DAY_TIME_TYPE);
        (COMPLEX_TYPES = new HashSet<TTypeId>()).add(TTypeId.ARRAY_TYPE);
        TCLIServiceConstants.COMPLEX_TYPES.add(TTypeId.MAP_TYPE);
        TCLIServiceConstants.COMPLEX_TYPES.add(TTypeId.STRUCT_TYPE);
        TCLIServiceConstants.COMPLEX_TYPES.add(TTypeId.UNION_TYPE);
        TCLIServiceConstants.COMPLEX_TYPES.add(TTypeId.USER_DEFINED_TYPE);
        (COLLECTION_TYPES = new HashSet<TTypeId>()).add(TTypeId.ARRAY_TYPE);
        TCLIServiceConstants.COLLECTION_TYPES.add(TTypeId.MAP_TYPE);
        (TYPE_NAMES = new HashMap<TTypeId, String>()).put(TTypeId.BOOLEAN_TYPE, "BOOLEAN");
        TCLIServiceConstants.TYPE_NAMES.put(TTypeId.TINYINT_TYPE, "TINYINT");
        TCLIServiceConstants.TYPE_NAMES.put(TTypeId.SMALLINT_TYPE, "SMALLINT");
        TCLIServiceConstants.TYPE_NAMES.put(TTypeId.INT_TYPE, "INT");
        TCLIServiceConstants.TYPE_NAMES.put(TTypeId.BIGINT_TYPE, "BIGINT");
        TCLIServiceConstants.TYPE_NAMES.put(TTypeId.FLOAT_TYPE, "FLOAT");
        TCLIServiceConstants.TYPE_NAMES.put(TTypeId.DOUBLE_TYPE, "DOUBLE");
        TCLIServiceConstants.TYPE_NAMES.put(TTypeId.STRING_TYPE, "STRING");
        TCLIServiceConstants.TYPE_NAMES.put(TTypeId.TIMESTAMP_TYPE, "TIMESTAMP");
        TCLIServiceConstants.TYPE_NAMES.put(TTypeId.BINARY_TYPE, "BINARY");
        TCLIServiceConstants.TYPE_NAMES.put(TTypeId.ARRAY_TYPE, "ARRAY");
        TCLIServiceConstants.TYPE_NAMES.put(TTypeId.MAP_TYPE, "MAP");
        TCLIServiceConstants.TYPE_NAMES.put(TTypeId.STRUCT_TYPE, "STRUCT");
        TCLIServiceConstants.TYPE_NAMES.put(TTypeId.UNION_TYPE, "UNIONTYPE");
        TCLIServiceConstants.TYPE_NAMES.put(TTypeId.DECIMAL_TYPE, "DECIMAL");
        TCLIServiceConstants.TYPE_NAMES.put(TTypeId.NULL_TYPE, "NULL");
        TCLIServiceConstants.TYPE_NAMES.put(TTypeId.DATE_TYPE, "DATE");
        TCLIServiceConstants.TYPE_NAMES.put(TTypeId.VARCHAR_TYPE, "VARCHAR");
        TCLIServiceConstants.TYPE_NAMES.put(TTypeId.CHAR_TYPE, "CHAR");
        TCLIServiceConstants.TYPE_NAMES.put(TTypeId.INTERVAL_YEAR_MONTH_TYPE, "INTERVAL_YEAR_MONTH");
        TCLIServiceConstants.TYPE_NAMES.put(TTypeId.INTERVAL_DAY_TIME_TYPE, "INTERVAL_DAY_TIME");
    }
}
