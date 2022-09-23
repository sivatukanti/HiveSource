// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.LazyListObjectInspector;

public class LazyArray extends LazyNonPrimitive<LazyListObjectInspector>
{
    boolean parsed;
    int arrayLength;
    int[] startPosition;
    boolean[] elementInited;
    LazyObject[] arrayElements;
    ArrayList<Object> cachedList;
    
    protected LazyArray(final LazyListObjectInspector oi) {
        super(oi);
        this.parsed = false;
        this.arrayLength = 0;
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        super.init(bytes, start, length);
        this.parsed = false;
        this.cachedList = null;
    }
    
    private void enlargeArrays() {
        if (this.startPosition == null) {
            final int initialSize = 2;
            this.startPosition = new int[initialSize];
            this.arrayElements = new LazyObject[initialSize];
            this.elementInited = new boolean[initialSize];
        }
        else {
            this.startPosition = Arrays.copyOf(this.startPosition, this.startPosition.length * 2);
            this.arrayElements = Arrays.copyOf(this.arrayElements, this.arrayElements.length * 2);
            this.elementInited = Arrays.copyOf(this.elementInited, this.elementInited.length * 2);
        }
    }
    
    private void parse() {
        this.parsed = true;
        final byte separator = ((LazyListObjectInspector)this.oi).getSeparator();
        final boolean isEscaped = ((LazyListObjectInspector)this.oi).isEscaped();
        final byte escapeChar = ((LazyListObjectInspector)this.oi).getEscapeChar();
        if (this.length == 0) {
            this.arrayLength = 0;
            return;
        }
        final byte[] bytes = this.bytes.getData();
        this.arrayLength = 0;
        final int arrayByteEnd = this.start + this.length;
        int elementByteBegin = this.start;
        int elementByteEnd = this.start;
        while (elementByteEnd <= arrayByteEnd) {
            if (elementByteEnd == arrayByteEnd || bytes[elementByteEnd] == separator) {
                if (this.startPosition == null || this.arrayLength + 1 == this.startPosition.length) {
                    this.enlargeArrays();
                }
                this.startPosition[this.arrayLength] = elementByteBegin;
                ++this.arrayLength;
                elementByteBegin = elementByteEnd + 1;
                ++elementByteEnd;
            }
            else if (isEscaped && bytes[elementByteEnd] == escapeChar && elementByteEnd + 1 < arrayByteEnd) {
                elementByteEnd += 2;
            }
            else {
                ++elementByteEnd;
            }
        }
        this.startPosition[this.arrayLength] = arrayByteEnd + 1;
        if (this.arrayLength > 0) {
            Arrays.fill(this.elementInited, 0, this.arrayLength, false);
        }
    }
    
    public Object getListElementObject(final int index) {
        if (!this.parsed) {
            this.parse();
        }
        if (index < 0 || index >= this.arrayLength) {
            return null;
        }
        return this.uncheckedGetElement(index);
    }
    
    private Object uncheckedGetElement(final int index) {
        if (this.elementInited[index]) {
            return this.arrayElements[index].getObject();
        }
        this.elementInited[index] = true;
        final int elementStart = this.startPosition[index];
        final int elementLength = this.startPosition[index + 1] - elementStart - 1;
        if (this.arrayElements[index] == null) {
            this.arrayElements[index] = LazyFactory.createLazyObject(((LazyListObjectInspector)this.oi).getListElementObjectInspector());
        }
        if (this.isNull(((LazyListObjectInspector)this.oi).getNullSequence(), this.bytes, elementStart, elementLength)) {
            this.arrayElements[index].setNull();
        }
        else {
            this.arrayElements[index].init(this.bytes, elementStart, elementLength);
        }
        return this.arrayElements[index].getObject();
    }
    
    public int getListLength() {
        if (!this.parsed) {
            this.parse();
        }
        return this.arrayLength;
    }
    
    public List<Object> getList() {
        if (!this.parsed) {
            this.parse();
        }
        if (this.arrayLength == -1) {
            return null;
        }
        if (this.cachedList != null) {
            return this.cachedList;
        }
        this.cachedList = new ArrayList<Object>(this.arrayLength);
        for (int index = 0; index < this.arrayLength; ++index) {
            this.cachedList.add(this.uncheckedGetElement(index));
        }
        return this.cachedList;
    }
}
