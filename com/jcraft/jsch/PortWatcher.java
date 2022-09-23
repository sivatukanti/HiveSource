// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.util.Vector;

class PortWatcher implements Runnable
{
    private static Vector pool;
    private static InetAddress anyLocalAddress;
    Session session;
    int lport;
    int rport;
    String host;
    InetAddress boundaddress;
    Runnable thread;
    ServerSocket ss;
    int connectTimeout;
    
    static String[] getPortForwarding(final Session session) {
        final Vector foo = new Vector();
        synchronized (PortWatcher.pool) {
            for (int i = 0; i < PortWatcher.pool.size(); ++i) {
                final PortWatcher p = PortWatcher.pool.elementAt(i);
                if (p.session == session) {
                    foo.addElement(p.lport + ":" + p.host + ":" + p.rport);
                }
            }
        }
        final String[] bar = new String[foo.size()];
        for (int i = 0; i < foo.size(); ++i) {
            bar[i] = foo.elementAt(i);
        }
        return bar;
    }
    
    static PortWatcher getPort(final Session session, final String address, final int lport) throws JSchException {
        InetAddress addr;
        try {
            addr = InetAddress.getByName(address);
        }
        catch (UnknownHostException uhe) {
            throw new JSchException("PortForwardingL: invalid address " + address + " specified.", uhe);
        }
        synchronized (PortWatcher.pool) {
            for (int i = 0; i < PortWatcher.pool.size(); ++i) {
                final PortWatcher p = PortWatcher.pool.elementAt(i);
                if (p.session == session && p.lport == lport && ((PortWatcher.anyLocalAddress != null && p.boundaddress.equals(PortWatcher.anyLocalAddress)) || p.boundaddress.equals(addr))) {
                    return p;
                }
            }
            return null;
        }
    }
    
    private static String normalize(String address) {
        if (address != null) {
            if (address.length() == 0 || address.equals("*")) {
                address = "0.0.0.0";
            }
            else if (address.equals("localhost")) {
                address = "127.0.0.1";
            }
        }
        return address;
    }
    
    static PortWatcher addPort(final Session session, String address, final int lport, final String host, final int rport, final ServerSocketFactory ssf) throws JSchException {
        address = normalize(address);
        if (getPort(session, address, lport) != null) {
            throw new JSchException("PortForwardingL: local port " + address + ":" + lport + " is already registered.");
        }
        final PortWatcher pw = new PortWatcher(session, address, lport, host, rport, ssf);
        PortWatcher.pool.addElement(pw);
        return pw;
    }
    
    static void delPort(final Session session, String address, final int lport) throws JSchException {
        address = normalize(address);
        final PortWatcher pw = getPort(session, address, lport);
        if (pw == null) {
            throw new JSchException("PortForwardingL: local port " + address + ":" + lport + " is not registered.");
        }
        pw.delete();
        PortWatcher.pool.removeElement(pw);
    }
    
    static void delPort(final Session session) {
        synchronized (PortWatcher.pool) {
            final PortWatcher[] foo = new PortWatcher[PortWatcher.pool.size()];
            int count = 0;
            for (int i = 0; i < PortWatcher.pool.size(); ++i) {
                final PortWatcher p = PortWatcher.pool.elementAt(i);
                if (p.session == session) {
                    p.delete();
                    foo[count++] = p;
                }
            }
            for (int i = 0; i < count; ++i) {
                final PortWatcher p = foo[i];
                PortWatcher.pool.removeElement(p);
            }
        }
    }
    
    PortWatcher(final Session session, final String address, final int lport, final String host, final int rport, final ServerSocketFactory factory) throws JSchException {
        this.connectTimeout = 0;
        this.session = session;
        this.lport = lport;
        this.host = host;
        this.rport = rport;
        try {
            this.boundaddress = InetAddress.getByName(address);
            this.ss = ((factory == null) ? new ServerSocket(lport, 0, this.boundaddress) : factory.createServerSocket(lport, 0, this.boundaddress));
        }
        catch (Exception e) {
            final String message = "PortForwardingL: local port " + address + ":" + lport + " cannot be bound.";
            if (e instanceof Throwable) {
                throw new JSchException(message, e);
            }
            throw new JSchException(message);
        }
        if (lport == 0) {
            final int assigned = this.ss.getLocalPort();
            if (assigned != -1) {
                this.lport = assigned;
            }
        }
    }
    
    public void run() {
        this.thread = this;
        try {
            while (this.thread != null) {
                final Socket socket = this.ss.accept();
                socket.setTcpNoDelay(true);
                final InputStream in = socket.getInputStream();
                final OutputStream out = socket.getOutputStream();
                final ChannelDirectTCPIP channel = new ChannelDirectTCPIP();
                channel.init();
                channel.setInputStream(in);
                channel.setOutputStream(out);
                this.session.addChannel(channel);
                channel.setHost(this.host);
                channel.setPort(this.rport);
                channel.setOrgIPAddress(socket.getInetAddress().getHostAddress());
                channel.setOrgPort(socket.getPort());
                channel.connect(this.connectTimeout);
                if (channel.exitstatus != -1) {}
            }
        }
        catch (Exception ex) {}
        this.delete();
    }
    
    void delete() {
        this.thread = null;
        try {
            if (this.ss != null) {
                this.ss.close();
            }
            this.ss = null;
        }
        catch (Exception ex) {}
    }
    
    void setConnectTimeout(final int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
    
    static {
        PortWatcher.pool = new Vector();
        PortWatcher.anyLocalAddress = null;
        try {
            PortWatcher.anyLocalAddress = InetAddress.getByName("0.0.0.0");
        }
        catch (UnknownHostException ex) {}
    }
}
