// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

class RequestExec extends Request
{
    private byte[] command;
    
    RequestExec(final byte[] command) {
        this.command = new byte[0];
        this.command = command;
    }
    
    public void request(final Session session, final Channel channel) throws Exception {
        super.request(session, channel);
        final Buffer buf = new Buffer();
        final Packet packet = new Packet(buf);
        packet.reset();
        buf.putByte((byte)98);
        buf.putInt(channel.getRecipient());
        buf.putString(Util.str2byte("exec"));
        buf.putByte((byte)(this.waitForReply() ? 1 : 0));
        buf.checkFreeSize(4 + this.command.length);
        buf.putString(this.command);
        this.write(packet);
    }
}
