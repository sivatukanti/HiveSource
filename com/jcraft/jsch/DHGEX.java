// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public class DHGEX extends KeyExchange
{
    private static final int SSH_MSG_KEX_DH_GEX_GROUP = 31;
    private static final int SSH_MSG_KEX_DH_GEX_INIT = 32;
    private static final int SSH_MSG_KEX_DH_GEX_REPLY = 33;
    private static final int SSH_MSG_KEX_DH_GEX_REQUEST = 34;
    static int min;
    static int preferred;
    int max;
    private int state;
    DH dh;
    byte[] V_S;
    byte[] V_C;
    byte[] I_S;
    byte[] I_C;
    private Buffer buf;
    private Packet packet;
    private byte[] p;
    private byte[] g;
    private byte[] e;
    protected String hash;
    
    public DHGEX() {
        this.max = 1024;
        this.hash = "sha-1";
    }
    
    @Override
    public void init(final Session session, final byte[] V_S, final byte[] V_C, final byte[] I_S, final byte[] I_C) throws Exception {
        this.session = session;
        this.V_S = V_S;
        this.V_C = V_C;
        this.I_S = I_S;
        this.I_C = I_C;
        try {
            final Class c = Class.forName(session.getConfig(this.hash));
            (this.sha = c.newInstance()).init();
        }
        catch (Exception e) {
            System.err.println(e);
        }
        this.buf = new Buffer();
        this.packet = new Packet(this.buf);
        try {
            final Class c = Class.forName(session.getConfig("dh"));
            final int check2048 = this.check2048(c, this.max);
            this.max = check2048;
            DHGEX.preferred = check2048;
            (this.dh = c.newInstance()).init();
        }
        catch (Exception e) {
            throw e;
        }
        this.packet.reset();
        this.buf.putByte((byte)34);
        this.buf.putInt(DHGEX.min);
        this.buf.putInt(DHGEX.preferred);
        this.buf.putInt(this.max);
        session.write(this.packet);
        if (JSch.getLogger().isEnabled(1)) {
            JSch.getLogger().log(1, "SSH_MSG_KEX_DH_GEX_REQUEST(" + DHGEX.min + "<" + DHGEX.preferred + "<" + this.max + ") sent");
            JSch.getLogger().log(1, "expecting SSH_MSG_KEX_DH_GEX_GROUP");
        }
        this.state = 31;
    }
    
    @Override
    public boolean next(final Buffer _buf) throws Exception {
        switch (this.state) {
            case 31: {
                _buf.getInt();
                _buf.getByte();
                final int j = _buf.getByte();
                if (j != 31) {
                    System.err.println("type: must be SSH_MSG_KEX_DH_GEX_GROUP " + j);
                    return false;
                }
                this.p = _buf.getMPInt();
                this.g = _buf.getMPInt();
                this.dh.setP(this.p);
                this.dh.setG(this.g);
                this.e = this.dh.getE();
                this.packet.reset();
                this.buf.putByte((byte)32);
                this.buf.putMPInt(this.e);
                this.session.write(this.packet);
                if (JSch.getLogger().isEnabled(1)) {
                    JSch.getLogger().log(1, "SSH_MSG_KEX_DH_GEX_INIT sent");
                    JSch.getLogger().log(1, "expecting SSH_MSG_KEX_DH_GEX_REPLY");
                }
                this.state = 33;
                return true;
            }
            case 33: {
                int j = _buf.getInt();
                j = _buf.getByte();
                j = _buf.getByte();
                if (j != 33) {
                    System.err.println("type: must be SSH_MSG_KEX_DH_GEX_REPLY " + j);
                    return false;
                }
                this.K_S = _buf.getString();
                final byte[] f = _buf.getMPInt();
                final byte[] sig_of_H = _buf.getString();
                this.dh.setF(f);
                this.dh.checkRange();
                this.K = this.normalize(this.dh.getK());
                this.buf.reset();
                this.buf.putString(this.V_C);
                this.buf.putString(this.V_S);
                this.buf.putString(this.I_C);
                this.buf.putString(this.I_S);
                this.buf.putString(this.K_S);
                this.buf.putInt(DHGEX.min);
                this.buf.putInt(DHGEX.preferred);
                this.buf.putInt(this.max);
                this.buf.putMPInt(this.p);
                this.buf.putMPInt(this.g);
                this.buf.putMPInt(this.e);
                this.buf.putMPInt(f);
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
    
    protected int check2048(final Class c, int _max) throws Exception {
        final DH dh = c.newInstance();
        dh.init();
        final byte[] foo = { 0, -35, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 115 };
        dh.setP(foo);
        final byte[] bar = { 2 };
        dh.setG(bar);
        try {
            dh.getE();
            _max = 2048;
        }
        catch (Exception ex) {}
        return _max;
    }
    
    static {
        DHGEX.min = 1024;
        DHGEX.preferred = 1024;
    }
}
