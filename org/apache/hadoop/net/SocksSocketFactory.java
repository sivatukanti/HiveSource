// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.net;

import java.net.UnknownHostException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.io.IOException;
import java.net.Socket;
import java.net.Proxy;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configurable;
import javax.net.SocketFactory;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class SocksSocketFactory extends SocketFactory implements Configurable
{
    private Configuration conf;
    private Proxy proxy;
    
    public SocksSocketFactory() {
        this.proxy = Proxy.NO_PROXY;
    }
    
    public SocksSocketFactory(final Proxy proxy) {
        this.proxy = proxy;
    }
    
    @Override
    public Socket createSocket() throws IOException {
        return new Socket(this.proxy);
    }
    
    @Override
    public Socket createSocket(final InetAddress addr, final int port) throws IOException {
        final Socket socket = this.createSocket();
        socket.connect(new InetSocketAddress(addr, port));
        return socket;
    }
    
    @Override
    public Socket createSocket(final InetAddress addr, final int port, final InetAddress localHostAddr, final int localPort) throws IOException {
        final Socket socket = this.createSocket();
        socket.bind(new InetSocketAddress(localHostAddr, localPort));
        socket.connect(new InetSocketAddress(addr, port));
        return socket;
    }
    
    @Override
    public Socket createSocket(final String host, final int port) throws IOException, UnknownHostException {
        final Socket socket = this.createSocket();
        socket.connect(new InetSocketAddress(host, port));
        return socket;
    }
    
    @Override
    public Socket createSocket(final String host, final int port, final InetAddress localHostAddr, final int localPort) throws IOException, UnknownHostException {
        final Socket socket = this.createSocket();
        socket.bind(new InetSocketAddress(localHostAddr, localPort));
        socket.connect(new InetSocketAddress(host, port));
        return socket;
    }
    
    @Override
    public int hashCode() {
        return this.proxy.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SocksSocketFactory)) {
            return false;
        }
        final SocksSocketFactory other = (SocksSocketFactory)obj;
        if (this.proxy == null) {
            if (other.proxy != null) {
                return false;
            }
        }
        else if (!this.proxy.equals(other.proxy)) {
            return false;
        }
        return true;
    }
    
    @Override
    public Configuration getConf() {
        return this.conf;
    }
    
    @Override
    public void setConf(final Configuration conf) {
        this.conf = conf;
        final String proxyStr = conf.get("hadoop.socks.server");
        if (proxyStr != null && proxyStr.length() > 0) {
            this.setProxy(proxyStr);
        }
    }
    
    private void setProxy(final String proxyStr) {
        final String[] strs = proxyStr.split(":", 2);
        if (strs.length != 2) {
            throw new RuntimeException("Bad SOCKS proxy parameter: " + proxyStr);
        }
        final String host = strs[0];
        final int port = Integer.parseInt(strs[1]);
        this.proxy = new Proxy(Proxy.Type.SOCKS, InetSocketAddress.createUnresolved(host, port));
    }
}
