// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;

class ChannelX11 extends Channel
{
    private static final int LOCAL_WINDOW_SIZE_MAX = 131072;
    private static final int LOCAL_MAXIMUM_PACKET_SIZE = 16384;
    private static final int TIMEOUT = 10000;
    private static String host;
    private static int port;
    private boolean init;
    static byte[] cookie;
    private static byte[] cookie_hex;
    private static Hashtable faked_cookie_pool;
    private static Hashtable faked_cookie_hex_pool;
    private static byte[] table;
    private Socket socket;
    private byte[] cache;
    
    static int revtable(final byte foo) {
        for (int i = 0; i < ChannelX11.table.length; ++i) {
            if (ChannelX11.table[i] == foo) {
                return i;
            }
        }
        return 0;
    }
    
    static void setCookie(final String foo) {
        ChannelX11.cookie_hex = Util.str2byte(foo);
        ChannelX11.cookie = new byte[16];
        for (int i = 0; i < 16; ++i) {
            ChannelX11.cookie[i] = (byte)((revtable(ChannelX11.cookie_hex[i * 2]) << 4 & 0xF0) | (revtable(ChannelX11.cookie_hex[i * 2 + 1]) & 0xF));
        }
    }
    
    static void setHost(final String foo) {
        ChannelX11.host = foo;
    }
    
    static void setPort(final int foo) {
        ChannelX11.port = foo;
    }
    
    static byte[] getFakedCookie(final Session session) {
        synchronized (ChannelX11.faked_cookie_hex_pool) {
            byte[] foo = ChannelX11.faked_cookie_hex_pool.get(session);
            if (foo == null) {
                final Random random = Session.random;
                foo = new byte[16];
                synchronized (random) {
                    random.fill(foo, 0, 16);
                }
                ChannelX11.faked_cookie_pool.put(session, foo);
                final byte[] bar = new byte[32];
                for (int i = 0; i < 16; ++i) {
                    bar[2 * i] = ChannelX11.table[foo[i] >>> 4 & 0xF];
                    bar[2 * i + 1] = ChannelX11.table[foo[i] & 0xF];
                }
                ChannelX11.faked_cookie_hex_pool.put(session, bar);
                foo = bar;
            }
            return foo;
        }
    }
    
    static void removeFakedCookie(final Session session) {
        synchronized (ChannelX11.faked_cookie_hex_pool) {
            ChannelX11.faked_cookie_hex_pool.remove(session);
            ChannelX11.faked_cookie_pool.remove(session);
        }
    }
    
    ChannelX11() {
        this.init = true;
        this.socket = null;
        this.cache = new byte[0];
        this.setLocalWindowSizeMax(131072);
        this.setLocalWindowSize(131072);
        this.setLocalPacketSize(16384);
        this.type = Util.str2byte("x11");
        this.connected = true;
    }
    
    @Override
    public void run() {
        try {
            (this.socket = Util.createSocket(ChannelX11.host, ChannelX11.port, 10000)).setTcpNoDelay(true);
            (this.io = new IO()).setInputStream(this.socket.getInputStream());
            this.io.setOutputStream(this.socket.getOutputStream());
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
            while (this.thread != null && this.io != null && this.io.in != null) {
                i = this.io.in.read(buf.buffer, 14, buf.buffer.length - 14 - 128);
                if (i <= 0) {
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
        this.disconnect();
    }
    
    private byte[] addCache(final byte[] foo, final int s, final int l) {
        final byte[] bar = new byte[this.cache.length + l];
        System.arraycopy(foo, s, bar, this.cache.length, l);
        if (this.cache.length > 0) {
            System.arraycopy(this.cache, 0, bar, 0, this.cache.length);
        }
        return this.cache = bar;
    }
    
    @Override
    void write(byte[] foo, int s, int l) throws IOException {
        if (!this.init) {
            this.io.put(foo, s, l);
            return;
        }
        Session _session = null;
        try {
            _session = this.getSession();
        }
        catch (JSchException e) {
            throw new IOException(e.toString());
        }
        foo = this.addCache(foo, s, l);
        s = 0;
        l = foo.length;
        if (l < 9) {
            return;
        }
        int plen = (foo[s + 6] & 0xFF) * 256 + (foo[s + 7] & 0xFF);
        int dlen = (foo[s + 8] & 0xFF) * 256 + (foo[s + 9] & 0xFF);
        if ((foo[s] & 0xFF) != 0x42) {
            if ((foo[s] & 0xFF) == 0x6C) {
                plen = ((plen >>> 8 & 0xFF) | (plen << 8 & 0xFF00));
                dlen = ((dlen >>> 8 & 0xFF) | (dlen << 8 & 0xFF00));
            }
        }
        if (l < 12 + plen + (-plen & 0x3) + dlen) {
            return;
        }
        final byte[] bar = new byte[dlen];
        System.arraycopy(foo, s + 12 + plen + (-plen & 0x3), bar, 0, dlen);
        byte[] faked_cookie = null;
        synchronized (ChannelX11.faked_cookie_pool) {
            faked_cookie = ChannelX11.faked_cookie_pool.get(_session);
        }
        if (equals(bar, faked_cookie)) {
            if (ChannelX11.cookie != null) {
                System.arraycopy(ChannelX11.cookie, 0, foo, s + 12 + plen + (-plen & 0x3), dlen);
            }
        }
        else {
            this.thread = null;
            this.eof();
            this.io.close();
            this.disconnect();
        }
        this.init = false;
        this.io.put(foo, s, l);
        this.cache = null;
    }
    
    private static boolean equals(final byte[] foo, final byte[] bar) {
        if (foo.length != bar.length) {
            return false;
        }
        for (int i = 0; i < foo.length; ++i) {
            if (foo[i] != bar[i]) {
                return false;
            }
        }
        return true;
    }
    
    static {
        ChannelX11.host = "127.0.0.1";
        ChannelX11.port = 6000;
        ChannelX11.cookie = null;
        ChannelX11.cookie_hex = null;
        ChannelX11.faked_cookie_pool = new Hashtable();
        ChannelX11.faked_cookie_hex_pool = new Hashtable();
        ChannelX11.table = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
    }
}
