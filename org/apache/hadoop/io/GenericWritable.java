// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.util.ReflectionUtils;
import java.io.DataInput;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configurable;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class GenericWritable implements Writable, Configurable
{
    private static final byte NOT_SET = -1;
    private byte type;
    private Writable instance;
    private Configuration conf;
    
    public GenericWritable() {
        this.type = -1;
        this.conf = null;
    }
    
    public void set(final Writable obj) {
        this.instance = obj;
        final Class<? extends Writable> instanceClazz = this.instance.getClass();
        final Class<? extends Writable>[] clazzes = this.getTypes();
        for (int i = 0; i < clazzes.length; ++i) {
            final Class<? extends Writable> clazz = clazzes[i];
            if (clazz.equals(instanceClazz)) {
                this.type = (byte)i;
                return;
            }
        }
        throw new RuntimeException("The type of instance is: " + this.instance.getClass() + ", which is NOT registered.");
    }
    
    public Writable get() {
        return this.instance;
    }
    
    @Override
    public String toString() {
        return "GW[" + ((this.instance != null) ? ("class=" + this.instance.getClass().getName() + ",value=" + this.instance.toString()) : "(null)") + "]";
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.type = in.readByte();
        final Class<? extends Writable> clazz = this.getTypes()[this.type & 0xFF];
        try {
            this.instance = ReflectionUtils.newInstance(clazz, this.conf);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Cannot initialize the class: " + clazz);
        }
        this.instance.readFields(in);
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        if (this.type == -1 || this.instance == null) {
            throw new IOException("The GenericWritable has NOT been set correctly. type=" + this.type + ", instance=" + this.instance);
        }
        out.writeByte(this.type);
        this.instance.write(out);
    }
    
    protected abstract Class<? extends Writable>[] getTypes();
    
    @Override
    public Configuration getConf() {
        return this.conf;
    }
    
    @Override
    public void setConf(final Configuration conf) {
        this.conf = conf;
    }
}
