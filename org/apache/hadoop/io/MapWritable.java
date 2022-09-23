// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import org.apache.hadoop.util.ReflectionUtils;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.util.Map;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class MapWritable extends AbstractMapWritable implements Map<Writable, Writable>
{
    private Map<Writable, Writable> instance;
    
    public MapWritable() {
        this.instance = new HashMap<Writable, Writable>();
    }
    
    public MapWritable(final MapWritable other) {
        this();
        this.copy(other);
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
    public Set<Entry<Writable, Writable>> entrySet() {
        return this.instance.entrySet();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MapWritable) {
            final MapWritable map = (MapWritable)obj;
            return this.size() == map.size() && this.entrySet().equals(map.entrySet());
        }
        return false;
    }
    
    @Override
    public Writable get(final Object key) {
        return this.instance.get(key);
    }
    
    @Override
    public int hashCode() {
        return 1 + this.instance.hashCode();
    }
    
    @Override
    public boolean isEmpty() {
        return this.instance.isEmpty();
    }
    
    @Override
    public Set<Writable> keySet() {
        return this.instance.keySet();
    }
    
    @Override
    public Writable put(final Writable key, final Writable value) {
        this.addToMap(key.getClass());
        this.addToMap(value.getClass());
        return this.instance.put(key, value);
    }
    
    @Override
    public void putAll(final Map<? extends Writable, ? extends Writable> t) {
        for (final Entry<? extends Writable, ? extends Writable> e : t.entrySet()) {
            this.put((Writable)e.getKey(), (Writable)e.getValue());
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
    public void write(final DataOutput out) throws IOException {
        super.write(out);
        out.writeInt(this.instance.size());
        for (final Entry<Writable, Writable> e : this.instance.entrySet()) {
            out.writeByte(this.getId(e.getKey().getClass()));
            e.getKey().write(out);
            out.writeByte(this.getId(e.getValue().getClass()));
            e.getValue().write(out);
        }
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        super.readFields(in);
        this.instance.clear();
        for (int entries = in.readInt(), i = 0; i < entries; ++i) {
            final Writable key = ReflectionUtils.newInstance(this.getClass(in.readByte()), this.getConf());
            key.readFields(in);
            final Writable value = ReflectionUtils.newInstance(this.getClass(in.readByte()), this.getConf());
            value.readFields(in);
            this.instance.put(key, value);
        }
    }
    
    @Override
    public String toString() {
        return this.instance.toString();
    }
}
