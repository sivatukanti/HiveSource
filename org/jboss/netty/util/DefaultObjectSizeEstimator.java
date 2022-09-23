// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.nio.ByteBuffer;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.MessageEvent;
import java.util.Set;
import org.jboss.netty.util.internal.ConcurrentIdentityWeakKeyHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultObjectSizeEstimator implements ObjectSizeEstimator
{
    private final ConcurrentMap<Class<?>, Integer> class2size;
    
    public DefaultObjectSizeEstimator() {
        (this.class2size = new ConcurrentIdentityWeakKeyHashMap<Class<?>, Integer>()).put(Boolean.TYPE, 4);
        this.class2size.put(Byte.TYPE, 1);
        this.class2size.put(Character.TYPE, 2);
        this.class2size.put(Integer.TYPE, 4);
        this.class2size.put(Short.TYPE, 2);
        this.class2size.put(Long.TYPE, 8);
        this.class2size.put(Float.TYPE, 4);
        this.class2size.put(Double.TYPE, 8);
        this.class2size.put(Void.TYPE, 0);
    }
    
    public int estimateSize(final Object o) {
        if (o == null) {
            return 8;
        }
        int answer = 8 + this.estimateSize(o.getClass(), null);
        if (o instanceof EstimatableObjectWrapper) {
            answer += this.estimateSize(((EstimatableObjectWrapper)o).unwrap());
        }
        else if (o instanceof MessageEvent) {
            answer += this.estimateSize(((MessageEvent)o).getMessage());
        }
        else if (o instanceof ChannelBuffer) {
            answer += ((ChannelBuffer)o).capacity();
        }
        else if (o instanceof byte[]) {
            answer += ((byte[])o).length;
        }
        else if (o instanceof ByteBuffer) {
            answer += ((ByteBuffer)o).remaining();
        }
        else if (o instanceof CharSequence) {
            answer += ((CharSequence)o).length() << 1;
        }
        else if (o instanceof Iterable) {
            for (final Object m : (Iterable)o) {
                answer += this.estimateSize(m);
            }
        }
        return align(answer);
    }
    
    private int estimateSize(final Class<?> clazz, Set<Class<?>> visitedClasses) {
        final Integer objectSize = this.class2size.get(clazz);
        if (objectSize != null) {
            return objectSize;
        }
        if (visitedClasses != null) {
            if (visitedClasses.contains(clazz)) {
                return 0;
            }
        }
        else {
            visitedClasses = new HashSet<Class<?>>();
        }
        visitedClasses.add(clazz);
        int answer = 8;
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            final Field[] arr$;
            final Field[] fields = arr$ = c.getDeclaredFields();
            for (final Field f : arr$) {
                if ((f.getModifiers() & 0x8) == 0x0) {
                    answer += this.estimateSize(f.getType(), visitedClasses);
                }
            }
        }
        visitedClasses.remove(clazz);
        answer = align(answer);
        this.class2size.putIfAbsent(clazz, answer);
        return answer;
    }
    
    private static int align(int size) {
        final int r = size % 8;
        if (r != 0) {
            size += 8 - r;
        }
        return size;
    }
}
