// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public abstract class DHECN extends KeyExchange
{
    private static final int SSH_MSG_KEX_ECDH_INIT = 30;
    private static final int SSH_MSG_KEX_ECDH_REPLY = 31;
    private int state;
    byte[] Q_C;
    byte[] V_S;
    byte[] V_C;
    byte[] I_S;
    byte[] I_C;
    byte[] e;
    private Buffer buf;
    private Packet packet;
    private ECDH ecdh;
    protected String sha_name;
    protected int key_size;
    
    @Override
    public void init(final Session session, final byte[] V_S, final byte[] V_C, final byte[] I_S, final byte[] I_C) throws Exception {
        this.session = session;
        this.V_S = V_S;
        this.V_C = V_C;
        this.I_S = I_S;
        this.I_C = I_C;
        try {
            final Class c = Class.forName(session.getConfig(this.sha_name));
            (this.sha = c.newInstance()).init();
        }
        catch (Exception e) {
            System.err.println(e);
        }
        this.buf = new Buffer();
        (this.packet = new Packet(this.buf)).reset();
        this.buf.putByte((byte)30);
        try {
            final Class c = Class.forName(session.getConfig("ecdh-sha2-nistp"));
            (this.ecdh = c.newInstance()).init(this.key_size);
            this.Q_C = this.ecdh.getQ();
            this.buf.putString(this.Q_C);
        }
        catch (Exception e) {
            if (e instanceof Throwable) {
                throw new JSchException(e.toString(), e);
            }
            throw new JSchException(e.toString());
        }
        if (V_S == null) {
            return;
        }
        session.write(this.packet);
        if (JSch.getLogger().isEnabled(1)) {
            JSch.getLogger().log(1, "SSH_MSG_KEX_ECDH_INIT sent");
            JSch.getLogger().log(1, "expecting SSH_MSG_KEX_ECDH_REPLY");
        }
        this.state = 31;
    }
    
    @Override
    public boolean next(final Buffer _buf) throws Exception {
        switch (this.state) {
            case 31: {
                int j = _buf.getInt();
                j = _buf.getByte();
                j = _buf.getByte();
                if (j != 31) {
                    System.err.println("type: must be 31 " + j);
                    return false;
                }
                this.K_S = _buf.getString();
                final byte[] Q_S = _buf.getString();
                final byte[][] r_s = KeyPairECDSA.fromPoint(Q_S);
                if (!this.ecdh.validate(r_s[0], r_s[1])) {
                    return false;
                }
                this.K = this.ecdh.getSecret(r_s[0], r_s[1]);
                this.K = this.normalize(this.K);
                final byte[] sig_of_H = _buf.getString();
                this.buf.reset();
                this.buf.putString(this.V_C);
                this.buf.putString(this.V_S);
                this.buf.putString(this.I_C);
                this.buf.putString(this.I_S);
                this.buf.putString(this.K_S);
                this.buf.putString(this.Q_C);
                this.buf.putString(Q_S);
                this.buf.putMPInt(this.K);
                final byte[] foo = new byte[this.buf.getLength()];
                this.buf.getByte(foo);
                this.sha.update(foo, 0, foo.length);
                this.H = this.sha.digest();
                int i = 0;
                j = 0;
                j = ((this.K_S[i++] << 24 & 0xFF000000) | (this.K_S[i++] << 16 & 0xFF0000) | (this.K_S[i++] << 8 & 0xFF00) | (this.K_S[i++] & 0xFF));
                final String alg = Util.byte2str(this.K_S, i, j);
                i += j;
                final boolean result = this.verify(alg, this.K_S, i, sig_of_H);
                this.state = 0;
                return result;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public int getState() {
        return this.state;
    }
}
