// 
// Decompiled by Procyon v0.5.36
// 

package org.fusesource.leveldbjni.internal;

import java.util.AbstractMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.iq80.leveldb.DBIterator;

public class JniDBIterator implements DBIterator
{
    private final NativeIterator iterator;
    
    JniDBIterator(final NativeIterator iterator) {
        this.iterator = iterator;
    }
    
    public void close() {
        this.iterator.delete();
    }
    
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    public void seek(final byte[] key) {
        try {
            this.iterator.seek(key);
        }
        catch (NativeDB.DBException e) {
            if (e.isNotFound()) {
                throw new NoSuchElementException();
            }
            throw new RuntimeException(e);
        }
    }
    
    public void seekToFirst() {
        this.iterator.seekToFirst();
    }
    
    public void seekToLast() {
        this.iterator.seekToLast();
    }
    
    public Map.Entry<byte[], byte[]> peekNext() {
        if (!this.iterator.isValid()) {
            throw new NoSuchElementException();
        }
        try {
            return new AbstractMap.SimpleImmutableEntry<byte[], byte[]>(this.iterator.key(), this.iterator.value());
        }
        catch (NativeDB.DBException e) {
            throw new RuntimeException(e);
        }
    }
    
    public boolean hasNext() {
        return this.iterator.isValid();
    }
    
    public Map.Entry<byte[], byte[]> next() {
        final Map.Entry<byte[], byte[]> rc = this.peekNext();
        try {
            this.iterator.next();
        }
        catch (NativeDB.DBException e) {
            throw new RuntimeException(e);
        }
        return rc;
    }
    
    public boolean hasPrev() {
        if (!this.iterator.isValid()) {
            return false;
        }
        try {
            this.iterator.prev();
            try {
                return this.iterator.isValid();
            }
            finally {
                if (this.iterator.isValid()) {
                    this.iterator.next();
                }
                else {
                    this.iterator.seekToFirst();
                }
            }
        }
        catch (NativeDB.DBException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Map.Entry<byte[], byte[]> peekPrev() {
        try {
            this.iterator.prev();
            try {
                return this.peekNext();
            }
            finally {
                if (this.iterator.isValid()) {
                    this.iterator.next();
                }
                else {
                    this.iterator.seekToFirst();
                }
            }
        }
        catch (NativeDB.DBException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Map.Entry<byte[], byte[]> prev() {
        final Map.Entry<byte[], byte[]> rc = this.peekPrev();
        try {
            this.iterator.prev();
        }
        catch (NativeDB.DBException e) {
            throw new RuntimeException(e);
        }
        return rc;
    }
}
