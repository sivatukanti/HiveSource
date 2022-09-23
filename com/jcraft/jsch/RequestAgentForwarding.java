// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

class RequestAgentForwarding extends Request
{
    public void request(final Session session, final Channel channel) throws Exception {
        super.request(session, channel);
        this.setReply(false);
        final Buffer buf = new Buffer();
        final Packet packet = new Packet(buf);
        packet.reset();
        buf.putByte((byte)98);
        buf.putInt(channel.getRecipient());
        buf.putString(Util.str2byte("auth-agent-req@openssh.com"));
        buf.putByte((byte)(this.waitForReply() ? 1 : 0));
        this.write(packet);
        session.agent_forwarding = true;
    }
}
