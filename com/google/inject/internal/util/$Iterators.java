// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.ListIterator;
import java.util.Iterator;

public final class $Iterators
{
    static final Iterator<Object> EMPTY_ITERATOR;
    private static final ListIterator<Object> EMPTY_LIST_ITERATOR;
    
    private $Iterators() {
    }
    
    public static <T> $UnmodifiableIterator<T> emptyIterator() {
        return ($UnmodifiableIterator<T>)($UnmodifiableIterator)$Iterators.EMPTY_ITERATOR;
    }
    
    public static <T> ListIterator<T> emptyListIterator() {
        return (ListIterator<T>)$Iterators.EMPTY_LIST_ITERATOR;
    }
    
    public static <T> $UnmodifiableIterator<T> unmodifiableIterator(final Iterator<T> iterator) {
        $Preconditions.checkNotNull(iterator);
        return new $UnmodifiableIterator<T>() {
            public boolean hasNext() {
                return iterator.hasNext();
            }
            
            public T next() {
                return iterator.next();
            }
        };
    }
    
    public static String toString(final Iterator<?> iterator) {
        if (!iterator.hasNext()) {
            return "[]";
        }
        final StringBuilder builder = new StringBuilder();
        builder.append('[').append(iterator.next());
        while (iterator.hasNext()) {
            builder.append(", ").append(iterator.next());
        }
        return builder.append(']').toString();
    }
    
    public static <T> T getOnlyElement(final Iterator<T> iterator) {
        final T first = iterator.next();
        if (!iterator.hasNext()) {
            return first;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("expected one element but was: <" + first);
        for (int i = 0; i < 4 && iterator.hasNext(); ++i) {
            sb.append(", " + iterator.next());
        }
        if (iterator.hasNext()) {
            sb.append(", ...");
        }
        sb.append(">");
        throw new IllegalArgumentException(sb.toString());
    }
    
    public static <T> Iterator<T> concat(final Iterator<? extends Iterator<? extends T>> inputs) {
        $Preconditions.checkNotNull(inputs);
        return new Iterator<T>() {
            Iterator<? extends T> current = $Iterators.emptyIterator();
            Iterator<? extends T> removeFrom;
            
            public boolean hasNext() {
                while (!this.current.hasNext() && inputs.hasNext()) {
                    this.current = inputs.next();
                }
                return this.current.hasNext();
            }
            
            public T next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.removeFrom = this.current;
                return (T)this.current.next();
            }
            
            public void remove() {
                $Preconditions.checkState(this.removeFrom != null, (Object)"no calls to next() since last call to remove()");
                this.removeFrom.remove();
                this.removeFrom = null;
            }
        };
    }
    
    public static <F, T> Iterator<T> transform(final Iterator<F> fromIterator, final $Function<? super F, ? extends T> function) {
        $Preconditions.checkNotNull(fromIterator);
        $Preconditions.checkNotNull(function);
        return new Iterator<T>() {
            public boolean hasNext() {
                return fromIterator.hasNext();
            }
            
            public T next() {
                final F from = fromIterator.next();
                return function.apply(from);
            }
            
            public void remove() {
                fromIterator.remove();
            }
        };
    }
    
    public static <T> $UnmodifiableIterator<T> forArray(final T... array) {
        return new $UnmodifiableIterator<T>() {
            final int length = array.length;
            int i = 0;
            
            public boolean hasNext() {
                return this.i < this.length;
            }
            
            public T next() {
                try {
                    final T t = array[this.i];
                    ++this.i;
                    return t;
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    throw new NoSuchElementException();
                }
            }
        };
    }
    
    public static <T> $UnmodifiableIterator<T> forArray(final T[] array, final int offset, final int length) {
        $Preconditions.checkArgument(length >= 0);
        final int end = offset + length;
        $Preconditions.checkPositionIndexes(offset, end, array.length);
        return new $UnmodifiableIterator<T>() {
            int i = offset;
            
            public boolean hasNext() {
                return this.i < end;
            }
            
            public T next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return array[this.i++];
            }
        };
    }
    
    public static <T> $UnmodifiableIterator<T> singletonIterator(@$Nullable final T value) {
        return new $UnmodifiableIterator<T>() {
            boolean done;
            
            public boolean hasNext() {
                return !this.done;
            }
            
            public T next() {
                if (this.done) {
                    throw new NoSuchElementException();
                }
                this.done = true;
                return value;
            }
        };
    }
    
    public static <T> Enumeration<T> asEnumeration(final Iterator<T> iterator) {
        $Preconditions.checkNotNull(iterator);
        return new Enumeration<T>() {
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }
            
            public T nextElement() {
                return iterator.next();
            }
        };
    }
    
    static {
        EMPTY_ITERATOR = new $UnmodifiableIterator<Object>() {
            public boolean hasNext() {
                return false;
            }
            
            public Object next() {
                throw new NoSuchElementException();
            }
        };
        EMPTY_LIST_ITERATOR = new ListIterator<Object>() {
            public boolean hasNext() {
                return false;
            }
            
            public boolean hasPrevious() {
                return false;
            }
            
            public int nextIndex() {
                return 0;
            }
            
            public int previousIndex() {
                return -1;
            }
            
            public Object next() {
                throw new NoSuchElementException();
            }
            
            public Object previous() {
                throw new NoSuchElementException();
            }
            
            public void set(final Object o) {
                throw new UnsupportedOperationException();
            }
            
            public void add(final Object o) {
                throw new UnsupportedOperationException();
            }
            
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
