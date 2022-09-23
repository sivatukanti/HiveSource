// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.io.DataInput;
import java.io.DataOutput;
import com.google.common.annotations.VisibleForTesting;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configurable;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class AbstractMapWritable implements Writable, Configurable
{
    private AtomicReference<Configuration> conf;
    @VisibleForTesting
    Map<Class<?>, Byte> classToIdMap;
    @VisibleForTesting
    Map<Byte, Class<?>> idToClassMap;
    private volatile byte newClasses;
    
    byte getNewClasses() {
        return this.newClasses;
    }
    
    private synchronized void addToMap(final Class<?> clazz, final byte id) {
        if (this.classToIdMap.containsKey(clazz)) {
            final byte b = this.classToIdMap.get(clazz);
            if (b != id) {
                throw new IllegalArgumentException("Class " + clazz.getName() + " already registered but maps to " + b + " and not " + id);
            }
        }
        if (this.idToClassMap.containsKey(id)) {
            final Class<?> c = this.idToClassMap.get(id);
            if (!c.equals(clazz)) {
                throw new IllegalArgumentException("Id " + id + " exists but maps to " + c.getName() + " and not " + clazz.getName());
            }
        }
        this.classToIdMap.put(clazz, id);
        this.idToClassMap.put(id, clazz);
    }
    
    protected synchronized void addToMap(final Class<?> clazz) {
        if (this.classToIdMap.containsKey(clazz)) {
            return;
        }
        if (this.newClasses + 1 > 127) {
            throw new IndexOutOfBoundsException("adding an additional class would exceed the maximum number allowed");
        }
        final byte newClasses = (byte)(this.newClasses + 1);
        this.newClasses = newClasses;
        final byte id = newClasses;
        this.addToMap(clazz, id);
    }
    
    protected Class<?> getClass(final byte id) {
        return this.idToClassMap.get(id);
    }
    
    protected byte getId(final Class<?> clazz) {
        return (byte)(this.classToIdMap.containsKey(clazz) ? ((byte)this.classToIdMap.get(clazz)) : -1);
    }
    
    protected synchronized void copy(final Writable other) {
        if (other != null) {
            try {
                final DataOutputBuffer out = new DataOutputBuffer();
                other.write(out);
                final DataInputBuffer in = new DataInputBuffer();
                in.reset(out.getData(), out.getLength());
                this.readFields(in);
                return;
            }
            catch (IOException e) {
                throw new IllegalArgumentException("map cannot be copied: " + e.getMessage());
            }
            throw new IllegalArgumentException("source map cannot be null");
        }
        throw new IllegalArgumentException("source map cannot be null");
    }
    
    protected AbstractMapWritable() {
        this.classToIdMap = new ConcurrentHashMap<Class<?>, Byte>();
        this.idToClassMap = new ConcurrentHashMap<Byte, Class<?>>();
        this.newClasses = 0;
        this.conf = new AtomicReference<Configuration>();
        this.addToMap(ArrayWritable.class, (byte)(-127));
        this.addToMap(BooleanWritable.class, (byte)(-126));
        this.addToMap(BytesWritable.class, (byte)(-125));
        this.addToMap(FloatWritable.class, (byte)(-124));
        this.addToMap(IntWritable.class, (byte)(-123));
        this.addToMap(LongWritable.class, (byte)(-122));
        this.addToMap(MapWritable.class, (byte)(-121));
        this.addToMap(MD5Hash.class, (byte)(-120));
        this.addToMap(NullWritable.class, (byte)(-119));
        this.addToMap(ObjectWritable.class, (byte)(-118));
        this.addToMap(SortedMapWritable.class, (byte)(-117));
        this.addToMap(Text.class, (byte)(-116));
        this.addToMap(TwoDArrayWritable.class, (byte)(-115));
        this.addToMap(VIntWritable.class, (byte)(-114));
        this.addToMap(VLongWritable.class, (byte)(-113));
    }
    
    @Override
    public Configuration getConf() {
        return this.conf.get();
    }
    
    @Override
    public void setConf(final Configuration conf) {
        this.conf.set(conf);
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeByte(this.newClasses);
        for (byte i = 1; i <= this.newClasses; ++i) {
            out.writeByte(i);
            out.writeUTF(this.getClass(i).getName());
        }
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.newClasses = in.readByte();
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        for (int i = 0; i < this.newClasses; ++i) {
            final byte id = in.readByte();
            final String className = in.readUTF();
            try {
                this.addToMap(classLoader.loadClass(className), id);
            }
            catch (ClassNotFoundException e) {
                throw new IOException(e);
            }
        }
    }
}
