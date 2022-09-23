// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.util.Vector;
import java.io.IOException;

class ChannelAgentForwarding extends Channel
{
    private static final int LOCAL_WINDOW_SIZE_MAX = 131072;
    private static final int LOCAL_MAXIMUM_PACKET_SIZE = 16384;
    private final byte SSH_AGENTC_REQUEST_RSA_IDENTITIES = 1;
    private final byte SSH_AGENT_RSA_IDENTITIES_ANSWER = 2;
    private final byte SSH_AGENTC_RSA_CHALLENGE = 3;
    private final byte SSH_AGENT_RSA_RESPONSE = 4;
    private final byte SSH_AGENT_FAILURE = 5;
    private final byte SSH_AGENT_SUCCESS = 6;
    private final byte SSH_AGENTC_ADD_RSA_IDENTITY = 7;
    private final byte SSH_AGENTC_REMOVE_RSA_IDENTITY = 8;
    private final byte SSH_AGENTC_REMOVE_ALL_RSA_IDENTITIES = 9;
    private final byte SSH2_AGENTC_REQUEST_IDENTITIES = 11;
    private final byte SSH2_AGENT_IDENTITIES_ANSWER = 12;
    private final byte SSH2_AGENTC_SIGN_REQUEST = 13;
    private final byte SSH2_AGENT_SIGN_RESPONSE = 14;
    private final byte SSH2_AGENTC_ADD_IDENTITY = 17;
    private final byte SSH2_AGENTC_REMOVE_IDENTITY = 18;
    private final byte SSH2_AGENTC_REMOVE_ALL_IDENTITIES = 19;
    private final byte SSH2_AGENT_FAILURE = 30;
    boolean init;
    private Buffer rbuf;
    private Buffer wbuf;
    private Packet packet;
    private Buffer mbuf;
    
    ChannelAgentForwarding() {
        this.init = true;
        this.rbuf = null;
        this.wbuf = null;
        this.packet = null;
        this.mbuf = null;
        this.setLocalWindowSizeMax(131072);
        this.setLocalWindowSize(131072);
        this.setLocalPacketSize(16384);
        this.type = Util.str2byte("auth-agent@openssh.com");
        (this.rbuf = new Buffer()).reset();
        this.mbuf = new Buffer();
        this.connected = true;
    }
    
    @Override
    public void run() {
        try {
            this.sendOpenConfirmation();
        }
        catch (Exception e) {
            this.close = true;
            this.disconnect();
        }
    }
    
    @Override
    void write(final byte[] foo, final int s, final int l) throws IOException {
        if (this.packet == null) {
            this.wbuf = new Buffer(this.rmpsize);
            this.packet = new Packet(this.wbuf);
        }
        this.rbuf.shift();
        if (this.rbuf.buffer.length < this.rbuf.index + l) {
            final byte[] newbuf = new byte[this.rbuf.s + l];
            System.arraycopy(this.rbuf.buffer, 0, newbuf, 0, this.rbuf.buffer.length);
            this.rbuf.buffer = newbuf;
        }
        this.rbuf.putByte(foo, s, l);
        final int mlen = this.rbuf.getInt();
        if (mlen > this.rbuf.getLength()) {
            final Buffer rbuf = this.rbuf;
            rbuf.s -= 4;
            return;
        }
        final int typ = this.rbuf.getByte();
        Session _session = null;
        try {
            _session = this.getSession();
        }
        catch (JSchException e) {
            throw new IOException(e.toString());
        }
        final IdentityRepository irepo = _session.getIdentityRepository();
        final UserInfo userinfo = _session.getUserInfo();
        this.mbuf.reset();
        if (typ == 11) {
            this.mbuf.putByte((byte)12);
            final Vector identities = irepo.getIdentities();
            synchronized (identities) {
                int count = 0;
                for (int i = 0; i < identities.size(); ++i) {
                    final Identity identity = identities.elementAt(i);
                    if (identity.getPublicKeyBlob() != null) {
                        ++count;
                    }
                }
                this.mbuf.putInt(count);
                for (int i = 0; i < identities.size(); ++i) {
                    final Identity identity = identities.elementAt(i);
                    final byte[] pubkeyblob = identity.getPublicKeyBlob();
                    if (pubkeyblob != null) {
                        this.mbuf.putString(pubkeyblob);
                        this.mbuf.putString(Util.empty);
                    }
                }
            }
        }
        else if (typ == 1) {
            this.mbuf.putByte((byte)2);
            this.mbuf.putInt(0);
        }
        else if (typ == 13) {
            final byte[] blob = this.rbuf.getString();
            final byte[] data = this.rbuf.getString();
            final int flags = this.rbuf.getInt();
            final Vector identities2 = irepo.getIdentities();
            Identity identity = null;
            synchronized (identities2) {
                for (int j = 0; j < identities2.size(); ++j) {
                    final Identity _identity = identities2.elementAt(j);
                    if (_identity.getPublicKeyBlob() != null) {
                        if (Util.array_equals(blob, _identity.getPublicKeyBlob())) {
                            if (_identity.isEncrypted()) {
                                if (userinfo == null) {
                                    continue;
                                }
                                while (_identity.isEncrypted()) {
                                    if (!userinfo.promptPassphrase("Passphrase for " + _identity.getName())) {
                                        break;
                                    }
                                    final String _passphrase = userinfo.getPassphrase();
                                    if (_passphrase == null) {
                                        break;
                                    }
                                    final byte[] passphrase = Util.str2byte(_passphrase);
                                    try {
                                        if (_identity.setPassphrase(passphrase)) {
                                            break;
                                        }
                                        continue;
                                    }
                                    catch (JSchException e2) {
                                        break;
                                    }
                                }
                            }
                            if (!_identity.isEncrypted()) {
                                identity = _identity;
                                break;
                            }
                        }
                    }
                }
            }
            byte[] signature = null;
            if (identity != null) {
                signature = identity.getSignature(data);
            }
            if (signature == null) {
                this.mbuf.putByte((byte)30);
            }
            else {
                this.mbuf.putByte((byte)14);
                this.mbuf.putString(signature);
            }
        }
        else if (typ == 18) {
            final byte[] blob = this.rbuf.getString();
            irepo.remove(blob);
            this.mbuf.putByte((byte)6);
        }
        else if (typ == 9) {
            this.mbuf.putByte((byte)6);
        }
        else if (typ == 19) {
            irepo.removeAll();
            this.mbuf.putByte((byte)6);
        }
        else if (typ == 17) {
            final int fooo = this.rbuf.getLength();
            final byte[] tmp = new byte[fooo];
            this.rbuf.getByte(tmp);
            final boolean result = irepo.add(tmp);
            this.mbuf.putByte((byte)(result ? 6 : 5));
        }
        else {
            this.rbuf.skip(this.rbuf.getLength() - 1);
            this.mbuf.putByte((byte)5);
        }
        final byte[] response = new byte[this.mbuf.getLength()];
        this.mbuf.getByte(response);
        this.send(response);
    }
    
    private void send(final byte[] message) {
        this.packet.reset();
        this.wbuf.putByte((byte)94);
        this.wbuf.putInt(this.recipient);
        this.wbuf.putInt(4 + message.length);
        this.wbuf.putString(message);
        try {
            this.getSession().write(this.packet, this, 4 + message.length);
        }
        catch (Exception ex) {}
    }
    
    @Override
    void eof_remote() {
        super.eof_remote();
        this.eof();
    }
}
