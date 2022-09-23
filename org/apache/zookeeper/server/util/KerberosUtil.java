// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class KerberosUtil
{
    public static String getDefaultRealm() throws ClassNotFoundException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Class<?> classRef;
        if (System.getProperty("java.vendor").contains("IBM")) {
            classRef = Class.forName("com.ibm.security.krb5.internal.Config");
        }
        else {
            classRef = Class.forName("sun.security.krb5.Config");
        }
        final Method getInstanceMethod = classRef.getMethod("getInstance", (Class<?>[])new Class[0]);
        final Object kerbConf = getInstanceMethod.invoke(classRef, new Object[0]);
        final Method getDefaultRealmMethod = classRef.getDeclaredMethod("getDefaultRealm", (Class<?>[])new Class[0]);
        return (String)getDefaultRealmMethod.invoke(kerbConf, new Object[0]);
    }
}
