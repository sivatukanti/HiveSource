// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

abstract class Request
{
    private boolean reply;
    private Session session;
    private Channel channel;
    
    Request() {
        this.reply = false;
        this.session = null;
        this.channel = null;
    }
    
    void request(final Session session, final Channel channel) throws Exception {
        this.session = session;
        this.channel = channel;
        if (channel.connectTimeout > 0) {
            this.setReply(true);
        }
    }
    
    boolean waitForReply() {
        return this.reply;
    }
    
    void setReply(final boolean reply) {
        this.reply = reply;
    }
    
    void write(final Packet packet) throws Exception {
        if (this.reply) {
            this.channel.reply = -1;
        }
        this.session.write(packet);
        if (this.reply) {
            final long start = System.currentTimeMillis();
            final long timeout = this.channel.connectTimeout;
            while (this.channel.isConnected() && this.channel.reply == -1) {
                try {
                    Thread.sleep(10L);
                }
                catch (Exception ex) {}
                if (timeout > 0L && System.currentTimeMillis() - start > timeout) {
                    this.channel.reply = 0;
                    throw new JSchException("channel request: timeout");
                }
            }
            if (this.channel.reply == 0) {
                throw new JSchException("failed to send channel request");
            }
        }
    }
}
