// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.columnar;

import org.apache.hadoop.io.WritableFactories;
import org.apache.hadoop.io.WritableFactory;
import java.io.DataOutput;
import java.io.IOException;
import java.io.DataInput;
import java.util.Arrays;
import org.apache.hadoop.io.Writable;

public class BytesRefArrayWritable implements Writable, Comparable<BytesRefArrayWritable>
{
    private BytesRefWritable[] bytesRefWritables;
    private int valid;
    
    public BytesRefArrayWritable(final int capacity) {
        this.bytesRefWritables = null;
        this.valid = 0;
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity can not be negative.");
        }
        this.bytesRefWritables = new BytesRefWritable[0];
        this.ensureCapacity(capacity);
    }
    
    public BytesRefArrayWritable() {
        this(10);
    }
    
    public int size() {
        return this.valid;
    }
    
    public BytesRefWritable get(final int index) {
        if (index >= this.valid) {
            throw new IndexOutOfBoundsException("This BytesRefArrayWritable only has " + this.valid + " valid values.");
        }
        return this.bytesRefWritables[index];
    }
    
    public BytesRefWritable unCheckedGet(final int index) {
        return this.bytesRefWritables[index];
    }
    
    public void set(final int index, final BytesRefWritable bytesRefWritable) {
        this.ensureCapacity(index + 1);
        this.bytesRefWritables[index] = bytesRefWritable;
        if (this.valid <= index) {
            this.valid = index + 1;
        }
    }
    
    @Override
    public int compareTo(final BytesRefArrayWritable other) {
        if (other == null) {
            throw new IllegalArgumentException("Argument can not be null.");
        }
        if (this == other) {
            return 0;
        }
        final int sizeDiff = this.valid - other.valid;
        if (sizeDiff != 0) {
            return sizeDiff;
        }
        for (int i = 0; i < this.valid; ++i) {
            final int res = this.bytesRefWritables[i].compareTo(other.bytesRefWritables[i]);
            if (res != 0) {
                return res;
            }
        }
        return 0;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof BytesRefArrayWritable && this.compareTo((BytesRefArrayWritable)o) == 0;
    }
    
    public void clear() {
        this.valid = 0;
    }
    
    public void resetValid(final int newValidCapacity) {
        this.ensureCapacity(newValidCapacity);
        this.valid = newValidCapacity;
    }
    
    protected void ensureCapacity(final int newCapacity) {
        int size = this.bytesRefWritables.length;
        if (size < newCapacity) {
            this.bytesRefWritables = Arrays.copyOf(this.bytesRefWritables, newCapacity);
            while (size < newCapacity) {
                this.bytesRefWritables[size] = new BytesRefWritable();
                ++size;
            }
        }
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        final int count = in.readInt();
        this.ensureCapacity(count);
        for (int i = 0; i < count; ++i) {
            this.bytesRefWritables[i].readFields(in);
        }
        this.valid = count;
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeInt(this.valid);
        for (int i = 0; i < this.valid; ++i) {
            final BytesRefWritable cu = this.bytesRefWritables[i];
            cu.write(out);
        }
    }
    
    static {
        WritableFactories.setFactory(BytesRefArrayWritable.class, new WritableFactory() {
            @Override
            public Writable newInstance() {
                return new BytesRefArrayWritable();
            }
        });
    }
}
