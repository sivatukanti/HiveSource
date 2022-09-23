// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.transport;

import java.util.Arrays;
import java.net.Socket;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import java.security.SecureRandom;
import javax.net.ssl.KeyManagerFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLSocketFactory;
import java.net.ServerSocket;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import java.net.InetAddress;

public class TSSLTransportFactory
{
    public static TServerSocket getServerSocket(final int port) throws TTransportException {
        return getServerSocket(port, 0);
    }
    
    public static TServerSocket getServerSocket(final int port, final int clientTimeout) throws TTransportException {
        return getServerSocket(port, clientTimeout, false, null);
    }
    
    public static TServerSocket getServerSocket(final int port, final int clientTimeout, final boolean clientAuth, final InetAddress ifAddress) throws TTransportException {
        final SSLServerSocketFactory factory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
        return createServer(factory, port, clientTimeout, clientAuth, ifAddress, null);
    }
    
    public static TServerSocket getServerSocket(final int port, final int clientTimeout, final InetAddress ifAddress, final TSSLTransportParameters params) throws TTransportException {
        if (params == null || (!params.isKeyStoreSet && !params.isTrustStoreSet)) {
            throw new TTransportException("Either one of the KeyStore or TrustStore must be set for SSLTransportParameters");
        }
        final SSLContext ctx = createSSLContext(params);
        return createServer(ctx.getServerSocketFactory(), port, clientTimeout, params.clientAuth, ifAddress, params);
    }
    
    private static TServerSocket createServer(final SSLServerSocketFactory factory, final int port, final int timeout, final boolean clientAuth, final InetAddress ifAddress, final TSSLTransportParameters params) throws TTransportException {
        try {
            final SSLServerSocket serverSocket = (SSLServerSocket)factory.createServerSocket(port, 100, ifAddress);
            serverSocket.setSoTimeout(timeout);
            serverSocket.setNeedClientAuth(clientAuth);
            if (params != null && params.cipherSuites != null) {
                serverSocket.setEnabledCipherSuites(params.cipherSuites);
            }
            return new TServerSocket(new TServerSocket.ServerSocketTransportArgs().serverSocket(serverSocket).clientTimeout(timeout));
        }
        catch (Exception e) {
            throw new TTransportException("Could not bind to port " + port, e);
        }
    }
    
    public static TSocket getClientSocket(final String host, final int port, final int timeout) throws TTransportException {
        final SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
        return createClient(factory, host, port, timeout);
    }
    
    public static TSocket getClientSocket(final String host, final int port) throws TTransportException {
        return getClientSocket(host, port, 0);
    }
    
    public static TSocket getClientSocket(final String host, final int port, final int timeout, final TSSLTransportParameters params) throws TTransportException {
        if (params == null || (!params.isKeyStoreSet && !params.isTrustStoreSet)) {
            throw new TTransportException("Either one of the KeyStore or TrustStore must be set for SSLTransportParameters");
        }
        final SSLContext ctx = createSSLContext(params);
        return createClient(ctx.getSocketFactory(), host, port, timeout);
    }
    
    private static SSLContext createSSLContext(final TSSLTransportParameters params) throws TTransportException {
        FileInputStream fin = null;
        FileInputStream fis = null;
        SSLContext ctx;
        try {
            ctx = SSLContext.getInstance(params.protocol);
            TrustManagerFactory tmf = null;
            KeyManagerFactory kmf = null;
            if (params.isTrustStoreSet) {
                tmf = TrustManagerFactory.getInstance(params.trustManagerType);
                final KeyStore ts = KeyStore.getInstance(params.trustStoreType);
                fin = new FileInputStream(params.trustStore);
                ts.load(fin, (char[])((params.trustPass != null) ? params.trustPass.toCharArray() : null));
                tmf.init(ts);
            }
            if (params.isKeyStoreSet) {
                kmf = KeyManagerFactory.getInstance(params.keyManagerType);
                final KeyStore ks = KeyStore.getInstance(params.keyStoreType);
                fis = new FileInputStream(params.keyStore);
                ks.load(fis, params.keyPass.toCharArray());
                kmf.init(ks, params.keyPass.toCharArray());
            }
            if (params.isKeyStoreSet && params.isTrustStoreSet) {
                ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            }
            else if (params.isKeyStoreSet) {
                ctx.init(kmf.getKeyManagers(), null, null);
            }
            else {
                ctx.init(null, tmf.getTrustManagers(), null);
            }
        }
        catch (Exception e) {
            throw new TTransportException("Error creating the transport", e);
        }
        finally {
            if (fin != null) {
                try {
                    fin.close();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return ctx;
    }
    
    private static TSocket createClient(final SSLSocketFactory factory, final String host, final int port, final int timeout) throws TTransportException {
        try {
            final SSLSocket socket = (SSLSocket)factory.createSocket(host, port);
            socket.setSoTimeout(timeout);
            return new TSocket(socket);
        }
        catch (Exception e) {
            throw new TTransportException("Could not connect to " + host + " on port " + port, e);
        }
    }
    
    public static class TSSLTransportParameters
    {
        protected String protocol;
        protected String keyStore;
        protected String keyPass;
        protected String keyManagerType;
        protected String keyStoreType;
        protected String trustStore;
        protected String trustPass;
        protected String trustManagerType;
        protected String trustStoreType;
        protected String[] cipherSuites;
        protected boolean clientAuth;
        protected boolean isKeyStoreSet;
        protected boolean isTrustStoreSet;
        
        public TSSLTransportParameters() {
            this.protocol = "TLS";
            this.keyManagerType = KeyManagerFactory.getDefaultAlgorithm();
            this.keyStoreType = "JKS";
            this.trustManagerType = TrustManagerFactory.getDefaultAlgorithm();
            this.trustStoreType = "JKS";
            this.clientAuth = false;
            this.isKeyStoreSet = false;
            this.isTrustStoreSet = false;
        }
        
        public TSSLTransportParameters(final String protocol, final String[] cipherSuites) {
            this(protocol, cipherSuites, false);
        }
        
        public TSSLTransportParameters(final String protocol, final String[] cipherSuites, final boolean clientAuth) {
            this.protocol = "TLS";
            this.keyManagerType = KeyManagerFactory.getDefaultAlgorithm();
            this.keyStoreType = "JKS";
            this.trustManagerType = TrustManagerFactory.getDefaultAlgorithm();
            this.trustStoreType = "JKS";
            this.clientAuth = false;
            this.isKeyStoreSet = false;
            this.isTrustStoreSet = false;
            if (protocol != null) {
                this.protocol = protocol;
            }
            this.cipherSuites = Arrays.copyOf(cipherSuites, cipherSuites.length);
            this.clientAuth = clientAuth;
        }
        
        public void setKeyStore(final String keyStore, final String keyPass, final String keyManagerType, final String keyStoreType) {
            this.keyStore = keyStore;
            this.keyPass = keyPass;
            if (keyManagerType != null) {
                this.keyManagerType = keyManagerType;
            }
            if (keyStoreType != null) {
                this.keyStoreType = keyStoreType;
            }
            this.isKeyStoreSet = true;
        }
        
        public void setKeyStore(final String keyStore, final String keyPass) {
            this.setKeyStore(keyStore, keyPass, null, null);
        }
        
        public void setTrustStore(final String trustStore, final String trustPass, final String trustManagerType, final String trustStoreType) {
            this.trustStore = trustStore;
            this.trustPass = trustPass;
            if (trustManagerType != null) {
                this.trustManagerType = trustManagerType;
            }
            if (trustStoreType != null) {
                this.trustStoreType = trustStoreType;
            }
            this.isTrustStoreSet = true;
        }
        
        public void setTrustStore(final String trustStore, final String trustPass) {
            this.setTrustStore(trustStore, trustPass, null, null);
        }
        
        public void requireClientAuth(final boolean clientAuth) {
            this.clientAuth = clientAuth;
        }
    }
}
