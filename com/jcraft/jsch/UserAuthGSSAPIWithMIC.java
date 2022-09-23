// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public class UserAuthGSSAPIWithMIC extends UserAuth
{
    private static final int SSH_MSG_USERAUTH_GSSAPI_RESPONSE = 60;
    private static final int SSH_MSG_USERAUTH_GSSAPI_TOKEN = 61;
    private static final int SSH_MSG_USERAUTH_GSSAPI_EXCHANGE_COMPLETE = 63;
    private static final int SSH_MSG_USERAUTH_GSSAPI_ERROR = 64;
    private static final int SSH_MSG_USERAUTH_GSSAPI_ERRTOK = 65;
    private static final int SSH_MSG_USERAUTH_GSSAPI_MIC = 66;
    private static final byte[][] supported_oid;
    private static final String[] supported_method;
    
    @Override
    public boolean start(final Session session) throws Exception {
        super.start(session);
        final byte[] _username = Util.str2byte(this.username);
        this.packet.reset();
        this.buf.putByte((byte)50);
        this.buf.putString(_username);
        this.buf.putString(Util.str2byte("ssh-connection"));
        this.buf.putString(Util.str2byte("gssapi-with-mic"));
        this.buf.putInt(UserAuthGSSAPIWithMIC.supported_oid.length);
        for (int i = 0; i < UserAuthGSSAPIWithMIC.supported_oid.length; ++i) {
            this.buf.putString(UserAuthGSSAPIWithMIC.supported_oid[i]);
        }
        session.write(this.packet);
        String method = null;
        while (true) {
            this.buf = session.read(this.buf);
            int command = this.buf.getCommand() & 0xFF;
            if (command == 51) {
                return false;
            }
            if (command == 60) {
                this.buf.getInt();
                this.buf.getByte();
                this.buf.getByte();
                final byte[] message = this.buf.getString();
                for (int j = 0; j < UserAuthGSSAPIWithMIC.supported_oid.length; ++j) {
                    if (Util.array_equals(message, UserAuthGSSAPIWithMIC.supported_oid[j])) {
                        method = UserAuthGSSAPIWithMIC.supported_method[j];
                        break;
                    }
                }
                if (method == null) {
                    return false;
                }
                GSSContext context = null;
                try {
                    final Class c = Class.forName(session.getConfig(method));
                    context = c.newInstance();
                }
                catch (Exception e) {
                    return false;
                }
                try {
                    context.create(this.username, session.host);
                }
                catch (JSchException e2) {
                    return false;
                }
                byte[] token = new byte[0];
                while (!context.isEstablished()) {
                    try {
                        token = context.init(token, 0, token.length);
                    }
                    catch (JSchException e3) {
                        return false;
                    }
                    if (token != null) {
                        this.packet.reset();
                        this.buf.putByte((byte)61);
                        this.buf.putString(token);
                        session.write(this.packet);
                    }
                    if (!context.isEstablished()) {
                        this.buf = session.read(this.buf);
                        command = (this.buf.getCommand() & 0xFF);
                        if (command == 64) {
                            this.buf = session.read(this.buf);
                            command = (this.buf.getCommand() & 0xFF);
                        }
                        else if (command == 65) {
                            this.buf = session.read(this.buf);
                            command = (this.buf.getCommand() & 0xFF);
                        }
                        if (command == 51) {
                            return false;
                        }
                        this.buf.getInt();
                        this.buf.getByte();
                        this.buf.getByte();
                        token = this.buf.getString();
                    }
                }
                final Buffer mbuf = new Buffer();
                mbuf.putString(session.getSessionId());
                mbuf.putByte((byte)50);
                mbuf.putString(_username);
                mbuf.putString(Util.str2byte("ssh-connection"));
                mbuf.putString(Util.str2byte("gssapi-with-mic"));
                final byte[] mic = context.getMIC(mbuf.buffer, 0, mbuf.getLength());
                if (mic == null) {
                    return false;
                }
                this.packet.reset();
                this.buf.putByte((byte)66);
                this.buf.putString(mic);
                session.write(this.packet);
                context.dispose();
                this.buf = session.read(this.buf);
                command = (this.buf.getCommand() & 0xFF);
                if (command == 52) {
                    return true;
                }
                if (command == 51) {
                    this.buf.getInt();
                    this.buf.getByte();
                    this.buf.getByte();
                    final byte[] foo = this.buf.getString();
                    final int partial_success = this.buf.getByte();
                    if (partial_success != 0) {
                        throw new JSchPartialAuthException(Util.byte2str(foo));
                    }
                }
                return false;
            }
            else {
                if (command != 53) {
                    return false;
                }
                this.buf.getInt();
                this.buf.getByte();
                this.buf.getByte();
                final byte[] _message = this.buf.getString();
                final byte[] lang = this.buf.getString();
                final String message2 = Util.byte2str(_message);
                if (this.userinfo == null) {
                    continue;
                }
                this.userinfo.showMessage(message2);
            }
        }
    }
    
    static {
        supported_oid = new byte[][] { { 6, 9, 42, -122, 72, -122, -9, 18, 1, 2, 2 } };
        supported_method = new String[] { "gssapi-with-mic.krb5" };
    }
}
