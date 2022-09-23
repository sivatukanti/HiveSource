// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.session;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.lang.reflect.Method;
import org.apache.hive.service.cli.HiveSQLException;
import java.lang.reflect.Proxy;
import org.apache.hadoop.security.UserGroupInformation;
import java.lang.reflect.InvocationHandler;

public class HiveSessionProxy implements InvocationHandler
{
    private final HiveSession base;
    private final UserGroupInformation ugi;
    
    public HiveSessionProxy(final HiveSession hiveSession, final UserGroupInformation ugi) {
        this.base = hiveSession;
        this.ugi = ugi;
    }
    
    public static HiveSession getProxy(final HiveSession hiveSession, final UserGroupInformation ugi) throws IllegalArgumentException, HiveSQLException {
        return (HiveSession)Proxy.newProxyInstance(HiveSession.class.getClassLoader(), new Class[] { HiveSession.class }, new HiveSessionProxy(hiveSession, ugi));
    }
    
    @Override
    public Object invoke(final Object arg0, final Method method, final Object[] args) throws Throwable {
        try {
            if (method.getDeclaringClass() == HiveSessionBase.class) {
                return this.invoke(method, args);
            }
            return this.ugi.doAs((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws HiveSQLException {
                    return HiveSessionProxy.this.invoke(method, args);
                }
            });
        }
        catch (UndeclaredThrowableException e) {
            final Throwable innerException = e.getCause();
            if (innerException instanceof PrivilegedActionException) {
                throw innerException.getCause();
            }
            throw e.getCause();
        }
    }
    
    private Object invoke(final Method method, final Object[] args) throws HiveSQLException {
        try {
            return method.invoke(this.base, args);
        }
        catch (InvocationTargetException e) {
            if (e.getCause() instanceof HiveSQLException) {
                throw (HiveSQLException)e.getCause();
            }
            throw new RuntimeException(e.getCause());
        }
        catch (IllegalArgumentException e2) {
            throw new RuntimeException(e2);
        }
        catch (IllegalAccessException e3) {
            throw new RuntimeException(e3);
        }
    }
}
