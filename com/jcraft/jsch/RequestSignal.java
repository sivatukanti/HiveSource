// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

class RequestSignal extends Request
{
    private String signal;
    
    RequestSignal() {
        this.signal = "KILL";
    }
    
    public void setSignal(final String foo) {
        this.signal = foo;
    }
    
    public void request(final Session session, final Channel channel) throws Exception {
        super.request(session, channel);
        final Buffer buf = new Buffer();
        final Packet packet = new Packet(buf);
        packet.reset();
        buf.putByte((byte)98);
        buf.putInt(channel.getRecipient());
        buf.putString(Util.str2byte("signal"));
        buf.putByte((byte)(this.waitForReply() ? 1 : 0));
        buf.putString(Util.str2byte(this.signal));
        this.write(packet);
    }
}
