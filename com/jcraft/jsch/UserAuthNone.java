// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

class UserAuthNone extends UserAuth
{
    private static final int SSH_MSG_SERVICE_ACCEPT = 6;
    private String methods;
    
    UserAuthNone() {
        this.methods = null;
    }
    
    @Override
    public boolean start(final Session session) throws Exception {
        super.start(session);
        this.packet.reset();
        this.buf.putByte((byte)5);
        this.buf.putString(Util.str2byte("ssh-userauth"));
        session.write(this.packet);
        if (JSch.getLogger().isEnabled(1)) {
            JSch.getLogger().log(1, "SSH_MSG_SERVICE_REQUEST sent");
        }
        this.buf = session.read(this.buf);
        int command = this.buf.getCommand();
        final boolean result = command == 6;
        if (JSch.getLogger().isEnabled(1)) {
            JSch.getLogger().log(1, "SSH_MSG_SERVICE_ACCEPT received");
        }
        if (!result) {
            return false;
        }
        byte[] _username = null;
        _username = Util.str2byte(this.username);
        this.packet.reset();
        this.buf.putByte((byte)50);
        this.buf.putString(_username);
        this.buf.putString(Util.str2byte("ssh-connection"));
        this.buf.putString(Util.str2byte("none"));
        session.write(this.packet);
        while (true) {
            this.buf = session.read(this.buf);
            command = (this.buf.getCommand() & 0xFF);
            if (command == 52) {
                return true;
            }
            if (command == 53) {
                this.buf.getInt();
                this.buf.getByte();
                this.buf.getByte();
                final byte[] _message = this.buf.getString();
                final byte[] lang = this.buf.getString();
                final String message = Util.byte2str(_message);
                if (this.userinfo == null) {
                    continue;
                }
                try {
                    this.userinfo.showMessage(message);
                }
                catch (RuntimeException ee) {}
            }
            else {
                if (command == 51) {
                    this.buf.getInt();
                    this.buf.getByte();
                    this.buf.getByte();
                    final byte[] foo = this.buf.getString();
                    final int partial_success = this.buf.getByte();
                    this.methods = Util.byte2str(foo);
                    return false;
                }
                throw new JSchException("USERAUTH fail (" + command + ")");
            }
        }
    }
    
    String getMethods() {
        return this.methods;
    }
}
