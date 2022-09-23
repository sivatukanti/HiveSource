// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.ListIterator;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import java.util.RandomAccess;
import java.util.List;

public abstract class $ImmutableList<E> extends $ImmutableCollection<E> implements List<E>, RandomAccess
{
    private static final $ImmutableList<?> EMPTY_IMMUTABLE_LIST;
    
    public static <E> $ImmutableList<E> of() {
        return ($ImmutableList<E>)$ImmutableList.EMPTY_IMMUTABLE_LIST;
    }
    
    public static <E> $ImmutableList<E> of(final E element) {
        return new RegularImmutableList<E>(copyIntoArray(element));
    }
    
    public static <E> $ImmutableList<E> of(final E e1, final E e2) {
        return new RegularImmutableList<E>(copyIntoArray(e1, e2));
    }
    
    public static <E> $ImmutableList<E> of(final E e1, final E e2, final E e3) {
        return new RegularImmutableList<E>(copyIntoArray(e1, e2, e3));
    }
    
    public static <E> $ImmutableList<E> of(final E e1, final E e2, final E e3, final E e4) {
        return new RegularImmutableList<E>(copyIntoArray(e1, e2, e3, e4));
    }
    
    public static <E> $ImmutableList<E> of(final E e1, final E e2, final E e3, final E e4, final E e5) {
        return new RegularImmutableList<E>(copyIntoArray(e1, e2, e3, e4, e5));
    }
    
    public static <E> $ImmutableList<E> of(final E... elements) {
        return (elements.length == 0) ? of() : new RegularImmutableList<E>(copyIntoArray((Object[])elements));
    }
    
    public static <E> $ImmutableList<E> copyOf(final Iterable<? extends E> elements) {
        if (elements instanceof $ImmutableList) {
            final $ImmutableList<E> list = ($ImmutableList<E>)($ImmutableList)elements;
            return list;
        }
        if (elements instanceof Collection) {
            final Collection<? extends E> coll = (Collection<? extends E>)(Collection)elements;
            return copyOfInternal(coll);
        }
        return copyOfInternal($Lists.newArrayList(elements));
    }
    
    public static <E> $ImmutableList<E> copyOf(final Iterator<? extends E> elements) {
        return copyOfInternal($Lists.newArrayList(elements));
    }
    
    private static <E> $ImmutableList<E> copyOfInternal(final ArrayList<? extends E> list) {
        return list.isEmpty() ? of() : new RegularImmutableList<E>(nullChecked(list.toArray()));
    }
    
    private static Object[] nullChecked(final Object[] array) {
        for (int i = 0, len = array.length; i < len; ++i) {
            if (array[i] == null) {
                throw new NullPointerException("at index " + i);
            }
        }
        return array;
    }
    
    private static <E> $ImmutableList<E> copyOfInternal(final Collection<? extends E> collection) {
        final int size = collection.size();
        return (size == 0) ? of() : createFromIterable(collection, size);
    }
    
    private $ImmutableList() {
    }
    
    @Override
    public abstract $UnmodifiableIterator<E> iterator();
    
    public abstract int indexOf(@$Nullable final Object p0);
    
    public abstract int lastIndexOf(@$Nullable final Object p0);
    
    public abstract $ImmutableList<E> subList(final int p0, final int p1);
    
    public final boolean addAll(final int index, final Collection<? extends E> newElements) {
        throw new UnsupportedOperationException();
    }
    
    public final E set(final int index, final E element) {
        throw new UnsupportedOperationException();
    }
    
    public final void add(final int index, final E element) {
        throw new UnsupportedOperationException();
    }
    
    public final E remove(final int index) {
        throw new UnsupportedOperationException();
    }
    
    private static Object[] copyIntoArray(final Object... source) {
        final Object[] array = new Object[source.length];
        int index = 0;
        for (final Object element : source) {
            if (element == null) {
                throw new NullPointerException("at index " + index);
            }
            array[index++] = element;
        }
        return array;
    }
    
    private static <E> $ImmutableList<E> createFromIterable(final Iterable<?> source, int estimatedSize) {
        Object[] array = new Object[estimatedSize];
        int index = 0;
        for (final Object element : source) {
            if (index == estimatedSize) {
                estimatedSize = (estimatedSize / 2 + 1) * 3;
                array = copyOf(array, estimatedSize);
            }
            if (element == null) {
                throw new NullPointerException("at index " + index);
            }
            array[index++] = element;
        }
        if (index == 0) {
            return of();
        }
        if (index != estimatedSize) {
            array = copyOf(array, index);
        }
        return new RegularImmutableList<E>(array, 0, index);
    }
    
    private static Object[] copyOf(final Object[] oldArray, final int newSize) {
        final Object[] newArray = new Object[newSize];
        System.arraycopy(oldArray, 0, newArray, 0, Math.min(oldArray.length, newSize));
        return newArray;
    }
    
    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Use SerializedForm");
    }
    
    @Override
    Object writeReplace() {
        return new SerializedForm(this.toArray());
    }
    
    public static <E> Builder<E> builder() {
        return new Builder<E>();
    }
    
    static {
        EMPTY_IMMUTABLE_LIST = new EmptyImmutableList();
    }
    
    private static final class EmptyImmutableList extends $ImmutableList<Object>
    {
        private static final Object[] EMPTY_ARRAY;
        
        private EmptyImmutableList() {
            super(null);
        }
        
        public int size() {
            return 0;
        }
        
        @Override
        public boolean isEmpty() {
            return true;
        }
        
        @Override
        public boolean contains(final Object target) {
            return false;
        }
        
        @Override
        public $UnmodifiableIterator<Object> iterator() {
            return $Iterators.emptyIterator();
        }
        
        @Override
        public Object[] toArray() {
            return EmptyImmutableList.EMPTY_ARRAY;
        }
        
        @Override
        public <T> T[] toArray(final T[] a) {
            if (a.length > 0) {
                a[0] = null;
            }
            return a;
        }
        
        public Object get(final int index) {
            $Preconditions.checkElementIndex(index, 0);
            throw new AssertionError((Object)"unreachable");
        }
        
        @Override
        public int indexOf(final Object target) {
            return -1;
        }
        
        @Override
        public int lastIndexOf(final Object target) {
            return -1;
        }
        
        @Override
        public $ImmutableList<Object> subList(final int fromIndex, final int toIndex) {
            $Preconditions.checkPositionIndexes(fromIndex, toIndex, 0);
            return this;
        }
        
        public ListIterator<Object> listIterator() {
            return $Iterators.emptyListIterator();
        }
        
        public ListIterator<Object> listIterator(final int start) {
            $Preconditions.checkPositionIndex(start, 0);
            return $Iterators.emptyListIterator();
        }
        
        @Override
        public boolean containsAll(final Collection<?> targets) {
            return targets.isEmpty();
        }
        
        @Override
        public boolean equals(@$Nullable final Object object) {
            if (object instanceof List) {
                final List<?> that = (List<?>)object;
                return that.isEmpty();
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return 1;
        }
        
        @Override
        public String toString() {
            return "[]";
        }
        
        static {
            EMPTY_ARRAY = new Object[0];
        }
    }
    
    private static final class RegularImmutableList<E> extends $ImmutableList<E>
    {
        private final int offset;
        private final int size;
        private final Object[] array;
        
        private RegularImmutableList(final Object[] array, final int offset, final int size) {
            super(null);
            this.offset = offset;
            this.size = size;
            this.array = array;
        }
        
        private RegularImmutableList(final Object[] array) {
            this(array, 0, array.length);
        }
        
        public int size() {
            return this.size;
        }
        
        @Override
        public boolean isEmpty() {
            return false;
        }
        
        @Override
        public boolean contains(final Object target) {
            return this.indexOf(target) != -1;
        }
        
        @Override
        public $UnmodifiableIterator<E> iterator() {
            return $Iterators.forArray(this.array, this.offset, this.size);
        }
        
        @Override
        public Object[] toArray() {
            final Object[] newArray = new Object[this.size()];
            System.arraycopy(this.array, this.offset, newArray, 0, this.size);
            return newArray;
        }
        
        @Override
        public <T> T[] toArray(T[] other) {
            if (other.length < this.size) {
                other = $ObjectArrays.newArray(other, this.size);
            }
            else if (other.length > this.size) {
                other[this.size] = null;
            }
            System.arraycopy(this.array, this.offset, other, 0, this.size);
            return other;
        }
        
        public E get(final int index) {
            $Preconditions.checkElementIndex(index, this.size);
            return (E)this.array[index + this.offset];
        }
        
        @Override
        public int indexOf(final Object target) {
            if (target != null) {
                for (int i = this.offset; i < this.offset + this.size; ++i) {
                    if (this.array[i].equals(target)) {
                        return i - this.offset;
                    }
                }
            }
            return -1;
        }
        
        @Override
        public int lastIndexOf(final Object target) {
            if (target != null) {
                for (int i = this.offset + this.size - 1; i >= this.offset; --i) {
                    if (this.array[i].equals(target)) {
                        return i - this.offset;
                    }
                }
            }
            return -1;
        }
        
        @Override
        public $ImmutableList<E> subList(final int fromIndex, final int toIndex) {
            $Preconditions.checkPositionIndexes(fromIndex, toIndex, this.size);
            return (fromIndex == toIndex) ? $ImmutableList.of() : new RegularImmutableList(this.array, this.offset + fromIndex, toIndex - fromIndex);
        }
        
        public ListIterator<E> listIterator() {
            return this.listIterator(0);
        }
        
        public ListIterator<E> listIterator(final int start) {
            $Preconditions.checkPositionIndex(start, this.size);
            return new ListIterator<E>() {
                int index = start;
                
                public boolean hasNext() {
                    return this.index < RegularImmutableList.this.size;
                }
                
                public boolean hasPrevious() {
                    return this.index > 0;
                }
                
                public int nextIndex() {
                    return this.index;
                }
                
                public int previousIndex() {
                    return this.index - 1;
                }
                
                public E next() {
                    E result;
                    try {
                        result = RegularImmutableList.this.get(this.index);
                    }
                    catch (IndexOutOfBoundsException rethrown) {
                        throw new NoSuchElementException();
                    }
                    ++this.index;
                    return result;
                }
                
                public E previous() {
                    E result;
                    try {
                        result = RegularImmutableList.this.get(this.index - 1);
                    }
                    catch (IndexOutOfBoundsException rethrown) {
                        throw new NoSuchElementException();
                    }
                    --this.index;
                    return result;
                }
                
                public void set(final E o) {
                    throw new UnsupportedOperationException();
                }
                
                public void add(final E o) {
                    throw new UnsupportedOperationException();
                }
                
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        
        @Override
        public boolean equals(@$Nullable final Object object) {
            if (object == this) {
                return true;
            }
            if (!(object instanceof List)) {
                return false;
            }
            final List<?> that = (List<?>)object;
            if (this.size() != that.size()) {
                return false;
            }
            int index = this.offset;
            if (object instanceof RegularImmutableList) {
                final RegularImmutableList<?> other = (RegularImmutableList<?>)object;
                for (int i = other.offset; i < other.offset + other.size; ++i) {
                    if (!this.array[index++].equals(other.array[i])) {
                        return false;
                    }
                }
            }
            else {
                for (final Object element : that) {
                    if (!this.array[index++].equals(element)) {
                        return false;
                    }
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 1;
            for (int i = this.offset; i < this.offset + this.size; ++i) {
                hashCode = 31 * hashCode + this.array[i].hashCode();
            }
            return hashCode;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(this.size() * 16);
            sb.append('[').append(this.array[this.offset]);
            for (int i = this.offset + 1; i < this.offset + this.size; ++i) {
                sb.append(", ").append(this.array[i]);
            }
            return sb.append(']').toString();
        }
    }
    
    private static class SerializedForm implements Serializable
    {
        final Object[] elements;
        private static final long serialVersionUID = 0L;
        
        SerializedForm(final Object[] elements) {
            this.elements = elements;
        }
        
        Object readResolve() {
            return $ImmutableList.of(this.elements);
        }
    }
    
    public static class Builder<E>
    {
        private final ArrayList<E> contents;
        
        public Builder() {
            this.contents = $Lists.newArrayList();
        }
        
        public Builder<E> add(final E element) {
            $Preconditions.checkNotNull(element, (Object)"element cannot be null");
            this.contents.add(element);
            return this;
        }
        
        public Builder<E> addAll(final Iterable<? extends E> elements) {
            if (elements instanceof Collection) {
                final Collection<? extends E> collection = (Collection<? extends E>)(Collection)elements;
                this.contents.ensureCapacity(this.contents.size() + collection.size());
            }
            for (final E elem : elements) {
                $Preconditions.checkNotNull(elem, (Object)"elements contains a null");
                this.contents.add(elem);
            }
            return this;
        }
        
        public $ImmutableList<E> build() {
            return $ImmutableList.copyOf((Iterable<? extends E>)this.contents);
        }
    }
}
