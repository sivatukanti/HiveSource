// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json.reader;

import net.minidev.json.JSONStreamAware;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;
import net.minidev.json.JSONValue;
import java.io.IOException;
import net.minidev.json.JSONStyle;
import java.util.Map;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONAwareEx;
import net.minidev.json.JSONStreamAwareEx;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class JsonWriter
{
    private ConcurrentHashMap<Class<?>, JsonWriterI<?>> data;
    private LinkedList<WriterByInterface> writerInterfaces;
    public static final JsonWriterI<JSONStreamAwareEx> JSONStreamAwareWriter;
    public static final JsonWriterI<JSONStreamAwareEx> JSONStreamAwareExWriter;
    public static final JsonWriterI<JSONAwareEx> JSONJSONAwareExWriter;
    public static final JsonWriterI<JSONAware> JSONJSONAwareWriter;
    public static final JsonWriterI<Iterable<?>> JSONIterableWriter;
    public static final JsonWriterI<Enum<?>> EnumWriter;
    public static final JsonWriterI<Map<String, ?>> JSONMapWriter;
    public static final JsonWriterI<Object> beansWriterASM;
    public static final JsonWriterI<Object> beansWriter;
    public static final JsonWriterI<Object> arrayWriter;
    public static final JsonWriterI<Object> toStringWriter;
    
    static {
        JSONStreamAwareWriter = new JsonWriterI<JSONStreamAwareEx>() {
            public <E extends JSONStreamAwareEx> void writeJSONString(final E value, final Appendable out, final JSONStyle compression) throws IOException {
                value.writeJSONString(out);
            }
        };
        JSONStreamAwareExWriter = new JsonWriterI<JSONStreamAwareEx>() {
            public <E extends JSONStreamAwareEx> void writeJSONString(final E value, final Appendable out, final JSONStyle compression) throws IOException {
                value.writeJSONString(out, compression);
            }
        };
        JSONJSONAwareExWriter = new JsonWriterI<JSONAwareEx>() {
            public <E extends JSONAwareEx> void writeJSONString(final E value, final Appendable out, final JSONStyle compression) throws IOException {
                out.append(value.toJSONString(compression));
            }
        };
        JSONJSONAwareWriter = new JsonWriterI<JSONAware>() {
            public <E extends JSONAware> void writeJSONString(final E value, final Appendable out, final JSONStyle compression) throws IOException {
                out.append(value.toJSONString());
            }
        };
        JSONIterableWriter = new JsonWriterI<Iterable<?>>() {
            public <E extends Iterable<?>> void writeJSONString(final E list, final Appendable out, final JSONStyle compression) throws IOException {
                boolean first = true;
                compression.arrayStart(out);
                for (final Object value : list) {
                    if (first) {
                        first = false;
                        compression.arrayfirstObject(out);
                    }
                    else {
                        compression.arrayNextElm(out);
                    }
                    if (value == null) {
                        out.append("null");
                    }
                    else {
                        JSONValue.writeJSONString(value, out, compression);
                    }
                    compression.arrayObjectEnd(out);
                }
                compression.arrayStop(out);
            }
        };
        EnumWriter = new JsonWriterI<Enum<?>>() {
            public <E extends Enum<?>> void writeJSONString(final E value, final Appendable out, final JSONStyle compression) throws IOException {
                final String s = value.name();
                compression.writeString(out, s);
            }
        };
        JSONMapWriter = new JsonWriterI<Map<String, ?>>() {
            public <E extends Map<String, ?>> void writeJSONString(final E map, final Appendable out, final JSONStyle compression) throws IOException {
                boolean first = true;
                compression.objectStart(out);
                for (final Map.Entry<?, ?> entry : map.entrySet()) {
                    final Object v = entry.getValue();
                    if (v == null && compression.ignoreNull()) {
                        continue;
                    }
                    if (first) {
                        compression.objectFirstStart(out);
                        first = false;
                    }
                    else {
                        compression.objectNext(out);
                    }
                    JsonWriter.writeJSONKV(entry.getKey().toString(), v, out, compression);
                }
                compression.objectStop(out);
            }
        };
        beansWriterASM = new BeansWriterASM();
        beansWriter = new BeansWriter();
        arrayWriter = new ArrayWriter();
        toStringWriter = new JsonWriterI<Object>() {
            @Override
            public void writeJSONString(final Object value, final Appendable out, final JSONStyle compression) throws IOException {
                out.append(value.toString());
            }
        };
    }
    
    public JsonWriter() {
        this.data = new ConcurrentHashMap<Class<?>, JsonWriterI<?>>();
        this.writerInterfaces = new LinkedList<WriterByInterface>();
        this.init();
    }
    
    public <T> void remapField(final Class<T> type, final String fromJava, final String toJson) {
        JsonWriterI map = this.getWrite(type);
        if (!(map instanceof BeansWriterASMRemap)) {
            map = new BeansWriterASMRemap();
            this.registerWriter((JsonWriterI<Object>)map, type);
        }
        ((BeansWriterASMRemap)map).renameField(fromJava, toJson);
    }
    
    public JsonWriterI getWriterByInterface(final Class<?> clazz) {
        for (final WriterByInterface w : this.writerInterfaces) {
            if (w._interface.isAssignableFrom(clazz)) {
                return w._writer;
            }
        }
        return null;
    }
    
    public JsonWriterI getWrite(final Class cls) {
        return this.data.get(cls);
    }
    
    public void init() {
        this.registerWriter((JsonWriterI<Object>)new JsonWriterI<String>() {
            public void writeJSONString(final String value, final Appendable out, final JSONStyle compression) throws IOException {
                compression.writeString(out, value);
            }
        }, String.class);
        this.registerWriter((JsonWriterI<Object>)new JsonWriterI<Double>() {
            public void writeJSONString(final Double value, final Appendable out, final JSONStyle compression) throws IOException {
                if (value.isInfinite()) {
                    out.append("null");
                }
                else {
                    out.append(value.toString());
                }
            }
        }, Double.class);
        this.registerWriter((JsonWriterI<Object>)new JsonWriterI<Date>() {
            public void writeJSONString(final Date value, final Appendable out, final JSONStyle compression) throws IOException {
                out.append('\"');
                JSONValue.escape(value.toString(), out, compression);
                out.append('\"');
            }
        }, Date.class);
        this.registerWriter((JsonWriterI<Object>)new JsonWriterI<Float>() {
            public void writeJSONString(final Float value, final Appendable out, final JSONStyle compression) throws IOException {
                if (value.isInfinite()) {
                    out.append("null");
                }
                else {
                    out.append(value.toString());
                }
            }
        }, Float.class);
        this.registerWriter(JsonWriter.toStringWriter, Integer.class, Long.class, Byte.class, Short.class, BigInteger.class, BigDecimal.class);
        this.registerWriter(JsonWriter.toStringWriter, Boolean.class);
        this.registerWriter((JsonWriterI<Object>)new JsonWriterI<int[]>() {
            public void writeJSONString(final int[] value, final Appendable out, final JSONStyle compression) throws IOException {
                boolean needSep = false;
                compression.arrayStart(out);
                for (final int b : value) {
                    if (needSep) {
                        compression.objectNext(out);
                    }
                    else {
                        needSep = true;
                    }
                    out.append(Integer.toString(b));
                }
                compression.arrayStop(out);
            }
        }, int[].class);
        this.registerWriter((JsonWriterI<Object>)new JsonWriterI<short[]>() {
            public void writeJSONString(final short[] value, final Appendable out, final JSONStyle compression) throws IOException {
                boolean needSep = false;
                compression.arrayStart(out);
                for (final short b : value) {
                    if (needSep) {
                        compression.objectNext(out);
                    }
                    else {
                        needSep = true;
                    }
                    out.append(Short.toString(b));
                }
                compression.arrayStop(out);
            }
        }, short[].class);
        this.registerWriter((JsonWriterI<Object>)new JsonWriterI<long[]>() {
            public void writeJSONString(final long[] value, final Appendable out, final JSONStyle compression) throws IOException {
                boolean needSep = false;
                compression.arrayStart(out);
                for (final long b : value) {
                    if (needSep) {
                        compression.objectNext(out);
                    }
                    else {
                        needSep = true;
                    }
                    out.append(Long.toString(b));
                }
                compression.arrayStop(out);
            }
        }, long[].class);
        this.registerWriter((JsonWriterI<Object>)new JsonWriterI<float[]>() {
            public void writeJSONString(final float[] value, final Appendable out, final JSONStyle compression) throws IOException {
                boolean needSep = false;
                compression.arrayStart(out);
                for (final float b : value) {
                    if (needSep) {
                        compression.objectNext(out);
                    }
                    else {
                        needSep = true;
                    }
                    out.append(Float.toString(b));
                }
                compression.arrayStop(out);
            }
        }, float[].class);
        this.registerWriter((JsonWriterI<Object>)new JsonWriterI<double[]>() {
            public void writeJSONString(final double[] value, final Appendable out, final JSONStyle compression) throws IOException {
                boolean needSep = false;
                compression.arrayStart(out);
                for (final double b : value) {
                    if (needSep) {
                        compression.objectNext(out);
                    }
                    else {
                        needSep = true;
                    }
                    out.append(Double.toString(b));
                }
                compression.arrayStop(out);
            }
        }, double[].class);
        this.registerWriter((JsonWriterI<Object>)new JsonWriterI<boolean[]>() {
            public void writeJSONString(final boolean[] value, final Appendable out, final JSONStyle compression) throws IOException {
                boolean needSep = false;
                compression.arrayStart(out);
                for (final boolean b : value) {
                    if (needSep) {
                        compression.objectNext(out);
                    }
                    else {
                        needSep = true;
                    }
                    out.append(Boolean.toString(b));
                }
                compression.arrayStop(out);
            }
        }, boolean[].class);
        this.registerWriterInterface(JSONStreamAwareEx.class, JsonWriter.JSONStreamAwareExWriter);
        this.registerWriterInterface(JSONStreamAware.class, JsonWriter.JSONStreamAwareWriter);
        this.registerWriterInterface(JSONAwareEx.class, JsonWriter.JSONJSONAwareExWriter);
        this.registerWriterInterface(JSONAware.class, JsonWriter.JSONJSONAwareWriter);
        this.registerWriterInterface(Map.class, JsonWriter.JSONMapWriter);
        this.registerWriterInterface(Iterable.class, JsonWriter.JSONIterableWriter);
        this.registerWriterInterface(Enum.class, JsonWriter.EnumWriter);
        this.registerWriterInterface(Number.class, JsonWriter.toStringWriter);
    }
    
    @Deprecated
    public void addInterfaceWriterFirst(final Class<?> interFace, final JsonWriterI<?> writer) {
        this.registerWriterInterfaceFirst(interFace, writer);
    }
    
    @Deprecated
    public void addInterfaceWriterLast(final Class<?> interFace, final JsonWriterI<?> writer) {
        this.registerWriterInterfaceLast(interFace, writer);
    }
    
    public void registerWriterInterfaceLast(final Class<?> interFace, final JsonWriterI<?> writer) {
        this.writerInterfaces.addLast(new WriterByInterface(interFace, writer));
    }
    
    public void registerWriterInterfaceFirst(final Class<?> interFace, final JsonWriterI<?> writer) {
        this.writerInterfaces.addFirst(new WriterByInterface(interFace, writer));
    }
    
    public void registerWriterInterface(final Class<?> interFace, final JsonWriterI<?> writer) {
        this.registerWriterInterfaceLast(interFace, writer);
    }
    
    public <T> void registerWriter(final JsonWriterI<T> writer, final Class<?>... cls) {
        for (final Class<?> c : cls) {
            this.data.put(c, writer);
        }
    }
    
    public static void writeJSONKV(final String key, final Object value, final Appendable out, final JSONStyle compression) throws IOException {
        if (key == null) {
            out.append("null");
        }
        else if (!compression.mustProtectKey(key)) {
            out.append(key);
        }
        else {
            out.append('\"');
            JSONValue.escape(key, out, compression);
            out.append('\"');
        }
        compression.objectEndOfKey(out);
        if (value instanceof String) {
            compression.writeString(out, (String)value);
        }
        else {
            JSONValue.writeJSONString(value, out, compression);
        }
        compression.objectElmStop(out);
    }
    
    static class WriterByInterface
    {
        public Class<?> _interface;
        public JsonWriterI<?> _writer;
        
        public WriterByInterface(final Class<?> _interface, final JsonWriterI<?> _writer) {
            this._interface = _interface;
            this._writer = _writer;
        }
    }
}
