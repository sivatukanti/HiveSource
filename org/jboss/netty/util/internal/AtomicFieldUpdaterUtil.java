// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util.internal;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

final class AtomicFieldUpdaterUtil
{
    private static final boolean AVAILABLE;
    
    static <T, V> AtomicReferenceFieldUpdater<T, V> newRefUpdater(final Class<T> tclass, final Class<V> vclass, final String fieldName) {
        if (AtomicFieldUpdaterUtil.AVAILABLE) {
            return AtomicReferenceFieldUpdater.newUpdater(tclass, vclass, fieldName);
        }
        return null;
    }
    
    static <T> AtomicIntegerFieldUpdater<T> newIntUpdater(final Class<T> tclass, final String fieldName) {
        if (AtomicFieldUpdaterUtil.AVAILABLE) {
            return AtomicIntegerFieldUpdater.newUpdater(tclass, fieldName);
        }
        return null;
    }
    
    static boolean isAvailable() {
        return AtomicFieldUpdaterUtil.AVAILABLE;
    }
    
    private AtomicFieldUpdaterUtil() {
    }
    
    static {
        boolean available = false;
        try {
            final AtomicReferenceFieldUpdater<Node, Node> tmp = AtomicReferenceFieldUpdater.newUpdater(Node.class, Node.class, "next");
            final Node testNode = new Node();
            tmp.set(testNode, testNode);
            if (testNode.next != testNode) {
                throw new Exception();
            }
            available = true;
        }
        catch (Throwable t) {}
        AVAILABLE = available;
    }
    
    static final class Node
    {
        volatile Node next;
    }
}
