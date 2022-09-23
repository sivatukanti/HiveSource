// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.util;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.Lock;
import java.io.Serializable;
import java.util.LinkedHashMap;

public class LRUMap<K, V> extends LinkedHashMap<K, V> implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final transient Lock _readLock;
    protected final transient Lock _writeLock;
    protected final transient int _maxEntries;
    protected transient int _jdkSerializeMaxEntries;
    
    public LRUMap(final int initialEntries, final int maxEntries) {
        super(initialEntries, 0.8f, true);
        this._maxEntries = maxEntries;
        final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
        this._readLock = rwl.readLock();
        this._writeLock = rwl.writeLock();
    }
    
    @Override
    protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
        return this.size() > this._maxEntries;
    }
    
    @Override
    public V get(final Object key) {
        this._readLock.lock();
        try {
            return super.get(key);
        }
        finally {
            this._readLock.unlock();
        }
    }
    
    @Override
    public V put(final K key, final V value) {
        this._writeLock.lock();
        try {
            return super.put(key, value);
        }
        finally {
            this._writeLock.unlock();
        }
    }
    
    @Override
    public V remove(final Object key) {
        this._writeLock.lock();
        try {
            return super.remove(key);
        }
        finally {
            this._writeLock.unlock();
        }
    }
    
    private void readObject(final ObjectInputStream in) throws IOException {
        this._jdkSerializeMaxEntries = in.readInt();
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.writeInt(this._jdkSerializeMaxEntries);
    }
    
    protected Object readResolve() {
        return new LRUMap(this._jdkSerializeMaxEntries, this._jdkSerializeMaxEntries);
    }
}
