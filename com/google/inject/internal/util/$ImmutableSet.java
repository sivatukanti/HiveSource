// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import java.util.Set;

public abstract class $ImmutableSet<E> extends $ImmutableCollection<E> implements Set<E>
{
    private static final $ImmutableSet<?> EMPTY_IMMUTABLE_SET;
    
    public static <E> $ImmutableSet<E> of() {
        return ($ImmutableSet<E>)$ImmutableSet.EMPTY_IMMUTABLE_SET;
    }
    
    public static <E> $ImmutableSet<E> of(final E element) {
        return new SingletonImmutableSet<E>(element, element.hashCode());
    }
    
    public static <E> $ImmutableSet<E> of(final E... elements) {
        switch (elements.length) {
            case 0: {
                return of();
            }
            case 1: {
                return of(elements[0]);
            }
            default: {
                return create((Iterable<? extends E>)Arrays.asList(elements), elements.length);
            }
        }
    }
    
    public static <E> $ImmutableSet<E> copyOf(final Iterable<? extends E> elements) {
        if (elements instanceof $ImmutableSet) {
            final $ImmutableSet<E> set = ($ImmutableSet<E>)($ImmutableSet)elements;
            return set;
        }
        return copyOfInternal((Collection<? extends E>)$Collections2.toCollection((Iterable<? extends E>)elements));
    }
    
    public static <E> $ImmutableSet<E> copyOf(final Iterator<? extends E> elements) {
        final Collection<E> list = (Collection<E>)$Lists.newArrayList((Iterator<?>)elements);
        return copyOfInternal((Collection<? extends E>)list);
    }
    
    private static <E> $ImmutableSet<E> copyOfInternal(final Collection<? extends E> collection) {
        switch (collection.size()) {
            case 0: {
                return of();
            }
            case 1: {
                return of((E)collection.iterator().next());
            }
            default: {
                return create((Iterable<? extends E>)collection, collection.size());
            }
        }
    }
    
    $ImmutableSet() {
    }
    
    boolean isHashCodeFast() {
        return false;
    }
    
    @Override
    public boolean equals(@$Nullable final Object object) {
        return object == this || ((!(object instanceof $ImmutableSet) || !this.isHashCodeFast() || !(($ImmutableSet)object).isHashCodeFast() || this.hashCode() == object.hashCode()) && $Collections2.setEquals(this, object));
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        for (final Object o : this) {
            hashCode += o.hashCode();
        }
        return hashCode;
    }
    
    @Override
    public abstract $UnmodifiableIterator<E> iterator();
    
    @Override
    public String toString() {
        if (this.isEmpty()) {
            return "[]";
        }
        final Iterator<E> iterator = this.iterator();
        final StringBuilder result = new StringBuilder(this.size() * 16);
        result.append('[').append(iterator.next().toString());
        for (int i = 1; i < this.size(); ++i) {
            result.append(", ").append(iterator.next().toString());
        }
        return result.append(']').toString();
    }
    
    private static <E> $ImmutableSet<E> create(final Iterable<? extends E> iterable, final int count) {
        final int tableSize = $Hashing.chooseTableSize(count);
        final Object[] table = new Object[tableSize];
        final int mask = tableSize - 1;
        final List<E> elements = new ArrayList<E>(count);
        int hashCode = 0;
        for (final E element : iterable) {
            final int hash = element.hashCode();
            int i = $Hashing.smear(hash);
            while (true) {
                final int index = i & mask;
                final Object value = table[index];
                if (value == null) {
                    elements.add((E)(table[index] = element));
                    hashCode += hash;
                    break;
                }
                if (value.equals(element)) {
                    break;
                }
                ++i;
            }
        }
        return ($ImmutableSet<E>)((elements.size() == 1) ? new SingletonImmutableSet<Object>(elements.get(0), hashCode) : new RegularImmutableSet<Object>(elements.toArray(), hashCode, table, mask));
    }
    
    @Override
    Object writeReplace() {
        return new SerializedForm(this.toArray());
    }
    
    public static <E> Builder<E> builder() {
        return new Builder<E>();
    }
    
    static {
        EMPTY_IMMUTABLE_SET = new EmptyImmutableSet();
    }
    
    private static final class EmptyImmutableSet extends $ImmutableSet<Object>
    {
        private static final Object[] EMPTY_ARRAY;
        
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
            return EmptyImmutableSet.EMPTY_ARRAY;
        }
        
        @Override
        public <T> T[] toArray(final T[] a) {
            if (a.length > 0) {
                a[0] = null;
            }
            return a;
        }
        
        @Override
        public boolean containsAll(final Collection<?> targets) {
            return targets.isEmpty();
        }
        
        @Override
        public boolean equals(@$Nullable final Object object) {
            if (object instanceof Set) {
                final Set<?> that = (Set<?>)object;
                return that.isEmpty();
            }
            return false;
        }
        
        @Override
        public final int hashCode() {
            return 0;
        }
        
        @Override
        boolean isHashCodeFast() {
            return true;
        }
        
        @Override
        public String toString() {
            return "[]";
        }
        
        static {
            EMPTY_ARRAY = new Object[0];
        }
    }
    
    private static final class SingletonImmutableSet<E> extends $ImmutableSet<E>
    {
        final E element;
        final int hashCode;
        
        SingletonImmutableSet(final E element, final int hashCode) {
            this.element = element;
            this.hashCode = hashCode;
        }
        
        public int size() {
            return 1;
        }
        
        @Override
        public boolean isEmpty() {
            return false;
        }
        
        @Override
        public boolean contains(final Object target) {
            return this.element.equals(target);
        }
        
        @Override
        public $UnmodifiableIterator<E> iterator() {
            return $Iterators.singletonIterator(this.element);
        }
        
        @Override
        public Object[] toArray() {
            return new Object[] { this.element };
        }
        
        @Override
        public <T> T[] toArray(T[] array) {
            if (array.length == 0) {
                array = $ObjectArrays.newArray(array, 1);
            }
            else if (array.length > 1) {
                array[1] = null;
            }
            array[0] = (T)this.element;
            return array;
        }
        
        @Override
        public boolean equals(@$Nullable final Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof Set) {
                final Set<?> that = (Set<?>)object;
                return that.size() == 1 && this.element.equals(that.iterator().next());
            }
            return false;
        }
        
        @Override
        public final int hashCode() {
            return this.hashCode;
        }
        
        @Override
        boolean isHashCodeFast() {
            return true;
        }
        
        @Override
        public String toString() {
            final String elementToString = this.element.toString();
            return new StringBuilder(elementToString.length() + 2).append('[').append(elementToString).append(']').toString();
        }
    }
    
    abstract static class ArrayImmutableSet<E> extends $ImmutableSet<E>
    {
        final Object[] elements;
        
        ArrayImmutableSet(final Object[] elements) {
            this.elements = elements;
        }
        
        public int size() {
            return this.elements.length;
        }
        
        @Override
        public boolean isEmpty() {
            return false;
        }
        
        @Override
        public $UnmodifiableIterator<E> iterator() {
            return $Iterators.forArray((E[])this.elements);
        }
        
        @Override
        public Object[] toArray() {
            final Object[] array = new Object[this.size()];
            System.arraycopy(this.elements, 0, array, 0, this.size());
            return array;
        }
        
        @Override
        public <T> T[] toArray(T[] array) {
            final int size = this.size();
            if (array.length < size) {
                array = $ObjectArrays.newArray(array, size);
            }
            else if (array.length > size) {
                array[size] = null;
            }
            System.arraycopy(this.elements, 0, array, 0, size);
            return array;
        }
        
        @Override
        public boolean containsAll(final Collection<?> targets) {
            if (targets == this) {
                return true;
            }
            if (!(targets instanceof ArrayImmutableSet)) {
                return super.containsAll(targets);
            }
            if (targets.size() > this.size()) {
                return false;
            }
            for (final Object target : ((ArrayImmutableSet)targets).elements) {
                if (!this.contains(target)) {
                    return false;
                }
            }
            return true;
        }
    }
    
    private static final class RegularImmutableSet<E> extends ArrayImmutableSet<E>
    {
        final Object[] table;
        final int mask;
        final int hashCode;
        
        RegularImmutableSet(final Object[] elements, final int hashCode, final Object[] table, final int mask) {
            super(elements);
            this.table = table;
            this.mask = mask;
            this.hashCode = hashCode;
        }
        
        @Override
        public boolean contains(final Object target) {
            if (target == null) {
                return false;
            }
            int i = $Hashing.smear(target.hashCode());
            while (true) {
                final Object candidate = this.table[i & this.mask];
                if (candidate == null) {
                    return false;
                }
                if (candidate.equals(target)) {
                    return true;
                }
                ++i;
            }
        }
        
        @Override
        public int hashCode() {
            return this.hashCode;
        }
        
        @Override
        boolean isHashCodeFast() {
            return true;
        }
    }
    
    abstract static class TransformedImmutableSet<D, E> extends $ImmutableSet<E>
    {
        final D[] source;
        final int hashCode;
        
        TransformedImmutableSet(final D[] source, final int hashCode) {
            this.source = source;
            this.hashCode = hashCode;
        }
        
        abstract E transform(final D p0);
        
        public int size() {
            return this.source.length;
        }
        
        @Override
        public boolean isEmpty() {
            return false;
        }
        
        @Override
        public $UnmodifiableIterator<E> iterator() {
            final Iterator<E> iterator = new $AbstractIterator<E>() {
                int index = 0;
                
                @Override
                protected E computeNext() {
                    return (this.index < TransformedImmutableSet.this.source.length) ? TransformedImmutableSet.this.transform(TransformedImmutableSet.this.source[this.index++]) : this.endOfData();
                }
            };
            return $Iterators.unmodifiableIterator(iterator);
        }
        
        @Override
        public Object[] toArray() {
            return this.toArray(new Object[this.size()]);
        }
        
        @Override
        public <T> T[] toArray(T[] array) {
            final int size = this.size();
            if (array.length < size) {
                array = $ObjectArrays.newArray(array, size);
            }
            else if (array.length > size) {
                array[size] = null;
            }
            for (int i = 0; i < this.source.length; ++i) {
                array[i] = this.transform(this.source[i]);
            }
            return array;
        }
        
        @Override
        public final int hashCode() {
            return this.hashCode;
        }
        
        @Override
        boolean isHashCodeFast() {
            return true;
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
            return $ImmutableSet.of(this.elements);
        }
    }
    
    public static class Builder<E>
    {
        final ArrayList<E> contents;
        
        public Builder() {
            this.contents = $Lists.newArrayList();
        }
        
        public Builder<E> add(final E element) {
            $Preconditions.checkNotNull(element, (Object)"element cannot be null");
            this.contents.add(element);
            return this;
        }
        
        public Builder<E> add(final E... elements) {
            $Preconditions.checkNotNull(elements, (Object)"elements cannot be null");
            final List<E> elemsAsList = Arrays.asList(elements);
            $Preconditions.checkContentsNotNull(elemsAsList, (Object)"elements cannot contain null");
            this.contents.addAll((Collection<? extends E>)elemsAsList);
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
        
        public Builder<E> addAll(final Iterator<? extends E> elements) {
            while (elements.hasNext()) {
                final E element = (E)elements.next();
                $Preconditions.checkNotNull(element, (Object)"element cannot be null");
                this.contents.add(element);
            }
            return this;
        }
        
        public $ImmutableSet<E> build() {
            return $ImmutableSet.copyOf((Iterable<? extends E>)this.contents);
        }
    }
}
