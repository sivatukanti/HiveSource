// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.typeinfo;

import org.apache.hadoop.hive.serde2.io.HiveDecimalWritable;
import java.math.BigDecimal;
import org.apache.hadoop.hive.common.type.HiveDecimal;

public class HiveDecimalUtils
{
    public static HiveDecimal enforcePrecisionScale(final HiveDecimal dec, final DecimalTypeInfo typeInfo) {
        return enforcePrecisionScale(dec, typeInfo.precision(), typeInfo.scale());
    }
    
    public static HiveDecimal enforcePrecisionScale(final HiveDecimal dec, final int maxPrecision, final int maxScale) {
        if (dec == null) {
            return null;
        }
        if (dec.precision() - dec.scale() <= maxPrecision - maxScale && dec.scale() <= maxScale) {
            return dec;
        }
        final BigDecimal bd = HiveDecimal.enforcePrecisionScale(dec.bigDecimalValue(), maxPrecision, maxScale);
        if (bd == null) {
            return null;
        }
        return HiveDecimal.create(bd);
    }
    
    public static HiveDecimalWritable enforcePrecisionScale(final HiveDecimalWritable writable, final DecimalTypeInfo typeInfo) {
        if (writable == null) {
            return null;
        }
        final HiveDecimal dec = enforcePrecisionScale(writable.getHiveDecimal(), typeInfo);
        return (dec == null) ? null : new HiveDecimalWritable(dec);
    }
    
    public static HiveDecimalWritable enforcePrecisionScale(final HiveDecimalWritable writable, final int precision, final int scale) {
        if (writable == null) {
            return null;
        }
        final HiveDecimal dec = enforcePrecisionScale(writable.getHiveDecimal(), precision, scale);
        return (dec == null) ? null : new HiveDecimalWritable(dec);
    }
    
    public static void validateParameter(final int precision, final int scale) {
        if (precision < 1 || precision > 38) {
            throw new IllegalArgumentException("Decimal precision out of allowed range [1,38]");
        }
        if (scale < 0 || scale > 38) {
            throw new IllegalArgumentException("Decimal scale out of allowed range [0,38]");
        }
        if (precision < scale) {
            throw new IllegalArgumentException("Decimal scale must be less than or equal to precision");
        }
    }
    
    public static int getPrecisionForType(final PrimitiveTypeInfo typeInfo) {
        switch (typeInfo.getPrimitiveCategory()) {
            case DECIMAL: {
                return ((DecimalTypeInfo)typeInfo).precision();
            }
            case FLOAT: {
                return 7;
            }
            case DOUBLE: {
                return 15;
            }
            case BYTE: {
                return 3;
            }
            case SHORT: {
                return 5;
            }
            case INT: {
                return 10;
            }
            case LONG: {
                return 19;
            }
            case VOID: {
                return 1;
            }
            default: {
                return 38;
            }
        }
    }
    
    public static int getScaleForType(final PrimitiveTypeInfo typeInfo) {
        switch (typeInfo.getPrimitiveCategory()) {
            case DECIMAL: {
                return ((DecimalTypeInfo)typeInfo).scale();
            }
            case FLOAT: {
                return 7;
            }
            case DOUBLE: {
                return 15;
            }
            case BYTE:
            case SHORT:
            case INT:
            case LONG:
            case VOID: {
                return 0;
            }
            default: {
                return 38;
            }
        }
    }
    
    public static TypeInfo getDecimalTypeForPrimitiveCategories(final PrimitiveTypeInfo a, final PrimitiveTypeInfo b) {
        final int prec1 = getPrecisionForType(a);
        final int prec2 = getPrecisionForType(b);
        final int scale1 = getScaleForType(a);
        final int scale2 = getScaleForType(b);
        final int intPart = Math.max(prec1 - scale1, prec2 - scale2);
        final int decPart = Math.max(scale1, scale2);
        final int prec3 = Math.min(intPart + decPart, 38);
        final int scale3 = Math.min(decPart, 38 - intPart);
        return TypeInfoFactory.getDecimalTypeInfo(prec3, scale3);
    }
    
    public static DecimalTypeInfo getDecimalTypeForPrimitiveCategory(final PrimitiveTypeInfo a) {
        if (a instanceof DecimalTypeInfo) {
            return (DecimalTypeInfo)a;
        }
        int prec = getPrecisionForType(a);
        int scale = getScaleForType(a);
        prec = Math.min(prec, 38);
        scale = Math.min(scale, 38 - (prec - scale));
        return TypeInfoFactory.getDecimalTypeInfo(prec, scale);
    }
}
