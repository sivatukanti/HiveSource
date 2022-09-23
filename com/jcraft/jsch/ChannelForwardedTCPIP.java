// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.PipedOutputStream;
import java.net.Socket;
import java.util.Vector;

public class ChannelForwardedTCPIP extends Channel
{
    private static Vector pool;
    private static final int LOCAL_WINDOW_SIZE_MAX = 131072;
    private static final int LOCAL_MAXIMUM_PACKET_SIZE = 16384;
    private static final int TIMEOUT = 10000;
    private Socket socket;
    private ForwardedTCPIPDaemon daemon;
    private Config config;
    
    ChannelForwardedTCPIP() {
        this.socket = null;
        this.daemon = null;
        this.config = null;
        this.setLocalWindowSizeMax(131072);
        this.setLocalWindowSize(131072);
        this.setLocalPacketSize(16384);
        this.io = new IO();
        this.connected = true;
    }
    
    @Override
    public void run() {
        try {
            if (this.config instanceof ConfigDaemon) {
                final ConfigDaemon _config = (ConfigDaemon)this.config;
                final Class c = Class.forName(_config.target);
                this.daemon = c.newInstance();
                final PipedOutputStream out = new PipedOutputStream();
                this.io.setInputStream(new PassiveInputStream(out, 32768), false);
                this.daemon.setChannel(this, this.getInputStream(), out);
                this.daemon.setArg(_config.arg);
                new Thread(this.daemon).start();
            }
            else {
                final ConfigLHost _config2 = (ConfigLHost)this.config;
                (this.socket = ((_config2.factory == null) ? Util.createSocket(_config2.target, _config2.lport, 10000) : _config2.factory.createSocket(_config2.target, _config2.lport))).setTcpNoDelay(true);
                this.io.setInputStream(this.socket.getInputStream());
                this.io.setOutputStream(this.socket.getOutputStream());
            }
            this.sendOpenConfirmation();
        }
        catch (Exception e) {
            this.sendOpenFailure(1);
            this.close = true;
            this.disconnect();
            return;
        }
        this.thread = Thread.currentThread();
        final Buffer buf = new Buffer(this.rmpsize);
        final Packet packet = new Packet(buf);
        int i = 0;
        try {
            final Session _session = this.getSession();
            while (this.thread != null && this.io != null && this.io.in != null) {
                i = this.io.in.read(buf.buffer, 14, buf.buffer.length - 14 - 128);
                if (i <= 0) {
                    this.eof();
                    break;
                }
                packet.reset();
                buf.putByte((byte)94);
                buf.putInt(this.recipient);
                buf.putInt(i);
                buf.skip(i);
                synchronized (this) {
                    if (this.close) {
                        break;
                    }
                    _session.write(packet, this, i);
                }
            }
        }
        catch (Exception ex) {}
        this.disconnect();
    }
    
    @Override
    void getData(final Buffer buf) {
        this.setRecipient(buf.getInt());
        this.setRemoteWindowSize(buf.getUInt());
        this.setRemotePacketSize(buf.getInt());
        final byte[] addr = buf.getString();
        final int port = buf.getInt();
        final byte[] orgaddr = buf.getString();
        final int orgport = buf.getInt();
        Session _session = null;
        try {
            _session = this.getSession();
        }
        catch (JSchException ex) {}
        this.config = getPort(_session, Util.byte2str(addr), port);
        if (this.config == null) {
            this.config = getPort(_session, null, port);
        }
        if (this.config == null && JSch.getLogger().isEnabled(3)) {
            JSch.getLogger().log(3, "ChannelForwardedTCPIP: " + Util.byte2str(addr) + ":" + port + " is not registered.");
        }
    }
    
    private static Config getPort(final Session session, final String address_to_bind, final int rport) {
        synchronized (ChannelForwardedTCPIP.pool) {
            for (int i = 0; i < ChannelForwardedTCPIP.pool.size(); ++i) {
                final Config bar = ChannelForwardedTCPIP.pool.elementAt(i);
                if (bar.session == session) {
                    if (bar.rport != rport) {
                        if (bar.rport != 0) {
                            continue;
                        }
                        if (bar.allocated_rport != rport) {
                            continue;
                        }
                    }
                    if (address_to_bind == null || bar.address_to_bind.equals(address_to_bind)) {
                        return bar;
                    }
                }
            }
            return null;
        }
    }
    
    static String[] getPortForwarding(final Session session) {
        final Vector foo = new Vector();
        synchronized (ChannelForwardedTCPIP.pool) {
            for (int i = 0; i < ChannelForwardedTCPIP.pool.size(); ++i) {
                final Config config = ChannelForwardedTCPIP.pool.elementAt(i);
                if (config instanceof ConfigDaemon) {
                    foo.addElement(config.allocated_rport + ":" + config.target + ":");
                }
                else {
                    foo.addElement(config.allocated_rport + ":" + config.target + ":" + ((ConfigLHost)config).lport);
                }
            }
        }
        final String[] bar = new String[foo.size()];
        for (int i = 0; i < foo.size(); ++i) {
            bar[i] = foo.elementAt(i);
        }
        return bar;
    }
    
    static String normalize(final String address) {
        if (address == null) {
            return "localhost";
        }
        if (address.length() == 0 || address.equals("*")) {
            return "";
        }
        return address;
    }
    
    static void addPort(final Session session, final String _address_to_bind, final int port, final int allocated_port, final String target, final int lport, final SocketFactory factory) throws JSchException {
        final String address_to_bind = normalize(_address_to_bind);
        synchronized (ChannelForwardedTCPIP.pool) {
            if (getPort(session, address_to_bind, port) != null) {
                throw new JSchException("PortForwardingR: remote port " + port + " is already registered.");
            }
            final ConfigLHost config = new ConfigLHost();
            config.session = session;
            config.rport = port;
            config.allocated_rport = allocated_port;
            config.target = target;
            config.lport = lport;
            config.address_to_bind = address_to_bind;
            config.factory = factory;
            ChannelForwardedTCPIP.pool.addElement(config);
        }
    }
    
    static void addPort(final Session session, final String _address_to_bind, final int port, final int allocated_port, final String daemon, final Object[] arg) throws JSchException {
        final String address_to_bind = normalize(_address_to_bind);
        synchronized (ChannelForwardedTCPIP.pool) {
            if (getPort(session, address_to_bind, port) != null) {
                throw new JSchException("PortForwardingR: remote port " + port + " is already registered.");
            }
            final ConfigDaemon config = new ConfigDaemon();
            config.session = session;
            config.rport = port;
            config.allocated_rport = port;
            config.target = daemon;
            config.arg = arg;
            config.address_to_bind = address_to_bind;
            ChannelForwardedTCPIP.pool.addElement(config);
        }
    }
    
    static void delPort(final ChannelForwardedTCPIP c) {
        Session _session = null;
        try {
            _session = c.getSession();
        }
        catch (JSchException ex) {}
        if (_session != null && c.config != null) {
            delPort(_session, c.config.rport);
        }
    }
    
    static void delPort(final Session session, final int rport) {
        delPort(session, null, rport);
    }
    
    static void delPort(final Session session, String address_to_bind, final int rport) {
        synchronized (ChannelForwardedTCPIP.pool) {
            Config foo = getPort(session, normalize(address_to_bind), rport);
            if (foo == null) {
                foo = getPort(session, null, rport);
            }
            if (foo == null) {
                return;
            }
            ChannelForwardedTCPIP.pool.removeElement(foo);
            if (address_to_bind == null) {
                address_to_bind = foo.address_to_bind;
            }
            if (address_to_bind == null) {
                address_to_bind = "0.0.0.0";
            }
        }
        final Buffer buf = new Buffer(100);
        final Packet packet = new Packet(buf);
        try {
            packet.reset();
            buf.putByte((byte)80);
            buf.putString(Util.str2byte("cancel-tcpip-forward"));
            buf.putByte((byte)0);
            buf.putString(Util.str2byte(address_to_bind));
            buf.putInt(rport);
            session.write(packet);
        }
        catch (Exception ex) {}
    }
    
    static void delPort(final Session session) {
        int[] rport = null;
        int count = 0;
        synchronized (ChannelForwardedTCPIP.pool) {
            rport = new int[ChannelForwardedTCPIP.pool.size()];
            for (int i = 0; i < ChannelForwardedTCPIP.pool.size(); ++i) {
                final Config config = ChannelForwardedTCPIP.pool.elementAt(i);
                if (config.session == session) {
                    rport[count++] = config.rport;
                }
            }
        }
        for (int j = 0; j < count; ++j) {
            delPort(session, rport[j]);
        }
    }
    
    public int getRemotePort() {
        return (this.config != null) ? this.config.rport : 0;
    }
    
    private void setSocketFactory(final SocketFactory factory) {
        if (this.config != null && this.config instanceof ConfigLHost) {
            ((ConfigLHost)this.config).factory = factory;
        }
    }
    
    static {
        ChannelForwardedTCPIP.pool = new Vector();
    }
    
    abstract static class Config
    {
        Session session;
        int rport;
        int allocated_rport;
        String address_to_bind;
        String target;
    }
    
    static class ConfigDaemon extends Config
    {
        Object[] arg;
    }
    
    static class ConfigLHost extends Config
    {
        int lport;
        SocketFactory factory;
    }
}
