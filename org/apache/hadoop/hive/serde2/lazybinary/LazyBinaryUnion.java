// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.SerDeStatsStruct;
import org.apache.hadoop.hive.serde2.lazybinary.objectinspector.LazyBinaryUnionObjectInspector;

public class LazyBinaryUnion extends LazyBinaryNonPrimitive<LazyBinaryUnionObjectInspector> implements SerDeStatsStruct
{
    private static Log LOG;
    boolean parsed;
    long serializedSize;
    LazyBinaryObject field;
    boolean fieldInited;
    int fieldStart;
    int fieldLength;
    byte tag;
    final LazyBinaryUtils.VInt vInt;
    LazyBinaryUtils.RecordInfo recordInfo;
    boolean missingFieldWarned;
    boolean extraFieldWarned;
    Object cachedObject;
    
    protected LazyBinaryUnion(final LazyBinaryUnionObjectInspector oi) {
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
        this.fieldInited = false;
        this.field = null;
        this.cachedObject = null;
    }
    
    private void parse() {
        final LazyBinaryUnionObjectInspector uoi = (LazyBinaryUnionObjectInspector)this.oi;
        final int unionByteEnd = this.start + this.length;
        final byte[] byteArr = this.bytes.getData();
        final int tagEnd = this.start + 1;
        this.tag = byteArr[this.start];
        this.field = LazyBinaryFactory.createLazyBinaryObject(uoi.getObjectInspectors().get(this.tag));
        LazyBinaryUtils.checkObjectByteInfo(uoi.getObjectInspectors().get(this.tag), byteArr, tagEnd, this.recordInfo, this.vInt);
        this.fieldStart = tagEnd + this.recordInfo.elementOffset;
        this.fieldLength = this.recordInfo.elementSize;
        if (!this.extraFieldWarned && this.fieldStart + this.fieldLength < unionByteEnd) {
            this.extraFieldWarned = true;
            LazyBinaryUnion.LOG.warn("Extra bytes detected at the end of the row! Ignoring similar problems.");
        }
        if (!this.missingFieldWarned && this.fieldStart + this.fieldLength > unionByteEnd) {
            this.missingFieldWarned = true;
            LazyBinaryUnion.LOG.info("Missing fields! Expected 1 fields but only got " + this.field + "! Ignoring similar problems.");
        }
        this.parsed = true;
    }
    
    public Object getField() {
        if (!this.parsed) {
            this.parse();
        }
        if (this.cachedObject == null) {
            return this.uncheckedGetField();
        }
        return this.cachedObject;
    }
    
    private Object uncheckedGetField() {
        if (!this.fieldInited) {
            this.fieldInited = true;
            this.field.init(this.bytes, this.fieldStart, this.fieldLength);
        }
        this.cachedObject = this.field.getObject();
        return this.field.getObject();
    }
    
    @Override
    public Object getObject() {
        return this;
    }
    
    @Override
    public long getRawDataSerializedSize() {
        return this.serializedSize;
    }
    
    public byte getTag() {
        if (!this.parsed) {
            this.parse();
        }
        return this.tag;
    }
    
    static {
        LazyBinaryUnion.LOG = LogFactory.getLog(LazyBinaryUnion.class.getName());
    }
}
