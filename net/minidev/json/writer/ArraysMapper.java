// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json.writer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ArraysMapper<T> extends JsonReaderI<T>
{
    public static JsonReaderI<int[]> MAPPER_PRIM_INT;
    public static JsonReaderI<Integer[]> MAPPER_INT;
    public static JsonReaderI<short[]> MAPPER_PRIM_SHORT;
    public static JsonReaderI<Short[]> MAPPER_SHORT;
    public static JsonReaderI<byte[]> MAPPER_PRIM_BYTE;
    public static JsonReaderI<Byte[]> MAPPER_BYTE;
    public static JsonReaderI<char[]> MAPPER_PRIM_CHAR;
    public static JsonReaderI<Character[]> MAPPER_CHAR;
    public static JsonReaderI<long[]> MAPPER_PRIM_LONG;
    public static JsonReaderI<Long[]> MAPPER_LONG;
    public static JsonReaderI<float[]> MAPPER_PRIM_FLOAT;
    public static JsonReaderI<Float[]> MAPPER_FLOAT;
    public static JsonReaderI<double[]> MAPPER_PRIM_DOUBLE;
    public static JsonReaderI<Double[]> MAPPER_DOUBLE;
    public static JsonReaderI<boolean[]> MAPPER_PRIM_BOOL;
    public static JsonReaderI<Boolean[]> MAPPER_BOOL;
    
    static {
        ArraysMapper.MAPPER_PRIM_INT = new ArraysMapper<int[]>() {
            @Override
            public int[] convert(final Object current) {
                int p = 0;
                final int[] r = new int[((List)current).size()];
                for (final Object e : (List)current) {
                    r[p++] = ((Number)e).intValue();
                }
                return r;
            }
        };
        ArraysMapper.MAPPER_INT = new ArraysMapper<Integer[]>() {
            @Override
            public Integer[] convert(final Object current) {
                int p = 0;
                final Integer[] r = new Integer[((List)current).size()];
                for (final Object e : (List)current) {
                    if (e == null) {
                        continue;
                    }
                    if (e instanceof Integer) {
                        r[p] = (Integer)e;
                    }
                    else {
                        r[p] = ((Number)e).intValue();
                    }
                    ++p;
                }
                return r;
            }
        };
        ArraysMapper.MAPPER_PRIM_SHORT = new ArraysMapper<short[]>() {
            @Override
            public short[] convert(final Object current) {
                int p = 0;
                final short[] r = new short[((List)current).size()];
                for (final Object e : (List)current) {
                    r[p++] = ((Number)e).shortValue();
                }
                return r;
            }
        };
        ArraysMapper.MAPPER_SHORT = new ArraysMapper<Short[]>() {
            @Override
            public Short[] convert(final Object current) {
                int p = 0;
                final Short[] r = new Short[((List)current).size()];
                for (final Object e : (List)current) {
                    if (e == null) {
                        continue;
                    }
                    if (e instanceof Short) {
                        r[p] = (Short)e;
                    }
                    else {
                        r[p] = ((Number)e).shortValue();
                    }
                    ++p;
                }
                return r;
            }
        };
        ArraysMapper.MAPPER_PRIM_BYTE = new ArraysMapper<byte[]>() {
            @Override
            public byte[] convert(final Object current) {
                int p = 0;
                final byte[] r = new byte[((List)current).size()];
                for (final Object e : (List)current) {
                    r[p++] = ((Number)e).byteValue();
                }
                return r;
            }
        };
        ArraysMapper.MAPPER_BYTE = new ArraysMapper<Byte[]>() {
            @Override
            public Byte[] convert(final Object current) {
                int p = 0;
                final Byte[] r = new Byte[((List)current).size()];
                for (final Object e : (List)current) {
                    if (e == null) {
                        continue;
                    }
                    if (e instanceof Byte) {
                        r[p] = (Byte)e;
                    }
                    else {
                        r[p] = ((Number)e).byteValue();
                    }
                    ++p;
                }
                return r;
            }
        };
        ArraysMapper.MAPPER_PRIM_CHAR = new ArraysMapper<char[]>() {
            @Override
            public char[] convert(final Object current) {
                int p = 0;
                final char[] r = new char[((List)current).size()];
                for (final Object e : (List)current) {
                    r[p++] = e.toString().charAt(0);
                }
                return r;
            }
        };
        ArraysMapper.MAPPER_CHAR = new ArraysMapper<Character[]>() {
            @Override
            public Character[] convert(final Object current) {
                int p = 0;
                final Character[] r = new Character[((List)current).size()];
                for (final Object e : (List)current) {
                    if (e == null) {
                        continue;
                    }
                    r[p] = e.toString().charAt(0);
                    ++p;
                }
                return r;
            }
        };
        ArraysMapper.MAPPER_PRIM_LONG = new ArraysMapper<long[]>() {
            @Override
            public long[] convert(final Object current) {
                int p = 0;
                final long[] r = new long[((List)current).size()];
                for (final Object e : (List)current) {
                    r[p++] = ((Number)e).intValue();
                }
                return r;
            }
        };
        ArraysMapper.MAPPER_LONG = new ArraysMapper<Long[]>() {
            @Override
            public Long[] convert(final Object current) {
                int p = 0;
                final Long[] r = new Long[((List)current).size()];
                for (final Object e : (List)current) {
                    if (e == null) {
                        continue;
                    }
                    if (e instanceof Float) {
                        r[p] = (Long)e;
                    }
                    else {
                        r[p] = ((Number)e).longValue();
                    }
                    ++p;
                }
                return r;
            }
        };
        ArraysMapper.MAPPER_PRIM_FLOAT = new ArraysMapper<float[]>() {
            @Override
            public float[] convert(final Object current) {
                int p = 0;
                final float[] r = new float[((List)current).size()];
                for (final Object e : (List)current) {
                    r[p++] = ((Number)e).floatValue();
                }
                return r;
            }
        };
        ArraysMapper.MAPPER_FLOAT = new ArraysMapper<Float[]>() {
            @Override
            public Float[] convert(final Object current) {
                int p = 0;
                final Float[] r = new Float[((List)current).size()];
                for (final Object e : (List)current) {
                    if (e == null) {
                        continue;
                    }
                    if (e instanceof Float) {
                        r[p] = (Float)e;
                    }
                    else {
                        r[p] = ((Number)e).floatValue();
                    }
                    ++p;
                }
                return r;
            }
        };
        ArraysMapper.MAPPER_PRIM_DOUBLE = new ArraysMapper<double[]>() {
            @Override
            public double[] convert(final Object current) {
                int p = 0;
                final double[] r = new double[((List)current).size()];
                for (final Object e : (List)current) {
                    r[p++] = ((Number)e).doubleValue();
                }
                return r;
            }
        };
        ArraysMapper.MAPPER_DOUBLE = new ArraysMapper<Double[]>() {
            @Override
            public Double[] convert(final Object current) {
                int p = 0;
                final Double[] r = new Double[((List)current).size()];
                for (final Object e : (List)current) {
                    if (e == null) {
                        continue;
                    }
                    if (e instanceof Double) {
                        r[p] = (Double)e;
                    }
                    else {
                        r[p] = ((Number)e).doubleValue();
                    }
                    ++p;
                }
                return r;
            }
        };
        ArraysMapper.MAPPER_PRIM_BOOL = new ArraysMapper<boolean[]>() {
            @Override
            public boolean[] convert(final Object current) {
                int p = 0;
                final boolean[] r = new boolean[((List)current).size()];
                for (final Object e : (List)current) {
                    r[p++] = (boolean)e;
                }
                return r;
            }
        };
        ArraysMapper.MAPPER_BOOL = new ArraysMapper<Boolean[]>() {
            @Override
            public Boolean[] convert(final Object current) {
                int p = 0;
                final Boolean[] r = new Boolean[((List)current).size()];
                for (final Object e : (List)current) {
                    if (e == null) {
                        continue;
                    }
                    if (e instanceof Boolean) {
                        r[p] = (boolean)e;
                    }
                    else {
                        if (!(e instanceof Number)) {
                            throw new RuntimeException("can not convert " + e + " toBoolean");
                        }
                        r[p] = (((Number)e).intValue() != 0);
                    }
                    ++p;
                }
                return r;
            }
        };
    }
    
    public ArraysMapper(final JsonReader base) {
        super(base);
    }
    
    @Override
    public Object createArray() {
        return new ArrayList();
    }
    
    @Override
    public void addValue(final Object current, final Object value) {
        ((List)current).add(value);
    }
    
    @Override
    public T convert(final Object current) {
        return (T)current;
    }
    
    public static class GenericMapper<T> extends ArraysMapper<T>
    {
        final Class<?> componentType;
        JsonReaderI<?> subMapper;
        
        public GenericMapper(final JsonReader base, final Class<T> type) {
            super(base);
            this.componentType = type.getComponentType();
        }
        
        @Override
        public T convert(final Object current) {
            int p = 0;
            final Object[] r = (Object[])Array.newInstance(this.componentType, ((List)current).size());
            for (final Object e : (List)current) {
                r[p++] = e;
            }
            return (T)r;
        }
        
        @Override
        public JsonReaderI<?> startArray(final String key) {
            if (this.subMapper == null) {
                this.subMapper = this.base.getMapper(this.componentType);
            }
            return this.subMapper;
        }
        
        @Override
        public JsonReaderI<?> startObject(final String key) {
            if (this.subMapper == null) {
                this.subMapper = this.base.getMapper(this.componentType);
            }
            return this.subMapper;
        }
    }
}
