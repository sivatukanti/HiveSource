// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import com.google.common.collect.ComparisonChain;
import java.util.Map;
import java.nio.ByteBuffer;
import java.util.TreeMap;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public final class ElasticByteBufferPool implements ByteBufferPool
{
    private final TreeMap<Key, ByteBuffer> buffers;
    private final TreeMap<Key, ByteBuffer> directBuffers;
    
    public ElasticByteBufferPool() {
        this.buffers = new TreeMap<Key, ByteBuffer>();
        this.directBuffers = new TreeMap<Key, ByteBuffer>();
    }
    
    private final TreeMap<Key, ByteBuffer> getBufferTree(final boolean direct) {
        return direct ? this.directBuffers : this.buffers;
    }
    
    @Override
    public synchronized ByteBuffer getBuffer(final boolean direct, final int length) {
        final TreeMap<Key, ByteBuffer> tree = this.getBufferTree(direct);
        final Map.Entry<Key, ByteBuffer> entry = tree.ceilingEntry(new Key(length, 0L));
        if (entry == null) {
            return direct ? ByteBuffer.allocateDirect(length) : ByteBuffer.allocate(length);
        }
        tree.remove(entry.getKey());
        return entry.getValue();
    }
    
    @Override
    public synchronized void putBuffer(final ByteBuffer buffer) {
        buffer.clear();
        final TreeMap<Key, ByteBuffer> tree = this.getBufferTree(buffer.isDirect());
        Key key;
        do {
            key = new Key(buffer.capacity(), System.nanoTime());
        } while (tree.containsKey(key));
        tree.put(key, buffer);
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public int size(final boolean direct) {
        return this.getBufferTree(direct).size();
    }
    
    private static final class Key implements Comparable<Key>
    {
        private final int capacity;
        private final long insertionTime;
        
        Key(final int capacity, final long insertionTime) {
            this.capacity = capacity;
            this.insertionTime = insertionTime;
        }
        
        @Override
        public int compareTo(final Key other) {
            return ComparisonChain.start().compare(this.capacity, other.capacity).compare(this.insertionTime, other.insertionTime).result();
        }
        
        @Override
        public boolean equals(final Object rhs) {
            if (rhs == null) {
                return false;
            }
            try {
                final Key o = (Key)rhs;
                return this.compareTo(o) == 0;
            }
            catch (ClassCastException e) {
                return false;
            }
        }
        
        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(this.capacity).append(this.insertionTime).toHashCode();
        }
    }
}
