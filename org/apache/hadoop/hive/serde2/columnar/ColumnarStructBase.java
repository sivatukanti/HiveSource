// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.columnar;

import java.io.IOException;
import org.apache.hadoop.hive.serde2.lazy.LazyObjectBase;
import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import java.util.List;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import java.util.ArrayList;
import org.apache.hadoop.hive.serde2.SerDeStatsStruct;
import org.apache.hadoop.hive.serde2.StructObject;

public abstract class ColumnarStructBase implements StructObject, SerDeStatsStruct
{
    protected int[] prjColIDs;
    private FieldInfo[] fieldInfoList;
    private ArrayList<Object> cachedList;
    
    public ColumnarStructBase(final ObjectInspector oi, final List<Integer> notSkippedColumnIDs) {
        this.prjColIDs = null;
        this.fieldInfoList = null;
        final List<? extends StructField> fieldRefs = ((StructObjectInspector)oi).getAllStructFieldRefs();
        final int num = fieldRefs.size();
        this.fieldInfoList = new FieldInfo[num];
        for (int i = 0; i < num; ++i) {
            final ObjectInspector foi = ((StructField)fieldRefs.get(i)).getFieldObjectInspector();
            this.fieldInfoList[i] = new FieldInfo(this.createLazyObjectBase(foi), !notSkippedColumnIDs.contains(i), foi);
        }
        final int min = (notSkippedColumnIDs.size() > num) ? num : notSkippedColumnIDs.size();
        this.prjColIDs = new int[min];
        int j = 0;
        int index = 0;
        while (j < notSkippedColumnIDs.size()) {
            final int readCol = notSkippedColumnIDs.get(j);
            if (readCol < num) {
                this.prjColIDs[index] = readCol;
                ++index;
            }
            ++j;
        }
    }
    
    @Override
    public Object getField(final int fieldID) {
        return this.fieldInfoList[fieldID].uncheckedGetField();
    }
    
    protected abstract int getLength(final ObjectInspector p0, final ByteArrayRef p1, final int p2, final int p3);
    
    protected abstract LazyObjectBase createLazyObjectBase(final ObjectInspector p0);
    
    public void init(final BytesRefArrayWritable cols) {
        for (int i = 0; i < this.prjColIDs.length; ++i) {
            final int fieldIndex = this.prjColIDs[i];
            if (fieldIndex < cols.size()) {
                this.fieldInfoList[fieldIndex].init(cols.unCheckedGet(fieldIndex));
            }
            else {
                this.fieldInfoList[fieldIndex].init(null);
            }
        }
    }
    
    @Override
    public ArrayList<Object> getFieldsAsList() {
        if (this.cachedList == null) {
            this.cachedList = new ArrayList<Object>();
        }
        else {
            this.cachedList.clear();
        }
        for (int i = 0; i < this.fieldInfoList.length; ++i) {
            this.cachedList.add(this.fieldInfoList[i].uncheckedGetField());
        }
        return this.cachedList;
    }
    
    @Override
    public long getRawDataSerializedSize() {
        long serializedSize = 0L;
        for (int i = 0; i < this.fieldInfoList.length; ++i) {
            serializedSize += this.fieldInfoList[i].getSerializedSize();
        }
        return serializedSize;
    }
    
    class FieldInfo
    {
        LazyObjectBase field;
        ByteArrayRef cachedByteArrayRef;
        BytesRefWritable rawBytesField;
        boolean inited;
        boolean fieldSkipped;
        ObjectInspector objectInspector;
        
        public FieldInfo(final LazyObjectBase lazyObject, final boolean fieldSkipped, final ObjectInspector oi) {
            this.field = lazyObject;
            this.cachedByteArrayRef = new ByteArrayRef();
            this.objectInspector = oi;
            if (fieldSkipped) {
                this.fieldSkipped = true;
                this.inited = true;
            }
            else {
                this.inited = false;
            }
        }
        
        public void init(final BytesRefWritable col) {
            if (col != null) {
                this.rawBytesField = col;
                this.inited = false;
                this.fieldSkipped = false;
            }
            else {
                this.fieldSkipped = true;
            }
        }
        
        public long getSerializedSize() {
            if (this.rawBytesField == null) {
                return 0L;
            }
            return this.rawBytesField.getLength();
        }
        
        protected Object uncheckedGetField() {
            if (this.fieldSkipped) {
                return null;
            }
            if (!this.inited) {
                try {
                    this.cachedByteArrayRef.setData(this.rawBytesField.getData());
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
                this.inited = true;
                final int byteLength = ColumnarStructBase.this.getLength(this.objectInspector, this.cachedByteArrayRef, this.rawBytesField.getStart(), this.rawBytesField.getLength());
                if (byteLength == -1) {
                    return null;
                }
                this.field.init(this.cachedByteArrayRef, this.rawBytesField.getStart(), byteLength);
                return this.field.getObject();
            }
            else {
                if (ColumnarStructBase.this.getLength(this.objectInspector, this.cachedByteArrayRef, this.rawBytesField.getStart(), this.rawBytesField.getLength()) == -1) {
                    return null;
                }
                return this.field.getObject();
            }
        }
    }
}
