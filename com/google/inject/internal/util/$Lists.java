// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;

public final class $Lists
{
    private $Lists() {
    }
    
    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<E>();
    }
    
    public static <E> ArrayList<E> newArrayList(final E... elements) {
        final int capacity = computeArrayListCapacity(elements.length);
        final ArrayList<E> list = new ArrayList<E>(capacity);
        Collections.addAll(list, elements);
        return list;
    }
    
    static int computeArrayListCapacity(final int arraySize) {
        $Preconditions.checkArgument(arraySize >= 0);
        return (int)Math.min(5L + arraySize + arraySize / 10, 2147483647L);
    }
    
    public static <E> ArrayList<E> newArrayList(final Iterable<? extends E> elements) {
        if (elements instanceof Collection) {
            final Collection<? extends E> collection = (Collection<? extends E>)(Collection)elements;
            return new ArrayList<E>(collection);
        }
        return newArrayList(elements.iterator());
    }
    
    public static <E> ArrayList<E> newArrayList(final Iterator<? extends E> elements) {
        final ArrayList<E> list = newArrayList();
        while (elements.hasNext()) {
            list.add((E)elements.next());
        }
        return list;
    }
    
    public static <E> ArrayList<E> newArrayList(@$Nullable final E first, final E[] rest) {
        final ArrayList<E> result = new ArrayList<E>(rest.length + 1);
        result.add(first);
        for (final E element : rest) {
            result.add(element);
        }
        return result;
    }
}
