// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

class RequestWindowChange extends Request
{
    int width_columns;
    int height_rows;
    int width_pixels;
    int height_pixels;
    
    RequestWindowChange() {
        this.width_columns = 80;
        this.height_rows = 24;
        this.width_pixels = 640;
        this.height_pixels = 480;
    }
    
    void setSize(final int col, final int row, final int wp, final int hp) {
        this.width_columns = col;
        this.height_rows = row;
        this.width_pixels = wp;
        this.height_pixels = hp;
    }
    
    public void request(final Session session, final Channel channel) throws Exception {
        super.request(session, channel);
        final Buffer buf = new Buffer();
        final Packet packet = new Packet(buf);
        packet.reset();
        buf.putByte((byte)98);
        buf.putInt(channel.getRecipient());
        buf.putString(Util.str2byte("window-change"));
        buf.putByte((byte)(this.waitForReply() ? 1 : 0));
        buf.putInt(this.width_columns);
        buf.putInt(this.height_rows);
        buf.putInt(this.width_pixels);
        buf.putInt(this.height_pixels);
        this.write(packet);
    }
}
