// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.io.DataOutput;
import java.io.IOException;
import java.io.DataInput;
import java.util.Iterator;
import org.apache.hadoop.conf.Configuration;
import java.util.EnumSet;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configurable;
import java.util.AbstractCollection;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class EnumSetWritable<E extends Enum<E>> extends AbstractCollection<E> implements Writable, Configurable
{
    private EnumSet<E> value;
    private transient Class<E> elementType;
    private transient Configuration conf;
    
    EnumSetWritable() {
    }
    
    @Override
    public Iterator<E> iterator() {
        return this.value.iterator();
    }
    
    @Override
    public int size() {
        return this.value.size();
    }
    
    @Override
    public boolean add(final E e) {
        if (this.value == null) {
            this.set(this.value = EnumSet.of(e), null);
        }
        return this.value.add(e);
    }
    
    public EnumSetWritable(final EnumSet<E> value, final Class<E> elementType) {
        this.set(value, elementType);
    }
    
    public EnumSetWritable(final EnumSet<E> value) {
        this(value, null);
    }
    
    public void set(final EnumSet<E> value, final Class<E> elementType) {
        if ((value == null || value.size() == 0) && this.elementType == null && elementType == null) {
            throw new IllegalArgumentException("The EnumSet argument is null, or is an empty set but with no elementType provided.");
        }
        if ((this.value = value) != null && value.size() > 0) {
            final Iterator<E> iterator = value.iterator();
            this.elementType = iterator.next().getDeclaringClass();
        }
        else if (elementType != null) {
            this.elementType = elementType;
        }
    }
    
    public EnumSet<E> get() {
        return this.value;
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        final int length = in.readInt();
        if (length == -1) {
            this.value = null;
        }
        else if (length == 0) {
            this.elementType = (Class<E>)ObjectWritable.loadClass(this.conf, WritableUtils.readString(in));
            this.value = EnumSet.noneOf(this.elementType);
        }
        else {
            final E first = (E)ObjectWritable.readObject(in, this.conf);
            this.value = EnumSet.of(first);
            for (int i = 1; i < length; ++i) {
                this.value.add((E)ObjectWritable.readObject(in, this.conf));
            }
        }
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        if (this.value == null) {
            out.writeInt(-1);
            WritableUtils.writeString(out, this.elementType.getName());
        }
        else {
            final Object[] array = this.value.toArray();
            final int length = array.length;
            out.writeInt(length);
            if (length == 0) {
                if (this.elementType == null) {
                    throw new UnsupportedOperationException("Unable to serialize empty EnumSet with no element type provided.");
                }
                WritableUtils.writeString(out, this.elementType.getName());
            }
            for (int i = 0; i < length; ++i) {
                ObjectWritable.writeObject(out, array[i], array[i].getClass(), this.conf);
            }
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            throw new IllegalArgumentException("null argument passed in equal().");
        }
        if (!(o instanceof EnumSetWritable)) {
            return false;
        }
        final EnumSetWritable<?> other = (EnumSetWritable<?>)o;
        return this == o || this.value == other.value || (this.value != null && this.value.equals(other.value));
    }
    
    public Class<E> getElementType() {
        return this.elementType;
    }
    
    @Override
    public int hashCode() {
        if (this.value == null) {
            return 0;
        }
        return this.value.hashCode();
    }
    
    @Override
    public String toString() {
        if (this.value == null) {
            return "(null)";
        }
        return this.value.toString();
    }
    
    @Override
    public Configuration getConf() {
        return this.conf;
    }
    
    @Override
    public void setConf(final Configuration conf) {
        this.conf = conf;
    }
    
    static {
        WritableFactories.setFactory(EnumSetWritable.class, new WritableFactory() {
            @Override
            public Writable newInstance() {
                return new EnumSetWritable<Object>();
            }
        });
    }
}
