// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import java.util.Map;
import java.util.ArrayList;
import org.eclipse.jetty.util.StringMap;
import java.util.HashMap;

public class BufferCache
{
    private final HashMap _bufferMap;
    private final StringMap _stringMap;
    private final ArrayList _index;
    
    public BufferCache() {
        this._bufferMap = new HashMap();
        this._stringMap = new StringMap(true);
        this._index = new ArrayList();
    }
    
    public CachedBuffer add(final String value, final int ordinal) {
        final CachedBuffer buffer = new CachedBuffer(value, ordinal);
        this._bufferMap.put(buffer, buffer);
        this._stringMap.put(value, buffer);
        while (ordinal - this._index.size() >= 0) {
            this._index.add(null);
        }
        if (this._index.get(ordinal) == null) {
            this._index.add(ordinal, buffer);
        }
        return buffer;
    }
    
    public CachedBuffer get(final int ordinal) {
        if (ordinal < 0 || ordinal >= this._index.size()) {
            return null;
        }
        return this._index.get(ordinal);
    }
    
    public CachedBuffer get(final Buffer buffer) {
        return this._bufferMap.get(buffer);
    }
    
    public CachedBuffer get(final String value) {
        return (CachedBuffer)this._stringMap.get(value);
    }
    
    public Buffer lookup(final Buffer buffer) {
        if (buffer instanceof CachedBuffer) {
            return buffer;
        }
        final Buffer b = this.get(buffer);
        if (b != null) {
            return b;
        }
        if (buffer instanceof Buffer.CaseInsensitve) {
            return buffer;
        }
        return new ByteArrayBuffer.CaseInsensitive(buffer.asArray(), 0, buffer.length(), 0);
    }
    
    public CachedBuffer getBest(final byte[] value, final int offset, final int maxLength) {
        final Map.Entry entry = this._stringMap.getBestEntry(value, offset, maxLength);
        if (entry != null) {
            return entry.getValue();
        }
        return null;
    }
    
    public Buffer lookup(final String value) {
        final Buffer b = this.get(value);
        if (b == null) {
            return new CachedBuffer(value, -1);
        }
        return b;
    }
    
    public String toString(final Buffer buffer) {
        return this.lookup(buffer).toString();
    }
    
    public int getOrdinal(final String value) {
        final CachedBuffer buffer = (CachedBuffer)this._stringMap.get(value);
        return (buffer == null) ? -1 : buffer.getOrdinal();
    }
    
    public int getOrdinal(Buffer buffer) {
        if (buffer instanceof CachedBuffer) {
            return ((CachedBuffer)buffer).getOrdinal();
        }
        buffer = this.lookup(buffer);
        if (buffer != null && buffer instanceof CachedBuffer) {
            return ((CachedBuffer)buffer).getOrdinal();
        }
        return -1;
    }
    
    @Override
    public String toString() {
        return "CACHE[bufferMap=" + this._bufferMap + ",stringMap=" + this._stringMap + ",index=" + this._index + "]";
    }
    
    public static class CachedBuffer extends CaseInsensitive
    {
        private final int _ordinal;
        private HashMap _associateMap;
        
        public CachedBuffer(final String value, final int ordinal) {
            super(value);
            this._associateMap = null;
            this._ordinal = ordinal;
        }
        
        public int getOrdinal() {
            return this._ordinal;
        }
        
        public CachedBuffer getAssociate(final Object key) {
            if (this._associateMap == null) {
                return null;
            }
            return this._associateMap.get(key);
        }
        
        public void setAssociate(final Object key, final CachedBuffer associate) {
            if (this._associateMap == null) {
                this._associateMap = new HashMap();
            }
            this._associateMap.put(key, associate);
        }
    }
}
