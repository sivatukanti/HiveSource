// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.httpclient;

import org.apache.commons.logging.LogFactory;
import java.lang.reflect.Method;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.net.SocketException;
import java.io.InterruptedIOException;
import org.apache.commons.httpclient.util.ExceptionUtil;
import java.io.IOException;
import java.net.InetAddress;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.Protocol;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;
import org.apache.commons.logging.Log;

public class HttpConnection
{
    private static final byte[] CRLF;
    private static final Log LOG;
    private String hostName;
    private int portNumber;
    private String proxyHostName;
    private int proxyPortNumber;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private InputStream lastResponseInputStream;
    protected boolean isOpen;
    private Protocol protocolInUse;
    private HttpConnectionParams params;
    private boolean locked;
    private boolean usingSecureSocket;
    private boolean tunnelEstablished;
    private HttpConnectionManager httpConnectionManager;
    private InetAddress localAddress;
    
    public HttpConnection(final String host, final int port) {
        this(null, -1, host, null, port, Protocol.getProtocol("http"));
    }
    
    public HttpConnection(final String host, final int port, final Protocol protocol) {
        this(null, -1, host, null, port, protocol);
    }
    
    public HttpConnection(final String host, final String virtualHost, final int port, final Protocol protocol) {
        this(null, -1, host, virtualHost, port, protocol);
    }
    
    public HttpConnection(final String proxyHost, final int proxyPort, final String host, final int port) {
        this(proxyHost, proxyPort, host, null, port, Protocol.getProtocol("http"));
    }
    
    public HttpConnection(final HostConfiguration hostConfiguration) {
        this(hostConfiguration.getProxyHost(), hostConfiguration.getProxyPort(), hostConfiguration.getHost(), hostConfiguration.getPort(), hostConfiguration.getProtocol());
        this.localAddress = hostConfiguration.getLocalAddress();
    }
    
    public HttpConnection(final String proxyHost, final int proxyPort, final String host, final String virtualHost, final int port, final Protocol protocol) {
        this(proxyHost, proxyPort, host, port, protocol);
    }
    
    public HttpConnection(final String proxyHost, final int proxyPort, final String host, final int port, final Protocol protocol) {
        this.hostName = null;
        this.portNumber = -1;
        this.proxyHostName = null;
        this.proxyPortNumber = -1;
        this.socket = null;
        this.inputStream = null;
        this.outputStream = null;
        this.lastResponseInputStream = null;
        this.isOpen = false;
        this.params = new HttpConnectionParams();
        this.locked = false;
        this.usingSecureSocket = false;
        this.tunnelEstablished = false;
        if (host == null) {
            throw new IllegalArgumentException("host parameter is null");
        }
        if (protocol == null) {
            throw new IllegalArgumentException("protocol is null");
        }
        this.proxyHostName = proxyHost;
        this.proxyPortNumber = proxyPort;
        this.hostName = host;
        this.portNumber = protocol.resolvePort(port);
        this.protocolInUse = protocol;
    }
    
    protected Socket getSocket() {
        return this.socket;
    }
    
    public String getHost() {
        return this.hostName;
    }
    
    public void setHost(final String host) throws IllegalStateException {
        if (host == null) {
            throw new IllegalArgumentException("host parameter is null");
        }
        this.assertNotOpen();
        this.hostName = host;
    }
    
    public String getVirtualHost() {
        return this.hostName;
    }
    
    public void setVirtualHost(final String host) throws IllegalStateException {
        this.assertNotOpen();
    }
    
    public int getPort() {
        if (this.portNumber < 0) {
            return this.isSecure() ? 443 : 80;
        }
        return this.portNumber;
    }
    
    public void setPort(final int port) throws IllegalStateException {
        this.assertNotOpen();
        this.portNumber = port;
    }
    
    public String getProxyHost() {
        return this.proxyHostName;
    }
    
    public void setProxyHost(final String host) throws IllegalStateException {
        this.assertNotOpen();
        this.proxyHostName = host;
    }
    
    public int getProxyPort() {
        return this.proxyPortNumber;
    }
    
    public void setProxyPort(final int port) throws IllegalStateException {
        this.assertNotOpen();
        this.proxyPortNumber = port;
    }
    
    public boolean isSecure() {
        return this.protocolInUse.isSecure();
    }
    
    public Protocol getProtocol() {
        return this.protocolInUse;
    }
    
    public void setProtocol(final Protocol protocol) {
        this.assertNotOpen();
        if (protocol == null) {
            throw new IllegalArgumentException("protocol is null");
        }
        this.protocolInUse = protocol;
    }
    
    public InetAddress getLocalAddress() {
        return this.localAddress;
    }
    
    public void setLocalAddress(final InetAddress localAddress) {
        this.assertNotOpen();
        this.localAddress = localAddress;
    }
    
    public boolean isOpen() {
        return this.isOpen;
    }
    
    public boolean closeIfStale() throws IOException {
        if (this.isOpen && this.isStale()) {
            HttpConnection.LOG.debug("Connection is stale, closing...");
            this.close();
            return true;
        }
        return false;
    }
    
    public boolean isStaleCheckingEnabled() {
        return this.params.isStaleCheckingEnabled();
    }
    
    public void setStaleCheckingEnabled(final boolean staleCheckEnabled) {
        this.params.setStaleCheckingEnabled(staleCheckEnabled);
    }
    
    protected boolean isStale() throws IOException {
        boolean isStale = true;
        if (this.isOpen) {
            isStale = false;
            try {
                if (this.inputStream.available() <= 0) {
                    try {
                        this.socket.setSoTimeout(1);
                        this.inputStream.mark(1);
                        final int byteRead = this.inputStream.read();
                        if (byteRead == -1) {
                            isStale = true;
                        }
                        else {
                            this.inputStream.reset();
                        }
                    }
                    finally {
                        this.socket.setSoTimeout(this.params.getSoTimeout());
                    }
                }
            }
            catch (InterruptedIOException e) {
                if (!ExceptionUtil.isSocketTimeoutException(e)) {
                    throw e;
                }
            }
            catch (IOException e2) {
                HttpConnection.LOG.debug("An error occurred while reading from the socket, is appears to be stale", e2);
                isStale = true;
            }
        }
        return isStale;
    }
    
    public boolean isProxied() {
        return null != this.proxyHostName && 0 < this.proxyPortNumber;
    }
    
    public void setLastResponseInputStream(final InputStream inStream) {
        this.lastResponseInputStream = inStream;
    }
    
    public InputStream getLastResponseInputStream() {
        return this.lastResponseInputStream;
    }
    
    public HttpConnectionParams getParams() {
        return this.params;
    }
    
    public void setParams(final HttpConnectionParams params) {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        this.params = params;
    }
    
    public void setSoTimeout(final int timeout) throws SocketException, IllegalStateException {
        this.params.setSoTimeout(timeout);
        if (this.socket != null) {
            this.socket.setSoTimeout(timeout);
        }
    }
    
    public void setSocketTimeout(final int timeout) throws SocketException, IllegalStateException {
        this.assertOpen();
        if (this.socket != null) {
            this.socket.setSoTimeout(timeout);
        }
    }
    
    public int getSoTimeout() throws SocketException {
        return this.params.getSoTimeout();
    }
    
    public void setConnectionTimeout(final int timeout) {
        this.params.setConnectionTimeout(timeout);
    }
    
    public void open() throws IOException {
        HttpConnection.LOG.trace("enter HttpConnection.open()");
        final String host = (this.proxyHostName == null) ? this.hostName : this.proxyHostName;
        final int port = (this.proxyHostName == null) ? this.portNumber : this.proxyPortNumber;
        this.assertNotOpen();
        if (HttpConnection.LOG.isDebugEnabled()) {
            HttpConnection.LOG.debug("Open connection to " + host + ":" + port);
        }
        try {
            if (this.socket == null) {
                this.usingSecureSocket = (this.isSecure() && !this.isProxied());
                ProtocolSocketFactory socketFactory = null;
                if (this.isSecure() && this.isProxied()) {
                    final Protocol defaultprotocol = Protocol.getProtocol("http");
                    socketFactory = defaultprotocol.getSocketFactory();
                }
                else {
                    socketFactory = this.protocolInUse.getSocketFactory();
                }
                this.socket = socketFactory.createSocket(host, port, this.localAddress, 0, this.params);
            }
            this.socket.setTcpNoDelay(this.params.getTcpNoDelay());
            this.socket.setSoTimeout(this.params.getSoTimeout());
            final int linger = this.params.getLinger();
            if (linger >= 0) {
                this.socket.setSoLinger(linger > 0, linger);
            }
            final int sndBufSize = this.params.getSendBufferSize();
            if (sndBufSize >= 0) {
                this.socket.setSendBufferSize(sndBufSize);
            }
            final int rcvBufSize = this.params.getReceiveBufferSize();
            if (rcvBufSize >= 0) {
                this.socket.setReceiveBufferSize(rcvBufSize);
            }
            int outbuffersize = this.socket.getSendBufferSize();
            if (outbuffersize > 2048 || outbuffersize <= 0) {
                outbuffersize = 2048;
            }
            int inbuffersize = this.socket.getReceiveBufferSize();
            if (inbuffersize > 2048 || inbuffersize <= 0) {
                inbuffersize = 2048;
            }
            this.inputStream = new BufferedInputStream(this.socket.getInputStream(), inbuffersize);
            this.outputStream = new BufferedOutputStream(this.socket.getOutputStream(), outbuffersize);
            this.isOpen = true;
        }
        catch (IOException e) {
            this.closeSocketAndStreams();
            throw e;
        }
    }
    
    public void tunnelCreated() throws IllegalStateException, IOException {
        HttpConnection.LOG.trace("enter HttpConnection.tunnelCreated()");
        if (!this.isSecure() || !this.isProxied()) {
            throw new IllegalStateException("Connection must be secure and proxied to use this feature");
        }
        if (this.usingSecureSocket) {
            throw new IllegalStateException("Already using a secure socket");
        }
        if (HttpConnection.LOG.isDebugEnabled()) {
            HttpConnection.LOG.debug("Secure tunnel to " + this.hostName + ":" + this.portNumber);
        }
        final SecureProtocolSocketFactory socketFactory = (SecureProtocolSocketFactory)this.protocolInUse.getSocketFactory();
        this.socket = socketFactory.createSocket(this.socket, this.hostName, this.portNumber, true);
        final int sndBufSize = this.params.getSendBufferSize();
        if (sndBufSize >= 0) {
            this.socket.setSendBufferSize(sndBufSize);
        }
        final int rcvBufSize = this.params.getReceiveBufferSize();
        if (rcvBufSize >= 0) {
            this.socket.setReceiveBufferSize(rcvBufSize);
        }
        int outbuffersize = this.socket.getSendBufferSize();
        if (outbuffersize > 2048) {
            outbuffersize = 2048;
        }
        int inbuffersize = this.socket.getReceiveBufferSize();
        if (inbuffersize > 2048) {
            inbuffersize = 2048;
        }
        this.inputStream = new BufferedInputStream(this.socket.getInputStream(), inbuffersize);
        this.outputStream = new BufferedOutputStream(this.socket.getOutputStream(), outbuffersize);
        this.usingSecureSocket = true;
        this.tunnelEstablished = true;
    }
    
    public boolean isTransparent() {
        return !this.isProxied() || this.tunnelEstablished;
    }
    
    public void flushRequestOutputStream() throws IOException {
        HttpConnection.LOG.trace("enter HttpConnection.flushRequestOutputStream()");
        this.assertOpen();
        this.outputStream.flush();
    }
    
    public OutputStream getRequestOutputStream() throws IOException, IllegalStateException {
        HttpConnection.LOG.trace("enter HttpConnection.getRequestOutputStream()");
        this.assertOpen();
        OutputStream out = this.outputStream;
        if (Wire.CONTENT_WIRE.enabled()) {
            out = new WireLogOutputStream(out, Wire.CONTENT_WIRE);
        }
        return out;
    }
    
    public InputStream getResponseInputStream() throws IOException, IllegalStateException {
        HttpConnection.LOG.trace("enter HttpConnection.getResponseInputStream()");
        this.assertOpen();
        return this.inputStream;
    }
    
    public boolean isResponseAvailable() throws IOException {
        HttpConnection.LOG.trace("enter HttpConnection.isResponseAvailable()");
        return this.isOpen && this.inputStream.available() > 0;
    }
    
    public boolean isResponseAvailable(final int timeout) throws IOException {
        HttpConnection.LOG.trace("enter HttpConnection.isResponseAvailable(int)");
        this.assertOpen();
        boolean result = false;
        if (this.inputStream.available() > 0) {
            result = true;
        }
        else {
            try {
                this.socket.setSoTimeout(timeout);
                this.inputStream.mark(1);
                final int byteRead = this.inputStream.read();
                if (byteRead != -1) {
                    this.inputStream.reset();
                    HttpConnection.LOG.debug("Input data available");
                    result = true;
                }
                else {
                    HttpConnection.LOG.debug("Input data not available");
                }
            }
            catch (InterruptedIOException e) {
                if (!ExceptionUtil.isSocketTimeoutException(e)) {
                    throw e;
                }
                if (HttpConnection.LOG.isDebugEnabled()) {
                    HttpConnection.LOG.debug("Input data not available after " + timeout + " ms");
                }
                try {
                    this.socket.setSoTimeout(this.params.getSoTimeout());
                }
                catch (IOException ioe) {
                    HttpConnection.LOG.debug("An error ocurred while resetting soTimeout, we will assume that no response is available.", ioe);
                    result = false;
                }
            }
            finally {
                try {
                    this.socket.setSoTimeout(this.params.getSoTimeout());
                }
                catch (IOException ioe2) {
                    HttpConnection.LOG.debug("An error ocurred while resetting soTimeout, we will assume that no response is available.", ioe2);
                    result = false;
                }
            }
        }
        return result;
    }
    
    public void write(final byte[] data) throws IOException, IllegalStateException {
        HttpConnection.LOG.trace("enter HttpConnection.write(byte[])");
        this.write(data, 0, data.length);
    }
    
    public void write(final byte[] data, final int offset, final int length) throws IOException, IllegalStateException {
        HttpConnection.LOG.trace("enter HttpConnection.write(byte[], int, int)");
        if (offset < 0) {
            throw new IllegalArgumentException("Array offset may not be negative");
        }
        if (length < 0) {
            throw new IllegalArgumentException("Array length may not be negative");
        }
        if (offset + length > data.length) {
            throw new IllegalArgumentException("Given offset and length exceed the array length");
        }
        this.assertOpen();
        this.outputStream.write(data, offset, length);
    }
    
    public void writeLine(final byte[] data) throws IOException, IllegalStateException {
        HttpConnection.LOG.trace("enter HttpConnection.writeLine(byte[])");
        this.write(data);
        this.writeLine();
    }
    
    public void writeLine() throws IOException, IllegalStateException {
        HttpConnection.LOG.trace("enter HttpConnection.writeLine()");
        this.write(HttpConnection.CRLF);
    }
    
    public void print(final String data) throws IOException, IllegalStateException {
        HttpConnection.LOG.trace("enter HttpConnection.print(String)");
        this.write(EncodingUtil.getBytes(data, "ISO-8859-1"));
    }
    
    public void print(final String data, final String charset) throws IOException, IllegalStateException {
        HttpConnection.LOG.trace("enter HttpConnection.print(String)");
        this.write(EncodingUtil.getBytes(data, charset));
    }
    
    public void printLine(final String data) throws IOException, IllegalStateException {
        HttpConnection.LOG.trace("enter HttpConnection.printLine(String)");
        this.writeLine(EncodingUtil.getBytes(data, "ISO-8859-1"));
    }
    
    public void printLine(final String data, final String charset) throws IOException, IllegalStateException {
        HttpConnection.LOG.trace("enter HttpConnection.printLine(String)");
        this.writeLine(EncodingUtil.getBytes(data, charset));
    }
    
    public void printLine() throws IOException, IllegalStateException {
        HttpConnection.LOG.trace("enter HttpConnection.printLine()");
        this.writeLine();
    }
    
    public String readLine() throws IOException, IllegalStateException {
        HttpConnection.LOG.trace("enter HttpConnection.readLine()");
        this.assertOpen();
        return HttpParser.readLine(this.inputStream);
    }
    
    public String readLine(final String charset) throws IOException, IllegalStateException {
        HttpConnection.LOG.trace("enter HttpConnection.readLine()");
        this.assertOpen();
        return HttpParser.readLine(this.inputStream, charset);
    }
    
    public void shutdownOutput() {
        HttpConnection.LOG.trace("enter HttpConnection.shutdownOutput()");
        try {
            final Class[] paramsClasses = new Class[0];
            final Method shutdownOutput = this.socket.getClass().getMethod("shutdownOutput", (Class<?>[])paramsClasses);
            final Object[] params = new Object[0];
            shutdownOutput.invoke(this.socket, params);
        }
        catch (Exception ex) {
            HttpConnection.LOG.debug("Unexpected Exception caught", ex);
        }
    }
    
    public void close() {
        HttpConnection.LOG.trace("enter HttpConnection.close()");
        this.closeSocketAndStreams();
    }
    
    public HttpConnectionManager getHttpConnectionManager() {
        return this.httpConnectionManager;
    }
    
    public void setHttpConnectionManager(final HttpConnectionManager httpConnectionManager) {
        this.httpConnectionManager = httpConnectionManager;
    }
    
    public void releaseConnection() {
        HttpConnection.LOG.trace("enter HttpConnection.releaseConnection()");
        if (this.locked) {
            HttpConnection.LOG.debug("Connection is locked.  Call to releaseConnection() ignored.");
        }
        else if (this.httpConnectionManager != null) {
            HttpConnection.LOG.debug("Releasing connection back to connection manager.");
            this.httpConnectionManager.releaseConnection(this);
        }
        else {
            HttpConnection.LOG.warn("HttpConnectionManager is null.  Connection cannot be released.");
        }
    }
    
    protected boolean isLocked() {
        return this.locked;
    }
    
    protected void setLocked(final boolean locked) {
        this.locked = locked;
    }
    
    protected void closeSocketAndStreams() {
        HttpConnection.LOG.trace("enter HttpConnection.closeSockedAndStreams()");
        this.isOpen = false;
        this.lastResponseInputStream = null;
        if (null != this.outputStream) {
            final OutputStream temp = this.outputStream;
            this.outputStream = null;
            try {
                temp.close();
            }
            catch (Exception ex) {
                HttpConnection.LOG.debug("Exception caught when closing output", ex);
            }
        }
        if (null != this.inputStream) {
            final InputStream temp2 = this.inputStream;
            this.inputStream = null;
            try {
                temp2.close();
            }
            catch (Exception ex) {
                HttpConnection.LOG.debug("Exception caught when closing input", ex);
            }
        }
        if (null != this.socket) {
            final Socket temp3 = this.socket;
            this.socket = null;
            try {
                temp3.close();
            }
            catch (Exception ex) {
                HttpConnection.LOG.debug("Exception caught when closing socket", ex);
            }
        }
        this.tunnelEstablished = false;
        this.usingSecureSocket = false;
    }
    
    protected void assertNotOpen() throws IllegalStateException {
        if (this.isOpen) {
            throw new IllegalStateException("Connection is open");
        }
    }
    
    protected void assertOpen() throws IllegalStateException {
        if (!this.isOpen) {
            throw new IllegalStateException("Connection is not open");
        }
    }
    
    public int getSendBufferSize() throws SocketException {
        if (this.socket == null) {
            return -1;
        }
        return this.socket.getSendBufferSize();
    }
    
    public void setSendBufferSize(final int sendBufferSize) throws SocketException {
        this.params.setSendBufferSize(sendBufferSize);
    }
    
    static {
        CRLF = new byte[] { 13, 10 };
        LOG = LogFactory.getLog(HttpConnection.class);
    }
}
