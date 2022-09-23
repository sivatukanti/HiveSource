// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.yarn.api.records.SerializedException;

public class SerializedExceptionPBImpl extends SerializedException
{
    YarnProtos.SerializedExceptionProto proto;
    YarnProtos.SerializedExceptionProto.Builder builder;
    boolean viaProto;
    
    public SerializedExceptionPBImpl() {
        this.proto = null;
        this.builder = YarnProtos.SerializedExceptionProto.newBuilder();
        this.viaProto = false;
    }
    
    public SerializedExceptionPBImpl(final YarnProtos.SerializedExceptionProto proto) {
        this.proto = null;
        this.builder = YarnProtos.SerializedExceptionProto.newBuilder();
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    private SerializedExceptionPBImpl(final Throwable t) {
        this.proto = null;
        this.builder = YarnProtos.SerializedExceptionProto.newBuilder();
        this.viaProto = false;
        this.init(t);
    }
    
    @Override
    public void init(final String message) {
        this.maybeInitBuilder();
        this.builder.setMessage(message);
    }
    
    @Override
    public void init(final Throwable t) {
        this.maybeInitBuilder();
        if (t == null) {
            return;
        }
        if (t.getCause() != null) {
            this.builder.setCause(new SerializedExceptionPBImpl(t.getCause()).getProto());
        }
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.close();
        if (sw.toString() != null) {
            this.builder.setTrace(sw.toString());
        }
        if (t.getMessage() != null) {
            this.builder.setMessage(t.getMessage());
        }
        this.builder.setClassName(t.getClass().getCanonicalName());
    }
    
    @Override
    public void init(final String message, final Throwable t) {
        this.init(t);
        if (message != null) {
            this.builder.setMessage(message);
        }
    }
    
    @Override
    public Throwable deSerialize() {
        final SerializedException cause = this.getCause();
        final YarnProtos.SerializedExceptionProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        Class<?> realClass = null;
        try {
            realClass = Class.forName(p.getClassName());
        }
        catch (ClassNotFoundException e) {
            throw new YarnRuntimeException(e);
        }
        Class classType = null;
        if (YarnException.class.isAssignableFrom(realClass)) {
            classType = YarnException.class;
        }
        else if (IOException.class.isAssignableFrom(realClass)) {
            classType = IOException.class;
        }
        else if (RuntimeException.class.isAssignableFrom(realClass)) {
            classType = RuntimeException.class;
        }
        else {
            classType = Exception.class;
        }
        return instantiateException((Class<? extends Throwable>)realClass.asSubclass((Class<Object>)classType), this.getMessage(), (cause == null) ? null : cause.deSerialize());
    }
    
    @Override
    public String getMessage() {
        final YarnProtos.SerializedExceptionProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getMessage();
    }
    
    @Override
    public String getRemoteTrace() {
        final YarnProtos.SerializedExceptionProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getTrace();
    }
    
    @Override
    public SerializedException getCause() {
        final YarnProtos.SerializedExceptionProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (p.hasCause()) {
            return new SerializedExceptionPBImpl(p.getCause());
        }
        return null;
    }
    
    public YarnProtos.SerializedExceptionProto getProto() {
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    @Override
    public int hashCode() {
        return this.getProto().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((SerializedExceptionPBImpl)this.getClass().cast(other)).getProto());
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnProtos.SerializedExceptionProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private static <T extends Throwable> T instantiateException(final Class<? extends T> cls, final String message, final Throwable cause) {
        T ex = null;
        try {
            final Constructor<? extends T> cn = cls.getConstructor(String.class);
            cn.setAccessible(true);
            ex = (T)cn.newInstance(message);
            ex.initCause(cause);
        }
        catch (SecurityException e) {
            throw new YarnRuntimeException(e);
        }
        catch (NoSuchMethodException e2) {
            throw new YarnRuntimeException(e2);
        }
        catch (IllegalArgumentException e3) {
            throw new YarnRuntimeException(e3);
        }
        catch (InstantiationException e4) {
            throw new YarnRuntimeException(e4);
        }
        catch (IllegalAccessException e5) {
            throw new YarnRuntimeException(e5);
        }
        catch (InvocationTargetException e6) {
            throw new YarnRuntimeException(e6);
        }
        return ex;
    }
}
