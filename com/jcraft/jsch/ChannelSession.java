// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.util.Enumeration;
import java.util.Hashtable;

class ChannelSession extends Channel
{
    private static byte[] _session;
    protected boolean agent_forwarding;
    protected boolean xforwading;
    protected Hashtable env;
    protected boolean pty;
    protected String ttype;
    protected int tcol;
    protected int trow;
    protected int twp;
    protected int thp;
    protected byte[] terminal_mode;
    
    ChannelSession() {
        this.agent_forwarding = false;
        this.xforwading = false;
        this.env = null;
        this.pty = false;
        this.ttype = "vt100";
        this.tcol = 80;
        this.trow = 24;
        this.twp = 640;
        this.thp = 480;
        this.terminal_mode = null;
        this.type = ChannelSession._session;
        this.io = new IO();
    }
    
    public void setAgentForwarding(final boolean enable) {
        this.agent_forwarding = enable;
    }
    
    @Override
    public void setXForwarding(final boolean enable) {
        this.xforwading = enable;
    }
    
    @Deprecated
    public void setEnv(final Hashtable env) {
        synchronized (this) {
            this.env = env;
        }
    }
    
    public void setEnv(final String name, final String value) {
        this.setEnv(Util.str2byte(name), Util.str2byte(value));
    }
    
    public void setEnv(final byte[] name, final byte[] value) {
        synchronized (this) {
            this.getEnv().put(name, value);
        }
    }
    
    private Hashtable getEnv() {
        if (this.env == null) {
            this.env = new Hashtable();
        }
        return this.env;
    }
    
    public void setPty(final boolean enable) {
        this.pty = enable;
    }
    
    public void setTerminalMode(final byte[] terminal_mode) {
        this.terminal_mode = terminal_mode;
    }
    
    public void setPtySize(final int col, final int row, final int wp, final int hp) {
        this.setPtyType(this.ttype, col, row, wp, hp);
        if (!this.pty || !this.isConnected()) {
            return;
        }
        try {
            final RequestWindowChange request = new RequestWindowChange();
            request.setSize(col, row, wp, hp);
            request.request(this.getSession(), this);
        }
        catch (Exception ex) {}
    }
    
    public void setPtyType(final String ttype) {
        this.setPtyType(ttype, 80, 24, 640, 480);
    }
    
    public void setPtyType(final String ttype, final int col, final int row, final int wp, final int hp) {
        this.ttype = ttype;
        this.tcol = col;
        this.trow = row;
        this.twp = wp;
        this.thp = hp;
    }
    
    protected void sendRequests() throws Exception {
        final Session _session = this.getSession();
        if (this.agent_forwarding) {
            final Request request = new RequestAgentForwarding();
            request.request(_session, this);
        }
        if (this.xforwading) {
            final Request request = new RequestX11();
            request.request(_session, this);
        }
        if (this.pty) {
            final Request request = new RequestPtyReq();
            ((RequestPtyReq)request).setTType(this.ttype);
            ((RequestPtyReq)request).setTSize(this.tcol, this.trow, this.twp, this.thp);
            if (this.terminal_mode != null) {
                ((RequestPtyReq)request).setTerminalMode(this.terminal_mode);
            }
            request.request(_session, this);
        }
        if (this.env != null) {
            final Enumeration _env = this.env.keys();
            while (_env.hasMoreElements()) {
                final Object name = _env.nextElement();
                final Object value = this.env.get(name);
                final Request request = new RequestEnv();
                ((RequestEnv)request).setEnv(this.toByteArray(name), this.toByteArray(value));
                request.request(_session, this);
            }
        }
    }
    
    private byte[] toByteArray(final Object o) {
        if (o instanceof String) {
            return Util.str2byte((String)o);
        }
        return (byte[])o;
    }
    
    @Override
    public void run() {
        final Buffer buf = new Buffer(this.rmpsize);
        final Packet packet = new Packet(buf);
        int i = -1;
        try {
            while (this.isConnected() && this.thread != null && this.io != null && this.io.in != null) {
                i = this.io.in.read(buf.buffer, 14, buf.buffer.length - 14 - 128);
                if (i == 0) {
                    continue;
                }
                if (i == -1) {
                    this.eof();
                    break;
                }
                if (this.close) {
                    break;
                }
                packet.reset();
                buf.putByte((byte)94);
                buf.putInt(this.recipient);
                buf.putInt(i);
                buf.skip(i);
                this.getSession().write(packet, this, i);
            }
        }
        catch (Exception ex) {}
        final Thread _thread = this.thread;
        if (_thread != null) {
            synchronized (_thread) {
                _thread.notifyAll();
            }
        }
        this.thread = null;
    }
    
    static {
        ChannelSession._session = Util.str2byte("session");
    }
}
