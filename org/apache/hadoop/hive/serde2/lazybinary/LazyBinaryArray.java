// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import java.util.List;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import java.util.Arrays;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import java.util.ArrayList;
import org.apache.hadoop.hive.serde2.lazybinary.objectinspector.LazyBinaryListObjectInspector;

public class LazyBinaryArray extends LazyBinaryNonPrimitive<LazyBinaryListObjectInspector>
{
    boolean parsed;
    int arraySize;
    int[] elementStart;
    int[] elementLength;
    boolean[] elementInited;
    boolean[] elementIsNull;
    LazyBinaryObject[] arrayElements;
    LazyBinaryUtils.VInt vInt;
    LazyBinaryUtils.RecordInfo recordInfo;
    ArrayList<Object> cachedList;
    
    protected LazyBinaryArray(final LazyBinaryListObjectInspector oi) {
        super(oi);
        this.parsed = false;
        this.arraySize = 0;
        this.vInt = new LazyBinaryUtils.VInt();
        this.recordInfo = new LazyBinaryUtils.RecordInfo();
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        super.init(bytes, start, length);
        this.parsed = false;
    }
    
    private void adjustArraySize(final int newSize) {
        if (this.elementStart == null || this.elementStart.length < newSize) {
            this.elementStart = new int[newSize];
            this.elementLength = new int[newSize];
            this.elementInited = new boolean[newSize];
            this.elementIsNull = new boolean[newSize];
            this.arrayElements = new LazyBinaryObject[newSize];
        }
    }
    
    private void parse() {
        final byte[] bytes = this.bytes.getData();
        LazyBinaryUtils.readVInt(bytes, this.start, this.vInt);
        this.arraySize = this.vInt.value;
        if (0 == this.arraySize) {
            this.parsed = true;
            return;
        }
        this.adjustArraySize(this.arraySize);
        int nullByteCur;
        final int arryByteStart = nullByteCur = this.start + this.vInt.length;
        int lastElementByteEnd;
        final int nullByteEnd = lastElementByteEnd = arryByteStart + (this.arraySize + 7) / 8;
        final ObjectInspector listEleObjectInspector = ((ListObjectInspector)this.oi).getListElementObjectInspector();
        for (int i = 0; i < this.arraySize; ++i) {
            this.elementIsNull[i] = true;
            if ((bytes[nullByteCur] & 1 << i % 8) != 0x0) {
                this.elementIsNull[i] = false;
                LazyBinaryUtils.checkObjectByteInfo(listEleObjectInspector, bytes, lastElementByteEnd, this.recordInfo, this.vInt);
                this.elementStart[i] = lastElementByteEnd + this.recordInfo.elementOffset;
                this.elementLength[i] = this.recordInfo.elementSize;
                lastElementByteEnd = this.elementStart[i] + this.elementLength[i];
            }
            if (7 == i % 8) {
                ++nullByteCur;
            }
        }
        Arrays.fill(this.elementInited, 0, this.arraySize, false);
        this.parsed = true;
    }
    
    public Object getListElementObject(final int index) {
        if (!this.parsed) {
            this.parse();
        }
        if (index < 0 || index >= this.arraySize) {
            return null;
        }
        return this.uncheckedGetElement(index);
    }
    
    private Object uncheckedGetElement(final int index) {
        if (this.elementIsNull[index]) {
            return null;
        }
        if (!this.elementInited[index]) {
            this.elementInited[index] = true;
            if (this.arrayElements[index] == null) {
                this.arrayElements[index] = LazyBinaryFactory.createLazyBinaryObject(((LazyBinaryListObjectInspector)this.oi).getListElementObjectInspector());
            }
            this.arrayElements[index].init(this.bytes, this.elementStart[index], this.elementLength[index]);
        }
        return this.arrayElements[index].getObject();
    }
    
    public int getListLength() {
        if (!this.parsed) {
            this.parse();
        }
        return this.arraySize;
    }
    
    public List<Object> getList() {
        if (!this.parsed) {
            this.parse();
        }
        if (this.cachedList == null) {
            this.cachedList = new ArrayList<Object>(this.arraySize);
        }
        else {
            this.cachedList.clear();
        }
        for (int index = 0; index < this.arraySize; ++index) {
            this.cachedList.add(this.uncheckedGetElement(index));
        }
        return this.cachedList;
    }
}
