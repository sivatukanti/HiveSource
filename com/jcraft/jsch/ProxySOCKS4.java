// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.net.UnknownHostException;
import java.net.InetAddress;
import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;

public class ProxySOCKS4 implements Proxy
{
    private static int DEFAULTPORT;
    private String proxy_host;
    private int proxy_port;
    private InputStream in;
    private OutputStream out;
    private Socket socket;
    private String user;
    private String passwd;
    
    public ProxySOCKS4(final String proxy_host) {
        int port = ProxySOCKS4.DEFAULTPORT;
        String host = proxy_host;
        if (proxy_host.indexOf(58) != -1) {
            try {
                host = proxy_host.substring(0, proxy_host.indexOf(58));
                port = Integer.parseInt(proxy_host.substring(proxy_host.indexOf(58) + 1));
            }
            catch (Exception ex) {}
        }
        this.proxy_host = host;
        this.proxy_port = port;
    }
    
    public ProxySOCKS4(final String proxy_host, final int proxy_port) {
        this.proxy_host = proxy_host;
        this.proxy_port = proxy_port;
    }
    
    public void setUserPasswd(final String user, final String passwd) {
        this.user = user;
        this.passwd = passwd;
    }
    
    public void connect(final SocketFactory socket_factory, final String host, final int port, final int timeout) throws JSchException {
        try {
            if (socket_factory == null) {
                this.socket = Util.createSocket(this.proxy_host, this.proxy_port, timeout);
                this.in = this.socket.getInputStream();
                this.out = this.socket.getOutputStream();
            }
            else {
                this.socket = socket_factory.createSocket(this.proxy_host, this.proxy_port);
                this.in = socket_factory.getInputStream(this.socket);
                this.out = socket_factory.getOutputStream(this.socket);
            }
            if (timeout > 0) {
                this.socket.setSoTimeout(timeout);
            }
            this.socket.setTcpNoDelay(true);
            final byte[] buf = new byte[1024];
            int index = 0;
            index = 0;
            buf[index++] = 4;
            buf[index++] = 1;
            buf[index++] = (byte)(port >>> 8);
            buf[index++] = (byte)(port & 0xFF);
            try {
                final InetAddress addr = InetAddress.getByName(host);
                final byte[] byteAddress = addr.getAddress();
                for (int i = 0; i < byteAddress.length; ++i) {
                    buf[index++] = byteAddress[i];
                }
            }
            catch (UnknownHostException uhe) {
                throw new JSchException("ProxySOCKS4: " + uhe.toString(), uhe);
            }
            if (this.user != null) {
                System.arraycopy(Util.str2byte(this.user), 0, buf, index, this.user.length());
                index += this.user.length();
            }
            buf[index++] = 0;
            this.out.write(buf, 0, index);
            int i;
            for (int len = 8, s = 0; s < len; s += i) {
                i = this.in.read(buf, s, len - s);
                if (i <= 0) {
                    throw new JSchException("ProxySOCKS4: stream is closed");
                }
            }
            if (buf[0] != 0) {
                throw new JSchException("ProxySOCKS4: server returns VN " + buf[0]);
            }
            if (buf[1] != 90) {
                try {
                    this.socket.close();
                }
                catch (Exception ex) {}
                final String message = "ProxySOCKS4: server returns CD " + buf[1];
                throw new JSchException(message);
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e2) {
            try {
                if (this.socket != null) {
                    this.socket.close();
                }
            }
            catch (Exception ex2) {}
            throw new JSchException("ProxySOCKS4: " + e2.toString());
        }
    }
    
    public InputStream getInputStream() {
        return this.in;
    }
    
    public OutputStream getOutputStream() {
        return this.out;
    }
    
    public Socket getSocket() {
        return this.socket;
    }
    
    public void close() {
        try {
            if (this.in != null) {
                this.in.close();
            }
            if (this.out != null) {
                this.out.close();
            }
            if (this.socket != null) {
                this.socket.close();
            }
        }
        catch (Exception ex) {}
        this.in = null;
        this.out = null;
        this.socket = null;
    }
    
    public static int getDefaultPort() {
        return ProxySOCKS4.DEFAULTPORT;
    }
    
    static {
        ProxySOCKS4.DEFAULTPORT = 1080;
    }
}
