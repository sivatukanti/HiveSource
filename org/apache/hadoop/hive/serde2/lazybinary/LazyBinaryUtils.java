// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import java.util.List;
import org.apache.hadoop.hive.serde2.typeinfo.UnionTypeInfo;
import java.util.ArrayList;
import org.apache.hadoop.hive.serde2.typeinfo.StructTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.MapTypeInfo;
import org.apache.hadoop.hive.serde2.lazybinary.objectinspector.LazyBinaryObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.typeinfo.ListTypeInfo;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.ByteStream;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import org.apache.hadoop.io.WritableUtils;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import java.util.concurrent.ConcurrentHashMap;

public final class LazyBinaryUtils
{
    public static final ThreadLocal<VInt> threadLocalVInt;
    private static ThreadLocal<byte[]> vLongBytesThreadLocal;
    static ConcurrentHashMap<TypeInfo, ObjectInspector> cachedLazyBinaryObjectInspector;
    
    public static int byteArrayToInt(final byte[] b, final int offset) {
        int value = 0;
        for (int i = 0; i < 4; ++i) {
            final int shift = (3 - i) * 8;
            value += (b[i + offset] & 0xFF) << shift;
        }
        return value;
    }
    
    public static long byteArrayToLong(final byte[] b, final int offset) {
        long value = 0L;
        for (int i = 0; i < 8; ++i) {
            final int shift = (7 - i) * 8;
            value += (long)(b[i + offset] & 0xFF) << shift;
        }
        return value;
    }
    
    public static short byteArrayToShort(final byte[] b, final int offset) {
        short value = 0;
        value += (short)((b[offset] & 0xFF) << 8);
        value += (short)(b[offset + 1] & 0xFF);
        return value;
    }
    
    public static void checkObjectByteInfo(final ObjectInspector objectInspector, final byte[] bytes, final int offset, final RecordInfo recordInfo, final VInt vInt) {
        final ObjectInspector.Category category = objectInspector.getCategory();
        Label_0561: {
            switch (category) {
                case PRIMITIVE: {
                    final PrimitiveObjectInspector.PrimitiveCategory primitiveCategory = ((PrimitiveObjectInspector)objectInspector).getPrimitiveCategory();
                    switch (primitiveCategory) {
                        case VOID: {
                            recordInfo.elementOffset = 0;
                            recordInfo.elementSize = 0;
                            break Label_0561;
                        }
                        case BOOLEAN:
                        case BYTE: {
                            recordInfo.elementOffset = 0;
                            recordInfo.elementSize = 1;
                            break Label_0561;
                        }
                        case SHORT: {
                            recordInfo.elementOffset = 0;
                            recordInfo.elementSize = 2;
                            break Label_0561;
                        }
                        case FLOAT: {
                            recordInfo.elementOffset = 0;
                            recordInfo.elementSize = 4;
                            break Label_0561;
                        }
                        case DOUBLE: {
                            recordInfo.elementOffset = 0;
                            recordInfo.elementSize = 8;
                            break Label_0561;
                        }
                        case INT: {
                            recordInfo.elementOffset = 0;
                            recordInfo.elementSize = WritableUtils.decodeVIntSize(bytes[offset]);
                            break Label_0561;
                        }
                        case LONG: {
                            recordInfo.elementOffset = 0;
                            recordInfo.elementSize = WritableUtils.decodeVIntSize(bytes[offset]);
                            break Label_0561;
                        }
                        case STRING: {
                            readVInt(bytes, offset, vInt);
                            recordInfo.elementOffset = vInt.length;
                            recordInfo.elementSize = vInt.value;
                            break Label_0561;
                        }
                        case CHAR:
                        case VARCHAR: {
                            readVInt(bytes, offset, vInt);
                            recordInfo.elementOffset = vInt.length;
                            recordInfo.elementSize = vInt.value;
                            break Label_0561;
                        }
                        case BINARY: {
                            readVInt(bytes, offset, vInt);
                            recordInfo.elementOffset = vInt.length;
                            recordInfo.elementSize = vInt.value;
                            break Label_0561;
                        }
                        case DATE: {
                            recordInfo.elementOffset = 0;
                            recordInfo.elementSize = WritableUtils.decodeVIntSize(bytes[offset]);
                            break Label_0561;
                        }
                        case TIMESTAMP: {
                            recordInfo.elementOffset = 0;
                            recordInfo.elementSize = TimestampWritable.getTotalLength(bytes, offset);
                            break Label_0561;
                        }
                        case INTERVAL_YEAR_MONTH: {
                            recordInfo.elementOffset = 0;
                            recordInfo.elementSize = WritableUtils.decodeVIntSize(bytes[offset]);
                            break Label_0561;
                        }
                        case INTERVAL_DAY_TIME: {
                            recordInfo.elementOffset = 0;
                            final int secondsSize = WritableUtils.decodeVIntSize(bytes[offset]);
                            final int nanosSize = WritableUtils.decodeVIntSize(bytes[offset + secondsSize]);
                            recordInfo.elementSize = secondsSize + nanosSize;
                            break Label_0561;
                        }
                        case DECIMAL: {
                            readVInt(bytes, offset, vInt);
                            recordInfo.elementOffset = 0;
                            recordInfo.elementSize = vInt.length;
                            readVInt(bytes, offset + vInt.length, vInt);
                            recordInfo.elementSize += vInt.length + vInt.value;
                            break Label_0561;
                        }
                        default: {
                            throw new RuntimeException("Unrecognized primitive type: " + primitiveCategory);
                        }
                    }
                    break;
                }
                case LIST:
                case MAP:
                case STRUCT:
                case UNION: {
                    recordInfo.elementOffset = 4;
                    recordInfo.elementSize = byteArrayToInt(bytes, offset);
                    break;
                }
                default: {
                    throw new RuntimeException("Unrecognized non-primitive type: " + category);
                }
            }
        }
    }
    
    public static void readVLong(final byte[] bytes, final int offset, final VLong vlong) {
        final byte firstByte = bytes[offset];
        vlong.length = (byte)WritableUtils.decodeVIntSize(firstByte);
        if (vlong.length == 1) {
            vlong.value = firstByte;
            return;
        }
        long i = 0L;
        for (int idx = 0; idx < vlong.length - 1; ++idx) {
            final byte b = bytes[offset + 1 + idx];
            i <<= 8;
            i |= (b & 0xFF);
        }
        vlong.value = (WritableUtils.isNegativeVInt(firstByte) ? (~i) : i);
    }
    
    public static void readVInt(final byte[] bytes, final int offset, final VInt vInt) {
        final byte firstByte = bytes[offset];
        vInt.length = (byte)WritableUtils.decodeVIntSize(firstByte);
        if (vInt.length == 1) {
            vInt.value = firstByte;
            return;
        }
        int i = 0;
        for (int idx = 0; idx < vInt.length - 1; ++idx) {
            final byte b = bytes[offset + 1 + idx];
            i <<= 8;
            i |= (b & 0xFF);
        }
        vInt.value = (WritableUtils.isNegativeVInt(firstByte) ? (~i) : i);
    }
    
    public static void writeVInt(final ByteStream.RandomAccessOutput byteStream, final int i) {
        writeVLong(byteStream, i);
    }
    
    public static long readVLongFromByteArray(final byte[] bytes, int offset) {
        final byte firstByte = bytes[offset++];
        final int len = WritableUtils.decodeVIntSize(firstByte);
        if (len == 1) {
            return firstByte;
        }
        long i = 0L;
        for (int idx = 0; idx < len - 1; ++idx) {
            final byte b = bytes[offset++];
            i <<= 8;
            i |= (b & 0xFF);
        }
        return WritableUtils.isNegativeVInt(firstByte) ? (~i) : i;
    }
    
    public static int writeVLongToByteArray(final byte[] bytes, final long l) {
        return writeVLongToByteArray(bytes, 0, l);
    }
    
    public static int writeVLongToByteArray(final byte[] bytes, final int offset, long l) {
        if (l >= -112L && l <= 127L) {
            bytes[offset] = (byte)l;
            return 1;
        }
        int len = -112;
        if (l < 0L) {
            l ^= -1L;
            len = -120;
        }
        for (long tmp = l; tmp != 0L; tmp >>= 8, --len) {}
        bytes[offset] = (byte)len;
        int idx;
        for (len = (idx = ((len < -120) ? (-(len + 120)) : (-(len + 112)))); idx != 0; --idx) {
            final int shiftbits = (idx - 1) * 8;
            final long mask = 255L << shiftbits;
            bytes[offset + 1 - (idx - len)] = (byte)((l & mask) >> shiftbits);
        }
        return 1 + len;
    }
    
    public static void writeVLong(final ByteStream.RandomAccessOutput byteStream, final long l) {
        final byte[] vLongBytes = LazyBinaryUtils.vLongBytesThreadLocal.get();
        final int len = writeVLongToByteArray(vLongBytes, l);
        byteStream.write(vLongBytes, 0, len);
    }
    
    public static void writeDouble(final ByteStream.RandomAccessOutput byteStream, final double d) {
        final long v = Double.doubleToLongBits(d);
        byteStream.write((byte)(v >> 56));
        byteStream.write((byte)(v >> 48));
        byteStream.write((byte)(v >> 40));
        byteStream.write((byte)(v >> 32));
        byteStream.write((byte)(v >> 24));
        byteStream.write((byte)(v >> 16));
        byteStream.write((byte)(v >> 8));
        byteStream.write((byte)v);
    }
    
    public static ObjectInspector getLazyBinaryObjectInspectorFromTypeInfo(final TypeInfo typeInfo) {
        ObjectInspector result = LazyBinaryUtils.cachedLazyBinaryObjectInspector.get(typeInfo);
        if (result == null) {
            switch (typeInfo.getCategory()) {
                case PRIMITIVE: {
                    result = PrimitiveObjectInspectorFactory.getPrimitiveWritableObjectInspector((PrimitiveTypeInfo)typeInfo);
                    break;
                }
                case LIST: {
                    final ObjectInspector elementObjectInspector = getLazyBinaryObjectInspectorFromTypeInfo(((ListTypeInfo)typeInfo).getListElementTypeInfo());
                    result = LazyBinaryObjectInspectorFactory.getLazyBinaryListObjectInspector(elementObjectInspector);
                    break;
                }
                case MAP: {
                    final MapTypeInfo mapTypeInfo = (MapTypeInfo)typeInfo;
                    final ObjectInspector keyObjectInspector = getLazyBinaryObjectInspectorFromTypeInfo(mapTypeInfo.getMapKeyTypeInfo());
                    final ObjectInspector valueObjectInspector = getLazyBinaryObjectInspectorFromTypeInfo(mapTypeInfo.getMapValueTypeInfo());
                    result = LazyBinaryObjectInspectorFactory.getLazyBinaryMapObjectInspector(keyObjectInspector, valueObjectInspector);
                    break;
                }
                case STRUCT: {
                    final StructTypeInfo structTypeInfo = (StructTypeInfo)typeInfo;
                    final List<String> fieldNames = structTypeInfo.getAllStructFieldNames();
                    final List<TypeInfo> fieldTypeInfos = structTypeInfo.getAllStructFieldTypeInfos();
                    final List<ObjectInspector> fieldObjectInspectors = new ArrayList<ObjectInspector>(fieldTypeInfos.size());
                    for (int i = 0; i < fieldTypeInfos.size(); ++i) {
                        fieldObjectInspectors.add(getLazyBinaryObjectInspectorFromTypeInfo(fieldTypeInfos.get(i)));
                    }
                    result = LazyBinaryObjectInspectorFactory.getLazyBinaryStructObjectInspector(fieldNames, fieldObjectInspectors);
                    break;
                }
                case UNION: {
                    final UnionTypeInfo unionTypeInfo = (UnionTypeInfo)typeInfo;
                    final List<TypeInfo> fieldTypeInfos2 = unionTypeInfo.getAllUnionObjectTypeInfos();
                    final List<ObjectInspector> fieldObjectInspectors2 = new ArrayList<ObjectInspector>(fieldTypeInfos2.size());
                    for (int j = 0; j < fieldTypeInfos2.size(); ++j) {
                        fieldObjectInspectors2.add(getLazyBinaryObjectInspectorFromTypeInfo(fieldTypeInfos2.get(j)));
                    }
                    result = LazyBinaryObjectInspectorFactory.getLazyBinaryUnionObjectInspector(fieldObjectInspectors2);
                    break;
                }
                default: {
                    result = null;
                    break;
                }
            }
            final ObjectInspector prev = LazyBinaryUtils.cachedLazyBinaryObjectInspector.putIfAbsent(typeInfo, result);
            if (prev != null) {
                result = prev;
            }
        }
        return result;
    }
    
    private LazyBinaryUtils() {
    }
    
    static {
        threadLocalVInt = new ThreadLocal<VInt>() {
            @Override
            protected VInt initialValue() {
                return new VInt();
            }
        };
        LazyBinaryUtils.vLongBytesThreadLocal = new ThreadLocal<byte[]>() {
            public byte[] initialValue() {
                return new byte[9];
            }
        };
        LazyBinaryUtils.cachedLazyBinaryObjectInspector = new ConcurrentHashMap<TypeInfo, ObjectInspector>();
    }
    
    public static class RecordInfo
    {
        public byte elementOffset;
        public int elementSize;
        
        public RecordInfo() {
            this.elementOffset = 0;
            this.elementSize = 0;
        }
        
        @Override
        public String toString() {
            return "(" + this.elementOffset + ", " + this.elementSize + ")";
        }
    }
    
    public static class VLong
    {
        public long value;
        public byte length;
        
        public VLong() {
            this.value = 0L;
            this.length = 0;
        }
    }
    
    public static class VInt
    {
        public int value;
        public byte length;
        
        public VInt() {
            this.value = 0;
            this.length = 0;
        }
    }
}
