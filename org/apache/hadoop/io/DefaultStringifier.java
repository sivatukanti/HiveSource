// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.util.List;
import java.util.ArrayList;
import org.apache.hadoop.util.GenericsUtil;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import org.apache.commons.codec.binary.Base64;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.hadoop.io.serializer.SerializationFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.serializer.Deserializer;
import org.apache.hadoop.io.serializer.Serializer;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class DefaultStringifier<T> implements Stringifier<T>
{
    private static final String SEPARATOR = ",";
    private Serializer<T> serializer;
    private Deserializer<T> deserializer;
    private DataInputBuffer inBuf;
    private DataOutputBuffer outBuf;
    
    public DefaultStringifier(final Configuration conf, final Class<T> c) {
        final SerializationFactory factory = new SerializationFactory(conf);
        this.serializer = factory.getSerializer(c);
        this.deserializer = factory.getDeserializer(c);
        this.inBuf = new DataInputBuffer();
        this.outBuf = new DataOutputBuffer();
        try {
            this.serializer.open(this.outBuf);
            this.deserializer.open(this.inBuf);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public T fromString(final String str) throws IOException {
        try {
            final byte[] bytes = Base64.decodeBase64(str.getBytes("UTF-8"));
            this.inBuf.reset(bytes, bytes.length);
            final T restored = this.deserializer.deserialize(null);
            return restored;
        }
        catch (UnsupportedCharsetException ex) {
            throw new IOException(ex.toString());
        }
    }
    
    @Override
    public String toString(final T obj) throws IOException {
        this.outBuf.reset();
        this.serializer.serialize(obj);
        final byte[] buf = new byte[this.outBuf.getLength()];
        System.arraycopy(this.outBuf.getData(), 0, buf, 0, buf.length);
        return new String(Base64.encodeBase64(buf), StandardCharsets.UTF_8);
    }
    
    @Override
    public void close() throws IOException {
        this.inBuf.close();
        this.outBuf.close();
        this.deserializer.close();
        this.serializer.close();
    }
    
    public static <K> void store(final Configuration conf, final K item, final String keyName) throws IOException {
        final DefaultStringifier<K> stringifier = new DefaultStringifier<K>(conf, GenericsUtil.getClass(item));
        conf.set(keyName, stringifier.toString(item));
        stringifier.close();
    }
    
    public static <K> K load(final Configuration conf, final String keyName, final Class<K> itemClass) throws IOException {
        final DefaultStringifier<K> stringifier = new DefaultStringifier<K>(conf, itemClass);
        try {
            final String itemStr = conf.get(keyName);
            return stringifier.fromString(itemStr);
        }
        finally {
            stringifier.close();
        }
    }
    
    public static <K> void storeArray(final Configuration conf, final K[] items, final String keyName) throws IOException {
        final DefaultStringifier<K> stringifier = new DefaultStringifier<K>(conf, GenericsUtil.getClass(items[0]));
        try {
            final StringBuilder builder = new StringBuilder();
            for (final K item : items) {
                builder.append(stringifier.toString(item)).append(",");
            }
            conf.set(keyName, builder.toString());
        }
        finally {
            stringifier.close();
        }
    }
    
    public static <K> K[] loadArray(final Configuration conf, final String keyName, final Class<K> itemClass) throws IOException {
        final DefaultStringifier<K> stringifier = new DefaultStringifier<K>(conf, itemClass);
        try {
            final String itemStr = conf.get(keyName);
            final ArrayList<K> list = new ArrayList<K>();
            final String[] split;
            final String[] parts = split = itemStr.split(",");
            for (final String part : split) {
                if (!part.isEmpty()) {
                    list.add(stringifier.fromString(part));
                }
            }
            return GenericsUtil.toArray(itemClass, list);
        }
        finally {
            stringifier.close();
        }
    }
}
