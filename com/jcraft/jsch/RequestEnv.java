// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

class RequestEnv extends Request
{
    byte[] name;
    byte[] value;
    
    RequestEnv() {
        this.name = new byte[0];
        this.value = new byte[0];
    }
    
    void setEnv(final byte[] name, final byte[] value) {
        this.name = name;
        this.value = value;
    }
    
    public void request(final Session session, final Channel channel) throws Exception {
        super.request(session, channel);
        final Buffer buf = new Buffer();
        final Packet packet = new Packet(buf);
        packet.reset();
        buf.putByte((byte)98);
        buf.putInt(channel.getRecipient());
        buf.putString(Util.str2byte("env"));
        buf.putByte((byte)(this.waitForReply() ? 1 : 0));
        buf.putString(this.name);
        buf.putString(this.value);
        this.write(packet);
    }
}
