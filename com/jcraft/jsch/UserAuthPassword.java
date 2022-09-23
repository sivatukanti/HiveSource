// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

class UserAuthPassword extends UserAuth
{
    private final int SSH_MSG_USERAUTH_PASSWD_CHANGEREQ = 60;
    
    @Override
    public boolean start(final Session session) throws Exception {
        super.start(session);
        byte[] password = session.password;
        String dest = this.username + "@" + session.host;
        if (session.port != 22) {
            dest = dest + ":" + session.port;
        }
        try {
            while (session.auth_failures < session.max_auth_tries) {
                if (password == null) {
                    if (this.userinfo == null) {
                        return false;
                    }
                    if (!this.userinfo.promptPassword("Password for " + dest)) {
                        throw new JSchAuthCancelException("password");
                    }
                    final String _password = this.userinfo.getPassword();
                    if (_password == null) {
                        throw new JSchAuthCancelException("password");
                    }
                    password = Util.str2byte(_password);
                }
                byte[] _username = null;
                _username = Util.str2byte(this.username);
                this.packet.reset();
                this.buf.putByte((byte)50);
                this.buf.putString(_username);
                this.buf.putString(Util.str2byte("ssh-connection"));
                this.buf.putString(Util.str2byte("password"));
                this.buf.putByte((byte)0);
                this.buf.putString(password);
                session.write(this.packet);
                while (true) {
                    this.buf = session.read(this.buf);
                    final int command = this.buf.getCommand() & 0xFF;
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
                        this.userinfo.showMessage(message);
                    }
                    else if (command == 60) {
                        this.buf.getInt();
                        this.buf.getByte();
                        this.buf.getByte();
                        final byte[] instruction = this.buf.getString();
                        final byte[] tag = this.buf.getString();
                        if (this.userinfo == null || !(this.userinfo instanceof UIKeyboardInteractive)) {
                            if (this.userinfo != null) {
                                this.userinfo.showMessage("Password must be changed.");
                            }
                            return false;
                        }
                        final UIKeyboardInteractive kbi = (UIKeyboardInteractive)this.userinfo;
                        final String name = "Password Change Required";
                        final String[] prompt = { "New Password: " };
                        final boolean[] echo = { false };
                        String[] response = kbi.promptKeyboardInteractive(dest, name, Util.byte2str(instruction), prompt, echo);
                        if (response == null) {
                            throw new JSchAuthCancelException("password");
                        }
                        final byte[] newpassword = Util.str2byte(response[0]);
                        this.packet.reset();
                        this.buf.putByte((byte)50);
                        this.buf.putString(_username);
                        this.buf.putString(Util.str2byte("ssh-connection"));
                        this.buf.putString(Util.str2byte("password"));
                        this.buf.putByte((byte)1);
                        this.buf.putString(password);
                        this.buf.putString(newpassword);
                        Util.bzero(newpassword);
                        response = null;
                        session.write(this.packet);
                    }
                    else {
                        if (command != 51) {
                            return false;
                        }
                        this.buf.getInt();
                        this.buf.getByte();
                        this.buf.getByte();
                        final byte[] foo = this.buf.getString();
                        final int partial_success = this.buf.getByte();
                        if (partial_success != 0) {
                            throw new JSchPartialAuthException(Util.byte2str(foo));
                        }
                        ++session.auth_failures;
                        if (password != null) {
                            Util.bzero(password);
                            password = null;
                        }
                        break;
                    }
                }
            }
            return false;
        }
        finally {
            if (password != null) {
                Util.bzero(password);
                password = null;
            }
        }
    }
}
