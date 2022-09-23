// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.net.ssl.SSLSocket;
import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import javax.net.ssl.SSLSocketFactory;
import javax.net.SocketFactory;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketTimeoutException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

public class SocketFetcher
{
    private SocketFetcher() {
    }
    
    public static Socket getSocket(final String host, final int port, Properties props, String prefix, final boolean useSSL) throws IOException {
        if (prefix == null) {
            prefix = "socket";
        }
        if (props == null) {
            props = new Properties();
        }
        final String s = props.getProperty(prefix + ".connectiontimeout", null);
        int cto = -1;
        if (s != null) {
            try {
                cto = Integer.parseInt(s);
            }
            catch (NumberFormatException ex2) {}
        }
        Socket socket = null;
        final String timeout = props.getProperty(prefix + ".timeout", null);
        final String localaddrstr = props.getProperty(prefix + ".localaddress", null);
        InetAddress localaddr = null;
        if (localaddrstr != null) {
            localaddr = InetAddress.getByName(localaddrstr);
        }
        final String localportstr = props.getProperty(prefix + ".localport", null);
        int localport = 0;
        if (localportstr != null) {
            try {
                localport = Integer.parseInt(localportstr);
            }
            catch (NumberFormatException ex3) {}
        }
        boolean fb = false;
        final String fallback = props.getProperty(prefix + ".socketFactory.fallback", null);
        fb = (fallback == null || !fallback.equalsIgnoreCase("false"));
        final String sfClass = props.getProperty(prefix + ".socketFactory.class", null);
        int sfPort = -1;
        try {
            final SocketFactory sf = getSocketFactory(sfClass);
            if (sf != null) {
                final String sfPortStr = props.getProperty(prefix + ".socketFactory.port", null);
                if (sfPortStr != null) {
                    try {
                        sfPort = Integer.parseInt(sfPortStr);
                    }
                    catch (NumberFormatException ex4) {}
                }
                if (sfPort == -1) {
                    sfPort = port;
                }
                socket = createSocket(localaddr, localport, host, sfPort, cto, sf, useSSL);
            }
        }
        catch (SocketTimeoutException sex) {
            throw sex;
        }
        catch (Exception ex) {
            if (!fb) {
                if (ex instanceof InvocationTargetException) {
                    final Throwable t = ((InvocationTargetException)ex).getTargetException();
                    if (t instanceof Exception) {
                        ex = (Exception)t;
                    }
                }
                if (ex instanceof IOException) {
                    throw (IOException)ex;
                }
                final IOException ioex = new IOException("Couldn't connect using \"" + sfClass + "\" socket factory to host, port: " + host + ", " + sfPort + "; Exception: " + ex);
                ioex.initCause(ex);
                throw ioex;
            }
        }
        if (socket == null) {
            socket = createSocket(localaddr, localport, host, port, cto, null, useSSL);
        }
        int to = -1;
        if (timeout != null) {
            try {
                to = Integer.parseInt(timeout);
            }
            catch (NumberFormatException ex5) {}
        }
        if (to >= 0) {
            socket.setSoTimeout(to);
        }
        configureSSLSocket(socket, props, prefix);
        return socket;
    }
    
    public static Socket getSocket(final String host, final int port, final Properties props, final String prefix) throws IOException {
        return getSocket(host, port, props, prefix, false);
    }
    
    private static Socket createSocket(final InetAddress localaddr, final int localport, final String host, final int port, final int cto, final SocketFactory sf, final boolean useSSL) throws IOException {
        Socket socket;
        if (sf != null) {
            socket = sf.createSocket();
        }
        else if (useSSL) {
            socket = SSLSocketFactory.getDefault().createSocket();
        }
        else {
            socket = new Socket();
        }
        if (localaddr != null) {
            socket.bind(new InetSocketAddress(localaddr, localport));
        }
        if (cto >= 0) {
            socket.connect(new InetSocketAddress(host, port), cto);
        }
        else {
            socket.connect(new InetSocketAddress(host, port));
        }
        return socket;
    }
    
    private static SocketFactory getSocketFactory(final String sfClass) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (sfClass == null || sfClass.length() == 0) {
            return null;
        }
        final ClassLoader cl = getContextClassLoader();
        Class clsSockFact = null;
        if (cl != null) {
            try {
                clsSockFact = cl.loadClass(sfClass);
            }
            catch (ClassNotFoundException ex) {}
        }
        if (clsSockFact == null) {
            clsSockFact = Class.forName(sfClass);
        }
        final Method mthGetDefault = clsSockFact.getMethod("getDefault", (Class[])new Class[0]);
        final SocketFactory sf = (SocketFactory)mthGetDefault.invoke(new Object(), new Object[0]);
        return sf;
    }
    
    public static Socket startTLS(final Socket socket) throws IOException {
        return startTLS(socket, new Properties(), "socket");
    }
    
    public static Socket startTLS(Socket socket, final Properties props, final String prefix) throws IOException {
        final InetAddress a = socket.getInetAddress();
        final String host = a.getHostName();
        final int port = socket.getPort();
        try {
            final String sfClass = props.getProperty(prefix + ".socketFactory.class", null);
            final SocketFactory sf = getSocketFactory(sfClass);
            SSLSocketFactory ssf;
            if (sf != null && sf instanceof SSLSocketFactory) {
                ssf = (SSLSocketFactory)sf;
            }
            else {
                ssf = (SSLSocketFactory)SSLSocketFactory.getDefault();
            }
            socket = ssf.createSocket(socket, host, port, true);
            configureSSLSocket(socket, props, prefix);
        }
        catch (Exception ex) {
            if (ex instanceof InvocationTargetException) {
                final Throwable t = ((InvocationTargetException)ex).getTargetException();
                if (t instanceof Exception) {
                    ex = (Exception)t;
                }
            }
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            final IOException ioex = new IOException("Exception in startTLS: host " + host + ", port " + port + "; Exception: " + ex);
            ioex.initCause(ex);
            throw ioex;
        }
        return socket;
    }
    
    private static void configureSSLSocket(final Socket socket, final Properties props, final String prefix) {
        if (!(socket instanceof SSLSocket)) {
            return;
        }
        final SSLSocket sslsocket = (SSLSocket)socket;
        final String protocols = props.getProperty(prefix + ".ssl.protocols", null);
        if (protocols != null) {
            sslsocket.setEnabledProtocols(stringArray(protocols));
        }
        else {
            sslsocket.setEnabledProtocols(new String[] { "TLSv1" });
        }
        final String ciphers = props.getProperty(prefix + ".ssl.ciphersuites", null);
        if (ciphers != null) {
            sslsocket.setEnabledCipherSuites(stringArray(ciphers));
        }
    }
    
    private static String[] stringArray(final String s) {
        final StringTokenizer st = new StringTokenizer(s);
        final List tokens = new ArrayList();
        while (st.hasMoreTokens()) {
            tokens.add(st.nextToken());
        }
        return tokens.toArray(new String[tokens.size()]);
    }
    
    private static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
            public Object run() {
                ClassLoader cl = null;
                try {
                    cl = Thread.currentThread().getContextClassLoader();
                }
                catch (SecurityException ex) {}
                return cl;
            }
        });
    }
}
