// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import org.apache.hadoop.conf.Configured;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.apache.hadoop.util.ProtoUtil;
import java.io.InputStream;
import com.google.protobuf.Message;
import java.lang.reflect.Array;
import java.io.DataOutput;
import java.io.IOException;
import java.io.DataInput;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configurable;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class ObjectWritable implements Writable, Configurable
{
    private Class declaredClass;
    private Object instance;
    private Configuration conf;
    private static final Map<String, Class<?>> PRIMITIVE_NAMES;
    
    public ObjectWritable() {
    }
    
    public ObjectWritable(final Object instance) {
        this.set(instance);
    }
    
    public ObjectWritable(final Class declaredClass, final Object instance) {
        this.declaredClass = declaredClass;
        this.instance = instance;
    }
    
    public Object get() {
        return this.instance;
    }
    
    public Class getDeclaredClass() {
        return this.declaredClass;
    }
    
    public void set(final Object instance) {
        this.declaredClass = instance.getClass();
        this.instance = instance;
    }
    
    @Override
    public String toString() {
        return "OW[class=" + this.declaredClass + ",value=" + this.instance + "]";
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        readObject(in, this, this.conf);
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        writeObject(out, this.instance, this.declaredClass, this.conf);
    }
    
    public static void writeObject(final DataOutput out, final Object instance, final Class declaredClass, final Configuration conf) throws IOException {
        writeObject(out, instance, declaredClass, conf, false);
    }
    
    public static void writeObject(final DataOutput out, Object instance, Class declaredClass, final Configuration conf, final boolean allowCompactArrays) throws IOException {
        if (instance == null) {
            instance = new NullInstance((Class)declaredClass, conf);
            declaredClass = Writable.class;
        }
        if (allowCompactArrays && ((Class)declaredClass).isArray() && instance.getClass().getName().equals(((Class)declaredClass).getName()) && instance.getClass().getComponentType().isPrimitive()) {
            instance = new ArrayPrimitiveWritable.Internal(instance);
            declaredClass = ArrayPrimitiveWritable.Internal.class;
        }
        UTF8.writeString(out, ((Class)declaredClass).getName());
        if (((Class)declaredClass).isArray()) {
            final int length = Array.getLength(instance);
            out.writeInt(length);
            for (int i = 0; i < length; ++i) {
                writeObject(out, Array.get(instance, i), ((Class)declaredClass).getComponentType(), conf, allowCompactArrays);
            }
        }
        else if (declaredClass == ArrayPrimitiveWritable.Internal.class) {
            ((ArrayPrimitiveWritable.Internal)instance).write(out);
        }
        else if (declaredClass == String.class) {
            UTF8.writeString(out, (String)instance);
        }
        else if (((Class)declaredClass).isPrimitive()) {
            if (declaredClass == Boolean.TYPE) {
                out.writeBoolean((boolean)instance);
            }
            else if (declaredClass == Character.TYPE) {
                out.writeChar((char)instance);
            }
            else if (declaredClass == Byte.TYPE) {
                out.writeByte((byte)instance);
            }
            else if (declaredClass == Short.TYPE) {
                out.writeShort((short)instance);
            }
            else if (declaredClass == Integer.TYPE) {
                out.writeInt((int)instance);
            }
            else if (declaredClass == Long.TYPE) {
                out.writeLong((long)instance);
            }
            else if (declaredClass == Float.TYPE) {
                out.writeFloat((float)instance);
            }
            else if (declaredClass == Double.TYPE) {
                out.writeDouble((double)instance);
            }
            else if (declaredClass != Void.TYPE) {
                throw new IllegalArgumentException("Not a primitive: " + declaredClass);
            }
        }
        else if (((Class)declaredClass).isEnum()) {
            UTF8.writeString(out, ((Enum)instance).name());
        }
        else if (Writable.class.isAssignableFrom((Class<?>)declaredClass)) {
            UTF8.writeString(out, instance.getClass().getName());
            ((Writable)instance).write(out);
        }
        else {
            if (!Message.class.isAssignableFrom((Class<?>)declaredClass)) {
                throw new IOException("Can't write: " + instance + " as " + declaredClass);
            }
            ((Message)instance).writeDelimitedTo(DataOutputOutputStream.constructOutputStream(out));
        }
    }
    
    public static Object readObject(final DataInput in, final Configuration conf) throws IOException {
        return readObject(in, null, conf);
    }
    
    public static Object readObject(final DataInput in, final ObjectWritable objectWritable, final Configuration conf) throws IOException {
        final String className = UTF8.readString(in);
        Class<?> declaredClass = ObjectWritable.PRIMITIVE_NAMES.get(className);
        if (declaredClass == null) {
            declaredClass = loadClass(conf, className);
        }
        Object instance;
        if (declaredClass.isPrimitive()) {
            if (declaredClass == Boolean.TYPE) {
                instance = in.readBoolean();
            }
            else if (declaredClass == Character.TYPE) {
                instance = in.readChar();
            }
            else if (declaredClass == Byte.TYPE) {
                instance = in.readByte();
            }
            else if (declaredClass == Short.TYPE) {
                instance = in.readShort();
            }
            else if (declaredClass == Integer.TYPE) {
                instance = in.readInt();
            }
            else if (declaredClass == Long.TYPE) {
                instance = in.readLong();
            }
            else if (declaredClass == Float.TYPE) {
                instance = in.readFloat();
            }
            else if (declaredClass == Double.TYPE) {
                instance = in.readDouble();
            }
            else {
                if (declaredClass != Void.TYPE) {
                    throw new IllegalArgumentException("Not a primitive: " + declaredClass);
                }
                instance = null;
            }
        }
        else if (declaredClass.isArray()) {
            final int length = in.readInt();
            instance = Array.newInstance(declaredClass.getComponentType(), length);
            for (int i = 0; i < length; ++i) {
                Array.set(instance, i, readObject(in, conf));
            }
        }
        else if (declaredClass == ArrayPrimitiveWritable.Internal.class) {
            final ArrayPrimitiveWritable.Internal temp = new ArrayPrimitiveWritable.Internal();
            temp.readFields(in);
            instance = temp.get();
            declaredClass = instance.getClass();
        }
        else if (declaredClass == String.class) {
            instance = UTF8.readString(in);
        }
        else if (declaredClass.isEnum()) {
            instance = Enum.valueOf(declaredClass, UTF8.readString(in));
        }
        else if (Message.class.isAssignableFrom(declaredClass)) {
            instance = tryInstantiateProtobuf(declaredClass, in);
        }
        else {
            Class instanceClass = null;
            final String str = UTF8.readString(in);
            instanceClass = loadClass(conf, str);
            final Writable writable = WritableFactories.newInstance(instanceClass, conf);
            writable.readFields(in);
            instance = writable;
            if (instanceClass == NullInstance.class) {
                declaredClass = ((NullInstance)instance).declaredClass;
                instance = null;
            }
        }
        if (objectWritable != null) {
            objectWritable.declaredClass = declaredClass;
            objectWritable.instance = instance;
        }
        return instance;
    }
    
    private static Message tryInstantiateProtobuf(final Class<?> protoClass, final DataInput dataIn) throws IOException {
        try {
            if (dataIn instanceof InputStream) {
                final Method parseMethod = getStaticProtobufMethod(protoClass, "parseDelimitedFrom", InputStream.class);
                return (Message)parseMethod.invoke(null, (InputStream)dataIn);
            }
            final int size = ProtoUtil.readRawVarint32(dataIn);
            if (size < 0) {
                throw new IOException("Invalid size: " + size);
            }
            final byte[] data = new byte[size];
            dataIn.readFully(data);
            final Method parseMethod2 = getStaticProtobufMethod(protoClass, "parseFrom", byte[].class);
            return (Message)parseMethod2.invoke(null, data);
        }
        catch (InvocationTargetException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException)e.getCause();
            }
            throw new IOException(e.getCause());
        }
        catch (IllegalAccessException iae) {
            throw new AssertionError((Object)("Could not access parse method in " + protoClass));
        }
    }
    
    static Method getStaticProtobufMethod(final Class<?> declaredClass, final String method, final Class<?>... args) {
        try {
            return declaredClass.getMethod(method, args);
        }
        catch (Exception e) {
            throw new AssertionError((Object)("Protocol buffer class " + declaredClass + " does not have an accessible parseFrom(InputStream) method!"));
        }
    }
    
    public static Class<?> loadClass(final Configuration conf, final String className) {
        Class<?> declaredClass = null;
        try {
            if (conf != null) {
                declaredClass = conf.getClassByName(className);
            }
            else {
                declaredClass = Class.forName(className);
            }
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("readObject can't find class " + className, e);
        }
        return declaredClass;
    }
    
    @Override
    public void setConf(final Configuration conf) {
        this.conf = conf;
    }
    
    @Override
    public Configuration getConf() {
        return this.conf;
    }
    
    static {
        (PRIMITIVE_NAMES = new HashMap<String, Class<?>>()).put("boolean", Boolean.TYPE);
        ObjectWritable.PRIMITIVE_NAMES.put("byte", Byte.TYPE);
        ObjectWritable.PRIMITIVE_NAMES.put("char", Character.TYPE);
        ObjectWritable.PRIMITIVE_NAMES.put("short", Short.TYPE);
        ObjectWritable.PRIMITIVE_NAMES.put("int", Integer.TYPE);
        ObjectWritable.PRIMITIVE_NAMES.put("long", Long.TYPE);
        ObjectWritable.PRIMITIVE_NAMES.put("float", Float.TYPE);
        ObjectWritable.PRIMITIVE_NAMES.put("double", Double.TYPE);
        ObjectWritable.PRIMITIVE_NAMES.put("void", Void.TYPE);
    }
    
    private static class NullInstance extends Configured implements Writable
    {
        private Class<?> declaredClass;
        
        public NullInstance() {
            super(null);
        }
        
        public NullInstance(final Class declaredClass, final Configuration conf) {
            super(conf);
            this.declaredClass = (Class<?>)declaredClass;
        }
        
        @Override
        public void readFields(final DataInput in) throws IOException {
            final String className = UTF8.readString(in);
            this.declaredClass = ObjectWritable.PRIMITIVE_NAMES.get(className);
            if (this.declaredClass == null) {
                try {
                    this.declaredClass = this.getConf().getClassByName(className);
                }
                catch (ClassNotFoundException e) {
                    throw new RuntimeException(e.toString());
                }
            }
        }
        
        @Override
        public void write(final DataOutput out) throws IOException {
            UTF8.writeString(out, this.declaredClass.getName());
        }
    }
}
