// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import org.apache.hadoop.io.BinaryComparable;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import java.util.List;
import java.util.Arrays;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import java.util.ArrayList;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.SerDeStatsStruct;
import org.apache.hadoop.hive.serde2.StructObject;
import org.apache.hadoop.hive.serde2.lazybinary.objectinspector.LazyBinaryStructObjectInspector;

public class LazyBinaryStruct extends LazyBinaryNonPrimitive<LazyBinaryStructObjectInspector> implements StructObject, SerDeStatsStruct
{
    private static Log LOG;
    boolean parsed;
    long serializedSize;
    LazyBinaryObject[] fields;
    boolean[] fieldInited;
    boolean[] fieldIsNull;
    int[] fieldStart;
    int[] fieldLength;
    final LazyBinaryUtils.VInt vInt;
    final LazyBinaryUtils.RecordInfo recordInfo;
    boolean missingFieldWarned;
    boolean extraFieldWarned;
    ArrayList<Object> cachedList;
    
    protected LazyBinaryStruct(final LazyBinaryStructObjectInspector oi) {
        super(oi);
        this.vInt = new LazyBinaryUtils.VInt();
        this.recordInfo = new LazyBinaryUtils.RecordInfo();
        this.missingFieldWarned = false;
        this.extraFieldWarned = false;
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        super.init(bytes, start, length);
        this.parsed = false;
        this.serializedSize = length;
    }
    
    private void parse() {
        final List<? extends StructField> fieldRefs = ((StructObjectInspector)this.oi).getAllStructFieldRefs();
        if (this.fields == null) {
            this.fields = new LazyBinaryObject[fieldRefs.size()];
            for (int i = 0; i < this.fields.length; ++i) {
                final ObjectInspector insp = ((StructField)fieldRefs.get(i)).getFieldObjectInspector();
                this.fields[i] = ((insp == null) ? null : LazyBinaryFactory.createLazyBinaryObject(insp));
            }
            this.fieldInited = new boolean[this.fields.length];
            this.fieldIsNull = new boolean[this.fields.length];
            this.fieldStart = new int[this.fields.length];
            this.fieldLength = new int[this.fields.length];
        }
        int fieldId = 0;
        final int structByteEnd = this.start + this.length;
        final byte[] bytes = this.bytes.getData();
        byte nullByte = bytes[this.start];
        int lastFieldByteEnd = this.start + 1;
        for (int j = 0; j < this.fields.length; ++j) {
            this.fieldIsNull[j] = true;
            if ((nullByte & 1 << j % 8) != 0x0) {
                this.fieldIsNull[j] = false;
                LazyBinaryUtils.checkObjectByteInfo(((StructField)fieldRefs.get(j)).getFieldObjectInspector(), bytes, lastFieldByteEnd, this.recordInfo, this.vInt);
                this.fieldStart[j] = lastFieldByteEnd + this.recordInfo.elementOffset;
                this.fieldLength[j] = this.recordInfo.elementSize;
                lastFieldByteEnd = this.fieldStart[j] + this.fieldLength[j];
            }
            if (lastFieldByteEnd <= structByteEnd) {
                ++fieldId;
            }
            if (7 == j % 8) {
                if (lastFieldByteEnd < structByteEnd) {
                    nullByte = bytes[lastFieldByteEnd];
                    ++lastFieldByteEnd;
                }
                else {
                    nullByte = 0;
                    ++lastFieldByteEnd;
                }
            }
        }
        if (!this.extraFieldWarned && lastFieldByteEnd < structByteEnd) {
            this.extraFieldWarned = true;
            LazyBinaryStruct.LOG.warn("Extra bytes detected at the end of the row! Last field end " + lastFieldByteEnd + " and serialize buffer end " + structByteEnd + ". " + "Ignoring similar problems.");
        }
        if (!this.missingFieldWarned && lastFieldByteEnd > structByteEnd) {
            this.missingFieldWarned = true;
            LazyBinaryStruct.LOG.info("Missing fields! Expected " + this.fields.length + " fields but " + "only got " + fieldId + "! " + "Last field end " + lastFieldByteEnd + " and serialize buffer end " + structByteEnd + ". " + "Ignoring similar problems.");
        }
        Arrays.fill(this.fieldInited, false);
        this.parsed = true;
    }
    
    @Override
    public Object getField(final int fieldID) {
        if (!this.parsed) {
            this.parse();
        }
        return this.uncheckedGetField(fieldID);
    }
    
    private Object uncheckedGetField(final int fieldID) {
        if (this.fieldIsNull[fieldID]) {
            return null;
        }
        if (!this.fieldInited[fieldID]) {
            this.fieldInited[fieldID] = true;
            this.fields[fieldID].init(this.bytes, this.fieldStart[fieldID], this.fieldLength[fieldID]);
        }
        return this.fields[fieldID].getObject();
    }
    
    @Override
    public ArrayList<Object> getFieldsAsList() {
        if (!this.parsed) {
            this.parse();
        }
        if (this.cachedList == null) {
            this.cachedList = new ArrayList<Object>(this.fields.length);
            for (int i = 0; i < this.fields.length; ++i) {
                this.cachedList.add(this.uncheckedGetField(i));
            }
        }
        else {
            assert this.fields.length == this.cachedList.size();
            for (int i = 0; i < this.fields.length; ++i) {
                this.cachedList.set(i, this.uncheckedGetField(i));
            }
        }
        return this.cachedList;
    }
    
    @Override
    public Object getObject() {
        return this;
    }
    
    @Override
    public long getRawDataSerializedSize() {
        return this.serializedSize;
    }
    
    static {
        LazyBinaryStruct.LOG = LogFactory.getLog(LazyBinaryStruct.class.getName());
    }
    
    public static final class SingleFieldGetter
    {
        private final LazyBinaryUtils.VInt vInt;
        private final LazyBinaryStructObjectInspector soi;
        private final int fieldIndex;
        private final LazyBinaryUtils.RecordInfo recordInfo;
        private byte[] fieldBytes;
        private int fieldStart;
        private int fieldLength;
        
        public SingleFieldGetter(final LazyBinaryStructObjectInspector soi, final int fieldIndex) {
            this.vInt = new LazyBinaryUtils.VInt();
            this.recordInfo = new LazyBinaryUtils.RecordInfo();
            this.soi = soi;
            this.fieldIndex = fieldIndex;
        }
        
        public void init(final BinaryComparable src) {
            final List<? extends StructField> fieldRefs = this.soi.getAllStructFieldRefs();
            this.fieldBytes = src.getBytes();
            final int length = src.getLength();
            byte nullByte = this.fieldBytes[0];
            int lastFieldByteEnd = 1;
            int fieldStart = -1;
            int fieldLength = -1;
            for (int i = 0; i <= this.fieldIndex; ++i) {
                if ((nullByte & 1 << i % 8) != 0x0) {
                    LazyBinaryUtils.checkObjectByteInfo(((StructField)fieldRefs.get(i)).getFieldObjectInspector(), this.fieldBytes, lastFieldByteEnd, this.recordInfo, this.vInt);
                    fieldStart = lastFieldByteEnd + this.recordInfo.elementOffset;
                    fieldLength = this.recordInfo.elementSize;
                    lastFieldByteEnd = fieldStart + fieldLength;
                }
                else {
                    fieldLength = (fieldStart = -1);
                }
                if (7 == i % 8) {
                    nullByte = (byte)((lastFieldByteEnd < length) ? this.fieldBytes[lastFieldByteEnd] : 0);
                    ++lastFieldByteEnd;
                }
            }
        }
        
        public short getShort() {
            assert 2 == this.fieldLength;
            return LazyBinaryUtils.byteArrayToShort(this.fieldBytes, this.fieldStart);
        }
    }
}
