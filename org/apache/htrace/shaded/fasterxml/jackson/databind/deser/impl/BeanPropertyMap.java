// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl;

import java.util.NoSuchElementException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import java.util.ArrayList;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.NameTransformer;
import java.util.Iterator;
import java.util.Collection;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.SettableBeanProperty;

public final class BeanPropertyMap implements Iterable<SettableBeanProperty>, Serializable
{
    private static final long serialVersionUID = 1L;
    private final Bucket[] _buckets;
    private final int _hashMask;
    private final int _size;
    private int _nextBucketIndex;
    
    public BeanPropertyMap(final Collection<SettableBeanProperty> properties) {
        this._nextBucketIndex = 0;
        this._size = properties.size();
        final int bucketCount = findSize(this._size);
        this._hashMask = bucketCount - 1;
        final Bucket[] buckets = new Bucket[bucketCount];
        for (final SettableBeanProperty property : properties) {
            final String key = property.getName();
            final int index = key.hashCode() & this._hashMask;
            buckets[index] = new Bucket(buckets[index], key, property, this._nextBucketIndex++);
        }
        this._buckets = buckets;
    }
    
    private BeanPropertyMap(final Bucket[] buckets, final int size, final int index) {
        this._nextBucketIndex = 0;
        this._buckets = buckets;
        this._size = size;
        this._hashMask = buckets.length - 1;
        this._nextBucketIndex = index;
    }
    
    public BeanPropertyMap withProperty(final SettableBeanProperty newProperty) {
        final int bcount = this._buckets.length;
        final Bucket[] newBuckets = new Bucket[bcount];
        System.arraycopy(this._buckets, 0, newBuckets, 0, bcount);
        final String propName = newProperty.getName();
        final SettableBeanProperty oldProp = this.find(newProperty.getName());
        if (oldProp == null) {
            final int index = propName.hashCode() & this._hashMask;
            newBuckets[index] = new Bucket(newBuckets[index], propName, newProperty, this._nextBucketIndex++);
            return new BeanPropertyMap(newBuckets, this._size + 1, this._nextBucketIndex);
        }
        final BeanPropertyMap newMap = new BeanPropertyMap(newBuckets, bcount, this._nextBucketIndex);
        newMap.replace(newProperty);
        return newMap;
    }
    
    public BeanPropertyMap renameAll(final NameTransformer transformer) {
        if (transformer == null || transformer == NameTransformer.NOP) {
            return this;
        }
        final Iterator<SettableBeanProperty> it = this.iterator();
        final ArrayList<SettableBeanProperty> newProps = new ArrayList<SettableBeanProperty>();
        while (it.hasNext()) {
            SettableBeanProperty prop = it.next();
            final String newName = transformer.transform(prop.getName());
            prop = prop.withSimpleName(newName);
            final JsonDeserializer<?> deser = prop.getValueDeserializer();
            if (deser != null) {
                final JsonDeserializer<Object> newDeser = (JsonDeserializer<Object>)deser.unwrappingDeserializer(transformer);
                if (newDeser != deser) {
                    prop = prop.withValueDeserializer(newDeser);
                }
            }
            newProps.add(prop);
        }
        return new BeanPropertyMap(newProps);
    }
    
    public BeanPropertyMap assignIndexes() {
        int index = 0;
        for (Bucket bucket : this._buckets) {
            while (bucket != null) {
                bucket.value.assignIndex(index++);
                bucket = bucket.next;
            }
        }
        return this;
    }
    
    private static final int findSize(final int size) {
        int needed;
        int result;
        for (needed = ((size <= 32) ? (size + size) : (size + (size >> 2))), result = 2; result < needed; result += result) {}
        return result;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Properties=[");
        int count = 0;
        for (final SettableBeanProperty prop : this.getPropertiesInInsertionOrder()) {
            if (prop != null) {
                if (count++ > 0) {
                    sb.append(", ");
                }
                sb.append(prop.getName());
                sb.append('(');
                sb.append(prop.getType());
                sb.append(')');
            }
        }
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    public Iterator<SettableBeanProperty> iterator() {
        return new IteratorImpl(this._buckets);
    }
    
    public SettableBeanProperty[] getPropertiesInInsertionOrder() {
        final int len = this._nextBucketIndex;
        final SettableBeanProperty[] result = new SettableBeanProperty[len];
        for (Bucket bucket : this._buckets) {
            final Bucket root = bucket;
            while (bucket != null) {
                result[bucket.index] = bucket.value;
                bucket = bucket.next;
            }
        }
        return result;
    }
    
    public int size() {
        return this._size;
    }
    
    public SettableBeanProperty find(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("Can not pass null property name");
        }
        final int index = key.hashCode() & this._hashMask;
        Bucket bucket = this._buckets[index];
        if (bucket == null) {
            return null;
        }
        if (bucket.key == key) {
            return bucket.value;
        }
        while ((bucket = bucket.next) != null) {
            if (bucket.key == key) {
                return bucket.value;
            }
        }
        return this._findWithEquals(key, index);
    }
    
    public SettableBeanProperty find(final int propertyIndex) {
        for (int i = 0, end = this._buckets.length; i < end; ++i) {
            for (Bucket bucket = this._buckets[i]; bucket != null; bucket = bucket.next) {
                if (bucket.index == propertyIndex) {
                    return bucket.value;
                }
            }
        }
        return null;
    }
    
    public void replace(final SettableBeanProperty property) {
        final String name = property.getName();
        final int index = name.hashCode() & this._buckets.length - 1;
        Bucket tail = null;
        int foundIndex = -1;
        for (Bucket bucket = this._buckets[index]; bucket != null; bucket = bucket.next) {
            if (foundIndex < 0 && bucket.key.equals(name)) {
                foundIndex = bucket.index;
            }
            else {
                tail = new Bucket(tail, bucket.key, bucket.value, bucket.index);
            }
        }
        if (foundIndex < 0) {
            throw new NoSuchElementException("No entry '" + property + "' found, can't replace");
        }
        this._buckets[index] = new Bucket(tail, name, property, foundIndex);
    }
    
    public void remove(final SettableBeanProperty property) {
        final String name = property.getName();
        final int index = name.hashCode() & this._buckets.length - 1;
        Bucket tail = null;
        boolean found = false;
        for (Bucket bucket = this._buckets[index]; bucket != null; bucket = bucket.next) {
            if (!found && bucket.key.equals(name)) {
                found = true;
            }
            else {
                tail = new Bucket(tail, bucket.key, bucket.value, bucket.index);
            }
        }
        if (!found) {
            throw new NoSuchElementException("No entry '" + property + "' found, can't remove");
        }
        this._buckets[index] = tail;
    }
    
    private SettableBeanProperty _findWithEquals(final String key, final int index) {
        for (Bucket bucket = this._buckets[index]; bucket != null; bucket = bucket.next) {
            if (key.equals(bucket.key)) {
                return bucket.value;
            }
        }
        return null;
    }
    
    private static final class Bucket implements Serializable
    {
        private static final long serialVersionUID = 1L;
        public final Bucket next;
        public final String key;
        public final SettableBeanProperty value;
        public final int index;
        
        public Bucket(final Bucket next, final String key, final SettableBeanProperty value, final int index) {
            this.next = next;
            this.key = key;
            this.value = value;
            this.index = index;
        }
    }
    
    private static final class IteratorImpl implements Iterator<SettableBeanProperty>
    {
        private final Bucket[] _buckets;
        private Bucket _currentBucket;
        private int _nextBucketIndex;
        
        public IteratorImpl(final Bucket[] buckets) {
            this._buckets = buckets;
            int i = 0;
            final int len = this._buckets.length;
            while (i < len) {
                final Bucket b = this._buckets[i++];
                if (b != null) {
                    this._currentBucket = b;
                    break;
                }
            }
            this._nextBucketIndex = i;
        }
        
        @Override
        public boolean hasNext() {
            return this._currentBucket != null;
        }
        
        @Override
        public SettableBeanProperty next() {
            final Bucket curr = this._currentBucket;
            if (curr == null) {
                throw new NoSuchElementException();
            }
            Bucket b;
            for (b = curr.next; b == null && this._nextBucketIndex < this._buckets.length; b = this._buckets[this._nextBucketIndex++]) {}
            this._currentBucket = b;
            return curr.value;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
