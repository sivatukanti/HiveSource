// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import org.xml.sax.Attributes;
import java.lang.reflect.Constructor;
import org.apache.hadoop.ipc.protobuf.RpcHeaderProtos;
import java.io.IOException;

public class RemoteException extends IOException
{
    private static final int UNSPECIFIED_ERROR = -1;
    private static final long serialVersionUID = 1L;
    private final int errorCode;
    private final String className;
    
    public RemoteException(final String className, final String msg) {
        this(className, msg, null);
    }
    
    public RemoteException(final String className, final String msg, final RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto erCode) {
        super(msg);
        this.className = className;
        if (erCode != null) {
            this.errorCode = erCode.getNumber();
        }
        else {
            this.errorCode = -1;
        }
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto getErrorCode() {
        return RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.valueOf(this.errorCode);
    }
    
    public IOException unwrapRemoteException(final Class<?>... lookupTypes) {
        if (lookupTypes == null) {
            return this;
        }
        for (final Class<?> lookupClass : lookupTypes) {
            if (lookupClass.getName().equals(this.getClassName())) {
                try {
                    return this.instantiateException(lookupClass.asSubclass(IOException.class));
                }
                catch (Exception e) {
                    return this;
                }
            }
        }
        return this;
    }
    
    public IOException unwrapRemoteException() {
        try {
            final Class<?> realClass = Class.forName(this.getClassName());
            return this.instantiateException(realClass.asSubclass(IOException.class));
        }
        catch (Exception ex) {
            return this;
        }
    }
    
    private IOException instantiateException(final Class<? extends IOException> cls) throws Exception {
        final Constructor<? extends IOException> cn = cls.getConstructor(String.class);
        cn.setAccessible(true);
        final IOException ex = (IOException)cn.newInstance(this.getMessage());
        ex.initCause(this);
        return ex;
    }
    
    public static RemoteException valueOf(final Attributes attrs) {
        return new RemoteException(attrs.getValue("class"), attrs.getValue("message"));
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "(" + this.className + "): " + this.getMessage();
    }
}
