// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.util;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;
import java.util.List;

public final class ContainerBuilder
{
    private static final int MAX_BUF = 1000;
    private Object[] b;
    private int tail;
    private int start;
    private List<Object> list;
    private Map<String, Object> map;
    
    public ContainerBuilder(final int bufSize) {
        this.b = new Object[bufSize & 0xFFFFFFFE];
    }
    
    public boolean canReuse() {
        return this.list == null && this.map == null;
    }
    
    public int bufferLength() {
        return this.b.length;
    }
    
    public int start() {
        if (this.list != null || this.map != null) {
            throw new IllegalStateException();
        }
        final int prevStart = this.start;
        this.start = this.tail;
        return prevStart;
    }
    
    public int startList(final Object value) {
        if (this.list != null || this.map != null) {
            throw new IllegalStateException();
        }
        final int prevStart = this.start;
        this.start = this.tail;
        this.add(value);
        return prevStart;
    }
    
    public int startMap(final String key, final Object value) {
        if (this.list != null || this.map != null) {
            throw new IllegalStateException();
        }
        final int prevStart = this.start;
        this.start = this.tail;
        this.put(key, value);
        return prevStart;
    }
    
    public void add(final Object value) {
        if (this.list != null) {
            this.list.add(value);
        }
        else if (this.tail >= this.b.length) {
            this._expandList(value);
        }
        else {
            this.b[this.tail++] = value;
        }
    }
    
    public void put(final String key, final Object value) {
        if (this.map != null) {
            this.map.put(key, value);
        }
        else if (this.tail + 2 > this.b.length) {
            this._expandMap(key, value);
        }
        else {
            this.b[this.tail++] = key;
            this.b[this.tail++] = value;
        }
    }
    
    public List<Object> finishList(final int prevStart) {
        List<Object> l = this.list;
        if (l == null) {
            l = this._buildList(true);
        }
        else {
            this.list = null;
        }
        this.start = prevStart;
        return l;
    }
    
    public Object[] finishArray(final int prevStart) {
        Object[] result;
        if (this.list == null) {
            result = Arrays.copyOfRange(this.b, this.start, this.tail);
        }
        else {
            result = this.list.toArray(new Object[this.tail - this.start]);
            this.list = null;
        }
        this.start = prevStart;
        return result;
    }
    
    public <T> Object[] finishArray(final int prevStart, final Class<T> elemType) {
        final int size = this.tail - this.start;
        T[] result = (T[])Array.newInstance(elemType, size);
        if (this.list == null) {
            System.arraycopy(this.b, this.start, result, 0, size);
        }
        else {
            result = this.list.toArray(result);
            this.list = null;
        }
        this.start = prevStart;
        return result;
    }
    
    public Map<String, Object> finishMap(final int prevStart) {
        Map<String, Object> m = this.map;
        if (m == null) {
            m = this._buildMap(true);
        }
        else {
            this.map = null;
        }
        this.start = prevStart;
        return m;
    }
    
    private void _expandList(final Object value) {
        if (this.b.length < 1000) {
            (this.b = Arrays.copyOf(this.b, this.b.length << 1))[this.tail++] = value;
        }
        else {
            (this.list = this._buildList(false)).add(value);
        }
    }
    
    private List<Object> _buildList(final boolean isComplete) {
        int currLen = this.tail - this.start;
        if (isComplete) {
            if (currLen < 2) {
                currLen = 2;
            }
        }
        else if (currLen < 20) {
            currLen = 20;
        }
        else if (currLen < 1000) {
            currLen += currLen >> 1;
        }
        else {
            currLen += currLen >> 2;
        }
        final List<Object> l = new ArrayList<Object>(currLen);
        for (int i = this.start; i < this.tail; ++i) {
            l.add(this.b[i]);
        }
        this.tail = this.start;
        return l;
    }
    
    private void _expandMap(final String key, final Object value) {
        if (this.b.length < 1000) {
            (this.b = Arrays.copyOf(this.b, this.b.length << 1))[this.tail++] = key;
            this.b[this.tail++] = value;
        }
        else {
            (this.map = this._buildMap(false)).put(key, value);
        }
    }
    
    private Map<String, Object> _buildMap(final boolean isComplete) {
        int size = this.tail - this.start >> 1;
        if (isComplete) {
            if (size <= 3) {
                size = 4;
            }
            else if (size <= 40) {
                size += size >> 1;
            }
            else {
                size += (size >> 2) + (size >> 4);
            }
        }
        else if (size < 10) {
            size = 16;
        }
        else if (size < 1000) {
            size += size >> 1;
        }
        else {
            size += size / 3;
        }
        final Map<String, Object> m = new LinkedHashMap<String, Object>(size, 0.8f);
        for (int i = this.start; i < this.tail; i += 2) {
            m.put((String)this.b[i], this.b[i + 1]);
        }
        this.tail = this.start;
        return m;
    }
}
