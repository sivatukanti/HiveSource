// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public class RequestSubsystem extends Request
{
    private String subsystem;
    
    public RequestSubsystem() {
        this.subsystem = null;
    }
    
    public void request(final Session session, final Channel channel, final String subsystem, final boolean want_reply) throws Exception {
        this.setReply(want_reply);
        this.subsystem = subsystem;
        this.request(session, channel);
    }
    
    public void request(final Session session, final Channel channel) throws Exception {
        super.request(session, channel);
        final Buffer buf = new Buffer();
        final Packet packet = new Packet(buf);
        packet.reset();
        buf.putByte((byte)98);
        buf.putInt(channel.getRecipient());
        buf.putString(Util.str2byte("subsystem"));
        buf.putByte((byte)(this.waitForReply() ? 1 : 0));
        buf.putString(Util.str2byte(this.subsystem));
        this.write(packet);
    }
}
