// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import java.util.Iterator;
import org.apache.hadoop.hive.serde2.typeinfo.UnionTypeInfo;
import java.util.ArrayList;
import org.apache.hadoop.hive.serde2.typeinfo.StructTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.ListTypeInfo;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.LazyObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.typeinfo.MapTypeInfo;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyPrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyObjectInspectorParameters;
import java.util.List;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyObjectInspectorParametersImpl;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.LazyUnionObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.LazySimpleStructObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.LazyListObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.LazyMapObjectInspector;
import org.apache.hadoop.hive.serde2.lazydio.LazyDioDouble;
import org.apache.hadoop.hive.serde2.lazydio.LazyDioFloat;
import org.apache.hadoop.hive.serde2.lazydio.LazyDioLong;
import org.apache.hadoop.hive.serde2.lazydio.LazyDioInteger;
import org.apache.hadoop.hive.serde2.lazydio.LazyDioShort;
import org.apache.hadoop.hive.serde2.lazydio.LazyDioByte;
import org.apache.hadoop.hive.serde2.lazydio.LazyDioBoolean;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyVoidObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyHiveDecimalObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyBinaryObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyHiveIntervalDayTimeObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyHiveIntervalYearMonthObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyTimestampObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyDateObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyHiveVarcharObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyHiveCharObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyStringObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyDoubleObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyFloatObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyLongObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyIntObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyShortObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyByteObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyBooleanObjectInspector;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;

public final class LazyFactory
{
    public static LazyPrimitive<? extends ObjectInspector, ? extends Writable> createLazyPrimitiveClass(final PrimitiveObjectInspector poi, final boolean typeBinary) {
        if (typeBinary) {
            return createLazyPrimitiveBinaryClass(poi);
        }
        return createLazyPrimitiveClass(poi);
    }
    
    public static LazyPrimitive<? extends ObjectInspector, ? extends Writable> createLazyPrimitiveClass(final PrimitiveObjectInspector oi) {
        final PrimitiveObjectInspector.PrimitiveCategory p = oi.getPrimitiveCategory();
        switch (p) {
            case BOOLEAN: {
                return new LazyBoolean((LazyBooleanObjectInspector)oi);
            }
            case BYTE: {
                return new LazyByte((LazyByteObjectInspector)oi);
            }
            case SHORT: {
                return new LazyShort((LazyShortObjectInspector)oi);
            }
            case INT: {
                return new LazyInteger((LazyIntObjectInspector)oi);
            }
            case LONG: {
                return new LazyLong((LazyLongObjectInspector)oi);
            }
            case FLOAT: {
                return new LazyFloat((LazyFloatObjectInspector)oi);
            }
            case DOUBLE: {
                return new LazyDouble((LazyDoubleObjectInspector)oi);
            }
            case STRING: {
                return new LazyString((LazyStringObjectInspector)oi);
            }
            case CHAR: {
                return new LazyHiveChar((LazyHiveCharObjectInspector)oi);
            }
            case VARCHAR: {
                return new LazyHiveVarchar((LazyHiveVarcharObjectInspector)oi);
            }
            case DATE: {
                return new LazyDate((LazyDateObjectInspector)oi);
            }
            case TIMESTAMP: {
                return new LazyTimestamp((LazyTimestampObjectInspector)oi);
            }
            case INTERVAL_YEAR_MONTH: {
                return new LazyHiveIntervalYearMonth((LazyHiveIntervalYearMonthObjectInspector)oi);
            }
            case INTERVAL_DAY_TIME: {
                return new LazyHiveIntervalDayTime((LazyHiveIntervalDayTimeObjectInspector)oi);
            }
            case BINARY: {
                return new LazyBinary((LazyBinaryObjectInspector)oi);
            }
            case DECIMAL: {
                return new LazyHiveDecimal((LazyHiveDecimalObjectInspector)oi);
            }
            case VOID: {
                return new LazyVoid((LazyVoidObjectInspector)oi);
            }
            default: {
                throw new RuntimeException("Internal error: no LazyObject for " + p);
            }
        }
    }
    
    public static LazyPrimitive<? extends ObjectInspector, ? extends Writable> createLazyPrimitiveBinaryClass(final PrimitiveObjectInspector poi) {
        final PrimitiveObjectInspector.PrimitiveCategory pc = poi.getPrimitiveCategory();
        switch (pc) {
            case BOOLEAN: {
                return new LazyDioBoolean((LazyBooleanObjectInspector)poi);
            }
            case BYTE: {
                return new LazyDioByte((LazyByteObjectInspector)poi);
            }
            case SHORT: {
                return new LazyDioShort((LazyShortObjectInspector)poi);
            }
            case INT: {
                return new LazyDioInteger((LazyIntObjectInspector)poi);
            }
            case LONG: {
                return new LazyDioLong((LazyLongObjectInspector)poi);
            }
            case FLOAT: {
                return new LazyDioFloat((LazyFloatObjectInspector)poi);
            }
            case DOUBLE: {
                return new LazyDioDouble((LazyDoubleObjectInspector)poi);
            }
            default: {
                throw new RuntimeException("Hive Internal Error: no LazyObject for " + poi);
            }
        }
    }
    
    public static LazyObject<? extends ObjectInspector> createLazyObject(final ObjectInspector oi) {
        final ObjectInspector.Category c = oi.getCategory();
        switch (c) {
            case PRIMITIVE: {
                return createLazyPrimitiveClass((PrimitiveObjectInspector)oi);
            }
            case MAP: {
                return new LazyMap((LazyMapObjectInspector)oi);
            }
            case LIST: {
                return new LazyArray((LazyListObjectInspector)oi);
            }
            case STRUCT: {
                return new LazyStruct((LazySimpleStructObjectInspector)oi);
            }
            case UNION: {
                return new LazyUnion((LazyUnionObjectInspector)oi);
            }
            default: {
                throw new RuntimeException("Hive LazySerDe Internal error.");
            }
        }
    }
    
    public static LazyObject<? extends ObjectInspector> createLazyObject(final ObjectInspector oi, final boolean typeBinary) {
        if (oi.getCategory() == ObjectInspector.Category.PRIMITIVE) {
            return createLazyPrimitiveClass((PrimitiveObjectInspector)oi, typeBinary);
        }
        return createLazyObject(oi);
    }
    
    @Deprecated
    public static ObjectInspector createLazyObjectInspector(final TypeInfo typeInfo, final byte[] separators, final int separatorIndex, final Text nullSequence, final boolean escaped, final byte escapeChar, final ObjectInspectorFactory.ObjectInspectorOptions option) throws SerDeException {
        return createLazyObjectInspector(typeInfo, separators, separatorIndex, nullSequence, escaped, escapeChar, false, option);
    }
    
    @Deprecated
    public static ObjectInspector createLazyObjectInspector(final TypeInfo typeInfo, final byte[] separators, final int separatorIndex, final Text nullSequence, final boolean escaped, final byte escapeChar) throws SerDeException {
        return createLazyObjectInspector(typeInfo, separators, separatorIndex, nullSequence, escaped, escapeChar, false, ObjectInspectorFactory.ObjectInspectorOptions.JAVA);
    }
    
    @Deprecated
    public static ObjectInspector createLazyObjectInspector(final TypeInfo typeInfo, final byte[] separators, final int separatorIndex, final Text nullSequence, final boolean escaped, final byte escapeChar, final boolean extendedBooleanLiteral) throws SerDeException {
        return createLazyObjectInspector(typeInfo, separators, separatorIndex, nullSequence, escaped, escapeChar, extendedBooleanLiteral, ObjectInspectorFactory.ObjectInspectorOptions.JAVA);
    }
    
    @Deprecated
    public static ObjectInspector createLazyObjectInspector(final TypeInfo typeInfo, final byte[] separators, final int separatorIndex, final Text nullSequence, final boolean escaped, final byte escapeChar, final boolean extendedBooleanLiteral, final ObjectInspectorFactory.ObjectInspectorOptions option) throws SerDeException {
        final LazyObjectInspectorParametersImpl lazyParams = new LazyObjectInspectorParametersImpl(escaped, escapeChar, extendedBooleanLiteral, null, separators, nullSequence);
        return createLazyObjectInspector(typeInfo, separatorIndex, lazyParams, option);
    }
    
    public static ObjectInspector createLazyObjectInspector(final TypeInfo typeInfo, final int separatorIndex, final LazyObjectInspectorParameters lazyParams, final ObjectInspectorFactory.ObjectInspectorOptions option) throws SerDeException {
        final ObjectInspector.Category c = typeInfo.getCategory();
        switch (c) {
            case PRIMITIVE: {
                return LazyPrimitiveObjectInspectorFactory.getLazyObjectInspector((PrimitiveTypeInfo)typeInfo, lazyParams);
            }
            case MAP: {
                return LazyObjectInspectorFactory.getLazySimpleMapObjectInspector(createLazyObjectInspector(((MapTypeInfo)typeInfo).getMapKeyTypeInfo(), separatorIndex + 2, lazyParams, option), createLazyObjectInspector(((MapTypeInfo)typeInfo).getMapValueTypeInfo(), separatorIndex + 2, lazyParams, option), LazyUtils.getSeparator(lazyParams.getSeparators(), separatorIndex), LazyUtils.getSeparator(lazyParams.getSeparators(), separatorIndex + 1), lazyParams);
            }
            case LIST: {
                return LazyObjectInspectorFactory.getLazySimpleListObjectInspector(createLazyObjectInspector(((ListTypeInfo)typeInfo).getListElementTypeInfo(), separatorIndex + 1, lazyParams, option), LazyUtils.getSeparator(lazyParams.getSeparators(), separatorIndex), lazyParams);
            }
            case STRUCT: {
                final StructTypeInfo structTypeInfo = (StructTypeInfo)typeInfo;
                final List<String> fieldNames = structTypeInfo.getAllStructFieldNames();
                final List<TypeInfo> fieldTypeInfos = structTypeInfo.getAllStructFieldTypeInfos();
                final List<ObjectInspector> fieldObjectInspectors = new ArrayList<ObjectInspector>(fieldTypeInfos.size());
                for (int i = 0; i < fieldTypeInfos.size(); ++i) {
                    fieldObjectInspectors.add(createLazyObjectInspector(fieldTypeInfos.get(i), separatorIndex + 1, lazyParams, option));
                }
                return LazyObjectInspectorFactory.getLazySimpleStructObjectInspector(fieldNames, fieldObjectInspectors, null, LazyUtils.getSeparator(lazyParams.getSeparators(), separatorIndex), lazyParams, option);
            }
            case UNION: {
                final UnionTypeInfo unionTypeInfo = (UnionTypeInfo)typeInfo;
                final List<ObjectInspector> lazyOIs = new ArrayList<ObjectInspector>();
                for (final TypeInfo uti : unionTypeInfo.getAllUnionObjectTypeInfos()) {
                    lazyOIs.add(createLazyObjectInspector(uti, separatorIndex + 1, lazyParams, option));
                }
                return LazyObjectInspectorFactory.getLazyUnionObjectInspector(lazyOIs, LazyUtils.getSeparator(lazyParams.getSeparators(), separatorIndex), lazyParams);
            }
            default: {
                throw new RuntimeException("Hive LazySerDe Internal error.");
            }
        }
    }
    
    @Deprecated
    public static ObjectInspector createLazyStructInspector(final List<String> columnNames, final List<TypeInfo> typeInfos, final byte[] separators, final Text nullSequence, final boolean lastColumnTakesRest, final boolean escaped, final byte escapeChar) throws SerDeException {
        return createLazyStructInspector(columnNames, typeInfos, separators, nullSequence, lastColumnTakesRest, escaped, escapeChar, false);
    }
    
    @Deprecated
    public static ObjectInspector createLazyStructInspector(final List<String> columnNames, final List<TypeInfo> typeInfos, final byte[] separators, final Text nullSequence, final boolean lastColumnTakesRest, final boolean escaped, final byte escapeChar, final boolean extendedBooleanLiteral) throws SerDeException {
        final LazyObjectInspectorParametersImpl lazyParams = new LazyObjectInspectorParametersImpl(escaped, escapeChar, extendedBooleanLiteral, null, separators, nullSequence, lastColumnTakesRest);
        return createLazyStructInspector(columnNames, typeInfos, lazyParams);
    }
    
    public static ObjectInspector createLazyStructInspector(final List<String> columnNames, final List<TypeInfo> typeInfos, final LazyObjectInspectorParameters lazyParams) throws SerDeException {
        final ArrayList<ObjectInspector> columnObjectInspectors = new ArrayList<ObjectInspector>(typeInfos.size());
        for (int i = 0; i < typeInfos.size(); ++i) {
            columnObjectInspectors.add(createLazyObjectInspector(typeInfos.get(i), 1, lazyParams, ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
        }
        return LazyObjectInspectorFactory.getLazySimpleStructObjectInspector(columnNames, columnObjectInspectors, null, lazyParams.getSeparators()[0], lazyParams, ObjectInspectorFactory.ObjectInspectorOptions.JAVA);
    }
    
    @Deprecated
    public static ObjectInspector createColumnarStructInspector(final List<String> columnNames, final List<TypeInfo> columnTypes, final byte[] separators, final Text nullSequence, final boolean escaped, final byte escapeChar) throws SerDeException {
        final LazyObjectInspectorParametersImpl lazyParams = new LazyObjectInspectorParametersImpl(escaped, escapeChar, false, null, separators, nullSequence);
        return createColumnarStructInspector(columnNames, columnTypes, lazyParams);
    }
    
    public static ObjectInspector createColumnarStructInspector(final List<String> columnNames, final List<TypeInfo> columnTypes, final LazyObjectInspectorParameters lazyParams) throws SerDeException {
        final ArrayList<ObjectInspector> columnObjectInspectors = new ArrayList<ObjectInspector>(columnTypes.size());
        for (int i = 0; i < columnTypes.size(); ++i) {
            columnObjectInspectors.add(createLazyObjectInspector(columnTypes.get(i), 1, lazyParams, ObjectInspectorFactory.ObjectInspectorOptions.JAVA));
        }
        return ObjectInspectorFactory.getColumnarStructObjectInspector(columnNames, columnObjectInspectors);
    }
    
    private LazyFactory() {
    }
}
