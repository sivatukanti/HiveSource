// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.ipc;

import java.io.IOException;
import com.google.protobuf.ServiceException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "MapReduce", "YARN" })
public class RPCUtil
{
    public static YarnException getRemoteException(final Throwable t) {
        return new YarnException(t);
    }
    
    public static YarnException getRemoteException(final String message) {
        return new YarnException(message);
    }
    
    private static <T extends Throwable> T instantiateException(final Class<? extends T> cls, final RemoteException re) throws RemoteException {
        try {
            final Constructor<? extends T> cn = cls.getConstructor(String.class);
            cn.setAccessible(true);
            final T ex = (T)cn.newInstance(re.getMessage());
            ex.initCause(re);
            return ex;
        }
        catch (NoSuchMethodException e) {
            throw re;
        }
        catch (IllegalArgumentException e2) {
            throw re;
        }
        catch (SecurityException e3) {
            throw re;
        }
        catch (InstantiationException e4) {
            throw re;
        }
        catch (IllegalAccessException e5) {
            throw re;
        }
        catch (InvocationTargetException e6) {
            throw re;
        }
    }
    
    public static Void unwrapAndThrowException(final ServiceException se) throws IOException, YarnException {
        final Throwable cause = se.getCause();
        if (cause == null) {
            throw new IOException(se);
        }
        if (cause instanceof RemoteException) {
            final RemoteException re = (RemoteException)cause;
            Class<?> realClass = null;
            try {
                realClass = Class.forName(re.getClassName());
            }
            catch (ClassNotFoundException cnf) {
                throw (YarnException)instantiateException((Class<? extends Throwable>)YarnException.class, re);
            }
            if (YarnException.class.isAssignableFrom(realClass)) {
                throw (YarnException)instantiateException((Class<? extends Throwable>)realClass.asSubclass(YarnException.class), re);
            }
            if (IOException.class.isAssignableFrom(realClass)) {
                throw (IOException)instantiateException((Class<? extends Throwable>)realClass.asSubclass(IOException.class), re);
            }
            if (RuntimeException.class.isAssignableFrom(realClass)) {
                throw (RuntimeException)instantiateException((Class<? extends Throwable>)realClass.asSubclass(RuntimeException.class), re);
            }
            throw re;
        }
        else {
            if (cause instanceof IOException) {
                throw (IOException)cause;
            }
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            throw new IOException(se);
        }
    }
}
