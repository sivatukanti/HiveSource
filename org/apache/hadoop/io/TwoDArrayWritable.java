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
public class TwoDArrayWritable implements Writable
{
    private Class valueClass;
    private Writable[][] values;
    
    public TwoDArrayWritable(final Class valueClass) {
        this.valueClass = valueClass;
    }
    
    public TwoDArrayWritable(final Class valueClass, final Writable[][] values) {
        this(valueClass);
        this.values = values;
    }
    
    public Object toArray() {
        final int[] dimensions = { this.values.length, 0 };
        final Object result = Array.newInstance(this.valueClass, dimensions);
        for (int i = 0; i < this.values.length; ++i) {
            final Object resultRow = Array.newInstance(this.valueClass, this.values[i].length);
            Array.set(result, i, resultRow);
            for (int j = 0; j < this.values[i].length; ++j) {
                Array.set(resultRow, j, this.values[i][j]);
            }
        }
        return result;
    }
    
    public void set(final Writable[][] values) {
        this.values = values;
    }
    
    public Writable[][] get() {
        return this.values;
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.values = new Writable[in.readInt()][];
        for (int i = 0; i < this.values.length; ++i) {
            this.values[i] = new Writable[in.readInt()];
        }
        for (int i = 0; i < this.values.length; ++i) {
            for (int j = 0; j < this.values[i].length; ++j) {
                Writable value;
                try {
                    value = this.valueClass.newInstance();
                }
                catch (InstantiationException e) {
                    throw new RuntimeException(e.toString());
                }
                catch (IllegalAccessException e2) {
                    throw new RuntimeException(e2.toString());
                }
                value.readFields(in);
                this.values[i][j] = value;
            }
        }
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeInt(this.values.length);
        for (int i = 0; i < this.values.length; ++i) {
            out.writeInt(this.values[i].length);
        }
        for (int i = 0; i < this.values.length; ++i) {
            for (int j = 0; j < this.values[i].length; ++j) {
                this.values[i][j].write(out);
            }
        }
    }
}
