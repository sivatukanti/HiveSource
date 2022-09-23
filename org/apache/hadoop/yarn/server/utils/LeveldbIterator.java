// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.utils;

import java.io.IOException;
import org.iq80.leveldb.DBException;
import org.iq80.leveldb.ReadOptions;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Closeable;
import java.util.Map;
import java.util.Iterator;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class LeveldbIterator implements Iterator<Map.Entry<byte[], byte[]>>, Closeable
{
    private DBIterator iter;
    
    public LeveldbIterator(final DB db) {
        this.iter = db.iterator();
    }
    
    public LeveldbIterator(final DB db, final ReadOptions options) {
        this.iter = db.iterator(options);
    }
    
    public LeveldbIterator(final DBIterator iter) {
        this.iter = iter;
    }
    
    public void seek(final byte[] key) throws DBException {
        try {
            this.iter.seek(key);
        }
        catch (DBException e) {
            throw e;
        }
        catch (RuntimeException e2) {
            throw new DBException(e2.getMessage(), e2);
        }
    }
    
    public void seekToFirst() throws DBException {
        try {
            this.iter.seekToFirst();
        }
        catch (DBException e) {
            throw e;
        }
        catch (RuntimeException e2) {
            throw new DBException(e2.getMessage(), e2);
        }
    }
    
    public void seekToLast() throws DBException {
        try {
            this.iter.seekToLast();
        }
        catch (DBException e) {
            throw e;
        }
        catch (RuntimeException e2) {
            throw new DBException(e2.getMessage(), e2);
        }
    }
    
    @Override
    public boolean hasNext() throws DBException {
        try {
            return this.iter.hasNext();
        }
        catch (DBException e) {
            throw e;
        }
        catch (RuntimeException e2) {
            throw new DBException(e2.getMessage(), e2);
        }
    }
    
    @Override
    public Map.Entry<byte[], byte[]> next() throws DBException {
        try {
            return this.iter.next();
        }
        catch (DBException e) {
            throw e;
        }
        catch (RuntimeException e2) {
            throw new DBException(e2.getMessage(), e2);
        }
    }
    
    public Map.Entry<byte[], byte[]> peekNext() throws DBException {
        try {
            return this.iter.peekNext();
        }
        catch (DBException e) {
            throw e;
        }
        catch (RuntimeException e2) {
            throw new DBException(e2.getMessage(), e2);
        }
    }
    
    public boolean hasPrev() throws DBException {
        try {
            return this.iter.hasPrev();
        }
        catch (DBException e) {
            throw e;
        }
        catch (RuntimeException e2) {
            throw new DBException(e2.getMessage(), e2);
        }
    }
    
    public Map.Entry<byte[], byte[]> prev() throws DBException {
        try {
            return this.iter.prev();
        }
        catch (DBException e) {
            throw e;
        }
        catch (RuntimeException e2) {
            throw new DBException(e2.getMessage(), e2);
        }
    }
    
    public Map.Entry<byte[], byte[]> peekPrev() throws DBException {
        try {
            return this.iter.peekPrev();
        }
        catch (DBException e) {
            throw e;
        }
        catch (RuntimeException e2) {
            throw new DBException(e2.getMessage(), e2);
        }
    }
    
    @Override
    public void remove() throws DBException {
        try {
            this.iter.remove();
        }
        catch (DBException e) {
            throw e;
        }
        catch (RuntimeException e2) {
            throw new DBException(e2.getMessage(), e2);
        }
    }
    
    @Override
    public void close() throws IOException {
        try {
            this.iter.close();
        }
        catch (RuntimeException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}
