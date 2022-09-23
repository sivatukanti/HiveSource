// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

class RequestX11 extends Request
{
    public void setCookie(final String cookie) {
        ChannelX11.cookie = Util.str2byte(cookie);
    }
    
    public void request(final Session session, final Channel channel) throws Exception {
        super.request(session, channel);
        final Buffer buf = new Buffer();
        final Packet packet = new Packet(buf);
        packet.reset();
        buf.putByte((byte)98);
        buf.putInt(channel.getRecipient());
        buf.putString(Util.str2byte("x11-req"));
        buf.putByte((byte)(this.waitForReply() ? 1 : 0));
        buf.putByte((byte)0);
        buf.putString(Util.str2byte("MIT-MAGIC-COOKIE-1"));
        buf.putString(ChannelX11.getFakedCookie(session));
        buf.putInt(0);
        this.write(packet);
        session.x11_forwarding = true;
    }
}
