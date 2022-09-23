// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.net.util;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import javax.net.ssl.SSLSocket;

public class SSLSocketUtils
{
    private SSLSocketUtils() {
    }
    
    public static boolean enableEndpointNameVerification(final SSLSocket socket) {
        try {
            final Class<?> cls = Class.forName("javax.net.ssl.SSLParameters");
            final Method setEndpointIdentificationAlgorithm = cls.getDeclaredMethod("setEndpointIdentificationAlgorithm", String.class);
            final Method getSSLParameters = SSLSocket.class.getDeclaredMethod("getSSLParameters", (Class<?>[])new Class[0]);
            final Method setSSLParameters = SSLSocket.class.getDeclaredMethod("setSSLParameters", cls);
            if (setEndpointIdentificationAlgorithm != null && getSSLParameters != null && setSSLParameters != null) {
                final Object sslParams = getSSLParameters.invoke(socket, new Object[0]);
                if (sslParams != null) {
                    setEndpointIdentificationAlgorithm.invoke(sslParams, "HTTPS");
                    setSSLParameters.invoke(socket, sslParams);
                    return true;
                }
            }
        }
        catch (SecurityException e) {}
        catch (ClassNotFoundException e2) {}
        catch (NoSuchMethodException e3) {}
        catch (IllegalArgumentException e4) {}
        catch (IllegalAccessException e5) {}
        catch (InvocationTargetException ex) {}
        return false;
    }
}
