// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableHiveDecimalObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableBinaryObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableHiveIntervalDayTimeObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableHiveIntervalYearMonthObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableTimestampObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableDateObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableHiveVarcharObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableHiveCharObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.JavaStringObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableStringObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableDoubleObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableFloatObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableLongObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableIntObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableShortObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableByteObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableBooleanObjectInspector;
import org.apache.hadoop.util.StringUtils;
import java.lang.reflect.Type;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import org.apache.hadoop.hive.serde2.io.HiveDecimalWritable;
import org.apache.hadoop.hive.serde2.io.DateWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.hive.serde2.io.HiveVarcharWritable;
import org.apache.hadoop.hive.serde2.io.HiveCharWritable;
import org.apache.hadoop.io.BinaryComparable;
import org.apache.hadoop.hive.serde2.io.HiveIntervalDayTimeWritable;
import org.apache.hadoop.hive.serde2.io.HiveIntervalYearMonthWritable;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.HiveDecimalObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.HiveIntervalDayTimeObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.HiveIntervalYearMonthObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.TimestampObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.DateObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.BinaryObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.HiveVarcharObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.HiveCharObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.DoubleObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.FloatObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.LongObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.IntObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.ShortObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.ByteObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.BooleanObjectInspector;
import java.lang.reflect.Modifier;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.HashMap;
import org.apache.hadoop.hive.serde2.SerDeException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.AbstractPrimitiveWritableObjectInspector;
import org.apache.commons.logging.Log;

public final class ObjectInspectorUtils
{
    protected static final Log LOG;
    
    public static ObjectInspector getWritableObjectInspector(final ObjectInspector oi) {
        if (oi.getCategory() == ObjectInspector.Category.PRIMITIVE) {
            final PrimitiveObjectInspector poi = (PrimitiveObjectInspector)oi;
            if (!(poi instanceof AbstractPrimitiveWritableObjectInspector)) {
                return PrimitiveObjectInspectorFactory.getPrimitiveWritableObjectInspector(poi.getTypeInfo());
            }
        }
        return oi;
    }
    
    public static ObjectInspector getStandardObjectInspector(final ObjectInspector oi) {
        return getStandardObjectInspector(oi, ObjectInspectorCopyOption.DEFAULT);
    }
    
    public static ObjectInspector getStandardObjectInspector(final ObjectInspector oi, final ObjectInspectorCopyOption objectInspectorOption) {
        ObjectInspector result = null;
        switch (oi.getCategory()) {
            case PRIMITIVE: {
                final PrimitiveObjectInspector poi = (PrimitiveObjectInspector)oi;
                switch (objectInspectorOption) {
                    case DEFAULT: {
                        if (poi.preferWritable()) {
                            result = PrimitiveObjectInspectorFactory.getPrimitiveWritableObjectInspector(poi.getTypeInfo());
                            break;
                        }
                        result = PrimitiveObjectInspectorFactory.getPrimitiveJavaObjectInspector(poi.getTypeInfo());
                        break;
                    }
                    case JAVA: {
                        result = PrimitiveObjectInspectorFactory.getPrimitiveJavaObjectInspector(poi.getTypeInfo());
                        break;
                    }
                    case WRITABLE: {
                        result = PrimitiveObjectInspectorFactory.getPrimitiveWritableObjectInspector(poi.getTypeInfo());
                        break;
                    }
                }
                break;
            }
            case LIST: {
                final ListObjectInspector loi = (ListObjectInspector)oi;
                result = ObjectInspectorFactory.getStandardListObjectInspector(getStandardObjectInspector(loi.getListElementObjectInspector(), objectInspectorOption));
                break;
            }
            case MAP: {
                final MapObjectInspector moi = (MapObjectInspector)oi;
                result = ObjectInspectorFactory.getStandardMapObjectInspector(getStandardObjectInspector(moi.getMapKeyObjectInspector(), objectInspectorOption), getStandardObjectInspector(moi.getMapValueObjectInspector(), objectInspectorOption));
                break;
            }
            case STRUCT: {
                final StructObjectInspector soi = (StructObjectInspector)oi;
                final List<? extends StructField> fields = soi.getAllStructFieldRefs();
                final List<String> fieldNames = new ArrayList<String>(fields.size());
                final List<ObjectInspector> fieldObjectInspectors = new ArrayList<ObjectInspector>(fields.size());
                for (final StructField f : fields) {
                    fieldNames.add(f.getFieldName());
                    fieldObjectInspectors.add(getStandardObjectInspector(f.getFieldObjectInspector(), objectInspectorOption));
                }
                result = ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldObjectInspectors);
                break;
            }
            case UNION: {
                final UnionObjectInspector uoi = (UnionObjectInspector)oi;
                final List<ObjectInspector> ois = new ArrayList<ObjectInspector>();
                for (final ObjectInspector eoi : uoi.getObjectInspectors()) {
                    ois.add(getStandardObjectInspector(eoi, objectInspectorOption));
                }
                result = ObjectInspectorFactory.getStandardUnionObjectInspector(ois);
                break;
            }
            default: {
                throw new RuntimeException("Unknown ObjectInspector category!");
            }
        }
        return result;
    }
    
    public static void partialCopyToStandardObject(final List<Object> result, final Object row, final int startCol, final int numCols, final StructObjectInspector soi, final ObjectInspectorCopyOption objectInspectorOption) {
        final List<? extends StructField> fields = soi.getAllStructFieldRefs();
        int i = 0;
        int j = 0;
        for (final StructField f : fields) {
            if (i++ >= startCol) {
                result.add(copyToStandardObject(soi.getStructFieldData(row, f), f.getFieldObjectInspector(), objectInspectorOption));
                if (++j == numCols) {
                    break;
                }
                continue;
            }
        }
    }
    
    public static void copyToStandardObject(final List<Object> result, final Object row, final StructObjectInspector soi, final ObjectInspectorCopyOption objectInspectorOption) {
        final List<? extends StructField> fields = soi.getAllStructFieldRefs();
        for (final StructField f : fields) {
            result.add(copyToStandardObject(soi.getStructFieldData(row, f), f.getFieldObjectInspector(), objectInspectorOption));
        }
    }
    
    public static Object copyToStandardObject(final Object o, final ObjectInspector oi) {
        return copyToStandardObject(o, oi, ObjectInspectorCopyOption.DEFAULT);
    }
    
    public static Object copyToStandardJavaObject(final Object o, final ObjectInspector oi) {
        return copyToStandardObject(o, oi, ObjectInspectorCopyOption.JAVA);
    }
    
    public static int getStructSize(final ObjectInspector oi) throws SerDeException {
        if (oi.getCategory() != ObjectInspector.Category.STRUCT) {
            throw new SerDeException("Unexpected category " + oi.getCategory());
        }
        return ((StructObjectInspector)oi).getAllStructFieldRefs().size();
    }
    
    public static void copyStructToArray(final Object o, final ObjectInspector oi, final ObjectInspectorCopyOption objectInspectorOption, final Object[] dest, final int offset) throws SerDeException {
        if (o == null) {
            return;
        }
        if (oi.getCategory() != ObjectInspector.Category.STRUCT) {
            throw new SerDeException("Unexpected category " + oi.getCategory());
        }
        final StructObjectInspector soi = (StructObjectInspector)oi;
        final List<? extends StructField> fields = soi.getAllStructFieldRefs();
        for (int i = 0; i < fields.size(); ++i) {
            final StructField f = (StructField)fields.get(i);
            dest[offset + i] = copyToStandardObject(soi.getStructFieldData(o, f), f.getFieldObjectInspector(), objectInspectorOption);
        }
    }
    
    public static Object copyToStandardObject(final Object o, final ObjectInspector oi, ObjectInspectorCopyOption objectInspectorOption) {
        if (o == null) {
            return null;
        }
        Object result = null;
        switch (oi.getCategory()) {
            case PRIMITIVE: {
                final PrimitiveObjectInspector loi = (PrimitiveObjectInspector)oi;
                if (objectInspectorOption == ObjectInspectorCopyOption.DEFAULT) {
                    objectInspectorOption = (loi.preferWritable() ? ObjectInspectorCopyOption.WRITABLE : ObjectInspectorCopyOption.JAVA);
                }
                switch (objectInspectorOption) {
                    case JAVA: {
                        result = loi.getPrimitiveJavaObject(o);
                        if (loi.getPrimitiveCategory() == PrimitiveObjectInspector.PrimitiveCategory.TIMESTAMP) {
                            result = PrimitiveObjectInspectorFactory.javaTimestampObjectInspector.copyObject(result);
                            break;
                        }
                        break;
                    }
                    case WRITABLE: {
                        result = loi.getPrimitiveWritableObject(loi.copyObject(o));
                        break;
                    }
                }
                break;
            }
            case LIST: {
                final ListObjectInspector loi2 = (ListObjectInspector)oi;
                final int length = loi2.getListLength(o);
                final ArrayList<Object> list = new ArrayList<Object>(length);
                for (int i = 0; i < length; ++i) {
                    list.add(copyToStandardObject(loi2.getListElement(o, i), loi2.getListElementObjectInspector(), objectInspectorOption));
                }
                result = list;
                break;
            }
            case MAP: {
                final MapObjectInspector moi = (MapObjectInspector)oi;
                final HashMap<Object, Object> map = new HashMap<Object, Object>();
                final Map<?, ?> omap = moi.getMap(o);
                for (final Map.Entry<?, ?> entry : omap.entrySet()) {
                    map.put(copyToStandardObject(entry.getKey(), moi.getMapKeyObjectInspector(), objectInspectorOption), copyToStandardObject(entry.getValue(), moi.getMapValueObjectInspector(), objectInspectorOption));
                }
                result = map;
                break;
            }
            case STRUCT: {
                final StructObjectInspector soi = (StructObjectInspector)oi;
                final List<? extends StructField> fields = soi.getAllStructFieldRefs();
                final ArrayList<Object> struct = new ArrayList<Object>(fields.size());
                for (final StructField f : fields) {
                    struct.add(copyToStandardObject(soi.getStructFieldData(o, f), f.getFieldObjectInspector(), objectInspectorOption));
                }
                result = struct;
                break;
            }
            case UNION: {
                final UnionObjectInspector uoi = (UnionObjectInspector)oi;
                final List<ObjectInspector> objectInspectors = uoi.getObjectInspectors();
                final Object object = result = copyToStandardObject(uoi.getField(o), objectInspectors.get(uoi.getTag(o)), objectInspectorOption);
                break;
            }
            default: {
                throw new RuntimeException("Unknown ObjectInspector category!");
            }
        }
        return result;
    }
    
    public static String getStandardStructTypeName(final StructObjectInspector soi) {
        final StringBuilder sb = new StringBuilder();
        sb.append("struct<");
        final List<? extends StructField> fields = soi.getAllStructFieldRefs();
        for (int i = 0; i < fields.size(); ++i) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(((StructField)fields.get(i)).getFieldName());
            sb.append(":");
            sb.append(((StructField)fields.get(i)).getFieldObjectInspector().getTypeName());
        }
        sb.append(">");
        return sb.toString();
    }
    
    public static String getStandardUnionTypeName(final UnionObjectInspector uoi) {
        final StringBuilder sb = new StringBuilder();
        sb.append("uniontype<");
        final List<ObjectInspector> ois = uoi.getObjectInspectors();
        for (int i = 0; i < ois.size(); ++i) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(ois.get(i).getTypeName());
        }
        sb.append(">");
        return sb.toString();
    }
    
    public static StructField getStandardStructFieldRef(String fieldName, final List<? extends StructField> fields) {
        fieldName = fieldName.toLowerCase();
        for (int i = 0; i < fields.size(); ++i) {
            if (((StructField)fields.get(i)).getFieldName().equals(fieldName)) {
                return (StructField)fields.get(i);
            }
        }
        try {
            final int i = Integer.parseInt(fieldName);
            if (i >= 0 && i < fields.size()) {
                return (StructField)fields.get(i);
            }
        }
        catch (NumberFormatException ex) {}
        throw new RuntimeException("cannot find field " + fieldName + " from " + fields);
    }
    
    public static Field[] getDeclaredNonStaticFields(final Class<?> c) {
        final Field[] f = c.getDeclaredFields();
        final ArrayList<Field> af = new ArrayList<Field>();
        for (int i = 0; i < f.length; ++i) {
            if (!Modifier.isStatic(f[i].getModifiers())) {
                af.add(f[i]);
            }
        }
        final Field[] r = new Field[af.size()];
        for (int j = 0; j < af.size(); ++j) {
            r[j] = af.get(j);
        }
        return r;
    }
    
    public static String getObjectInspectorName(final ObjectInspector oi) {
        switch (oi.getCategory()) {
            case PRIMITIVE: {
                return oi.getClass().getSimpleName();
            }
            case LIST: {
                final ListObjectInspector loi = (ListObjectInspector)oi;
                return oi.getClass().getSimpleName() + "<" + getObjectInspectorName(loi.getListElementObjectInspector()) + ">";
            }
            case MAP: {
                final MapObjectInspector moi = (MapObjectInspector)oi;
                return oi.getClass().getSimpleName() + "<" + getObjectInspectorName(moi.getMapKeyObjectInspector()) + "," + getObjectInspectorName(moi.getMapValueObjectInspector()) + ">";
            }
            case STRUCT: {
                final StringBuilder result = new StringBuilder();
                result.append(oi.getClass().getSimpleName() + "<");
                final StructObjectInspector soi = (StructObjectInspector)oi;
                final List<? extends StructField> fields = soi.getAllStructFieldRefs();
                for (int i = 0; i < fields.size(); ++i) {
                    result.append(((StructField)fields.get(i)).getFieldName());
                    result.append(":");
                    result.append(getObjectInspectorName(((StructField)fields.get(i)).getFieldObjectInspector()));
                    if (i == fields.size() - 1) {
                        result.append(">");
                    }
                    else {
                        result.append(",");
                    }
                }
                return result.toString();
            }
            case UNION: {
                final StringBuffer result2 = new StringBuffer();
                result2.append(oi.getClass().getSimpleName() + "<");
                final UnionObjectInspector uoi = (UnionObjectInspector)oi;
                final List<ObjectInspector> ois = uoi.getObjectInspectors();
                for (int i = 0; i < ois.size(); ++i) {
                    if (i > 0) {
                        result2.append(",");
                    }
                    result2.append(getObjectInspectorName(ois.get(i)));
                }
                result2.append(">");
                return result2.toString();
            }
            default: {
                throw new RuntimeException("Unknown ObjectInspector category!");
            }
        }
    }
    
    public static int hashCode(final Object o, final ObjectInspector objIns) {
        if (o == null) {
            return 0;
        }
        switch (objIns.getCategory()) {
            case PRIMITIVE: {
                final PrimitiveObjectInspector poi = (PrimitiveObjectInspector)objIns;
                switch (poi.getPrimitiveCategory()) {
                    case VOID: {
                        return 0;
                    }
                    case BOOLEAN: {
                        return ((BooleanObjectInspector)poi).get(o) ? 1 : 0;
                    }
                    case BYTE: {
                        return ((ByteObjectInspector)poi).get(o);
                    }
                    case SHORT: {
                        return ((ShortObjectInspector)poi).get(o);
                    }
                    case INT: {
                        return ((IntObjectInspector)poi).get(o);
                    }
                    case LONG: {
                        final long a = ((LongObjectInspector)poi).get(o);
                        return (int)(a >>> 32 ^ a);
                    }
                    case FLOAT: {
                        return Float.floatToIntBits(((FloatObjectInspector)poi).get(o));
                    }
                    case DOUBLE: {
                        final long a = Double.doubleToLongBits(((DoubleObjectInspector)poi).get(o));
                        return (int)(a >>> 32 ^ a);
                    }
                    case STRING: {
                        final Text t = ((StringObjectInspector)poi).getPrimitiveWritableObject(o);
                        int r = 0;
                        for (int i = 0; i < t.getLength(); ++i) {
                            r = r * 31 + t.getBytes()[i];
                        }
                        return r;
                    }
                    case CHAR: {
                        return ((HiveCharObjectInspector)poi).getPrimitiveWritableObject(o).hashCode();
                    }
                    case VARCHAR: {
                        return ((HiveVarcharObjectInspector)poi).getPrimitiveWritableObject(o).hashCode();
                    }
                    case BINARY: {
                        return ((BinaryObjectInspector)poi).getPrimitiveWritableObject(o).hashCode();
                    }
                    case DATE: {
                        return ((DateObjectInspector)poi).getPrimitiveWritableObject(o).hashCode();
                    }
                    case TIMESTAMP: {
                        final TimestampWritable t2 = ((TimestampObjectInspector)poi).getPrimitiveWritableObject(o);
                        return t2.hashCode();
                    }
                    case INTERVAL_YEAR_MONTH: {
                        final HiveIntervalYearMonthWritable intervalYearMonth = ((HiveIntervalYearMonthObjectInspector)poi).getPrimitiveWritableObject(o);
                        return intervalYearMonth.hashCode();
                    }
                    case INTERVAL_DAY_TIME: {
                        final HiveIntervalDayTimeWritable intervalDayTime = ((HiveIntervalDayTimeObjectInspector)poi).getPrimitiveWritableObject(o);
                        return intervalDayTime.hashCode();
                    }
                    case DECIMAL: {
                        return ((HiveDecimalObjectInspector)poi).getPrimitiveWritableObject(o).hashCode();
                    }
                    default: {
                        throw new RuntimeException("Unknown type: " + poi.getPrimitiveCategory());
                    }
                }
                break;
            }
            case LIST: {
                int r2 = 0;
                final ListObjectInspector listOI = (ListObjectInspector)objIns;
                final ObjectInspector elemOI = listOI.getListElementObjectInspector();
                for (int ii = 0; ii < listOI.getListLength(o); ++ii) {
                    r2 = 31 * r2 + hashCode(listOI.getListElement(o, ii), elemOI);
                }
                return r2;
            }
            case MAP: {
                int r2 = 0;
                final MapObjectInspector mapOI = (MapObjectInspector)objIns;
                final ObjectInspector keyOI = mapOI.getMapKeyObjectInspector();
                final ObjectInspector valueOI = mapOI.getMapValueObjectInspector();
                final Map<?, ?> map = mapOI.getMap(o);
                for (final Map.Entry<?, ?> entry : map.entrySet()) {
                    r2 += (hashCode(entry.getKey(), keyOI) ^ hashCode(entry.getValue(), valueOI));
                }
                return r2;
            }
            case STRUCT: {
                int r2 = 0;
                final StructObjectInspector structOI = (StructObjectInspector)objIns;
                final List<? extends StructField> fields = structOI.getAllStructFieldRefs();
                for (final StructField field : fields) {
                    r2 = 31 * r2 + hashCode(structOI.getStructFieldData(o, field), field.getFieldObjectInspector());
                }
                return r2;
            }
            case UNION: {
                final UnionObjectInspector uOI = (UnionObjectInspector)objIns;
                final byte tag = uOI.getTag(o);
                return hashCode(uOI.getField(o), uOI.getObjectInspectors().get(tag));
            }
            default: {
                throw new RuntimeException("Unknown type: " + objIns.getTypeName());
            }
        }
    }
    
    public static int compare(final Object[] o1, final ObjectInspector[] oi1, final Object[] o2, final ObjectInspector[] oi2) {
        assert o1.length == oi1.length;
        assert o2.length == oi2.length;
        assert o1.length == o2.length;
        for (int i = 0; i < o1.length; ++i) {
            final int r = compare(o1[i], oi1[i], o2[i], oi2[i]);
            if (r != 0) {
                return r;
            }
        }
        return 0;
    }
    
    public static boolean compareSupported(final ObjectInspector oi) {
        switch (oi.getCategory()) {
            case PRIMITIVE: {
                return true;
            }
            case LIST: {
                final ListObjectInspector loi = (ListObjectInspector)oi;
                return compareSupported(loi.getListElementObjectInspector());
            }
            case STRUCT: {
                final StructObjectInspector soi = (StructObjectInspector)oi;
                final List<? extends StructField> fields = soi.getAllStructFieldRefs();
                for (int f = 0; f < fields.size(); ++f) {
                    if (!compareSupported(((StructField)fields.get(f)).getFieldObjectInspector())) {
                        return false;
                    }
                }
                return true;
            }
            case MAP: {
                return false;
            }
            case UNION: {
                final UnionObjectInspector uoi = (UnionObjectInspector)oi;
                for (final ObjectInspector eoi : uoi.getObjectInspectors()) {
                    if (!compareSupported(eoi)) {
                        return false;
                    }
                }
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static int compare(final Object o1, final ObjectInspector oi1, final Object o2, final ObjectInspector oi2) {
        return compare(o1, oi1, o2, oi2, new FullMapEqualComparer());
    }
    
    public static int compare(final Object o1, final ObjectInspector oi1, final Object o2, final ObjectInspector oi2, final MapEqualComparer mapEqualComparer) {
        if (oi1.getCategory() != oi2.getCategory()) {
            return oi1.getCategory().compareTo(oi2.getCategory());
        }
        if (o1 == null) {
            return (o2 == null) ? 0 : -1;
        }
        if (o2 == null) {
            return 1;
        }
        switch (oi1.getCategory()) {
            case PRIMITIVE: {
                final PrimitiveObjectInspector poi1 = (PrimitiveObjectInspector)oi1;
                final PrimitiveObjectInspector poi2 = (PrimitiveObjectInspector)oi2;
                if (poi1.getPrimitiveCategory() != poi2.getPrimitiveCategory()) {
                    return poi1.getPrimitiveCategory().compareTo(poi2.getPrimitiveCategory());
                }
                switch (poi1.getPrimitiveCategory()) {
                    case VOID: {
                        return 0;
                    }
                    case BOOLEAN: {
                        final int v1 = ((BooleanObjectInspector)poi1).get(o1) ? 1 : 0;
                        final int v2 = ((BooleanObjectInspector)poi2).get(o2) ? 1 : 0;
                        return v1 - v2;
                    }
                    case BYTE: {
                        final int v1 = ((ByteObjectInspector)poi1).get(o1);
                        final int v2 = ((ByteObjectInspector)poi2).get(o2);
                        return v1 - v2;
                    }
                    case SHORT: {
                        final int v1 = ((ShortObjectInspector)poi1).get(o1);
                        final int v2 = ((ShortObjectInspector)poi2).get(o2);
                        return v1 - v2;
                    }
                    case INT: {
                        final int v1 = ((IntObjectInspector)poi1).get(o1);
                        final int v2 = ((IntObjectInspector)poi2).get(o2);
                        return (v1 > v2) ? 1 : ((v1 < v2) ? -1 : 0);
                    }
                    case LONG: {
                        final long v3 = ((LongObjectInspector)poi1).get(o1);
                        final long v4 = ((LongObjectInspector)poi2).get(o2);
                        return (v3 > v4) ? 1 : ((v3 < v4) ? -1 : 0);
                    }
                    case FLOAT: {
                        final float v5 = ((FloatObjectInspector)poi1).get(o1);
                        final float v6 = ((FloatObjectInspector)poi2).get(o2);
                        return Float.compare(v5, v6);
                    }
                    case DOUBLE: {
                        final double v7 = ((DoubleObjectInspector)poi1).get(o1);
                        final double v8 = ((DoubleObjectInspector)poi2).get(o2);
                        return Double.compare(v7, v8);
                    }
                    case STRING: {
                        if (poi1.preferWritable() || poi2.preferWritable()) {
                            final Text t1 = (Text)poi1.getPrimitiveWritableObject(o1);
                            final Text t2 = (Text)poi2.getPrimitiveWritableObject(o2);
                            return (t1 == null) ? ((t2 == null) ? 0 : -1) : ((t2 == null) ? 1 : t1.compareTo((BinaryComparable)t2));
                        }
                        final String s1 = (String)poi1.getPrimitiveJavaObject(o1);
                        final String s2 = (String)poi2.getPrimitiveJavaObject(o2);
                        return (s1 == null) ? ((s2 == null) ? 0 : -1) : ((s2 == null) ? 1 : s1.compareTo(s2));
                    }
                    case CHAR: {
                        final HiveCharWritable t3 = ((HiveCharObjectInspector)poi1).getPrimitiveWritableObject(o1);
                        final HiveCharWritable t4 = ((HiveCharObjectInspector)poi2).getPrimitiveWritableObject(o2);
                        return t3.compareTo(t4);
                    }
                    case VARCHAR: {
                        final HiveVarcharWritable t5 = ((HiveVarcharObjectInspector)poi1).getPrimitiveWritableObject(o1);
                        final HiveVarcharWritable t6 = ((HiveVarcharObjectInspector)poi2).getPrimitiveWritableObject(o2);
                        return t5.compareTo(t6);
                    }
                    case BINARY: {
                        final BytesWritable bw1 = ((BinaryObjectInspector)poi1).getPrimitiveWritableObject(o1);
                        final BytesWritable bw2 = ((BinaryObjectInspector)poi2).getPrimitiveWritableObject(o2);
                        return bw1.compareTo((BinaryComparable)bw2);
                    }
                    case DATE: {
                        final DateWritable d1 = ((DateObjectInspector)poi1).getPrimitiveWritableObject(o1);
                        final DateWritable d2 = ((DateObjectInspector)poi2).getPrimitiveWritableObject(o2);
                        return d1.compareTo(d2);
                    }
                    case TIMESTAMP: {
                        final TimestampWritable t7 = ((TimestampObjectInspector)poi1).getPrimitiveWritableObject(o1);
                        final TimestampWritable t8 = ((TimestampObjectInspector)poi2).getPrimitiveWritableObject(o2);
                        return t7.compareTo(t8);
                    }
                    case INTERVAL_YEAR_MONTH: {
                        final HiveIntervalYearMonthWritable i1 = ((HiveIntervalYearMonthObjectInspector)poi1).getPrimitiveWritableObject(o1);
                        final HiveIntervalYearMonthWritable i2 = ((HiveIntervalYearMonthObjectInspector)poi2).getPrimitiveWritableObject(o2);
                        return i1.compareTo(i2);
                    }
                    case INTERVAL_DAY_TIME: {
                        final HiveIntervalDayTimeWritable i3 = ((HiveIntervalDayTimeObjectInspector)poi1).getPrimitiveWritableObject(o1);
                        final HiveIntervalDayTimeWritable i4 = ((HiveIntervalDayTimeObjectInspector)poi2).getPrimitiveWritableObject(o2);
                        return i3.compareTo(i4);
                    }
                    case DECIMAL: {
                        final HiveDecimalWritable t9 = ((HiveDecimalObjectInspector)poi1).getPrimitiveWritableObject(o1);
                        final HiveDecimalWritable t10 = ((HiveDecimalObjectInspector)poi2).getPrimitiveWritableObject(o2);
                        return t9.compareTo(t10);
                    }
                    default: {
                        throw new RuntimeException("Unknown type: " + poi1.getPrimitiveCategory());
                    }
                }
                break;
            }
            case STRUCT: {
                final StructObjectInspector soi1 = (StructObjectInspector)oi1;
                final StructObjectInspector soi2 = (StructObjectInspector)oi2;
                final List<? extends StructField> fields1 = soi1.getAllStructFieldRefs();
                final List<? extends StructField> fields2 = soi2.getAllStructFieldRefs();
                for (int minimum = Math.min(fields1.size(), fields2.size()), j = 0; j < minimum; ++j) {
                    final int r = compare(soi1.getStructFieldData(o1, (StructField)fields1.get(j)), ((StructField)fields1.get(j)).getFieldObjectInspector(), soi2.getStructFieldData(o2, (StructField)fields2.get(j)), ((StructField)fields2.get(j)).getFieldObjectInspector(), mapEqualComparer);
                    if (r != 0) {
                        return r;
                    }
                }
                return fields1.size() - fields2.size();
            }
            case LIST: {
                final ListObjectInspector loi1 = (ListObjectInspector)oi1;
                final ListObjectInspector loi2 = (ListObjectInspector)oi2;
                for (int minimum2 = Math.min(loi1.getListLength(o1), loi2.getListLength(o2)), k = 0; k < minimum2; ++k) {
                    final int r2 = compare(loi1.getListElement(o1, k), loi1.getListElementObjectInspector(), loi2.getListElement(o2, k), loi2.getListElementObjectInspector(), mapEqualComparer);
                    if (r2 != 0) {
                        return r2;
                    }
                }
                return loi1.getListLength(o1) - loi2.getListLength(o2);
            }
            case MAP: {
                if (mapEqualComparer == null) {
                    throw new RuntimeException("Compare on map type not supported!");
                }
                return mapEqualComparer.compare(o1, (MapObjectInspector)oi1, o2, (MapObjectInspector)oi2);
            }
            case UNION: {
                final UnionObjectInspector uoi1 = (UnionObjectInspector)oi1;
                final UnionObjectInspector uoi2 = (UnionObjectInspector)oi2;
                final byte tag1 = uoi1.getTag(o1);
                final byte tag2 = uoi2.getTag(o2);
                if (tag1 != tag2) {
                    return tag1 - tag2;
                }
                return compare(uoi1.getField(o1), uoi1.getObjectInspectors().get(tag1), uoi2.getField(o2), uoi2.getObjectInspectors().get(tag2), mapEqualComparer);
            }
            default: {
                throw new RuntimeException("Compare on unknown type: " + oi1.getCategory());
            }
        }
    }
    
    public static String getFieldNames(final StructObjectInspector soi) {
        final List<? extends StructField> fields = soi.getAllStructFieldRefs();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.size(); ++i) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(((StructField)fields.get(i)).getFieldName());
        }
        return sb.toString();
    }
    
    public static String getFieldTypes(final StructObjectInspector soi) {
        final List<? extends StructField> fields = soi.getAllStructFieldRefs();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.size(); ++i) {
            if (i > 0) {
                sb.append(":");
            }
            sb.append(TypeInfoUtils.getTypeInfoFromObjectInspector(((StructField)fields.get(i)).getFieldObjectInspector()).getTypeName());
        }
        return sb.toString();
    }
    
    public static String getTypeNameFromJavaClass(final Type t) {
        try {
            final ObjectInspector oi = ObjectInspectorFactory.getReflectionObjectInspector(t, ObjectInspectorFactory.ObjectInspectorOptions.JAVA);
            return oi.getTypeName();
        }
        catch (Throwable e) {
            ObjectInspectorUtils.LOG.info(StringUtils.stringifyException(e));
            return "unknown";
        }
    }
    
    public static boolean compareTypes(final ObjectInspector o1, final ObjectInspector o2) {
        final ObjectInspector.Category c1 = o1.getCategory();
        final ObjectInspector.Category c2 = o2.getCategory();
        if (!c1.equals(c2)) {
            return false;
        }
        if (c1.equals(ObjectInspector.Category.PRIMITIVE)) {
            return o1.getTypeName().equals(o2.getTypeName());
        }
        if (c1.equals(ObjectInspector.Category.LIST)) {
            final ObjectInspector child1 = ((ListObjectInspector)o1).getListElementObjectInspector();
            final ObjectInspector child2 = ((ListObjectInspector)o2).getListElementObjectInspector();
            return compareTypes(child1, child2);
        }
        if (c1.equals(ObjectInspector.Category.MAP)) {
            final MapObjectInspector mapOI1 = (MapObjectInspector)o1;
            final MapObjectInspector mapOI2 = (MapObjectInspector)o2;
            final ObjectInspector childKey1 = mapOI1.getMapKeyObjectInspector();
            final ObjectInspector childKey2 = mapOI2.getMapKeyObjectInspector();
            if (compareTypes(childKey1, childKey2)) {
                final ObjectInspector childVal1 = mapOI1.getMapValueObjectInspector();
                final ObjectInspector childVal2 = mapOI2.getMapValueObjectInspector();
                if (compareTypes(childVal1, childVal2)) {
                    return true;
                }
            }
            return false;
        }
        if (c1.equals(ObjectInspector.Category.STRUCT)) {
            final StructObjectInspector structOI1 = (StructObjectInspector)o1;
            final StructObjectInspector structOI2 = (StructObjectInspector)o2;
            final List<? extends StructField> childFieldsList1 = structOI1.getAllStructFieldRefs();
            final List<? extends StructField> childFieldsList2 = structOI2.getAllStructFieldRefs();
            if (childFieldsList1 == null && childFieldsList2 == null) {
                return true;
            }
            if (childFieldsList1 == null || childFieldsList2 == null) {
                return false;
            }
            if (childFieldsList1.size() != childFieldsList2.size()) {
                return false;
            }
            final Iterator<? extends StructField> it1 = childFieldsList1.iterator();
            final Iterator<? extends StructField> it2 = childFieldsList2.iterator();
            while (it1.hasNext()) {
                final StructField field1 = (StructField)it1.next();
                final StructField field2 = (StructField)it2.next();
                if (!compareTypes(field1.getFieldObjectInspector(), field2.getFieldObjectInspector())) {
                    return false;
                }
            }
            return true;
        }
        else {
            if (!c1.equals(ObjectInspector.Category.UNION)) {
                throw new RuntimeException("Unknown category encountered: " + c1);
            }
            final UnionObjectInspector uoi1 = (UnionObjectInspector)o1;
            final UnionObjectInspector uoi2 = (UnionObjectInspector)o2;
            final List<ObjectInspector> ois1 = uoi1.getObjectInspectors();
            final List<ObjectInspector> ois2 = uoi2.getObjectInspectors();
            if (ois1 == null && ois2 == null) {
                return true;
            }
            if (ois1 == null || ois2 == null) {
                return false;
            }
            if (ois1.size() != ois2.size()) {
                return false;
            }
            final Iterator<? extends ObjectInspector> it3 = ois1.iterator();
            final Iterator<? extends ObjectInspector> it4 = ois2.iterator();
            while (it3.hasNext()) {
                if (!compareTypes((ObjectInspector)it3.next(), (ObjectInspector)it4.next())) {
                    return false;
                }
            }
            return true;
        }
    }
    
    public static ConstantObjectInspector getConstantObjectInspector(final ObjectInspector oi, final Object value) {
        if (oi instanceof ConstantObjectInspector) {
            return (ConstantObjectInspector)oi;
        }
        final ObjectInspector writableOI = getStandardObjectInspector(oi, ObjectInspectorCopyOption.WRITABLE);
        final Object writableValue = (value == null) ? value : ObjectInspectorConverters.getConverter(oi, writableOI).convert(value);
        switch (writableOI.getCategory()) {
            case PRIMITIVE: {
                final PrimitiveObjectInspector poi = (PrimitiveObjectInspector)oi;
                return PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(poi.getTypeInfo(), writableValue);
            }
            case LIST: {
                final ListObjectInspector loi = (ListObjectInspector)oi;
                return ObjectInspectorFactory.getStandardConstantListObjectInspector(getStandardObjectInspector(loi.getListElementObjectInspector(), ObjectInspectorCopyOption.WRITABLE), (List<?>)writableValue);
            }
            case MAP: {
                final MapObjectInspector moi = (MapObjectInspector)oi;
                return ObjectInspectorFactory.getStandardConstantMapObjectInspector(getStandardObjectInspector(moi.getMapKeyObjectInspector(), ObjectInspectorCopyOption.WRITABLE), getStandardObjectInspector(moi.getMapValueObjectInspector(), ObjectInspectorCopyOption.WRITABLE), (Map<?, ?>)writableValue);
            }
            default: {
                throw new IllegalArgumentException(writableOI.getCategory() + " not yet supported for constant OI");
            }
        }
    }
    
    public static Object getWritableConstantValue(final ObjectInspector oi) {
        return ((ConstantObjectInspector)oi).getWritableConstantValue();
    }
    
    public static boolean supportsConstantObjectInspector(final ObjectInspector oi) {
        switch (oi.getCategory()) {
            case PRIMITIVE:
            case LIST:
            case MAP: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isConstantObjectInspector(final ObjectInspector oi) {
        return oi instanceof ConstantObjectInspector;
    }
    
    private static boolean setOISettablePropertiesMap(final ObjectInspector oi, final Map<ObjectInspector, Boolean> oiSettableProperties, final boolean value) {
        if (oiSettableProperties != null) {
            oiSettableProperties.put(oi, value);
        }
        return value;
    }
    
    private static boolean isInstanceOfSettablePrimitiveOI(final PrimitiveObjectInspector oi) {
        switch (oi.getPrimitiveCategory()) {
            case BOOLEAN: {
                return oi instanceof SettableBooleanObjectInspector;
            }
            case BYTE: {
                return oi instanceof SettableByteObjectInspector;
            }
            case SHORT: {
                return oi instanceof SettableShortObjectInspector;
            }
            case INT: {
                return oi instanceof SettableIntObjectInspector;
            }
            case LONG: {
                return oi instanceof SettableLongObjectInspector;
            }
            case FLOAT: {
                return oi instanceof SettableFloatObjectInspector;
            }
            case DOUBLE: {
                return oi instanceof SettableDoubleObjectInspector;
            }
            case STRING: {
                return oi instanceof WritableStringObjectInspector || oi instanceof JavaStringObjectInspector;
            }
            case CHAR: {
                return oi instanceof SettableHiveCharObjectInspector;
            }
            case VARCHAR: {
                return oi instanceof SettableHiveVarcharObjectInspector;
            }
            case DATE: {
                return oi instanceof SettableDateObjectInspector;
            }
            case TIMESTAMP: {
                return oi instanceof SettableTimestampObjectInspector;
            }
            case INTERVAL_YEAR_MONTH: {
                return oi instanceof SettableHiveIntervalYearMonthObjectInspector;
            }
            case INTERVAL_DAY_TIME: {
                return oi instanceof SettableHiveIntervalDayTimeObjectInspector;
            }
            case BINARY: {
                return oi instanceof SettableBinaryObjectInspector;
            }
            case DECIMAL: {
                return oi instanceof SettableHiveDecimalObjectInspector;
            }
            default: {
                throw new RuntimeException("Hive internal error inside isAssignableFromSettablePrimitiveOI " + oi.getTypeName() + " not supported yet.");
            }
        }
    }
    
    private static boolean isInstanceOfSettableOI(final ObjectInspector oi) {
        switch (oi.getCategory()) {
            case PRIMITIVE: {
                return isInstanceOfSettablePrimitiveOI((PrimitiveObjectInspector)oi);
            }
            case STRUCT: {
                return oi instanceof SettableStructObjectInspector;
            }
            case LIST: {
                return oi instanceof SettableListObjectInspector;
            }
            case MAP: {
                return oi instanceof SettableMapObjectInspector;
            }
            case UNION: {
                return oi instanceof SettableUnionObjectInspector;
            }
            default: {
                throw new RuntimeException("Hive internal error inside isAssignableFromSettableOI : " + oi.getTypeName() + " not supported yet.");
            }
        }
    }
    
    public static Boolean hasAllFieldsSettable(final ObjectInspector oi) {
        return hasAllFieldsSettable(oi, null);
    }
    
    public static boolean hasAllFieldsSettable(final ObjectInspector oi, final Map<ObjectInspector, Boolean> oiSettableProperties) {
        if (oiSettableProperties != null && oiSettableProperties.containsKey(oi)) {
            return oiSettableProperties.get(oi);
        }
        if (!isInstanceOfSettableOI(oi)) {
            return setOISettablePropertiesMap(oi, oiSettableProperties, false);
        }
        Boolean returnValue = true;
        switch (oi.getCategory()) {
            case PRIMITIVE: {
                break;
            }
            case STRUCT: {
                final StructObjectInspector structOutputOI = (StructObjectInspector)oi;
                final List<? extends StructField> listFields = structOutputOI.getAllStructFieldRefs();
                for (final StructField listField : listFields) {
                    if (!hasAllFieldsSettable(listField.getFieldObjectInspector(), oiSettableProperties)) {
                        returnValue = false;
                        break;
                    }
                }
                break;
            }
            case LIST: {
                final ListObjectInspector listOutputOI = (ListObjectInspector)oi;
                returnValue = hasAllFieldsSettable(listOutputOI.getListElementObjectInspector(), oiSettableProperties);
                break;
            }
            case MAP: {
                final MapObjectInspector mapOutputOI = (MapObjectInspector)oi;
                returnValue = (hasAllFieldsSettable(mapOutputOI.getMapKeyObjectInspector(), oiSettableProperties) && hasAllFieldsSettable(mapOutputOI.getMapValueObjectInspector(), oiSettableProperties));
                break;
            }
            case UNION: {
                final UnionObjectInspector unionOutputOI = (UnionObjectInspector)oi;
                final List<ObjectInspector> unionListFields = unionOutputOI.getObjectInspectors();
                for (final ObjectInspector listField2 : unionListFields) {
                    if (!hasAllFieldsSettable(listField2, oiSettableProperties)) {
                        returnValue = false;
                        break;
                    }
                }
                break;
            }
            default: {
                throw new RuntimeException("Hive internal error inside hasAllFieldsSettable : " + oi.getTypeName() + " not supported yet.");
            }
        }
        return setOISettablePropertiesMap(oi, oiSettableProperties, returnValue);
    }
    
    private ObjectInspectorUtils() {
    }
    
    static {
        LOG = LogFactory.getLog(ObjectInspectorUtils.class.getName());
    }
    
    public enum ObjectInspectorCopyOption
    {
        DEFAULT, 
        JAVA, 
        WRITABLE;
    }
}
