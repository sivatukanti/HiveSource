// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

class RequestPtyReq extends Request
{
    private String ttype;
    private int tcol;
    private int trow;
    private int twp;
    private int thp;
    private byte[] terminal_mode;
    
    RequestPtyReq() {
        this.ttype = "vt100";
        this.tcol = 80;
        this.trow = 24;
        this.twp = 640;
        this.thp = 480;
        this.terminal_mode = Util.empty;
    }
    
    void setCode(final String cookie) {
    }
    
    void setTType(final String ttype) {
        this.ttype = ttype;
    }
    
    void setTerminalMode(final byte[] terminal_mode) {
        this.terminal_mode = terminal_mode;
    }
    
    void setTSize(final int tcol, final int trow, final int twp, final int thp) {
        this.tcol = tcol;
        this.trow = trow;
        this.twp = twp;
        this.thp = thp;
    }
    
    public void request(final Session session, final Channel channel) throws Exception {
        super.request(session, channel);
        final Buffer buf = new Buffer();
        final Packet packet = new Packet(buf);
        packet.reset();
        buf.putByte((byte)98);
        buf.putInt(channel.getRecipient());
        buf.putString(Util.str2byte("pty-req"));
        buf.putByte((byte)(this.waitForReply() ? 1 : 0));
        buf.putString(Util.str2byte(this.ttype));
        buf.putInt(this.tcol);
        buf.putInt(this.trow);
        buf.putInt(this.twp);
        buf.putInt(this.thp);
        buf.putString(this.terminal_mode);
        this.write(packet);
    }
}
