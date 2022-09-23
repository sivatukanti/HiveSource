// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.util.HashMap;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import java.lang.reflect.Array;
import org.apache.hadoop.HadoopIllegalArgumentException;
import java.util.Map;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class ArrayPrimitiveWritable implements Writable
{
    private Class<?> componentType;
    private Class<?> declaredComponentType;
    private int length;
    private Object value;
    private static final Map<String, Class<?>> PRIMITIVE_NAMES;
    
    private static Class<?> getPrimitiveClass(final String className) {
        return ArrayPrimitiveWritable.PRIMITIVE_NAMES.get(className);
    }
    
    private static void checkPrimitive(final Class<?> componentType) {
        if (componentType == null) {
            throw new HadoopIllegalArgumentException("null component type not allowed");
        }
        if (!ArrayPrimitiveWritable.PRIMITIVE_NAMES.containsKey(componentType.getName())) {
            throw new HadoopIllegalArgumentException("input array component type " + componentType.getName() + " is not a candidate primitive type");
        }
    }
    
    private void checkDeclaredComponentType(final Class<?> componentType) {
        if (this.declaredComponentType != null && componentType != this.declaredComponentType) {
            throw new HadoopIllegalArgumentException("input array component type " + componentType.getName() + " does not match declared type " + this.declaredComponentType.getName());
        }
    }
    
    private static void checkArray(final Object value) {
        if (value == null) {
            throw new HadoopIllegalArgumentException("null value not allowed");
        }
        if (!value.getClass().isArray()) {
            throw new HadoopIllegalArgumentException("non-array value of class " + value.getClass() + " not allowed");
        }
    }
    
    public ArrayPrimitiveWritable() {
        this.componentType = null;
        this.declaredComponentType = null;
    }
    
    public ArrayPrimitiveWritable(final Class<?> componentType) {
        this.componentType = null;
        this.declaredComponentType = null;
        checkPrimitive(componentType);
        this.declaredComponentType = componentType;
    }
    
    public ArrayPrimitiveWritable(final Object value) {
        this.componentType = null;
        this.declaredComponentType = null;
        this.set(value);
    }
    
    public Object get() {
        return this.value;
    }
    
    public Class<?> getComponentType() {
        return this.componentType;
    }
    
    public Class<?> getDeclaredComponentType() {
        return this.declaredComponentType;
    }
    
    public boolean isDeclaredComponentType(final Class<?> componentType) {
        return componentType == this.declaredComponentType;
    }
    
    public void set(final Object value) {
        checkArray(value);
        final Class<?> componentType = value.getClass().getComponentType();
        checkPrimitive(componentType);
        this.checkDeclaredComponentType(componentType);
        this.componentType = componentType;
        this.value = value;
        this.length = Array.getLength(value);
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        UTF8.writeString(out, this.componentType.getName());
        out.writeInt(this.length);
        if (this.componentType == Boolean.TYPE) {
            this.writeBooleanArray(out);
        }
        else if (this.componentType == Character.TYPE) {
            this.writeCharArray(out);
        }
        else if (this.componentType == Byte.TYPE) {
            this.writeByteArray(out);
        }
        else if (this.componentType == Short.TYPE) {
            this.writeShortArray(out);
        }
        else if (this.componentType == Integer.TYPE) {
            this.writeIntArray(out);
        }
        else if (this.componentType == Long.TYPE) {
            this.writeLongArray(out);
        }
        else if (this.componentType == Float.TYPE) {
            this.writeFloatArray(out);
        }
        else {
            if (this.componentType != Double.TYPE) {
                throw new IOException("Component type " + this.componentType.toString() + " is set as the output type, but no encoding is implemented for this type.");
            }
            this.writeDoubleArray(out);
        }
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        final String className = UTF8.readString(in);
        final Class<?> componentType = getPrimitiveClass(className);
        if (componentType == null) {
            throw new IOException("encoded array component type " + className + " is not a candidate primitive type");
        }
        this.checkDeclaredComponentType(componentType);
        this.componentType = componentType;
        final int length = in.readInt();
        if (length < 0) {
            throw new IOException("encoded array length is negative " + length);
        }
        this.length = length;
        this.value = Array.newInstance(componentType, length);
        if (componentType == Boolean.TYPE) {
            this.readBooleanArray(in);
        }
        else if (componentType == Character.TYPE) {
            this.readCharArray(in);
        }
        else if (componentType == Byte.TYPE) {
            this.readByteArray(in);
        }
        else if (componentType == Short.TYPE) {
            this.readShortArray(in);
        }
        else if (componentType == Integer.TYPE) {
            this.readIntArray(in);
        }
        else if (componentType == Long.TYPE) {
            this.readLongArray(in);
        }
        else if (componentType == Float.TYPE) {
            this.readFloatArray(in);
        }
        else {
            if (componentType != Double.TYPE) {
                throw new IOException("Encoded type " + className + " converted to valid component type " + componentType.toString() + " but no encoding is implemented for this type.");
            }
            this.readDoubleArray(in);
        }
    }
    
    private void writeBooleanArray(final DataOutput out) throws IOException {
        final boolean[] v = (boolean[])this.value;
        for (int i = 0; i < this.length; ++i) {
            out.writeBoolean(v[i]);
        }
    }
    
    private void writeCharArray(final DataOutput out) throws IOException {
        final char[] v = (char[])this.value;
        for (int i = 0; i < this.length; ++i) {
            out.writeChar(v[i]);
        }
    }
    
    private void writeByteArray(final DataOutput out) throws IOException {
        out.write((byte[])this.value, 0, this.length);
    }
    
    private void writeShortArray(final DataOutput out) throws IOException {
        final short[] v = (short[])this.value;
        for (int i = 0; i < this.length; ++i) {
            out.writeShort(v[i]);
        }
    }
    
    private void writeIntArray(final DataOutput out) throws IOException {
        final int[] v = (int[])this.value;
        for (int i = 0; i < this.length; ++i) {
            out.writeInt(v[i]);
        }
    }
    
    private void writeLongArray(final DataOutput out) throws IOException {
        final long[] v = (long[])this.value;
        for (int i = 0; i < this.length; ++i) {
            out.writeLong(v[i]);
        }
    }
    
    private void writeFloatArray(final DataOutput out) throws IOException {
        final float[] v = (float[])this.value;
        for (int i = 0; i < this.length; ++i) {
            out.writeFloat(v[i]);
        }
    }
    
    private void writeDoubleArray(final DataOutput out) throws IOException {
        final double[] v = (double[])this.value;
        for (int i = 0; i < this.length; ++i) {
            out.writeDouble(v[i]);
        }
    }
    
    private void readBooleanArray(final DataInput in) throws IOException {
        final boolean[] v = (boolean[])this.value;
        for (int i = 0; i < this.length; ++i) {
            v[i] = in.readBoolean();
        }
    }
    
    private void readCharArray(final DataInput in) throws IOException {
        final char[] v = (char[])this.value;
        for (int i = 0; i < this.length; ++i) {
            v[i] = in.readChar();
        }
    }
    
    private void readByteArray(final DataInput in) throws IOException {
        in.readFully((byte[])this.value, 0, this.length);
    }
    
    private void readShortArray(final DataInput in) throws IOException {
        final short[] v = (short[])this.value;
        for (int i = 0; i < this.length; ++i) {
            v[i] = in.readShort();
        }
    }
    
    private void readIntArray(final DataInput in) throws IOException {
        final int[] v = (int[])this.value;
        for (int i = 0; i < this.length; ++i) {
            v[i] = in.readInt();
        }
    }
    
    private void readLongArray(final DataInput in) throws IOException {
        final long[] v = (long[])this.value;
        for (int i = 0; i < this.length; ++i) {
            v[i] = in.readLong();
        }
    }
    
    private void readFloatArray(final DataInput in) throws IOException {
        final float[] v = (float[])this.value;
        for (int i = 0; i < this.length; ++i) {
            v[i] = in.readFloat();
        }
    }
    
    private void readDoubleArray(final DataInput in) throws IOException {
        final double[] v = (double[])this.value;
        for (int i = 0; i < this.length; ++i) {
            v[i] = in.readDouble();
        }
    }
    
    static {
        (PRIMITIVE_NAMES = new HashMap<String, Class<?>>(16)).put(Boolean.TYPE.getName(), Boolean.TYPE);
        ArrayPrimitiveWritable.PRIMITIVE_NAMES.put(Byte.TYPE.getName(), Byte.TYPE);
        ArrayPrimitiveWritable.PRIMITIVE_NAMES.put(Character.TYPE.getName(), Character.TYPE);
        ArrayPrimitiveWritable.PRIMITIVE_NAMES.put(Short.TYPE.getName(), Short.TYPE);
        ArrayPrimitiveWritable.PRIMITIVE_NAMES.put(Integer.TYPE.getName(), Integer.TYPE);
        ArrayPrimitiveWritable.PRIMITIVE_NAMES.put(Long.TYPE.getName(), Long.TYPE);
        ArrayPrimitiveWritable.PRIMITIVE_NAMES.put(Float.TYPE.getName(), Float.TYPE);
        ArrayPrimitiveWritable.PRIMITIVE_NAMES.put(Double.TYPE.getName(), Double.TYPE);
    }
    
    static class Internal extends ArrayPrimitiveWritable
    {
        Internal() {
        }
        
        Internal(final Object value) {
            super(value);
        }
    }
}
