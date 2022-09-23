// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.reflect;

import java.util.Arrays;
import org.apache.avro.io.ResolvingDecoder;
import java.io.IOException;
import org.apache.avro.io.Encoder;

class ArrayAccessor
{
    static void writeArray(final boolean[] data, final Encoder out) throws IOException {
        final int size = data.length;
        out.setItemCount(size);
        for (int i = 0; i < size; ++i) {
            out.startItem();
            out.writeBoolean(data[i]);
        }
    }
    
    static void writeArray(final short[] data, final Encoder out) throws IOException {
        final int size = data.length;
        out.setItemCount(size);
        for (int i = 0; i < size; ++i) {
            out.startItem();
            out.writeInt(data[i]);
        }
    }
    
    static void writeArray(final char[] data, final Encoder out) throws IOException {
        final int size = data.length;
        out.setItemCount(size);
        for (int i = 0; i < size; ++i) {
            out.startItem();
            out.writeInt(data[i]);
        }
    }
    
    static void writeArray(final int[] data, final Encoder out) throws IOException {
        final int size = data.length;
        out.setItemCount(size);
        for (int i = 0; i < size; ++i) {
            out.startItem();
            out.writeInt(data[i]);
        }
    }
    
    static void writeArray(final long[] data, final Encoder out) throws IOException {
        final int size = data.length;
        out.setItemCount(size);
        for (int i = 0; i < size; ++i) {
            out.startItem();
            out.writeLong(data[i]);
        }
    }
    
    static void writeArray(final float[] data, final Encoder out) throws IOException {
        final int size = data.length;
        out.setItemCount(size);
        for (int i = 0; i < size; ++i) {
            out.startItem();
            out.writeFloat(data[i]);
        }
    }
    
    static void writeArray(final double[] data, final Encoder out) throws IOException {
        final int size = data.length;
        out.setItemCount(size);
        for (int i = 0; i < size; ++i) {
            out.startItem();
            out.writeDouble(data[i]);
        }
    }
    
    static Object readArray(final Object array, final Class<?> elementType, final long l, final ResolvingDecoder in) throws IOException {
        if (elementType == Integer.TYPE) {
            return readArray((int[])array, l, in);
        }
        if (elementType == Long.TYPE) {
            return readArray((long[])array, l, in);
        }
        if (elementType == Float.TYPE) {
            return readArray((float[])array, l, in);
        }
        if (elementType == Double.TYPE) {
            return readArray((double[])array, l, in);
        }
        if (elementType == Boolean.TYPE) {
            return readArray((boolean[])array, l, in);
        }
        if (elementType == Character.TYPE) {
            return readArray((char[])array, l, in);
        }
        if (elementType == Short.TYPE) {
            return readArray((short[])array, l, in);
        }
        return null;
    }
    
    static boolean[] readArray(boolean[] array, long l, final ResolvingDecoder in) throws IOException {
        int index = 0;
        do {
            final int limit = index + (int)l;
            if (array.length < limit) {
                array = Arrays.copyOf(array, limit);
            }
            while (index < limit) {
                array[index] = in.readBoolean();
                ++index;
            }
        } while ((l = in.arrayNext()) > 0L);
        return array;
    }
    
    static int[] readArray(int[] array, long l, final ResolvingDecoder in) throws IOException {
        int index = 0;
        do {
            final int limit = index + (int)l;
            if (array.length < limit) {
                array = Arrays.copyOf(array, limit);
            }
            while (index < limit) {
                array[index] = in.readInt();
                ++index;
            }
        } while ((l = in.arrayNext()) > 0L);
        return array;
    }
    
    static short[] readArray(short[] array, long l, final ResolvingDecoder in) throws IOException {
        int index = 0;
        do {
            final int limit = index + (int)l;
            if (array.length < limit) {
                array = Arrays.copyOf(array, limit);
            }
            while (index < limit) {
                array[index] = (short)in.readInt();
                ++index;
            }
        } while ((l = in.arrayNext()) > 0L);
        return array;
    }
    
    static char[] readArray(char[] array, long l, final ResolvingDecoder in) throws IOException {
        int index = 0;
        do {
            final int limit = index + (int)l;
            if (array.length < limit) {
                array = Arrays.copyOf(array, limit);
            }
            while (index < limit) {
                array[index] = (char)in.readInt();
                ++index;
            }
        } while ((l = in.arrayNext()) > 0L);
        return array;
    }
    
    static long[] readArray(long[] array, long l, final ResolvingDecoder in) throws IOException {
        int index = 0;
        do {
            final int limit = index + (int)l;
            if (array.length < limit) {
                array = Arrays.copyOf(array, limit);
            }
            while (index < limit) {
                array[index] = in.readLong();
                ++index;
            }
        } while ((l = in.arrayNext()) > 0L);
        return array;
    }
    
    static float[] readArray(float[] array, long l, final ResolvingDecoder in) throws IOException {
        int index = 0;
        do {
            final int limit = index + (int)l;
            if (array.length < limit) {
                array = Arrays.copyOf(array, limit);
            }
            while (index < limit) {
                array[index] = in.readFloat();
                ++index;
            }
        } while ((l = in.arrayNext()) > 0L);
        return array;
    }
    
    static double[] readArray(double[] array, long l, final ResolvingDecoder in) throws IOException {
        int index = 0;
        do {
            final int limit = index + (int)l;
            if (array.length < limit) {
                array = Arrays.copyOf(array, limit);
            }
            while (index < limit) {
                array[index] = in.readDouble();
                ++index;
            }
        } while ((l = in.arrayNext()) > 0L);
        return array;
    }
}
