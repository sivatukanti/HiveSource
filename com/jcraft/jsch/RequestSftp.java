// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public class RequestSftp extends Request
{
    RequestSftp() {
        this.setReply(true);
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
        buf.putString(Util.str2byte("sftp"));
        this.write(packet);
    }
}
