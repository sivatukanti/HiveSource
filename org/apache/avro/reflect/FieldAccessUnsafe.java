// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.reflect;

import org.apache.avro.io.Encoder;
import java.io.IOException;
import org.apache.avro.io.Decoder;
import java.lang.annotation.Annotation;
import org.apache.avro.AvroRuntimeException;
import java.lang.reflect.Field;
import sun.misc.Unsafe;

class FieldAccessUnsafe extends FieldAccess
{
    private static final Unsafe UNSAFE;
    
    @Override
    protected FieldAccessor getAccessor(final Field field) {
        final AvroEncode enc = field.getAnnotation(AvroEncode.class);
        if (enc != null) {
            try {
                return new UnsafeCustomEncodedField(field, (CustomEncoding<?>)enc.using().newInstance());
            }
            catch (Exception e) {
                throw new AvroRuntimeException("Could not instantiate custom Encoding");
            }
        }
        final Class<?> c = field.getType();
        if (c == Integer.TYPE) {
            return new UnsafeIntField(field);
        }
        if (c == Long.TYPE) {
            return new UnsafeLongField(field);
        }
        if (c == Byte.TYPE) {
            return new UnsafeByteField(field);
        }
        if (c == Float.TYPE) {
            return new UnsafeFloatField(field);
        }
        if (c == Double.TYPE) {
            return new UnsafeDoubleField(field);
        }
        if (c == Character.TYPE) {
            return new UnsafeCharField(field);
        }
        if (c == Boolean.TYPE) {
            return new UnsafeBooleanField(field);
        }
        if (c == Short.TYPE) {
            return new UnsafeShortField(field);
        }
        return new UnsafeObjectField(field);
    }
    
    static {
        try {
            final Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            UNSAFE = (Unsafe)theUnsafe.get(null);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    abstract static class UnsafeCachedField extends FieldAccessor
    {
        protected final long offset;
        protected Field field;
        protected final boolean isStringable;
        
        UnsafeCachedField(final Field f) {
            this.offset = FieldAccessUnsafe.UNSAFE.objectFieldOffset(f);
            this.field = f;
            this.isStringable = f.isAnnotationPresent(Stringable.class);
        }
        
        @Override
        protected Field getField() {
            return this.field;
        }
        
        @Override
        protected boolean supportsIO() {
            return true;
        }
        
        @Override
        protected boolean isStringable() {
            return this.isStringable;
        }
    }
    
    static final class UnsafeIntField extends UnsafeCachedField
    {
        UnsafeIntField(final Field f) {
            super(f);
        }
        
        @Override
        protected void set(final Object object, final Object value) {
            FieldAccessUnsafe.UNSAFE.putInt(object, this.offset, (int)value);
        }
        
        @Override
        protected Object get(final Object object) {
            return FieldAccessUnsafe.UNSAFE.getInt(object, this.offset);
        }
        
        @Override
        protected void read(final Object object, final Decoder in) throws IOException {
            FieldAccessUnsafe.UNSAFE.putInt(object, this.offset, in.readInt());
        }
        
        @Override
        protected void write(final Object object, final Encoder out) throws IOException {
            out.writeInt(FieldAccessUnsafe.UNSAFE.getInt(object, this.offset));
        }
    }
    
    static final class UnsafeFloatField extends UnsafeCachedField
    {
        protected UnsafeFloatField(final Field f) {
            super(f);
        }
        
        @Override
        protected void set(final Object object, final Object value) {
            FieldAccessUnsafe.UNSAFE.putFloat(object, this.offset, (float)value);
        }
        
        @Override
        protected Object get(final Object object) {
            return FieldAccessUnsafe.UNSAFE.getFloat(object, this.offset);
        }
        
        @Override
        protected void read(final Object object, final Decoder in) throws IOException {
            FieldAccessUnsafe.UNSAFE.putFloat(object, this.offset, in.readFloat());
        }
        
        @Override
        protected void write(final Object object, final Encoder out) throws IOException {
            out.writeFloat(FieldAccessUnsafe.UNSAFE.getFloat(object, this.offset));
        }
    }
    
    static final class UnsafeShortField extends UnsafeCachedField
    {
        protected UnsafeShortField(final Field f) {
            super(f);
        }
        
        @Override
        protected void set(final Object object, final Object value) {
            FieldAccessUnsafe.UNSAFE.putShort(object, this.offset, (short)value);
        }
        
        @Override
        protected Object get(final Object object) {
            return FieldAccessUnsafe.UNSAFE.getShort(object, this.offset);
        }
        
        @Override
        protected void read(final Object object, final Decoder in) throws IOException {
            FieldAccessUnsafe.UNSAFE.putShort(object, this.offset, (short)in.readInt());
        }
        
        @Override
        protected void write(final Object object, final Encoder out) throws IOException {
            out.writeInt(FieldAccessUnsafe.UNSAFE.getShort(object, this.offset));
        }
    }
    
    static final class UnsafeByteField extends UnsafeCachedField
    {
        protected UnsafeByteField(final Field f) {
            super(f);
        }
        
        @Override
        protected void set(final Object object, final Object value) {
            FieldAccessUnsafe.UNSAFE.putByte(object, this.offset, (byte)value);
        }
        
        @Override
        protected Object get(final Object object) {
            return FieldAccessUnsafe.UNSAFE.getByte(object, this.offset);
        }
        
        @Override
        protected void read(final Object object, final Decoder in) throws IOException {
            FieldAccessUnsafe.UNSAFE.putByte(object, this.offset, (byte)in.readInt());
        }
        
        @Override
        protected void write(final Object object, final Encoder out) throws IOException {
            out.writeInt(FieldAccessUnsafe.UNSAFE.getByte(object, this.offset));
        }
    }
    
    static final class UnsafeBooleanField extends UnsafeCachedField
    {
        protected UnsafeBooleanField(final Field f) {
            super(f);
        }
        
        @Override
        protected void set(final Object object, final Object value) {
            FieldAccessUnsafe.UNSAFE.putBoolean(object, this.offset, (boolean)value);
        }
        
        @Override
        protected Object get(final Object object) {
            return FieldAccessUnsafe.UNSAFE.getBoolean(object, this.offset);
        }
        
        @Override
        protected void read(final Object object, final Decoder in) throws IOException {
            FieldAccessUnsafe.UNSAFE.putBoolean(object, this.offset, in.readBoolean());
        }
        
        @Override
        protected void write(final Object object, final Encoder out) throws IOException {
            out.writeBoolean(FieldAccessUnsafe.UNSAFE.getBoolean(object, this.offset));
        }
    }
    
    static final class UnsafeCharField extends UnsafeCachedField
    {
        protected UnsafeCharField(final Field f) {
            super(f);
        }
        
        @Override
        protected void set(final Object object, final Object value) {
            FieldAccessUnsafe.UNSAFE.putChar(object, this.offset, (char)value);
        }
        
        @Override
        protected Object get(final Object object) {
            return FieldAccessUnsafe.UNSAFE.getChar(object, this.offset);
        }
        
        @Override
        protected void read(final Object object, final Decoder in) throws IOException {
            FieldAccessUnsafe.UNSAFE.putChar(object, this.offset, (char)in.readInt());
        }
        
        @Override
        protected void write(final Object object, final Encoder out) throws IOException {
            out.writeInt(FieldAccessUnsafe.UNSAFE.getChar(object, this.offset));
        }
    }
    
    static final class UnsafeLongField extends UnsafeCachedField
    {
        protected UnsafeLongField(final Field f) {
            super(f);
        }
        
        @Override
        protected void set(final Object object, final Object value) {
            FieldAccessUnsafe.UNSAFE.putLong(object, this.offset, (long)value);
        }
        
        @Override
        protected Object get(final Object object) {
            return FieldAccessUnsafe.UNSAFE.getLong(object, this.offset);
        }
        
        @Override
        protected void read(final Object object, final Decoder in) throws IOException {
            FieldAccessUnsafe.UNSAFE.putLong(object, this.offset, in.readLong());
        }
        
        @Override
        protected void write(final Object object, final Encoder out) throws IOException {
            out.writeLong(FieldAccessUnsafe.UNSAFE.getLong(object, this.offset));
        }
    }
    
    static final class UnsafeDoubleField extends UnsafeCachedField
    {
        protected UnsafeDoubleField(final Field f) {
            super(f);
        }
        
        @Override
        protected void set(final Object object, final Object value) {
            FieldAccessUnsafe.UNSAFE.putDouble(object, this.offset, (double)value);
        }
        
        @Override
        protected Object get(final Object object) {
            return FieldAccessUnsafe.UNSAFE.getDouble(object, this.offset);
        }
        
        @Override
        protected void read(final Object object, final Decoder in) throws IOException {
            FieldAccessUnsafe.UNSAFE.putDouble(object, this.offset, in.readDouble());
        }
        
        @Override
        protected void write(final Object object, final Encoder out) throws IOException {
            out.writeDouble(FieldAccessUnsafe.UNSAFE.getDouble(object, this.offset));
        }
    }
    
    static final class UnsafeObjectField extends UnsafeCachedField
    {
        protected UnsafeObjectField(final Field f) {
            super(f);
        }
        
        @Override
        protected void set(final Object object, final Object value) {
            FieldAccessUnsafe.UNSAFE.putObject(object, this.offset, value);
        }
        
        @Override
        protected Object get(final Object object) {
            return FieldAccessUnsafe.UNSAFE.getObject(object, this.offset);
        }
        
        @Override
        protected boolean supportsIO() {
            return false;
        }
    }
    
    static final class UnsafeCustomEncodedField extends UnsafeCachedField
    {
        private CustomEncoding<?> encoding;
        
        UnsafeCustomEncodedField(final Field f, final CustomEncoding<?> encoding) {
            super(f);
            this.encoding = encoding;
        }
        
        @Override
        protected Object get(final Object object) throws IllegalAccessException {
            return FieldAccessUnsafe.UNSAFE.getObject(object, this.offset);
        }
        
        @Override
        protected void set(final Object object, final Object value) throws IllegalAccessException, IOException {
            FieldAccessUnsafe.UNSAFE.putObject(object, this.offset, value);
        }
        
        @Override
        protected void read(final Object object, final Decoder in) throws IOException {
            FieldAccessUnsafe.UNSAFE.putObject(object, this.offset, this.encoding.read(in));
        }
        
        @Override
        protected void write(final Object object, final Encoder out) throws IOException {
            this.encoding.write(FieldAccessUnsafe.UNSAFE.getObject(object, this.offset), out);
        }
        
        @Override
        protected boolean isCustomEncoded() {
            return true;
        }
    }
}
