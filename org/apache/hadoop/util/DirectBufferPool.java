// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.nio.ByteBuffer;
import java.lang.ref.WeakReference;
import java.util.Queue;
import java.util.concurrent.ConcurrentMap;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class DirectBufferPool
{
    final ConcurrentMap<Integer, Queue<WeakReference<ByteBuffer>>> buffersBySize;
    
    public DirectBufferPool() {
        this.buffersBySize = new ConcurrentHashMap<Integer, Queue<WeakReference<ByteBuffer>>>();
    }
    
    public ByteBuffer getBuffer(final int size) {
        final Queue<WeakReference<ByteBuffer>> list = this.buffersBySize.get(size);
        if (list == null) {
            return ByteBuffer.allocateDirect(size);
        }
        WeakReference<ByteBuffer> ref;
        while ((ref = list.poll()) != null) {
            final ByteBuffer b = ref.get();
            if (b != null) {
                return b;
            }
        }
        return ByteBuffer.allocateDirect(size);
    }
    
    public void returnBuffer(final ByteBuffer buf) {
        buf.clear();
        final int size = buf.capacity();
        Queue<WeakReference<ByteBuffer>> list = this.buffersBySize.get(size);
        if (list == null) {
            list = new ConcurrentLinkedQueue<WeakReference<ByteBuffer>>();
            final Queue<WeakReference<ByteBuffer>> prev = this.buffersBySize.putIfAbsent(size, list);
            if (prev != null) {
                list = prev;
            }
        }
        list.add(new WeakReference<ByteBuffer>(buf));
    }
    
    @VisibleForTesting
    int countBuffersOfSize(final int size) {
        final Queue<WeakReference<ByteBuffer>> list = this.buffersBySize.get(size);
        if (list == null) {
            return 0;
        }
        return list.size();
    }
}
