// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.io.OutputStream;
import java.io.InputStream;

public class ChannelDirectTCPIP extends Channel
{
    private static final int LOCAL_WINDOW_SIZE_MAX = 131072;
    private static final int LOCAL_MAXIMUM_PACKET_SIZE = 16384;
    private static final byte[] _type;
    String host;
    int port;
    String originator_IP_address;
    int originator_port;
    
    ChannelDirectTCPIP() {
        this.originator_IP_address = "127.0.0.1";
        this.originator_port = 0;
        this.type = ChannelDirectTCPIP._type;
        this.setLocalWindowSizeMax(131072);
        this.setLocalWindowSize(131072);
        this.setLocalPacketSize(16384);
    }
    
    @Override
    void init() {
        this.io = new IO();
    }
    
    @Override
    public void connect(final int connectTimeout) throws JSchException {
        this.connectTimeout = connectTimeout;
        try {
            final Session _session = this.getSession();
            if (!_session.isConnected()) {
                throw new JSchException("session is down");
            }
            if (this.io.in != null) {
                (this.thread = new Thread(this)).setName("DirectTCPIP thread " + _session.getHost());
                if (_session.daemon_thread) {
                    this.thread.setDaemon(_session.daemon_thread);
                }
                this.thread.start();
            }
            else {
                this.sendChannelOpen();
            }
        }
        catch (Exception e) {
            this.io.close();
            this.io = null;
            Channel.del(this);
            if (e instanceof JSchException) {
                throw (JSchException)e;
            }
        }
    }
    
    @Override
    public void run() {
        try {
            this.sendChannelOpen();
            final Buffer buf = new Buffer(this.rmpsize);
            final Packet packet = new Packet(buf);
            final Session _session = this.getSession();
            int i = 0;
            while (this.isConnected() && this.thread != null && this.io != null && this.io.in != null) {
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
        catch (Exception e) {
            if (!this.connected) {
                this.connected = true;
            }
            this.disconnect();
            return;
        }
        this.eof();
        this.disconnect();
    }
    
    @Override
    public void setInputStream(final InputStream in) {
        this.io.setInputStream(in);
    }
    
    @Override
    public void setOutputStream(final OutputStream out) {
        this.io.setOutputStream(out);
    }
    
    public void setHost(final String host) {
        this.host = host;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public void setOrgIPAddress(final String foo) {
        this.originator_IP_address = foo;
    }
    
    public void setOrgPort(final int foo) {
        this.originator_port = foo;
    }
    
    @Override
    protected Packet genChannelOpenPacket() {
        final Buffer buf = new Buffer(50 + this.host.length() + this.originator_IP_address.length() + 128);
        final Packet packet = new Packet(buf);
        packet.reset();
        buf.putByte((byte)90);
        buf.putString(this.type);
        buf.putInt(this.id);
        buf.putInt(this.lwsize);
        buf.putInt(this.lmpsize);
        buf.putString(Util.str2byte(this.host));
        buf.putInt(this.port);
        buf.putString(Util.str2byte(this.originator_IP_address));
        buf.putInt(this.originator_port);
        return packet;
    }
    
    static {
        _type = Util.str2byte("direct-tcpip");
    }
}
