// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

import java.util.Iterator;
import java.util.Arrays;

public final class $Iterables
{
    private $Iterables() {
    }
    
    public static String toString(final Iterable<?> iterable) {
        return $Iterators.toString(iterable.iterator());
    }
    
    public static <T> T getOnlyElement(final Iterable<T> iterable) {
        return $Iterators.getOnlyElement(iterable.iterator());
    }
    
    public static <T> Iterable<T> concat(final Iterable<? extends T> a, final Iterable<? extends T> b) {
        $Preconditions.checkNotNull(a);
        $Preconditions.checkNotNull(b);
        return concat((Iterable<? extends Iterable<? extends T>>)Arrays.asList(a, b));
    }
    
    public static <T> Iterable<T> concat(final Iterable<? extends Iterable<? extends T>> inputs) {
        final $Function<Iterable<? extends T>, Iterator<? extends T>> function = new $Function<Iterable<? extends T>, Iterator<? extends T>>() {
            public Iterator<? extends T> apply(final Iterable<? extends T> from) {
                return from.iterator();
            }
        };
        final Iterable<Iterator<? extends T>> iterators = transform(inputs, ($Function<? super Iterable<? extends T>, ? extends Iterator<? extends T>>)function);
        return new IterableWithToString<T>() {
            public Iterator<T> iterator() {
                return $Iterators.concat(iterators.iterator());
            }
        };
    }
    
    public static <F, T> Iterable<T> transform(final Iterable<F> fromIterable, final $Function<? super F, ? extends T> function) {
        $Preconditions.checkNotNull(fromIterable);
        $Preconditions.checkNotNull(function);
        return new IterableWithToString<T>() {
            public Iterator<T> iterator() {
                return $Iterators.transform(fromIterable.iterator(), ($Function<? super Object, ? extends T>)function);
            }
        };
    }
    
    abstract static class IterableWithToString<E> implements Iterable<E>
    {
        @Override
        public String toString() {
            return $Iterables.toString(this);
        }
    }
}
