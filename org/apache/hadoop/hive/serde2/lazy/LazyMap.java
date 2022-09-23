// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import org.apache.commons.logging.LogFactory;
import java.util.Map;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.io.Text;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Arrays;
import java.util.LinkedHashMap;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.LazyMapObjectInspector;

public class LazyMap extends LazyNonPrimitive<LazyMapObjectInspector>
{
    public static final Log LOG;
    boolean parsed;
    int mapSize;
    int[] keyStart;
    int[] keyEnd;
    int[] valueLength;
    LazyPrimitive<?, ?>[] keyObjects;
    boolean[] keyInited;
    LazyObject[] valueObjects;
    boolean[] valueInited;
    protected LinkedHashMap<Object, Object> cachedMap;
    
    protected LazyMap(final LazyMapObjectInspector oi) {
        super(oi);
        this.parsed = false;
        this.mapSize = 0;
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        super.init(bytes, start, length);
        this.parsed = false;
        this.cachedMap = null;
        this.keyStart = null;
    }
    
    protected void enlargeArrays() {
        if (this.keyStart == null) {
            final int initialSize = 2;
            this.keyStart = new int[initialSize];
            this.keyEnd = new int[initialSize];
            this.valueLength = new int[initialSize];
            this.keyObjects = (LazyPrimitive<?, ?>[])new LazyPrimitive[initialSize];
            this.valueObjects = new LazyObject[initialSize];
            this.keyInited = new boolean[initialSize];
            this.valueInited = new boolean[initialSize];
        }
        else {
            this.keyStart = Arrays.copyOf(this.keyStart, this.keyStart.length * 2);
            this.keyEnd = Arrays.copyOf(this.keyEnd, this.keyEnd.length * 2);
            this.valueLength = Arrays.copyOf(this.valueLength, this.valueLength.length * 2);
            this.keyObjects = Arrays.copyOf(this.keyObjects, this.keyObjects.length * 2);
            this.valueObjects = Arrays.copyOf(this.valueObjects, this.valueObjects.length * 2);
            this.keyInited = Arrays.copyOf(this.keyInited, this.keyInited.length * 2);
            this.valueInited = Arrays.copyOf(this.valueInited, this.valueInited.length * 2);
        }
    }
    
    private void parse() {
        this.parsed = true;
        final byte itemSeparator = ((LazyMapObjectInspector)this.oi).getItemSeparator();
        final byte keyValueSeparator = ((LazyMapObjectInspector)this.oi).getKeyValueSeparator();
        final boolean isEscaped = ((LazyMapObjectInspector)this.oi).isEscaped();
        final byte escapeChar = ((LazyMapObjectInspector)this.oi).getEscapeChar();
        if (this.length == 0) {
            this.mapSize = 0;
            return;
        }
        this.mapSize = 0;
        final int arrayByteEnd = this.start + this.length;
        int elementByteBegin = this.start;
        int keyValueSeparatorPosition = -1;
        int elementByteEnd = this.start;
        final byte[] bytes = this.bytes.getData();
        final Set<Object> keySet = new LinkedHashSet<Object>();
        while (elementByteEnd <= arrayByteEnd) {
            if (elementByteEnd == arrayByteEnd || bytes[elementByteEnd] == itemSeparator) {
                if (this.keyStart == null || this.mapSize + 1 == this.keyStart.length) {
                    this.enlargeArrays();
                }
                this.keyStart[this.mapSize] = elementByteBegin;
                this.keyEnd[this.mapSize] = ((keyValueSeparatorPosition == -1) ? elementByteEnd : keyValueSeparatorPosition);
                this.valueLength[this.mapSize] = elementByteEnd - (this.keyEnd[this.mapSize] + 1);
                final LazyPrimitive<?, ?> lazyKey = this.uncheckedGetKey(this.mapSize);
                if (lazyKey == null) {
                    LazyMap.LOG.warn("skipped empty entry or entry with empty key in the representation of column with MAP type.");
                    this.keyInited[this.mapSize] = false;
                }
                else {
                    final Object key = lazyKey.getObject();
                    if (!keySet.contains(key)) {
                        ++this.mapSize;
                        keySet.add(key);
                    }
                    else {
                        this.keyInited[this.mapSize] = false;
                    }
                }
                keyValueSeparatorPosition = -1;
                elementByteBegin = elementByteEnd + 1;
                ++elementByteEnd;
            }
            else {
                if (keyValueSeparatorPosition == -1 && bytes[elementByteEnd] == keyValueSeparator) {
                    keyValueSeparatorPosition = elementByteEnd;
                }
                if (isEscaped && bytes[elementByteEnd] == escapeChar && elementByteEnd + 1 < arrayByteEnd) {
                    elementByteEnd += 2;
                }
                else {
                    ++elementByteEnd;
                }
            }
        }
        this.keyStart[this.mapSize] = arrayByteEnd + 1;
        if (this.mapSize > 0) {
            Arrays.fill(this.valueInited, 0, this.mapSize, false);
        }
    }
    
    public Object getMapValueElement(final Object key) {
        if (!this.parsed) {
            this.parse();
        }
        for (int i = 0; i < this.mapSize; ++i) {
            final LazyPrimitive<?, ?> lazyKeyI = this.uncheckedGetKey(i);
            if (lazyKeyI != null) {
                final Object keyI = lazyKeyI.getWritableObject();
                if (keyI != null) {
                    if (keyI.equals(key)) {
                        return this.uncheckedGetValue(i);
                    }
                }
            }
        }
        return null;
    }
    
    private Object uncheckedGetValue(final int index) {
        if (this.valueInited[index]) {
            return this.valueObjects[index].getObject();
        }
        this.valueInited[index] = true;
        final Text nullSequence = ((LazyMapObjectInspector)this.oi).getNullSequence();
        final int valueIBegin = this.keyEnd[index] + 1;
        final int valueILength = this.valueLength[index];
        if (this.valueObjects[index] == null) {
            this.valueObjects[index] = LazyFactory.createLazyObject(((LazyMapObjectInspector)this.oi).getMapValueObjectInspector());
        }
        if (this.isNull(((LazyMapObjectInspector)this.oi).getNullSequence(), this.bytes, valueIBegin, valueILength)) {
            this.valueObjects[index].setNull();
        }
        else {
            this.valueObjects[index].init(this.bytes, valueIBegin, valueILength);
        }
        return this.valueObjects[index].getObject();
    }
    
    private LazyPrimitive<?, ?> uncheckedGetKey(final int index) {
        if (this.keyInited[index]) {
            return this.keyObjects[index];
        }
        final int keyIBegin = this.keyStart[index];
        final int keyILength = this.keyEnd[index] - this.keyStart[index];
        if (this.isNull(((LazyMapObjectInspector)this.oi).getNullSequence(), this.bytes, keyIBegin, keyILength)) {
            return null;
        }
        this.keyInited[index] = true;
        if (this.keyObjects[index] == null) {
            this.keyObjects[index] = LazyFactory.createLazyPrimitiveClass((PrimitiveObjectInspector)((LazyMapObjectInspector)this.oi).getMapKeyObjectInspector());
        }
        this.keyObjects[index].init(this.bytes, keyIBegin, keyILength);
        return this.keyObjects[index];
    }
    
    public Map<Object, Object> getMap() {
        if (!this.parsed) {
            this.parse();
        }
        if (this.cachedMap != null) {
            return this.cachedMap;
        }
        this.cachedMap = new LinkedHashMap<Object, Object>();
        for (int i = 0; i < this.mapSize; ++i) {
            final LazyPrimitive<?, ?> lazyKey = this.uncheckedGetKey(i);
            if (lazyKey != null) {
                final Object key = lazyKey.getObject();
                if (key != null && !this.cachedMap.containsKey(key)) {
                    this.cachedMap.put(key, this.uncheckedGetValue(i));
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
    
    protected boolean getParsed() {
        return this.parsed;
    }
    
    protected void setParsed(final boolean parsed) {
        this.parsed = parsed;
    }
    
    static {
        LOG = LogFactory.getLog(LazyMap.class);
    }
}
