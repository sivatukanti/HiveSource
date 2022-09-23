// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

class UserAuthKeyboardInteractive extends UserAuth
{
    @Override
    public boolean start(final Session session) throws Exception {
        super.start(session);
        if (this.userinfo != null && !(this.userinfo instanceof UIKeyboardInteractive)) {
            return false;
        }
        String dest = this.username + "@" + session.host;
        if (session.port != 22) {
            dest = dest + ":" + session.port;
        }
        byte[] password = session.password;
        boolean cancel = false;
        byte[] _username = null;
        _username = Util.str2byte(this.username);
        while (session.auth_failures < session.max_auth_tries) {
            this.packet.reset();
            this.buf.putByte((byte)50);
            this.buf.putString(_username);
            this.buf.putString(Util.str2byte("ssh-connection"));
            this.buf.putString(Util.str2byte("keyboard-interactive"));
            this.buf.putString(Util.empty);
            this.buf.putString(Util.empty);
            session.write(this.packet);
            boolean firsttime = true;
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
                else if (command == 51) {
                    this.buf.getInt();
                    this.buf.getByte();
                    this.buf.getByte();
                    final byte[] foo = this.buf.getString();
                    final int partial_success = this.buf.getByte();
                    if (partial_success != 0) {
                        throw new JSchPartialAuthException(Util.byte2str(foo));
                    }
                    if (firsttime) {
                        return false;
                    }
                    ++session.auth_failures;
                    if (cancel) {
                        throw new JSchAuthCancelException("keyboard-interactive");
                    }
                    break;
                }
                else {
                    if (command != 60) {
                        return false;
                    }
                    firsttime = false;
                    this.buf.getInt();
                    this.buf.getByte();
                    this.buf.getByte();
                    final String name = Util.byte2str(this.buf.getString());
                    final String instruction = Util.byte2str(this.buf.getString());
                    final String languate_tag = Util.byte2str(this.buf.getString());
                    final int num = this.buf.getInt();
                    final String[] prompt = new String[num];
                    final boolean[] echo = new boolean[num];
                    for (int i = 0; i < num; ++i) {
                        prompt[i] = Util.byte2str(this.buf.getString());
                        echo[i] = (this.buf.getByte() != 0);
                    }
                    byte[][] response = null;
                    if (password != null && prompt.length == 1 && !echo[0] && prompt[0].toLowerCase().indexOf("password:") >= 0) {
                        response = new byte[][] { password };
                        password = null;
                    }
                    else if ((num > 0 || name.length() > 0 || instruction.length() > 0) && this.userinfo != null) {
                        final UIKeyboardInteractive kbi = (UIKeyboardInteractive)this.userinfo;
                        final String[] _response = kbi.promptKeyboardInteractive(dest, name, instruction, prompt, echo);
                        if (_response != null) {
                            response = new byte[_response.length][];
                            for (int j = 0; j < _response.length; ++j) {
                                response[j] = Util.str2byte(_response[j]);
                            }
                        }
                    }
                    this.packet.reset();
                    this.buf.putByte((byte)61);
                    if (num > 0 && (response == null || num != response.length)) {
                        if (response == null) {
                            this.buf.putInt(num);
                            for (int k = 0; k < num; ++k) {
                                this.buf.putString(Util.empty);
                            }
                        }
                        else {
                            this.buf.putInt(0);
                        }
                        if (response == null) {
                            cancel = true;
                        }
                    }
                    else {
                        this.buf.putInt(num);
                        for (int k = 0; k < num; ++k) {
                            this.buf.putString(response[k]);
                        }
                    }
                    session.write(this.packet);
                }
            }
        }
        return false;
    }
}
