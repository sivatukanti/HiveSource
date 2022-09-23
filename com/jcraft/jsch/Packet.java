// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public class Packet
{
    private static Random random;
    Buffer buffer;
    byte[] ba4;
    
    static void setRandom(final Random foo) {
        Packet.random = foo;
    }
    
    public Packet(final Buffer buffer) {
        this.ba4 = new byte[4];
        this.buffer = buffer;
    }
    
    public void reset() {
        this.buffer.index = 5;
    }
    
    void padding(final int bsize) {
        int len = this.buffer.index;
        int pad = -len & bsize - 1;
        if (pad < bsize) {
            pad += bsize;
        }
        len = len + pad - 4;
        this.ba4[0] = (byte)(len >>> 24);
        this.ba4[1] = (byte)(len >>> 16);
        this.ba4[2] = (byte)(len >>> 8);
        this.ba4[3] = (byte)len;
        System.arraycopy(this.ba4, 0, this.buffer.buffer, 0, 4);
        this.buffer.buffer[4] = (byte)pad;
        synchronized (Packet.random) {
            Packet.random.fill(this.buffer.buffer, this.buffer.index, pad);
        }
        this.buffer.skip(pad);
    }
    
    int shift(final int len, final int bsize, final int mac) {
        int s = len + 5 + 9;
        int pad = -s & bsize - 1;
        if (pad < bsize) {
            pad += bsize;
        }
        s += pad;
        s += mac;
        s += 32;
        if (this.buffer.buffer.length < s + this.buffer.index - 5 - 9 - len) {
            final byte[] foo = new byte[s + this.buffer.index - 5 - 9 - len];
            System.arraycopy(this.buffer.buffer, 0, foo, 0, this.buffer.buffer.length);
            this.buffer.buffer = foo;
        }
        System.arraycopy(this.buffer.buffer, len + 5 + 9, this.buffer.buffer, s, this.buffer.index - 5 - 9 - len);
        this.buffer.index = 10;
        this.buffer.putInt(len);
        this.buffer.index = len + 5 + 9;
        return s;
    }
    
    void unshift(final byte command, final int recipient, final int s, final int len) {
        System.arraycopy(this.buffer.buffer, s, this.buffer.buffer, 14, len);
        this.buffer.buffer[5] = command;
        this.buffer.index = 6;
        this.buffer.putInt(recipient);
        this.buffer.putInt(len);
        this.buffer.index = len + 5 + 9;
    }
    
    Buffer getBuffer() {
        return this.buffer;
    }
    
    static {
        Packet.random = null;
    }
}
