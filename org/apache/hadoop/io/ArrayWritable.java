// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.io.DataOutput;
import java.io.IOException;
import java.io.DataInput;
import java.lang.reflect.Array;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class ArrayWritable implements Writable
{
    private Class<? extends Writable> valueClass;
    private Writable[] values;
    
    public ArrayWritable(final Class<? extends Writable> valueClass) {
        if (valueClass == null) {
            throw new IllegalArgumentException("null valueClass");
        }
        this.valueClass = valueClass;
    }
    
    public ArrayWritable(final Class<? extends Writable> valueClass, final Writable[] values) {
        this(valueClass);
        this.values = values;
    }
    
    public ArrayWritable(final String[] strings) {
        this(UTF8.class, new Writable[strings.length]);
        for (int i = 0; i < strings.length; ++i) {
            this.values[i] = new UTF8(strings[i]);
        }
    }
    
    public Class getValueClass() {
        return this.valueClass;
    }
    
    public String[] toStrings() {
        final String[] strings = new String[this.values.length];
        for (int i = 0; i < this.values.length; ++i) {
            strings[i] = this.values[i].toString();
        }
        return strings;
    }
    
    public Object toArray() {
        final Object result = Array.newInstance(this.valueClass, this.values.length);
        for (int i = 0; i < this.values.length; ++i) {
            Array.set(result, i, this.values[i]);
        }
        return result;
    }
    
    public void set(final Writable[] values) {
        this.values = values;
    }
    
    public Writable[] get() {
        return this.values;
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.values = new Writable[in.readInt()];
        for (int i = 0; i < this.values.length; ++i) {
            final Writable value = WritableFactories.newInstance(this.valueClass);
            value.readFields(in);
            this.values[i] = value;
        }
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeInt(this.values.length);
        for (int i = 0; i < this.values.length; ++i) {
            this.values[i].write(out);
        }
    }
}
