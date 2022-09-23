// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public class DHG1 extends KeyExchange
{
    static final byte[] g;
    static final byte[] p;
    private static final int SSH_MSG_KEXDH_INIT = 30;
    private static final int SSH_MSG_KEXDH_REPLY = 31;
    private int state;
    DH dh;
    byte[] V_S;
    byte[] V_C;
    byte[] I_S;
    byte[] I_C;
    byte[] e;
    private Buffer buf;
    private Packet packet;
    
    @Override
    public void init(final Session session, final byte[] V_S, final byte[] V_C, final byte[] I_S, final byte[] I_C) throws Exception {
        this.session = session;
        this.V_S = V_S;
        this.V_C = V_C;
        this.I_S = I_S;
        this.I_C = I_C;
        try {
            final Class c = Class.forName(session.getConfig("sha-1"));
            (this.sha = c.newInstance()).init();
        }
        catch (Exception e) {
            System.err.println(e);
        }
        this.buf = new Buffer();
        this.packet = new Packet(this.buf);
        try {
            final Class c = Class.forName(session.getConfig("dh"));
            (this.dh = c.newInstance()).init();
        }
        catch (Exception e) {
            throw e;
        }
        this.dh.setP(DHG1.p);
        this.dh.setG(DHG1.g);
        this.e = this.dh.getE();
        this.packet.reset();
        this.buf.putByte((byte)30);
        this.buf.putMPInt(this.e);
        session.write(this.packet);
        if (JSch.getLogger().isEnabled(1)) {
            JSch.getLogger().log(1, "SSH_MSG_KEXDH_INIT sent");
            JSch.getLogger().log(1, "expecting SSH_MSG_KEXDH_REPLY");
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
    
    static {
        g = new byte[] { 2 };
        p = new byte[] { 0, -1, -1, -1, -1, -1, -1, -1, -1, -55, 15, -38, -94, 33, 104, -62, 52, -60, -58, 98, -117, -128, -36, 28, -47, 41, 2, 78, 8, -118, 103, -52, 116, 2, 11, -66, -90, 59, 19, -101, 34, 81, 74, 8, 121, -114, 52, 4, -35, -17, -107, 25, -77, -51, 58, 67, 27, 48, 43, 10, 109, -14, 95, 20, 55, 79, -31, 53, 109, 109, 81, -62, 69, -28, -123, -75, 118, 98, 94, 126, -58, -12, 76, 66, -23, -90, 55, -19, 107, 11, -1, 92, -74, -12, 6, -73, -19, -18, 56, 107, -5, 90, -119, -97, -91, -82, -97, 36, 17, 124, 75, 31, -26, 73, 40, 102, 81, -20, -26, 83, -127, -1, -1, -1, -1, -1, -1, -1, -1 };
    }
}
