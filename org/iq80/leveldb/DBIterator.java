// 
// Decompiled by Procyon v0.5.36
// 

package org.iq80.leveldb;

import java.io.Closeable;
import java.util.Map;
import java.util.Iterator;

public interface DBIterator extends Iterator<Map.Entry<byte[], byte[]>>, Closeable
{
    void seek(final byte[] p0);
    
    void seekToFirst();
    
    Map.Entry<byte[], byte[]> peekNext();
    
    boolean hasPrev();
    
    Map.Entry<byte[], byte[]> prev();
    
    Map.Entry<byte[], byte[]> peekPrev();
    
    void seekToLast();
}
