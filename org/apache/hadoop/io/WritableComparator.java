// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.io.IOException;
import java.io.DataInput;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.conf.Configuration;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configurable;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class WritableComparator implements RawComparator, Configurable
{
    private static final ConcurrentHashMap<Class, WritableComparator> comparators;
    private Configuration conf;
    private final Class<? extends WritableComparable> keyClass;
    private final WritableComparable key1;
    private final WritableComparable key2;
    private final DataInputBuffer buffer;
    
    public static WritableComparator get(final Class<? extends WritableComparable> c) {
        return get(c, null);
    }
    
    public static WritableComparator get(final Class<? extends WritableComparable> c, final Configuration conf) {
        WritableComparator comparator = WritableComparator.comparators.get(c);
        if (comparator == null) {
            forceInit(c);
            comparator = WritableComparator.comparators.get(c);
            if (comparator == null) {
                comparator = new WritableComparator(c, conf, true);
            }
        }
        ReflectionUtils.setConf(comparator, conf);
        return comparator;
    }
    
    @Override
    public void setConf(final Configuration conf) {
        this.conf = conf;
    }
    
    @Override
    public Configuration getConf() {
        return this.conf;
    }
    
    private static void forceInit(final Class<?> cls) {
        try {
            Class.forName(cls.getName(), true, cls.getClassLoader());
        }
        catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Can't initialize class " + cls, e);
        }
    }
    
    public static void define(final Class c, final WritableComparator comparator) {
        WritableComparator.comparators.put(c, comparator);
    }
    
    protected WritableComparator() {
        this(null);
    }
    
    protected WritableComparator(final Class<? extends WritableComparable> keyClass) {
        this(keyClass, null, false);
    }
    
    protected WritableComparator(final Class<? extends WritableComparable> keyClass, final boolean createInstances) {
        this(keyClass, null, createInstances);
    }
    
    protected WritableComparator(final Class<? extends WritableComparable> keyClass, final Configuration conf, final boolean createInstances) {
        this.keyClass = keyClass;
        this.conf = ((conf != null) ? conf : new Configuration());
        if (createInstances) {
            this.key1 = this.newKey();
            this.key2 = this.newKey();
            this.buffer = new DataInputBuffer();
        }
        else {
            final WritableComparable writableComparable = null;
            this.key2 = writableComparable;
            this.key1 = writableComparable;
            this.buffer = null;
        }
    }
    
    public Class<? extends WritableComparable> getKeyClass() {
        return this.keyClass;
    }
    
    public WritableComparable newKey() {
        return ReflectionUtils.newInstance(this.keyClass, this.conf);
    }
    
    @Override
    public int compare(final byte[] b1, final int s1, final int l1, final byte[] b2, final int s2, final int l2) {
        try {
            this.buffer.reset(b1, s1, l1);
            this.key1.readFields(this.buffer);
            this.buffer.reset(b2, s2, l2);
            this.key2.readFields(this.buffer);
            this.buffer.reset(null, 0, 0);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this.compare(this.key1, this.key2);
    }
    
    public int compare(final WritableComparable a, final WritableComparable b) {
        return a.compareTo(b);
    }
    
    @Override
    public int compare(final Object a, final Object b) {
        return this.compare((WritableComparable)a, (WritableComparable)b);
    }
    
    public static int compareBytes(final byte[] b1, final int s1, final int l1, final byte[] b2, final int s2, final int l2) {
        return FastByteComparisons.compareTo(b1, s1, l1, b2, s2, l2);
    }
    
    public static int hashBytes(final byte[] bytes, final int offset, final int length) {
        int hash = 1;
        for (int i = offset; i < offset + length; ++i) {
            hash = 31 * hash + bytes[i];
        }
        return hash;
    }
    
    public static int hashBytes(final byte[] bytes, final int length) {
        return hashBytes(bytes, 0, length);
    }
    
    public static int readUnsignedShort(final byte[] bytes, final int start) {
        return ((bytes[start] & 0xFF) << 8) + (bytes[start + 1] & 0xFF);
    }
    
    public static int readInt(final byte[] bytes, final int start) {
        return ((bytes[start] & 0xFF) << 24) + ((bytes[start + 1] & 0xFF) << 16) + ((bytes[start + 2] & 0xFF) << 8) + (bytes[start + 3] & 0xFF);
    }
    
    public static float readFloat(final byte[] bytes, final int start) {
        return Float.intBitsToFloat(readInt(bytes, start));
    }
    
    public static long readLong(final byte[] bytes, final int start) {
        return ((long)readInt(bytes, start) << 32) + ((long)readInt(bytes, start + 4) & 0xFFFFFFFFL);
    }
    
    public static double readDouble(final byte[] bytes, final int start) {
        return Double.longBitsToDouble(readLong(bytes, start));
    }
    
    public static long readVLong(final byte[] bytes, final int start) throws IOException {
        int len = bytes[start];
        if (len >= -112) {
            return len;
        }
        final boolean isNegative = len < -120;
        len = (isNegative ? (-(len + 120)) : (-(len + 112)));
        if (start + 1 + len > bytes.length) {
            throw new IOException("Not enough number of bytes for a zero-compressed integer");
        }
        long i = 0L;
        for (int idx = 0; idx < len; ++idx) {
            i <<= 8;
            i |= (bytes[start + 1 + idx] & 0xFF);
        }
        return isNegative ? (~i) : i;
    }
    
    public static int readVInt(final byte[] bytes, final int start) throws IOException {
        return (int)readVLong(bytes, start);
    }
    
    static {
        comparators = new ConcurrentHashMap<Class, WritableComparator>();
    }
}
