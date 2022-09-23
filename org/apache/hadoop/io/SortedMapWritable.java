// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.util.ReflectionUtils;
import java.io.DataInput;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;
import java.util.TreeMap;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.util.SortedMap;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class SortedMapWritable<K extends WritableComparable<? super K>> extends AbstractMapWritable implements SortedMap<K, Writable>
{
    private SortedMap<K, Writable> instance;
    
    public SortedMapWritable() {
        this.instance = new TreeMap<K, Writable>();
    }
    
    public SortedMapWritable(final SortedMapWritable<K> other) {
        this();
        this.copy(other);
    }
    
    @Override
    public Comparator<? super K> comparator() {
        return null;
    }
    
    @Override
    public K firstKey() {
        return this.instance.firstKey();
    }
    
    @Override
    public SortedMap<K, Writable> headMap(final K toKey) {
        return this.instance.headMap(toKey);
    }
    
    @Override
    public K lastKey() {
        return this.instance.lastKey();
    }
    
    @Override
    public SortedMap<K, Writable> subMap(final K fromKey, final K toKey) {
        return this.instance.subMap(fromKey, toKey);
    }
    
    @Override
    public SortedMap<K, Writable> tailMap(final K fromKey) {
        return this.instance.tailMap(fromKey);
    }
    
    @Override
    public void clear() {
        this.instance.clear();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.instance.containsKey(key);
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return this.instance.containsValue(value);
    }
    
    @Override
    public Set<Map.Entry<K, Writable>> entrySet() {
        return this.instance.entrySet();
    }
    
    @Override
    public Writable get(final Object key) {
        return this.instance.get(key);
    }
    
    @Override
    public boolean isEmpty() {
        return this.instance.isEmpty();
    }
    
    @Override
    public Set<K> keySet() {
        return this.instance.keySet();
    }
    
    @Override
    public Writable put(final K key, final Writable value) {
        this.addToMap(key.getClass());
        this.addToMap(value.getClass());
        return this.instance.put(key, value);
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends Writable> t) {
        for (final Map.Entry<? extends K, ? extends Writable> e : t.entrySet()) {
            this.put((K)e.getKey(), (Writable)e.getValue());
        }
    }
    
    @Override
    public Writable remove(final Object key) {
        return this.instance.remove(key);
    }
    
    @Override
    public int size() {
        return this.instance.size();
    }
    
    @Override
    public Collection<Writable> values() {
        return this.instance.values();
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        super.readFields(in);
        for (int entries = in.readInt(), i = 0; i < entries; ++i) {
            final K key = ReflectionUtils.newInstance(this.getClass(in.readByte()), this.getConf());
            key.readFields(in);
            final Writable value = ReflectionUtils.newInstance(this.getClass(in.readByte()), this.getConf());
            value.readFields(in);
            this.instance.put(key, value);
        }
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        super.write(out);
        out.writeInt(this.instance.size());
        for (final Map.Entry<K, Writable> e : this.instance.entrySet()) {
            out.writeByte(this.getId(e.getKey().getClass()));
            e.getKey().write(out);
            out.writeByte(this.getId(e.getValue().getClass()));
            e.getValue().write(out);
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof SortedMapWritable) {
            final Map<?, ?> map = (Map<?, ?>)obj;
            return this.size() == map.size() && this.entrySet().equals(map.entrySet());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.instance.hashCode();
    }
}
