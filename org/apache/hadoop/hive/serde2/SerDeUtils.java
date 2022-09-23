// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2;

import org.apache.commons.logging.LogFactory;
import java.nio.charset.Charset;
import org.apache.hadoop.conf.Configuration;
import java.util.Properties;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.hive.serde2.objectinspector.UnionObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import java.util.Map;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.HiveDecimalObjectInspector;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.BinaryObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.TimestampObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.DateObjectInspector;
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
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.commons.logging.Log;

public final class SerDeUtils
{
    public static final char QUOTE = '\"';
    public static final char COLON = ':';
    public static final char COMMA = ',';
    public static final char COLUMN_COMMENTS_DELIMITER = '\0';
    public static final String LBRACKET = "[";
    public static final String RBRACKET = "]";
    public static final String LBRACE = "{";
    public static final String RBRACE = "}";
    private static final String JSON_NULL = "null";
    public static final Log LOG;
    
    public static String escapeString(final String str) {
        final int length = str.length();
        final StringBuilder escape = new StringBuilder(length + 16);
        for (int i = 0; i < length; ++i) {
            final char c = str.charAt(i);
            switch (c) {
                case '\"':
                case '\\': {
                    escape.append('\\');
                    escape.append(c);
                    break;
                }
                case '\b': {
                    escape.append('\\');
                    escape.append('b');
                    break;
                }
                case '\f': {
                    escape.append('\\');
                    escape.append('f');
                    break;
                }
                case '\n': {
                    escape.append('\\');
                    escape.append('n');
                    break;
                }
                case '\r': {
                    escape.append('\\');
                    escape.append('r');
                    break;
                }
                case '\t': {
                    escape.append('\\');
                    escape.append('t');
                    break;
                }
                default: {
                    if (c < ' ') {
                        final String hex = Integer.toHexString(c);
                        escape.append('\\');
                        escape.append('u');
                        for (int j = 4; j > hex.length(); --j) {
                            escape.append('0');
                        }
                        escape.append(hex);
                        break;
                    }
                    escape.append(c);
                    break;
                }
            }
        }
        return escape.toString();
    }
    
    public static String lightEscapeString(final String str) {
        final int length = str.length();
        final StringBuilder escape = new StringBuilder(length + 16);
        for (int i = 0; i < length; ++i) {
            final char c = str.charAt(i);
            switch (c) {
                case '\n': {
                    escape.append('\\');
                    escape.append('n');
                    break;
                }
                case '\r': {
                    escape.append('\\');
                    escape.append('r');
                    break;
                }
                case '\t': {
                    escape.append('\\');
                    escape.append('t');
                    break;
                }
                default: {
                    escape.append(c);
                    break;
                }
            }
        }
        return escape.toString();
    }
    
    public static Object toThriftPayload(final Object val, final ObjectInspector valOI, final int version) {
        if (valOI.getCategory() != ObjectInspector.Category.PRIMITIVE) {
            return getJSONString(val, valOI);
        }
        if (val == null) {
            return null;
        }
        final Object obj = ObjectInspectorUtils.copyToStandardObject(val, valOI, ObjectInspectorUtils.ObjectInspectorCopyOption.JAVA);
        if (version < 5 && ((PrimitiveObjectInspector)valOI).getPrimitiveCategory() == PrimitiveObjectInspector.PrimitiveCategory.BINARY) {
            return new String((byte[])obj);
        }
        return obj;
    }
    
    public static String getJSONString(final Object o, final ObjectInspector oi) {
        return getJSONString(o, oi, "null");
    }
    
    public static String getJSONString(final Object o, final ObjectInspector oi, final String nullStr) {
        final StringBuilder sb = new StringBuilder();
        buildJSONString(sb, o, oi, nullStr);
        return sb.toString();
    }
    
    static void buildJSONString(final StringBuilder sb, final Object o, final ObjectInspector oi, final String nullStr) {
        Label_1115: {
            switch (oi.getCategory()) {
                case PRIMITIVE: {
                    final PrimitiveObjectInspector poi = (PrimitiveObjectInspector)oi;
                    if (o == null) {
                        sb.append(nullStr);
                        break;
                    }
                    switch (poi.getPrimitiveCategory()) {
                        case BOOLEAN: {
                            final boolean b = ((BooleanObjectInspector)poi).get(o);
                            sb.append(b ? "true" : "false");
                            break Label_1115;
                        }
                        case BYTE: {
                            sb.append(((ByteObjectInspector)poi).get(o));
                            break Label_1115;
                        }
                        case SHORT: {
                            sb.append(((ShortObjectInspector)poi).get(o));
                            break Label_1115;
                        }
                        case INT: {
                            sb.append(((IntObjectInspector)poi).get(o));
                            break Label_1115;
                        }
                        case LONG: {
                            sb.append(((LongObjectInspector)poi).get(o));
                            break Label_1115;
                        }
                        case FLOAT: {
                            sb.append(((FloatObjectInspector)poi).get(o));
                            break Label_1115;
                        }
                        case DOUBLE: {
                            sb.append(((DoubleObjectInspector)poi).get(o));
                            break Label_1115;
                        }
                        case STRING: {
                            sb.append('\"');
                            sb.append(escapeString(((StringObjectInspector)poi).getPrimitiveJavaObject(o)));
                            sb.append('\"');
                            break Label_1115;
                        }
                        case CHAR: {
                            sb.append('\"');
                            sb.append(escapeString(((HiveCharObjectInspector)poi).getPrimitiveJavaObject(o).toString()));
                            sb.append('\"');
                            break Label_1115;
                        }
                        case VARCHAR: {
                            sb.append('\"');
                            sb.append(escapeString(((HiveVarcharObjectInspector)poi).getPrimitiveJavaObject(o).toString()));
                            sb.append('\"');
                            break Label_1115;
                        }
                        case DATE: {
                            sb.append('\"');
                            sb.append(((DateObjectInspector)poi).getPrimitiveWritableObject(o));
                            sb.append('\"');
                            break Label_1115;
                        }
                        case TIMESTAMP: {
                            sb.append('\"');
                            sb.append(((TimestampObjectInspector)poi).getPrimitiveWritableObject(o));
                            sb.append('\"');
                            break Label_1115;
                        }
                        case BINARY: {
                            final BytesWritable bw = ((BinaryObjectInspector)oi).getPrimitiveWritableObject(o);
                            final Text txt = new Text();
                            txt.set(bw.getBytes(), 0, bw.getLength());
                            sb.append(txt.toString());
                            break Label_1115;
                        }
                        case DECIMAL: {
                            sb.append(((HiveDecimalObjectInspector)oi).getPrimitiveJavaObject(o));
                            break Label_1115;
                        }
                        default: {
                            throw new RuntimeException("Unknown primitive type: " + poi.getPrimitiveCategory());
                        }
                    }
                    break;
                }
                case LIST: {
                    final ListObjectInspector loi = (ListObjectInspector)oi;
                    final ObjectInspector listElementObjectInspector = loi.getListElementObjectInspector();
                    final List<?> olist = loi.getList(o);
                    if (olist == null) {
                        sb.append(nullStr);
                        break;
                    }
                    sb.append("[");
                    for (int i = 0; i < olist.size(); ++i) {
                        if (i > 0) {
                            sb.append(',');
                        }
                        buildJSONString(sb, olist.get(i), listElementObjectInspector, "null");
                    }
                    sb.append("]");
                    break;
                }
                case MAP: {
                    final MapObjectInspector moi = (MapObjectInspector)oi;
                    final ObjectInspector mapKeyObjectInspector = moi.getMapKeyObjectInspector();
                    final ObjectInspector mapValueObjectInspector = moi.getMapValueObjectInspector();
                    final Map<?, ?> omap = moi.getMap(o);
                    if (omap == null) {
                        sb.append(nullStr);
                        break;
                    }
                    sb.append("{");
                    boolean first = true;
                    for (final Object entry : omap.entrySet()) {
                        if (first) {
                            first = false;
                        }
                        else {
                            sb.append(',');
                        }
                        final Map.Entry<?, ?> e = (Map.Entry<?, ?>)entry;
                        buildJSONString(sb, e.getKey(), mapKeyObjectInspector, "null");
                        sb.append(':');
                        buildJSONString(sb, e.getValue(), mapValueObjectInspector, "null");
                    }
                    sb.append("}");
                    break;
                }
                case STRUCT: {
                    final StructObjectInspector soi = (StructObjectInspector)oi;
                    final List<? extends StructField> structFields = soi.getAllStructFieldRefs();
                    if (o == null) {
                        sb.append(nullStr);
                        break;
                    }
                    sb.append("{");
                    for (int j = 0; j < structFields.size(); ++j) {
                        if (j > 0) {
                            sb.append(',');
                        }
                        sb.append('\"');
                        sb.append(((StructField)structFields.get(j)).getFieldName());
                        sb.append('\"');
                        sb.append(':');
                        buildJSONString(sb, soi.getStructFieldData(o, (StructField)structFields.get(j)), ((StructField)structFields.get(j)).getFieldObjectInspector(), "null");
                    }
                    sb.append("}");
                    break;
                }
                case UNION: {
                    final UnionObjectInspector uoi = (UnionObjectInspector)oi;
                    if (o == null) {
                        sb.append(nullStr);
                        break;
                    }
                    sb.append("{");
                    sb.append(uoi.getTag(o));
                    sb.append(':');
                    buildJSONString(sb, uoi.getField(o), uoi.getObjectInspectors().get(uoi.getTag(o)), "null");
                    sb.append("}");
                    break;
                }
                default: {
                    throw new RuntimeException("Unknown type in ObjectInspector!");
                }
            }
        }
    }
    
    public static boolean hasAnyNullObject(final List o, final StructObjectInspector loi, final boolean[] nullSafes) {
        final List<? extends StructField> fields = loi.getAllStructFieldRefs();
        for (int i = 0; i < o.size(); ++i) {
            if ((nullSafes == null || !nullSafes[i]) && hasAnyNullObject(o.get(i), ((StructField)fields.get(i)).getFieldObjectInspector())) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean hasAnyNullObject(final Object o, final ObjectInspector oi) {
        switch (oi.getCategory()) {
            case PRIMITIVE: {
                return o == null;
            }
            case LIST: {
                final ListObjectInspector loi = (ListObjectInspector)oi;
                final ObjectInspector listElementObjectInspector = loi.getListElementObjectInspector();
                final List<?> olist = loi.getList(o);
                if (olist == null) {
                    return true;
                }
                if (olist.size() == 0) {
                    return false;
                }
                for (int i = 0; i < olist.size(); ++i) {
                    if (hasAnyNullObject(olist.get(i), listElementObjectInspector)) {
                        return true;
                    }
                }
                return false;
            }
            case MAP: {
                final MapObjectInspector moi = (MapObjectInspector)oi;
                final ObjectInspector mapKeyObjectInspector = moi.getMapKeyObjectInspector();
                final ObjectInspector mapValueObjectInspector = moi.getMapValueObjectInspector();
                final Map<?, ?> omap = moi.getMap(o);
                if (omap == null) {
                    return true;
                }
                if (omap.entrySet().size() == 0) {
                    return false;
                }
                for (final Map.Entry<?, ?> entry : omap.entrySet()) {
                    if (hasAnyNullObject(entry.getKey(), mapKeyObjectInspector) || hasAnyNullObject(entry.getValue(), mapValueObjectInspector)) {
                        return true;
                    }
                }
                return false;
            }
            case STRUCT: {
                final StructObjectInspector soi = (StructObjectInspector)oi;
                final List<? extends StructField> structFields = soi.getAllStructFieldRefs();
                if (o == null) {
                    return true;
                }
                if (structFields.size() == 0) {
                    return false;
                }
                for (int j = 0; j < structFields.size(); ++j) {
                    if (hasAnyNullObject(soi.getStructFieldData(o, (StructField)structFields.get(j)), ((StructField)structFields.get(j)).getFieldObjectInspector())) {
                        return true;
                    }
                }
                return false;
            }
            case UNION: {
                final UnionObjectInspector uoi = (UnionObjectInspector)oi;
                return o == null || (uoi.getObjectInspectors().size() != 0 && hasAnyNullObject(uoi.getField(o), uoi.getObjectInspectors().get(uoi.getTag(o))));
            }
            default: {
                throw new RuntimeException("Unknown type in ObjectInspector!");
            }
        }
    }
    
    public static Properties createOverlayedProperties(final Properties tblProps, final Properties partProps) {
        final Properties props = new Properties();
        props.putAll(tblProps);
        if (partProps != null) {
            props.putAll(partProps);
        }
        return props;
    }
    
    public static void initializeSerDe(final Deserializer deserializer, final Configuration conf, final Properties tblProps, final Properties partProps) throws SerDeException {
        if (deserializer instanceof AbstractSerDe) {
            ((AbstractSerDe)deserializer).initialize(conf, tblProps, partProps);
            final String msg = ((AbstractSerDe)deserializer).getConfigurationErrors();
            if (msg != null && !msg.isEmpty()) {
                throw new SerDeException(msg);
            }
        }
        else {
            deserializer.initialize(conf, createOverlayedProperties(tblProps, partProps));
        }
    }
    
    public static void initializeSerDeWithoutErrorCheck(final Deserializer deserializer, final Configuration conf, final Properties tblProps, final Properties partProps) throws SerDeException {
        if (deserializer instanceof AbstractSerDe) {
            ((AbstractSerDe)deserializer).initialize(conf, tblProps, partProps);
        }
        else {
            deserializer.initialize(conf, createOverlayedProperties(tblProps, partProps));
        }
    }
    
    private SerDeUtils() {
    }
    
    public static Text transformTextToUTF8(final Text text, final Charset previousCharset) {
        return new Text(new String(text.getBytes(), previousCharset));
    }
    
    public static Text transformTextFromUTF8(final Text text, final Charset targetCharset) {
        return new Text(new String(text.getBytes()).getBytes(targetCharset));
    }
    
    static {
        LOG = LogFactory.getLog(SerDeUtils.class.getName());
    }
}
