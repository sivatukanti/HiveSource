// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import org.apache.commons.logging.LogFactory;
import java.util.Map;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import java.util.Arrays;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import java.util.LinkedHashMap;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.lazybinary.objectinspector.LazyBinaryMapObjectInspector;

public class LazyBinaryMap extends LazyBinaryNonPrimitive<LazyBinaryMapObjectInspector>
{
    private static Log LOG;
    boolean parsed;
    int mapSize;
    int[] keyStart;
    int[] keyLength;
    int[] valueStart;
    int[] valueLength;
    boolean[] keyInited;
    boolean[] valueInited;
    boolean[] keyIsNull;
    boolean[] valueIsNull;
    LazyBinaryPrimitive<?, ?>[] keyObjects;
    LazyBinaryObject[] valueObjects;
    boolean nullMapKey;
    LazyBinaryUtils.VInt vInt;
    LazyBinaryUtils.RecordInfo recordInfo;
    LinkedHashMap<Object, Object> cachedMap;
    
    protected LazyBinaryMap(final LazyBinaryMapObjectInspector oi) {
        super(oi);
        this.mapSize = 0;
        this.nullMapKey = false;
        this.vInt = new LazyBinaryUtils.VInt();
        this.recordInfo = new LazyBinaryUtils.RecordInfo();
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        super.init(bytes, start, length);
        this.parsed = false;
    }
    
    protected void adjustArraySize(final int newSize) {
        if (this.keyStart == null || this.keyStart.length < newSize) {
            this.keyStart = new int[newSize];
            this.keyLength = new int[newSize];
            this.valueStart = new int[newSize];
            this.valueLength = new int[newSize];
            this.keyInited = new boolean[newSize];
            this.keyIsNull = new boolean[newSize];
            this.valueInited = new boolean[newSize];
            this.valueIsNull = new boolean[newSize];
            this.keyObjects = (LazyBinaryPrimitive<?, ?>[])new LazyBinaryPrimitive[newSize];
            this.valueObjects = new LazyBinaryObject[newSize];
        }
    }
    
    private void parse() {
        final byte[] bytes = this.bytes.getData();
        LazyBinaryUtils.readVInt(bytes, this.start, this.vInt);
        this.mapSize = this.vInt.value;
        if (0 == this.mapSize) {
            this.parsed = true;
            return;
        }
        this.adjustArraySize(this.mapSize);
        int nullByteCur;
        final int mapByteStart = nullByteCur = this.start + this.vInt.length;
        int lastElementByteEnd;
        final int nullByteEnd = lastElementByteEnd = mapByteStart + (this.mapSize * 2 + 7) / 8;
        for (int i = 0; i < this.mapSize; ++i) {
            this.keyIsNull[i] = true;
            if ((bytes[nullByteCur] & 1 << i * 2 % 8) != 0x0) {
                this.keyIsNull[i] = false;
                LazyBinaryUtils.checkObjectByteInfo(((MapObjectInspector)this.oi).getMapKeyObjectInspector(), bytes, lastElementByteEnd, this.recordInfo, this.vInt);
                this.keyStart[i] = lastElementByteEnd + this.recordInfo.elementOffset;
                this.keyLength[i] = this.recordInfo.elementSize;
                lastElementByteEnd = this.keyStart[i] + this.keyLength[i];
            }
            else if (!this.nullMapKey) {
                this.nullMapKey = true;
                LazyBinaryMap.LOG.warn("Null map key encountered! Ignoring similar problems.");
            }
            this.valueIsNull[i] = true;
            if ((bytes[nullByteCur] & 1 << (i * 2 + 1) % 8) != 0x0) {
                this.valueIsNull[i] = false;
                LazyBinaryUtils.checkObjectByteInfo(((MapObjectInspector)this.oi).getMapValueObjectInspector(), bytes, lastElementByteEnd, this.recordInfo, this.vInt);
                this.valueStart[i] = lastElementByteEnd + this.recordInfo.elementOffset;
                this.valueLength[i] = this.recordInfo.elementSize;
                lastElementByteEnd = this.valueStart[i] + this.valueLength[i];
            }
            if (3 == i % 4) {
                ++nullByteCur;
            }
        }
        Arrays.fill(this.keyInited, 0, this.mapSize, false);
        Arrays.fill(this.valueInited, 0, this.mapSize, false);
        this.parsed = true;
    }
    
    private LazyBinaryObject uncheckedGetValue(final int index) {
        if (this.valueIsNull[index]) {
            return null;
        }
        if (!this.valueInited[index]) {
            this.valueInited[index] = true;
            if (this.valueObjects[index] == null) {
                this.valueObjects[index] = LazyBinaryFactory.createLazyBinaryObject(((MapObjectInspector)this.oi).getMapValueObjectInspector());
            }
            this.valueObjects[index].init(this.bytes, this.valueStart[index], this.valueLength[index]);
        }
        return this.valueObjects[index];
    }
    
    public Object getMapValueElement(final Object key) {
        if (!this.parsed) {
            this.parse();
        }
        for (int i = 0; i < this.mapSize; ++i) {
            final LazyBinaryPrimitive<?, ?> lazyKeyI = this.uncheckedGetKey(i);
            if (lazyKeyI != null) {
                final Object keyI = lazyKeyI.getWritableObject();
                if (keyI != null) {
                    if (keyI.equals(key)) {
                        final LazyBinaryObject v = this.uncheckedGetValue(i);
                        return (v == null) ? v : v.getObject();
                    }
                }
            }
        }
        return null;
    }
    
    private LazyBinaryPrimitive<?, ?> uncheckedGetKey(final int index) {
        if (this.keyIsNull[index]) {
            return null;
        }
        if (!this.keyInited[index]) {
            this.keyInited[index] = true;
            if (this.keyObjects[index] == null) {
                this.keyObjects[index] = LazyBinaryFactory.createLazyBinaryPrimitiveClass((PrimitiveObjectInspector)((MapObjectInspector)this.oi).getMapKeyObjectInspector());
            }
            this.keyObjects[index].init(this.bytes, this.keyStart[index], this.keyLength[index]);
        }
        return this.keyObjects[index];
    }
    
    public Map<Object, Object> getMap() {
        if (!this.parsed) {
            this.parse();
        }
        if (this.cachedMap == null) {
            this.cachedMap = new LinkedHashMap<Object, Object>();
        }
        else {
            this.cachedMap.clear();
        }
        for (int i = 0; i < this.mapSize; ++i) {
            final LazyBinaryPrimitive<?, ?> lazyKey = this.uncheckedGetKey(i);
            if (lazyKey != null) {
                final Object key = lazyKey.getObject();
                if (key != null && !this.cachedMap.containsKey(key)) {
                    final LazyBinaryObject lazyValue = this.uncheckedGetValue(i);
                    final Object value = (lazyValue == null) ? null : lazyValue.getObject();
                    this.cachedMap.put(key, value);
                }
            }
        }
        return this.cachedMap;
    }
    
    public int getMapSize() {
        if (!this.parsed) {
            this.parse();
        }
        return this.mapSize;
    }
    
    static {
        LazyBinaryMap.LOG = LogFactory.getLog(LazyBinaryMap.class.getName());
    }
}
