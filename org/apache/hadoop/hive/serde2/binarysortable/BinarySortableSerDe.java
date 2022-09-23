// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.binarysortable;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.serde2.SerDeStats;
import java.util.Iterator;
import org.apache.hadoop.hive.common.type.HiveIntervalDayTime;
import org.apache.hadoop.hive.common.type.HiveIntervalYearMonth;
import org.apache.hadoop.hive.serde2.objectinspector.UnionObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
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
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.typeinfo.BaseCharTypeInfo;
import java.util.Map;
import org.apache.hadoop.hive.serde2.objectinspector.StandardUnionObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.UnionTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.StructTypeInfo;
import java.util.HashMap;
import org.apache.hadoop.hive.serde2.typeinfo.MapTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.ListTypeInfo;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import java.math.BigInteger;
import org.apache.hadoop.hive.serde2.io.HiveDecimalWritable;
import org.apache.hadoop.hive.serde2.io.HiveIntervalDayTimeWritable;
import org.apache.hadoop.hive.serde2.io.HiveIntervalYearMonthWritable;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import org.apache.hadoop.hive.serde2.io.DateWritable;
import org.apache.hadoop.hive.serde2.io.HiveVarcharWritable;
import org.apache.hadoop.hive.serde2.io.HiveCharWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.hive.serde2.io.ShortWritable;
import org.apache.hadoop.hive.serde2.io.ByteWritable;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import java.io.IOException;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import java.util.Arrays;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.ByteStream;
import org.apache.hadoop.io.BytesWritable;
import java.util.ArrayList;
import java.nio.charset.Charset;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.SerDeSpec;
import org.apache.hadoop.hive.serde2.AbstractSerDe;

@SerDeSpec(schemaProps = { "columns", "columns.types", "serialization.sort.order" })
public class BinarySortableSerDe extends AbstractSerDe
{
    public static final Log LOG;
    List<String> columnNames;
    List<TypeInfo> columnTypes;
    TypeInfo rowTypeInfo;
    StructObjectInspector rowObjectInspector;
    boolean[] columnSortOrderIsDesc;
    private static byte[] decimalBuffer;
    public static Charset decimalCharSet;
    ArrayList<Object> row;
    InputByteBuffer inputByteBuffer;
    BytesWritable serializeBytesWritable;
    ByteStream.Output output;
    
    public BinarySortableSerDe() {
        this.inputByteBuffer = new InputByteBuffer();
        this.serializeBytesWritable = new BytesWritable();
        this.output = new ByteStream.Output();
    }
    
    @Override
    public void initialize(final Configuration conf, final Properties tbl) throws SerDeException {
        final String columnNameProperty = tbl.getProperty("columns");
        final String columnTypeProperty = tbl.getProperty("columns.types");
        if (columnNameProperty.length() == 0) {
            this.columnNames = new ArrayList<String>();
        }
        else {
            this.columnNames = Arrays.asList(columnNameProperty.split(","));
        }
        if (columnTypeProperty.length() == 0) {
            this.columnTypes = new ArrayList<TypeInfo>();
        }
        else {
            this.columnTypes = TypeInfoUtils.getTypeInfosFromTypeString(columnTypeProperty);
        }
        assert this.columnNames.size() == this.columnTypes.size();
        this.rowTypeInfo = TypeInfoFactory.getStructTypeInfo(this.columnNames, this.columnTypes);
        this.rowObjectInspector = (StructObjectInspector)TypeInfoUtils.getStandardWritableObjectInspectorFromTypeInfo(this.rowTypeInfo);
        this.row = new ArrayList<Object>(this.columnNames.size());
        for (int i = 0; i < this.columnNames.size(); ++i) {
            this.row.add(null);
        }
        final String columnSortOrder = tbl.getProperty("serialization.sort.order");
        this.columnSortOrderIsDesc = new boolean[this.columnNames.size()];
        for (int j = 0; j < this.columnSortOrderIsDesc.length; ++j) {
            this.columnSortOrderIsDesc[j] = (columnSortOrder != null && columnSortOrder.charAt(j) == '-');
        }
    }
    
    @Override
    public Class<? extends Writable> getSerializedClass() {
        return BytesWritable.class;
    }
    
    @Override
    public ObjectInspector getObjectInspector() throws SerDeException {
        return this.rowObjectInspector;
    }
    
    @Override
    public Object deserialize(final Writable blob) throws SerDeException {
        final BytesWritable data = (BytesWritable)blob;
        this.inputByteBuffer.reset(data.getBytes(), 0, data.getLength());
        try {
            for (int i = 0; i < this.columnNames.size(); ++i) {
                this.row.set(i, deserialize(this.inputByteBuffer, this.columnTypes.get(i), this.columnSortOrderIsDesc[i], this.row.get(i)));
            }
        }
        catch (IOException e) {
            throw new SerDeException(e);
        }
        return this.row;
    }
    
    static Object deserialize(final InputByteBuffer buffer, final TypeInfo type, final boolean invert, final Object reuse) throws IOException {
        final byte isNull = buffer.read(invert);
        if (isNull == 0) {
            return null;
        }
        assert isNull == 1;
        switch (type.getCategory()) {
            case PRIMITIVE: {
                final PrimitiveTypeInfo ptype = (PrimitiveTypeInfo)type;
                switch (ptype.getPrimitiveCategory()) {
                    case VOID: {
                        return null;
                    }
                    case BOOLEAN: {
                        final BooleanWritable r = (BooleanWritable)((reuse == null) ? new BooleanWritable() : reuse);
                        final byte b = buffer.read(invert);
                        assert b == 2;
                        r.set(b == 2);
                        return r;
                    }
                    case BYTE: {
                        final ByteWritable r2 = (ByteWritable)((reuse == null) ? new ByteWritable() : reuse);
                        r2.set((byte)(buffer.read(invert) ^ 0x80));
                        return r2;
                    }
                    case SHORT: {
                        final ShortWritable r3 = (ShortWritable)((reuse == null) ? new ShortWritable() : reuse);
                        int v = buffer.read(invert) ^ 0x80;
                        v = (v << 8) + (buffer.read(invert) & 0xFF);
                        r3.set((short)v);
                        return r3;
                    }
                    case INT: {
                        final IntWritable r4 = (IntWritable)((reuse == null) ? new IntWritable() : reuse);
                        r4.set(deserializeInt(buffer, invert));
                        return r4;
                    }
                    case LONG: {
                        final LongWritable r5 = (LongWritable)((reuse == null) ? new LongWritable() : reuse);
                        r5.set(deserializeLong(buffer, invert));
                        return r5;
                    }
                    case FLOAT: {
                        final FloatWritable r6 = (FloatWritable)((reuse == null) ? new FloatWritable() : reuse);
                        int v = 0;
                        for (int i = 0; i < 4; ++i) {
                            v = (v << 8) + (buffer.read(invert) & 0xFF);
                        }
                        if ((v & Integer.MIN_VALUE) == 0x0) {
                            v ^= -1;
                        }
                        else {
                            v ^= Integer.MIN_VALUE;
                        }
                        r6.set(Float.intBitsToFloat(v));
                        return r6;
                    }
                    case DOUBLE: {
                        final DoubleWritable r7 = (DoubleWritable)((reuse == null) ? new DoubleWritable() : reuse);
                        long v2 = 0L;
                        for (int j = 0; j < 8; ++j) {
                            v2 = (v2 << 8) + (buffer.read(invert) & 0xFF);
                        }
                        if ((v2 & Long.MIN_VALUE) == 0x0L) {
                            v2 ^= -1L;
                        }
                        else {
                            v2 ^= Long.MIN_VALUE;
                        }
                        r7.set(Double.longBitsToDouble(v2));
                        return r7;
                    }
                    case STRING: {
                        final Text r8 = (Text)((reuse == null) ? new Text() : reuse);
                        return deserializeText(buffer, invert, r8);
                    }
                    case CHAR: {
                        final HiveCharWritable r9 = (HiveCharWritable)((reuse == null) ? new HiveCharWritable() : reuse);
                        deserializeText(buffer, invert, r9.getTextValue());
                        r9.enforceMaxLength(getCharacterMaxLength(type));
                        return r9;
                    }
                    case VARCHAR: {
                        final HiveVarcharWritable r10 = (HiveVarcharWritable)((reuse == null) ? new HiveVarcharWritable() : reuse);
                        deserializeText(buffer, invert, r10.getTextValue());
                        r10.enforceMaxLength(getCharacterMaxLength(type));
                        return r10;
                    }
                    case BINARY: {
                        final BytesWritable bw = new BytesWritable();
                        final int start = buffer.tell();
                        int length = 0;
                        while (true) {
                            final byte b2 = buffer.read(invert);
                            if (b2 == 0) {
                                break;
                            }
                            if (b2 == 1) {
                                buffer.read(invert);
                            }
                            ++length;
                        }
                        if (length == buffer.tell() - start) {
                            bw.set(buffer.getData(), start, length);
                        }
                        else {
                            bw.set(buffer.getData(), start, length);
                            buffer.seek(start);
                            final byte[] rdata = bw.getBytes();
                            for (int k = 0; k < length; ++k) {
                                byte b3 = buffer.read(invert);
                                if (b3 == 1) {
                                    b3 = (byte)(buffer.read(invert) - 1);
                                }
                                rdata[k] = b3;
                            }
                            final byte b4 = buffer.read(invert);
                            assert b4 == 0;
                        }
                        return bw;
                    }
                    case DATE: {
                        final DateWritable d = (DateWritable)((reuse == null) ? new DateWritable() : reuse);
                        d.set(deserializeInt(buffer, invert));
                        return d;
                    }
                    case TIMESTAMP: {
                        final TimestampWritable t = (TimestampWritable)((reuse == null) ? new TimestampWritable() : reuse);
                        final byte[] bytes = new byte[11];
                        for (int i = 0; i < bytes.length; ++i) {
                            bytes[i] = buffer.read(invert);
                        }
                        t.setBinarySortable(bytes, 0);
                        return t;
                    }
                    case INTERVAL_YEAR_MONTH: {
                        final HiveIntervalYearMonthWritable l = (HiveIntervalYearMonthWritable)((reuse == null) ? new HiveIntervalYearMonthWritable() : reuse);
                        l.set(deserializeInt(buffer, invert));
                        return l;
                    }
                    case INTERVAL_DAY_TIME: {
                        final HiveIntervalDayTimeWritable m = (HiveIntervalDayTimeWritable)((reuse == null) ? new HiveIntervalDayTimeWritable() : reuse);
                        final long totalSecs = deserializeLong(buffer, invert);
                        final int nanos = deserializeInt(buffer, invert);
                        m.set(totalSecs, nanos);
                        return m;
                    }
                    case DECIMAL: {
                        final HiveDecimalWritable bdw = (HiveDecimalWritable)((reuse == null) ? new HiveDecimalWritable() : reuse);
                        int b5 = buffer.read(invert) - 1;
                        assert b5 == 0;
                        final boolean positive = b5 != -1;
                        int factor = buffer.read(invert) ^ 0x80;
                        for (int i2 = 0; i2 < 3; ++i2) {
                            factor = (factor << 8) + (buffer.read(invert) & 0xFF);
                        }
                        if (!positive) {
                            factor = -factor;
                        }
                        final int start2 = buffer.tell();
                        int length2 = 0;
                        while (true) {
                            b5 = buffer.read(positive ? invert : (!invert));
                            assert b5 != 1;
                            if (b5 == 0) {
                                if (BinarySortableSerDe.decimalBuffer == null || BinarySortableSerDe.decimalBuffer.length < length2) {
                                    BinarySortableSerDe.decimalBuffer = new byte[length2];
                                }
                                buffer.seek(start2);
                                for (int i3 = 0; i3 < length2; ++i3) {
                                    BinarySortableSerDe.decimalBuffer[i3] = buffer.read(positive ? invert : (!invert));
                                }
                                buffer.read(positive ? invert : (!invert));
                                final String digits = new String(BinarySortableSerDe.decimalBuffer, 0, length2, BinarySortableSerDe.decimalCharSet);
                                final BigInteger bi = new BigInteger(digits);
                                HiveDecimal bd = HiveDecimal.create(bi).scaleByPowerOfTen(factor - length2);
                                if (!positive) {
                                    bd = bd.negate();
                                }
                                bdw.set(bd);
                                return bdw;
                            }
                            ++length2;
                        }
                        break;
                    }
                    default: {
                        throw new RuntimeException("Unrecognized type: " + ptype.getPrimitiveCategory());
                    }
                }
                break;
            }
            case LIST: {
                final ListTypeInfo ltype = (ListTypeInfo)type;
                final TypeInfo etype = ltype.getListElementTypeInfo();
                final ArrayList<Object> r11 = (ArrayList<Object>)((reuse == null) ? new ArrayList<Object>() : reuse);
                int size = 0;
                while (true) {
                    final int more = buffer.read(invert);
                    if (more == 0) {
                        while (r11.size() > size) {
                            r11.remove(r11.size() - 1);
                        }
                        return r11;
                    }
                    assert more == 1;
                    if (size == r11.size()) {
                        r11.add(null);
                    }
                    r11.set(size, deserialize(buffer, etype, invert, r11.get(size)));
                    ++size;
                }
                break;
            }
            case MAP: {
                final MapTypeInfo mtype = (MapTypeInfo)type;
                final TypeInfo ktype = mtype.getMapKeyTypeInfo();
                final TypeInfo vtype = mtype.getMapValueTypeInfo();
                Map<Object, Object> r12;
                if (reuse == null) {
                    r12 = new HashMap<Object, Object>();
                }
                else {
                    r12 = (Map<Object, Object>)reuse;
                    r12.clear();
                }
                while (true) {
                    final int more = buffer.read(invert);
                    if (more == 0) {
                        return r12;
                    }
                    assert more == 1;
                    final Object k2 = deserialize(buffer, ktype, invert, null);
                    final Object v3 = deserialize(buffer, vtype, invert, null);
                    r12.put(k2, v3);
                }
                break;
            }
            case STRUCT: {
                final StructTypeInfo stype = (StructTypeInfo)type;
                final List<TypeInfo> fieldTypes = stype.getAllStructFieldTypeInfos();
                final int size2 = fieldTypes.size();
                final ArrayList<Object> r13 = (ArrayList<Object>)((reuse == null) ? new ArrayList<Object>(size2) : reuse);
                assert r13.size() <= size2;
                while (r13.size() < size2) {
                    r13.add(null);
                }
                for (int eid = 0; eid < size2; ++eid) {
                    r13.set(eid, deserialize(buffer, fieldTypes.get(eid), invert, r13.get(eid)));
                }
                return r13;
            }
            case UNION: {
                final UnionTypeInfo utype = (UnionTypeInfo)type;
                final StandardUnionObjectInspector.StandardUnion r14 = (StandardUnionObjectInspector.StandardUnion)((reuse == null) ? new StandardUnionObjectInspector.StandardUnion() : reuse);
                final byte tag = buffer.read(invert);
                r14.setTag(tag);
                r14.setObject(deserialize(buffer, utype.getAllUnionObjectTypeInfos().get(tag), invert, null));
                return r14;
            }
            default: {
                throw new RuntimeException("Unrecognized type: " + type.getCategory());
            }
        }
    }
    
    private static int deserializeInt(final InputByteBuffer buffer, final boolean invert) throws IOException {
        int v = buffer.read(invert) ^ 0x80;
        for (int i = 0; i < 3; ++i) {
            v = (v << 8) + (buffer.read(invert) & 0xFF);
        }
        return v;
    }
    
    private static long deserializeLong(final InputByteBuffer buffer, final boolean invert) throws IOException {
        long v = buffer.read(invert) ^ 0x80;
        for (int i = 0; i < 7; ++i) {
            v = (v << 8) + (buffer.read(invert) & 0xFF);
        }
        return v;
    }
    
    static int getCharacterMaxLength(final TypeInfo type) {
        return ((BaseCharTypeInfo)type).getLength();
    }
    
    public static Text deserializeText(final InputByteBuffer buffer, final boolean invert, final Text r) throws IOException {
        final int start = buffer.tell();
        int length = 0;
        while (true) {
            final byte b = buffer.read(invert);
            if (b == 0) {
                break;
            }
            if (b == 1) {
                buffer.read(invert);
            }
            ++length;
        }
        if (length == buffer.tell() - start) {
            r.set(buffer.getData(), start, length);
        }
        else {
            r.set(buffer.getData(), start, length);
            buffer.seek(start);
            final byte[] rdata = r.getBytes();
            for (int i = 0; i < length; ++i) {
                byte b2 = buffer.read(invert);
                if (b2 == 1) {
                    b2 = (byte)(buffer.read(invert) - 1);
                }
                rdata[i] = b2;
            }
            final byte b3 = buffer.read(invert);
            assert b3 == 0;
        }
        return r;
    }
    
    @Override
    public Writable serialize(final Object obj, final ObjectInspector objInspector) throws SerDeException {
        this.output.reset();
        final StructObjectInspector soi = (StructObjectInspector)objInspector;
        final List<? extends StructField> fields = soi.getAllStructFieldRefs();
        for (int i = 0; i < this.columnNames.size(); ++i) {
            serialize(this.output, soi.getStructFieldData(obj, (StructField)fields.get(i)), ((StructField)fields.get(i)).getFieldObjectInspector(), this.columnSortOrderIsDesc[i]);
        }
        this.serializeBytesWritable.set(this.output.getData(), 0, this.output.getLength());
        return this.serializeBytesWritable;
    }
    
    public static void writeByte(final ByteStream.RandomAccessOutput buffer, byte b, final boolean invert) {
        if (invert) {
            b ^= (byte)255;
        }
        buffer.write(b);
    }
    
    static void serialize(final ByteStream.Output buffer, final Object o, final ObjectInspector oi, final boolean invert) throws SerDeException {
        if (o == null) {
            writeByte(buffer, (byte)0, invert);
            return;
        }
        writeByte(buffer, (byte)1, invert);
        switch (oi.getCategory()) {
            case PRIMITIVE: {
                final PrimitiveObjectInspector poi = (PrimitiveObjectInspector)oi;
                switch (poi.getPrimitiveCategory()) {
                    case VOID: {
                        return;
                    }
                    case BOOLEAN: {
                        final boolean v = ((BooleanObjectInspector)poi).get(o);
                        writeByte(buffer, (byte)(v ? 2 : 1), invert);
                        return;
                    }
                    case BYTE: {
                        final ByteObjectInspector boi = (ByteObjectInspector)poi;
                        final byte v2 = boi.get(o);
                        writeByte(buffer, (byte)(v2 ^ 0x80), invert);
                        return;
                    }
                    case SHORT: {
                        final ShortObjectInspector spoi = (ShortObjectInspector)poi;
                        final short v3 = spoi.get(o);
                        serializeShort(buffer, v3, invert);
                        return;
                    }
                    case INT: {
                        final IntObjectInspector ioi = (IntObjectInspector)poi;
                        final int v4 = ioi.get(o);
                        serializeInt(buffer, v4, invert);
                        return;
                    }
                    case LONG: {
                        final LongObjectInspector loi = (LongObjectInspector)poi;
                        final long v5 = loi.get(o);
                        serializeLong(buffer, v5, invert);
                        return;
                    }
                    case FLOAT: {
                        final FloatObjectInspector foi = (FloatObjectInspector)poi;
                        serializeFloat(buffer, foi.get(o), invert);
                        return;
                    }
                    case DOUBLE: {
                        final DoubleObjectInspector doi = (DoubleObjectInspector)poi;
                        serializeDouble(buffer, doi.get(o), invert);
                        return;
                    }
                    case STRING: {
                        final StringObjectInspector soi = (StringObjectInspector)poi;
                        final Text t = soi.getPrimitiveWritableObject(o);
                        serializeBytes(buffer, t.getBytes(), t.getLength(), invert);
                        return;
                    }
                    case CHAR: {
                        final HiveCharObjectInspector hcoi = (HiveCharObjectInspector)poi;
                        final HiveCharWritable hc = hcoi.getPrimitiveWritableObject(o);
                        final Text t2 = hc.getStrippedValue();
                        serializeBytes(buffer, t2.getBytes(), t2.getLength(), invert);
                        return;
                    }
                    case VARCHAR: {
                        final HiveVarcharObjectInspector hcoi2 = (HiveVarcharObjectInspector)poi;
                        final HiveVarcharWritable hc2 = hcoi2.getPrimitiveWritableObject(o);
                        final Text t2 = hc2.getTextValue();
                        serializeBytes(buffer, t2.getBytes(), t2.getLength(), invert);
                        return;
                    }
                    case BINARY: {
                        final BinaryObjectInspector baoi = (BinaryObjectInspector)poi;
                        final BytesWritable ba = baoi.getPrimitiveWritableObject(o);
                        final byte[] toSer = new byte[ba.getLength()];
                        System.arraycopy(ba.getBytes(), 0, toSer, 0, ba.getLength());
                        serializeBytes(buffer, toSer, ba.getLength(), invert);
                        return;
                    }
                    case DATE: {
                        final DateObjectInspector doi2 = (DateObjectInspector)poi;
                        final int v4 = doi2.getPrimitiveWritableObject(o).getDays();
                        serializeInt(buffer, v4, invert);
                        return;
                    }
                    case TIMESTAMP: {
                        final TimestampObjectInspector toi = (TimestampObjectInspector)poi;
                        final TimestampWritable t3 = toi.getPrimitiveWritableObject(o);
                        serializeTimestampWritable(buffer, t3, invert);
                        return;
                    }
                    case INTERVAL_YEAR_MONTH: {
                        final HiveIntervalYearMonthObjectInspector ioi2 = (HiveIntervalYearMonthObjectInspector)poi;
                        final HiveIntervalYearMonth intervalYearMonth = ioi2.getPrimitiveJavaObject(o);
                        serializeHiveIntervalYearMonth(buffer, intervalYearMonth, invert);
                        return;
                    }
                    case INTERVAL_DAY_TIME: {
                        final HiveIntervalDayTimeObjectInspector ioi3 = (HiveIntervalDayTimeObjectInspector)poi;
                        final HiveIntervalDayTime intervalDayTime = ioi3.getPrimitiveJavaObject(o);
                        serializeHiveIntervalDayTime(buffer, intervalDayTime, invert);
                        return;
                    }
                    case DECIMAL: {
                        final HiveDecimalObjectInspector boi2 = (HiveDecimalObjectInspector)poi;
                        final HiveDecimal dec = boi2.getPrimitiveJavaObject(o);
                        serializeHiveDecimal(buffer, dec, invert);
                        return;
                    }
                    default: {
                        throw new RuntimeException("Unrecognized type: " + poi.getPrimitiveCategory());
                    }
                }
                break;
            }
            case LIST: {
                final ListObjectInspector loi2 = (ListObjectInspector)oi;
                final ObjectInspector eoi = loi2.getListElementObjectInspector();
                for (int size = loi2.getListLength(o), eid = 0; eid < size; ++eid) {
                    writeByte(buffer, (byte)1, invert);
                    serialize(buffer, loi2.getListElement(o, eid), eoi, invert);
                }
                writeByte(buffer, (byte)0, invert);
            }
            case MAP: {
                final MapObjectInspector moi = (MapObjectInspector)oi;
                final ObjectInspector koi = moi.getMapKeyObjectInspector();
                final ObjectInspector voi = moi.getMapValueObjectInspector();
                final Map<?, ?> map = moi.getMap(o);
                for (final Map.Entry<?, ?> entry : map.entrySet()) {
                    writeByte(buffer, (byte)1, invert);
                    serialize(buffer, entry.getKey(), koi, invert);
                    serialize(buffer, entry.getValue(), voi, invert);
                }
                writeByte(buffer, (byte)0, invert);
            }
            case STRUCT: {
                final StructObjectInspector soi2 = (StructObjectInspector)oi;
                final List<? extends StructField> fields = soi2.getAllStructFieldRefs();
                for (int i = 0; i < fields.size(); ++i) {
                    serialize(buffer, soi2.getStructFieldData(o, (StructField)fields.get(i)), ((StructField)fields.get(i)).getFieldObjectInspector(), invert);
                }
            }
            case UNION: {
                final UnionObjectInspector uoi = (UnionObjectInspector)oi;
                final byte tag = uoi.getTag(o);
                writeByte(buffer, tag, invert);
                serialize(buffer, uoi.getField(o), uoi.getObjectInspectors().get(tag), invert);
            }
            default: {
                throw new RuntimeException("Unrecognized type: " + oi.getCategory());
            }
        }
    }
    
    public static void serializeBytes(final ByteStream.Output buffer, final byte[] data, final int length, final boolean invert) {
        for (int i = 0; i < length; ++i) {
            if (data[i] == 0 || data[i] == 1) {
                writeByte(buffer, (byte)1, invert);
                writeByte(buffer, (byte)(data[i] + 1), invert);
            }
            else {
                writeByte(buffer, data[i], invert);
            }
        }
        writeByte(buffer, (byte)0, invert);
    }
    
    public static void serializeBytes(final ByteStream.Output buffer, final byte[] data, final int offset, final int length, final boolean invert) {
        for (int i = offset; i < offset + length; ++i) {
            if (data[i] == 0 || data[i] == 1) {
                writeByte(buffer, (byte)1, invert);
                writeByte(buffer, (byte)(data[i] + 1), invert);
            }
            else {
                writeByte(buffer, data[i], invert);
            }
        }
        writeByte(buffer, (byte)0, invert);
    }
    
    public static void serializeShort(final ByteStream.Output buffer, final short v, final boolean invert) {
        writeByte(buffer, (byte)(v >> 8 ^ 0x80), invert);
        writeByte(buffer, (byte)v, invert);
    }
    
    public static void serializeInt(final ByteStream.Output buffer, final int v, final boolean invert) {
        writeByte(buffer, (byte)(v >> 24 ^ 0x80), invert);
        writeByte(buffer, (byte)(v >> 16), invert);
        writeByte(buffer, (byte)(v >> 8), invert);
        writeByte(buffer, (byte)v, invert);
    }
    
    public static void serializeLong(final ByteStream.Output buffer, final long v, final boolean invert) {
        writeByte(buffer, (byte)(v >> 56 ^ 0x80L), invert);
        writeByte(buffer, (byte)(v >> 48), invert);
        writeByte(buffer, (byte)(v >> 40), invert);
        writeByte(buffer, (byte)(v >> 32), invert);
        writeByte(buffer, (byte)(v >> 24), invert);
        writeByte(buffer, (byte)(v >> 16), invert);
        writeByte(buffer, (byte)(v >> 8), invert);
        writeByte(buffer, (byte)v, invert);
    }
    
    public static void serializeFloat(final ByteStream.Output buffer, final float vf, final boolean invert) {
        int v = Float.floatToIntBits(vf);
        if ((v & Integer.MIN_VALUE) != 0x0) {
            v ^= -1;
        }
        else {
            v ^= Integer.MIN_VALUE;
        }
        writeByte(buffer, (byte)(v >> 24), invert);
        writeByte(buffer, (byte)(v >> 16), invert);
        writeByte(buffer, (byte)(v >> 8), invert);
        writeByte(buffer, (byte)v, invert);
    }
    
    public static void serializeDouble(final ByteStream.Output buffer, final double vd, final boolean invert) {
        long v = Double.doubleToLongBits(vd);
        if ((v & Long.MIN_VALUE) != 0x0L) {
            v ^= -1L;
        }
        else {
            v ^= Long.MIN_VALUE;
        }
        writeByte(buffer, (byte)(v >> 56), invert);
        writeByte(buffer, (byte)(v >> 48), invert);
        writeByte(buffer, (byte)(v >> 40), invert);
        writeByte(buffer, (byte)(v >> 32), invert);
        writeByte(buffer, (byte)(v >> 24), invert);
        writeByte(buffer, (byte)(v >> 16), invert);
        writeByte(buffer, (byte)(v >> 8), invert);
        writeByte(buffer, (byte)v, invert);
    }
    
    public static void serializeTimestampWritable(final ByteStream.Output buffer, final TimestampWritable t, final boolean invert) {
        final byte[] data = t.getBinarySortable();
        for (int i = 0; i < data.length; ++i) {
            writeByte(buffer, data[i], invert);
        }
    }
    
    public static void serializeHiveIntervalYearMonth(final ByteStream.Output buffer, final HiveIntervalYearMonth intervalYearMonth, final boolean invert) {
        final int totalMonths = intervalYearMonth.getTotalMonths();
        serializeInt(buffer, totalMonths, invert);
    }
    
    public static void serializeHiveIntervalDayTime(final ByteStream.Output buffer, final HiveIntervalDayTime intervalDayTime, final boolean invert) {
        final long totalSecs = intervalDayTime.getTotalSeconds();
        final int nanos = intervalDayTime.getNanos();
        serializeLong(buffer, totalSecs, invert);
        serializeInt(buffer, nanos, invert);
    }
    
    public static void serializeHiveDecimal(final ByteStream.Output buffer, HiveDecimal dec, final boolean invert) {
        final int sign = dec.compareTo(HiveDecimal.ZERO);
        dec = dec.abs();
        int factor = dec.bigDecimalValue().precision() - dec.bigDecimalValue().scale();
        factor = ((sign == 1) ? factor : (-factor));
        dec.scaleByPowerOfTen(Math.abs(dec.scale()));
        final String digits = dec.unscaledValue().toString();
        writeByte(buffer, (byte)(sign + 1), invert);
        writeByte(buffer, (byte)(factor >> 24 ^ 0x80), invert);
        writeByte(buffer, (byte)(factor >> 16), invert);
        writeByte(buffer, (byte)(factor >> 8), invert);
        writeByte(buffer, (byte)factor, invert);
        serializeBytes(buffer, digits.getBytes(BinarySortableSerDe.decimalCharSet), digits.length(), (sign == -1) ? (!invert) : invert);
    }
    
    @Override
    public SerDeStats getSerDeStats() {
        return null;
    }
    
    public static void serializeStruct(final ByteStream.Output byteStream, final Object[] fieldData, final List<ObjectInspector> fieldOis, final boolean[] sortableSortOrders) throws SerDeException {
        for (int i = 0; i < fieldData.length; ++i) {
            serialize(byteStream, fieldData[i], fieldOis.get(i), sortableSortOrders[i]);
        }
    }
    
    public boolean[] getSortOrders() {
        return this.columnSortOrderIsDesc;
    }
    
    static {
        LOG = LogFactory.getLog(BinarySortableSerDe.class.getName());
        BinarySortableSerDe.decimalBuffer = null;
        BinarySortableSerDe.decimalCharSet = Charset.forName("US-ASCII");
    }
}
