// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import org.apache.commons.logging.LogFactory;
import java.util.Iterator;
import org.apache.hadoop.hive.serde2.io.HiveDecimalWritable;
import org.apache.hadoop.hive.serde2.io.HiveIntervalDayTimeWritable;
import org.apache.hadoop.hive.serde2.io.HiveIntervalYearMonthWritable;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import org.apache.hadoop.hive.serde2.io.DateWritable;
import java.util.Map;
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
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.objectinspector.UnionObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.io.BinaryComparable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.ByteStream;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.hive.serde2.SerDeStats;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.SerDeSpec;
import org.apache.hadoop.hive.serde2.AbstractSerDe;

@SerDeSpec(schemaProps = { "columns", "columns.types" })
public class LazyBinarySerDe extends AbstractSerDe
{
    public static final Log LOG;
    List<String> columnNames;
    List<TypeInfo> columnTypes;
    TypeInfo rowTypeInfo;
    ObjectInspector cachedObjectInspector;
    LazyBinaryStruct cachedLazyBinaryStruct;
    private int serializedSize;
    private SerDeStats stats;
    private boolean lastOperationSerialize;
    private boolean lastOperationDeserialize;
    ByteArrayRef byteArrayRef;
    BytesWritable serializeBytesWritable;
    ByteStream.Output serializeByteStream;
    BooleanRef nullMapKey;
    
    public LazyBinarySerDe() throws SerDeException {
        this.serializeBytesWritable = new BytesWritable();
        this.serializeByteStream = new ByteStream.Output();
        this.nullMapKey = new BooleanRef(false);
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
        this.cachedObjectInspector = LazyBinaryUtils.getLazyBinaryObjectInspectorFromTypeInfo(this.rowTypeInfo);
        this.cachedLazyBinaryStruct = (LazyBinaryStruct)LazyBinaryFactory.createLazyBinaryObject(this.cachedObjectInspector);
        LazyBinarySerDe.LOG.debug("LazyBinarySerDe initialized with: columnNames=" + this.columnNames + " columnTypes=" + this.columnTypes);
        this.serializedSize = 0;
        this.stats = new SerDeStats();
        this.lastOperationSerialize = false;
        this.lastOperationDeserialize = false;
    }
    
    @Override
    public ObjectInspector getObjectInspector() throws SerDeException {
        return this.cachedObjectInspector;
    }
    
    @Override
    public Class<? extends Writable> getSerializedClass() {
        return BytesWritable.class;
    }
    
    @Override
    public Object deserialize(final Writable field) throws SerDeException {
        if (this.byteArrayRef == null) {
            this.byteArrayRef = new ByteArrayRef();
        }
        final BinaryComparable b = (BinaryComparable)field;
        if (b.getLength() == 0) {
            return null;
        }
        this.byteArrayRef.setData(b.getBytes());
        this.cachedLazyBinaryStruct.init(this.byteArrayRef, 0, b.getLength());
        this.lastOperationSerialize = false;
        this.lastOperationDeserialize = true;
        return this.cachedLazyBinaryStruct;
    }
    
    @Override
    public Writable serialize(final Object obj, final ObjectInspector objInspector) throws SerDeException {
        if (objInspector.getCategory() != ObjectInspector.Category.STRUCT) {
            throw new SerDeException(this.getClass().toString() + " can only serialize struct types, but we got: " + objInspector.getTypeName());
        }
        this.serializeByteStream.reset();
        serializeStruct(this.serializeByteStream, obj, (StructObjectInspector)objInspector, this.nullMapKey);
        this.serializeBytesWritable.set(this.serializeByteStream.getData(), 0, this.serializeByteStream.getLength());
        this.serializedSize = this.serializeByteStream.getLength();
        this.lastOperationSerialize = true;
        this.lastOperationDeserialize = false;
        return this.serializeBytesWritable;
    }
    
    private static void serializeStruct(final ByteStream.RandomAccessOutput byteStream, final Object obj, final StructObjectInspector soi, final BooleanRef warnedOnceNullMapKey) throws SerDeException {
        if (null == obj) {
            return;
        }
        final List<? extends StructField> fields = soi.getAllStructFieldRefs();
        final int size = fields.size();
        final Object[] fieldData = new Object[size];
        final List<ObjectInspector> fieldOis = new ArrayList<ObjectInspector>(size);
        for (int i = 0; i < size; ++i) {
            final StructField field = (StructField)fields.get(i);
            fieldData[i] = soi.getStructFieldData(obj, field);
            fieldOis.add(field.getFieldObjectInspector());
        }
        serializeStruct(byteStream, fieldData, fieldOis, warnedOnceNullMapKey);
    }
    
    public static void serializeStruct(final ByteStream.RandomAccessOutput byteStream, final Object[] fieldData, final List<ObjectInspector> fieldOis) throws SerDeException {
        serializeStruct(byteStream, fieldData, fieldOis, null);
    }
    
    private static void serializeStruct(final ByteStream.RandomAccessOutput byteStream, final Object[] fieldData, final List<ObjectInspector> fieldOis, final BooleanRef warnedOnceNullMapKey) throws SerDeException {
        int lasti = 0;
        byte nullByte = 0;
        for (int size = fieldData.length, i = 0; i < size; ++i) {
            if (null != fieldData[i]) {
                nullByte |= (byte)(1 << i % 8);
            }
            if (7 == i % 8 || i == size - 1) {
                byteStream.write(nullByte);
                for (int j = lasti; j <= i; ++j) {
                    serialize(byteStream, fieldData[j], fieldOis.get(j), false, warnedOnceNullMapKey);
                }
                lasti = i + 1;
                nullByte = 0;
            }
        }
    }
    
    private static void serializeUnion(final ByteStream.RandomAccessOutput byteStream, final Object obj, final UnionObjectInspector uoi, final BooleanRef warnedOnceNullMapKey) throws SerDeException {
        final byte tag = uoi.getTag(obj);
        byteStream.write(tag);
        serialize(byteStream, uoi.getField(obj), uoi.getObjectInspectors().get(tag), false, warnedOnceNullMapKey);
    }
    
    private static void serializeText(final ByteStream.RandomAccessOutput byteStream, final Text t, final boolean skipLengthPrefix) {
        final int length = t.getLength();
        if (!skipLengthPrefix) {
            LazyBinaryUtils.writeVInt(byteStream, length);
        }
        final byte[] data = t.getBytes();
        byteStream.write(data, 0, length);
    }
    
    public static void serialize(final ByteStream.RandomAccessOutput byteStream, final Object obj, final ObjectInspector objInspector, final boolean skipLengthPrefix, final BooleanRef warnedOnceNullMapKey) throws SerDeException {
        if (null == obj) {
            return;
        }
        switch (objInspector.getCategory()) {
            case PRIMITIVE: {
                final PrimitiveObjectInspector poi = (PrimitiveObjectInspector)objInspector;
                switch (poi.getPrimitiveCategory()) {
                    case VOID: {
                        return;
                    }
                    case BOOLEAN: {
                        final boolean v = ((BooleanObjectInspector)poi).get(obj);
                        byteStream.write((byte)(byte)(v ? 1 : 0));
                        return;
                    }
                    case BYTE: {
                        final ByteObjectInspector boi = (ByteObjectInspector)poi;
                        final byte v2 = boi.get(obj);
                        byteStream.write(v2);
                        return;
                    }
                    case SHORT: {
                        final ShortObjectInspector spoi = (ShortObjectInspector)poi;
                        final short v3 = spoi.get(obj);
                        byteStream.write((byte)(v3 >> 8));
                        byteStream.write((byte)v3);
                        return;
                    }
                    case INT: {
                        final IntObjectInspector ioi = (IntObjectInspector)poi;
                        final int v4 = ioi.get(obj);
                        LazyBinaryUtils.writeVInt(byteStream, v4);
                        return;
                    }
                    case LONG: {
                        final LongObjectInspector loi = (LongObjectInspector)poi;
                        final long v5 = loi.get(obj);
                        LazyBinaryUtils.writeVLong(byteStream, v5);
                        return;
                    }
                    case FLOAT: {
                        final FloatObjectInspector foi = (FloatObjectInspector)poi;
                        final int v4 = Float.floatToIntBits(foi.get(obj));
                        byteStream.write((byte)(v4 >> 24));
                        byteStream.write((byte)(v4 >> 16));
                        byteStream.write((byte)(v4 >> 8));
                        byteStream.write((byte)v4);
                        return;
                    }
                    case DOUBLE: {
                        final DoubleObjectInspector doi = (DoubleObjectInspector)poi;
                        LazyBinaryUtils.writeDouble(byteStream, doi.get(obj));
                        return;
                    }
                    case STRING: {
                        final StringObjectInspector soi = (StringObjectInspector)poi;
                        final Text t = soi.getPrimitiveWritableObject(obj);
                        serializeText(byteStream, t, skipLengthPrefix);
                        return;
                    }
                    case CHAR: {
                        final HiveCharObjectInspector hcoi = (HiveCharObjectInspector)poi;
                        final Text t = hcoi.getPrimitiveWritableObject(obj).getTextValue();
                        serializeText(byteStream, t, skipLengthPrefix);
                        return;
                    }
                    case VARCHAR: {
                        final HiveVarcharObjectInspector hcoi2 = (HiveVarcharObjectInspector)poi;
                        final Text t = hcoi2.getPrimitiveWritableObject(obj).getTextValue();
                        serializeText(byteStream, t, skipLengthPrefix);
                        return;
                    }
                    case BINARY: {
                        final BinaryObjectInspector baoi = (BinaryObjectInspector)poi;
                        final BytesWritable bw = baoi.getPrimitiveWritableObject(obj);
                        final int length = bw.getLength();
                        if (!skipLengthPrefix) {
                            LazyBinaryUtils.writeVInt(byteStream, length);
                        }
                        else if (length == 0) {
                            throw new RuntimeException("LazyBinaryColumnarSerde cannot serialize a non-null zero length binary field. Consider using either LazyBinarySerde or ColumnarSerde.");
                        }
                        byteStream.write(bw.getBytes(), 0, length);
                        return;
                    }
                    case DATE: {
                        final DateWritable d = ((DateObjectInspector)poi).getPrimitiveWritableObject(obj);
                        d.writeToByteStream(byteStream);
                        return;
                    }
                    case TIMESTAMP: {
                        final TimestampObjectInspector toi = (TimestampObjectInspector)poi;
                        final TimestampWritable t2 = toi.getPrimitiveWritableObject(obj);
                        t2.writeToByteStream(byteStream);
                        return;
                    }
                    case INTERVAL_YEAR_MONTH: {
                        final HiveIntervalYearMonthWritable intervalYearMonth = ((HiveIntervalYearMonthObjectInspector)poi).getPrimitiveWritableObject(obj);
                        intervalYearMonth.writeToByteStream(byteStream);
                        return;
                    }
                    case INTERVAL_DAY_TIME: {
                        final HiveIntervalDayTimeWritable intervalDayTime = ((HiveIntervalDayTimeObjectInspector)poi).getPrimitiveWritableObject(obj);
                        intervalDayTime.writeToByteStream(byteStream);
                        return;
                    }
                    case DECIMAL: {
                        final HiveDecimalObjectInspector bdoi = (HiveDecimalObjectInspector)poi;
                        final HiveDecimalWritable t3 = bdoi.getPrimitiveWritableObject(obj);
                        if (t3 == null) {
                            return;
                        }
                        t3.writeToByteStream(byteStream);
                        return;
                    }
                    default: {
                        throw new RuntimeException("Unrecognized type: " + poi.getPrimitiveCategory());
                    }
                }
                break;
            }
            case LIST: {
                final ListObjectInspector loi2 = (ListObjectInspector)objInspector;
                final ObjectInspector eoi = loi2.getListElementObjectInspector();
                int byteSizeStart = 0;
                int listStart = 0;
                if (!skipLengthPrefix) {
                    byteSizeStart = byteStream.getLength();
                    byteStream.reserve(4);
                    listStart = byteStream.getLength();
                }
                final int size = loi2.getListLength(obj);
                LazyBinaryUtils.writeVInt(byteStream, size);
                byte nullByte = 0;
                for (int eid = 0; eid < size; ++eid) {
                    if (null != loi2.getListElement(obj, eid)) {
                        nullByte |= (byte)(1 << eid % 8);
                    }
                    if (7 == eid % 8 || eid == size - 1) {
                        byteStream.write(nullByte);
                        nullByte = 0;
                    }
                }
                for (int eid = 0; eid < size; ++eid) {
                    serialize(byteStream, loi2.getListElement(obj, eid), eoi, false, warnedOnceNullMapKey);
                }
                if (!skipLengthPrefix) {
                    final int listEnd = byteStream.getLength();
                    final int listSize = listEnd - listStart;
                    writeSizeAtOffset(byteStream, byteSizeStart, listSize);
                }
            }
            case MAP: {
                final MapObjectInspector moi = (MapObjectInspector)objInspector;
                final ObjectInspector koi = moi.getMapKeyObjectInspector();
                final ObjectInspector voi = moi.getMapValueObjectInspector();
                final Map<?, ?> map = moi.getMap(obj);
                int byteSizeStart2 = 0;
                int mapStart = 0;
                if (!skipLengthPrefix) {
                    byteSizeStart2 = byteStream.getLength();
                    byteStream.reserve(4);
                    mapStart = byteStream.getLength();
                }
                final int size2 = map.size();
                LazyBinaryUtils.writeVInt(byteStream, size2);
                int b = 0;
                byte nullByte2 = 0;
                for (final Map.Entry<?, ?> entry : map.entrySet()) {
                    if (null != entry.getKey()) {
                        nullByte2 |= (byte)(1 << b % 8);
                    }
                    else if (warnedOnceNullMapKey != null) {
                        if (!warnedOnceNullMapKey.value) {
                            LazyBinarySerDe.LOG.warn("Null map key encountered! Ignoring similar problems.");
                        }
                        warnedOnceNullMapKey.value = true;
                    }
                    ++b;
                    if (null != entry.getValue()) {
                        nullByte2 |= (byte)(1 << b % 8);
                    }
                    ++b;
                    if (0 == b % 8 || b == size2 * 2) {
                        byteStream.write(nullByte2);
                        nullByte2 = 0;
                    }
                }
                for (final Map.Entry<?, ?> entry : map.entrySet()) {
                    serialize(byteStream, entry.getKey(), koi, false, warnedOnceNullMapKey);
                    serialize(byteStream, entry.getValue(), voi, false, warnedOnceNullMapKey);
                }
                if (!skipLengthPrefix) {
                    final int mapEnd = byteStream.getLength();
                    final int mapSize = mapEnd - mapStart;
                    writeSizeAtOffset(byteStream, byteSizeStart2, mapSize);
                }
            }
            case STRUCT:
            case UNION: {
                int byteSizeStart3 = 0;
                int typeStart = 0;
                if (!skipLengthPrefix) {
                    byteSizeStart3 = byteStream.getLength();
                    byteStream.reserve(4);
                    typeStart = byteStream.getLength();
                }
                if (ObjectInspector.Category.STRUCT.equals(objInspector.getCategory())) {
                    serializeStruct(byteStream, obj, (StructObjectInspector)objInspector, warnedOnceNullMapKey);
                }
                else {
                    serializeUnion(byteStream, obj, (UnionObjectInspector)objInspector, warnedOnceNullMapKey);
                }
                if (!skipLengthPrefix) {
                    final int typeEnd = byteStream.getLength();
                    final int typeSize = typeEnd - typeStart;
                    writeSizeAtOffset(byteStream, byteSizeStart3, typeSize);
                }
            }
            default: {
                throw new RuntimeException("Unrecognized type: " + objInspector.getCategory());
            }
        }
    }
    
    private static void writeSizeAtOffset(final ByteStream.RandomAccessOutput byteStream, final int byteSizeStart, final int size) {
        byteStream.writeInt(byteSizeStart, size);
    }
    
    @Override
    public SerDeStats getSerDeStats() {
        assert this.lastOperationSerialize != this.lastOperationDeserialize;
        if (this.lastOperationSerialize) {
            this.stats.setRawDataSize(this.serializedSize);
        }
        else {
            this.stats.setRawDataSize(this.cachedLazyBinaryStruct.getRawDataSerializedSize());
        }
        return this.stats;
    }
    
    static {
        LOG = LogFactory.getLog(LazyBinarySerDe.class.getName());
    }
    
    public static class StringWrapper
    {
        public byte[] bytes;
        public int start;
        public int length;
        
        public void set(final byte[] bytes, final int start, final int length) {
            this.bytes = bytes;
            this.start = start;
            this.length = length;
        }
    }
    
    public static class BooleanRef
    {
        public boolean value;
        
        public BooleanRef(final boolean v) {
            this.value = v;
        }
    }
}
