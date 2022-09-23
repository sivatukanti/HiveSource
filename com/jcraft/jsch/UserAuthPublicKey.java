// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.util.Vector;

class UserAuthPublicKey extends UserAuth
{
    @Override
    public boolean start(final Session session) throws Exception {
        super.start(session);
        final Vector identities = session.getIdentityRepository().getIdentities();
        byte[] passphrase = null;
        byte[] _username = null;
        synchronized (identities) {
            if (identities.size() <= 0) {
                return false;
            }
            _username = Util.str2byte(this.username);
            for (int i = 0; i < identities.size(); ++i) {
                if (session.auth_failures >= session.max_auth_tries) {
                    return false;
                }
                final Identity identity = identities.elementAt(i);
                byte[] pubkeyblob = identity.getPublicKeyBlob();
                if (pubkeyblob != null) {
                    this.packet.reset();
                    this.buf.putByte((byte)50);
                    this.buf.putString(_username);
                    this.buf.putString(Util.str2byte("ssh-connection"));
                    this.buf.putString(Util.str2byte("publickey"));
                    this.buf.putByte((byte)0);
                    this.buf.putString(Util.str2byte(identity.getAlgName()));
                    this.buf.putString(pubkeyblob);
                    session.write(this.packet);
                    int command;
                    while (true) {
                        this.buf = session.read(this.buf);
                        command = (this.buf.getCommand() & 0xFF);
                        if (command == 60) {
                            break;
                        }
                        if (command == 51) {
                            break;
                        }
                        if (command != 53) {
                            break;
                        }
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
                    if (command != 60) {
                        continue;
                    }
                }
                int count = 5;
                do {
                    if (identity.isEncrypted() && passphrase == null) {
                        if (this.userinfo == null) {
                            throw new JSchException("USERAUTH fail");
                        }
                        if (identity.isEncrypted() && !this.userinfo.promptPassphrase("Passphrase for " + identity.getName())) {
                            throw new JSchAuthCancelException("publickey");
                        }
                        final String _passphrase = this.userinfo.getPassphrase();
                        if (_passphrase != null) {
                            passphrase = Util.str2byte(_passphrase);
                        }
                    }
                    if ((!identity.isEncrypted() || passphrase != null) && identity.setPassphrase(passphrase)) {
                        if (passphrase != null && session.getIdentityRepository() instanceof IdentityRepository.Wrapper) {
                            ((IdentityRepository.Wrapper)session.getIdentityRepository()).check();
                            break;
                        }
                        break;
                    }
                    else {
                        Util.bzero(passphrase);
                        passphrase = null;
                    }
                } while (--count != 0);
                Util.bzero(passphrase);
                passphrase = null;
                if (!identity.isEncrypted()) {
                    if (pubkeyblob == null) {
                        pubkeyblob = identity.getPublicKeyBlob();
                    }
                    if (pubkeyblob != null) {
                        this.packet.reset();
                        this.buf.putByte((byte)50);
                        this.buf.putString(_username);
                        this.buf.putString(Util.str2byte("ssh-connection"));
                        this.buf.putString(Util.str2byte("publickey"));
                        this.buf.putByte((byte)1);
                        this.buf.putString(Util.str2byte(identity.getAlgName()));
                        this.buf.putString(pubkeyblob);
                        final byte[] sid = session.getSessionId();
                        final int sidlen = sid.length;
                        final byte[] tmp = new byte[4 + sidlen + this.buf.index - 5];
                        tmp[0] = (byte)(sidlen >>> 24);
                        tmp[1] = (byte)(sidlen >>> 16);
                        tmp[2] = (byte)(sidlen >>> 8);
                        tmp[3] = (byte)sidlen;
                        System.arraycopy(sid, 0, tmp, 4, sidlen);
                        System.arraycopy(this.buf.buffer, 5, tmp, 4 + sidlen, this.buf.index - 5);
                        final byte[] signature = identity.getSignature(tmp);
                        if (signature == null) {
                            break;
                        }
                        this.buf.putString(signature);
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
                                final byte[] _message2 = this.buf.getString();
                                final byte[] lang2 = this.buf.getString();
                                final String message2 = Util.byte2str(_message2);
                                if (this.userinfo == null) {
                                    continue;
                                }
                                this.userinfo.showMessage(message2);
                            }
                            else {
                                if (command != 51) {
                                    break;
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
                                break;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
