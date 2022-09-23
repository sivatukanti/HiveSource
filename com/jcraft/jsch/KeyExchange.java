// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public abstract class KeyExchange
{
    static final int PROPOSAL_KEX_ALGS = 0;
    static final int PROPOSAL_SERVER_HOST_KEY_ALGS = 1;
    static final int PROPOSAL_ENC_ALGS_CTOS = 2;
    static final int PROPOSAL_ENC_ALGS_STOC = 3;
    static final int PROPOSAL_MAC_ALGS_CTOS = 4;
    static final int PROPOSAL_MAC_ALGS_STOC = 5;
    static final int PROPOSAL_COMP_ALGS_CTOS = 6;
    static final int PROPOSAL_COMP_ALGS_STOC = 7;
    static final int PROPOSAL_LANG_CTOS = 8;
    static final int PROPOSAL_LANG_STOC = 9;
    static final int PROPOSAL_MAX = 10;
    static String kex;
    static String server_host_key;
    static String enc_c2s;
    static String enc_s2c;
    static String mac_c2s;
    static String mac_s2c;
    static String lang_c2s;
    static String lang_s2c;
    public static final int STATE_END = 0;
    protected Session session;
    protected HASH sha;
    protected byte[] K;
    protected byte[] H;
    protected byte[] K_S;
    protected final int RSA = 0;
    protected final int DSS = 1;
    protected final int ECDSA = 2;
    private int type;
    private String key_alg_name;
    
    public KeyExchange() {
        this.session = null;
        this.sha = null;
        this.K = null;
        this.H = null;
        this.K_S = null;
        this.type = 0;
        this.key_alg_name = "";
    }
    
    public abstract void init(final Session p0, final byte[] p1, final byte[] p2, final byte[] p3, final byte[] p4) throws Exception;
    
    public abstract boolean next(final Buffer p0) throws Exception;
    
    public abstract int getState();
    
    public String getKeyType() {
        if (this.type == 1) {
            return "DSA";
        }
        if (this.type == 0) {
            return "RSA";
        }
        return "ECDSA";
    }
    
    public String getKeyAlgorithName() {
        return this.key_alg_name;
    }
    
    protected static String[] guess(final byte[] I_S, final byte[] I_C) {
        final String[] guess = new String[10];
        final Buffer sb = new Buffer(I_S);
        sb.setOffSet(17);
        final Buffer cb = new Buffer(I_C);
        cb.setOffSet(17);
        if (JSch.getLogger().isEnabled(1)) {
            for (int i = 0; i < 10; ++i) {
                JSch.getLogger().log(1, "kex: server: " + Util.byte2str(sb.getString()));
            }
            for (int i = 0; i < 10; ++i) {
                JSch.getLogger().log(1, "kex: client: " + Util.byte2str(cb.getString()));
            }
            sb.setOffSet(17);
            cb.setOffSet(17);
        }
        for (int i = 0; i < 10; ++i) {
            final byte[] sp = sb.getString();
            final byte[] cp = cb.getString();
            int j = 0;
            int k = 0;
        Label_0344:
            while (j < cp.length) {
                while (j < cp.length && cp[j] != 44) {
                    ++j;
                }
                if (k == j) {
                    return null;
                }
                final String algorithm = Util.byte2str(cp, k, j - k);
                int l = 0;
                int m = 0;
                while (l < sp.length) {
                    while (l < sp.length && sp[l] != 44) {
                        ++l;
                    }
                    if (m == l) {
                        return null;
                    }
                    if (algorithm.equals(Util.byte2str(sp, m, l - m))) {
                        guess[i] = algorithm;
                        break Label_0344;
                    }
                    m = ++l;
                }
                k = ++j;
            }
            if (j == 0) {
                guess[i] = "";
            }
            else if (guess[i] == null) {
                return null;
            }
        }
        if (JSch.getLogger().isEnabled(1)) {
            JSch.getLogger().log(1, "kex: server->client " + guess[3] + " " + guess[5] + " " + guess[7]);
            JSch.getLogger().log(1, "kex: client->server " + guess[2] + " " + guess[4] + " " + guess[6]);
        }
        return guess;
    }
    
    public String getFingerPrint() {
        HASH hash = null;
        try {
            final Class c = Class.forName(this.session.getConfig("md5"));
            hash = c.newInstance();
        }
        catch (Exception e) {
            System.err.println("getFingerPrint: " + e);
        }
        return Util.getFingerPrint(hash, this.getHostKey());
    }
    
    byte[] getK() {
        return this.K;
    }
    
    byte[] getH() {
        return this.H;
    }
    
    HASH getHash() {
        return this.sha;
    }
    
    byte[] getHostKey() {
        return this.K_S;
    }
    
    protected byte[] normalize(final byte[] secret) {
        if (secret.length > 1 && secret[0] == 0 && (secret[1] & 0x80) == 0x0) {
            final byte[] tmp = new byte[secret.length - 1];
            System.arraycopy(secret, 1, tmp, 0, tmp.length);
            return this.normalize(tmp);
        }
        return secret;
    }
    
    protected boolean verify(final String alg, final byte[] K_S, final int index, final byte[] sig_of_H) throws Exception {
        int i = index;
        boolean result = false;
        if (alg.equals("ssh-rsa")) {
            this.type = 0;
            this.key_alg_name = alg;
            int j = (K_S[i++] << 24 & 0xFF000000) | (K_S[i++] << 16 & 0xFF0000) | (K_S[i++] << 8 & 0xFF00) | (K_S[i++] & 0xFF);
            byte[] tmp = new byte[j];
            System.arraycopy(K_S, i, tmp, 0, j);
            i += j;
            final byte[] ee = tmp;
            j = ((K_S[i++] << 24 & 0xFF000000) | (K_S[i++] << 16 & 0xFF0000) | (K_S[i++] << 8 & 0xFF00) | (K_S[i++] & 0xFF));
            tmp = new byte[j];
            System.arraycopy(K_S, i, tmp, 0, j);
            i += j;
            final byte[] n = tmp;
            SignatureRSA sig = null;
            try {
                final Class c = Class.forName(this.session.getConfig("signature.rsa"));
                sig = c.newInstance();
                sig.init();
            }
            catch (Exception e) {
                System.err.println(e);
            }
            sig.setPubKey(ee, n);
            sig.update(this.H);
            result = sig.verify(sig_of_H);
            if (JSch.getLogger().isEnabled(1)) {
                JSch.getLogger().log(1, "ssh_rsa_verify: signature " + result);
            }
        }
        else if (alg.equals("ssh-dss")) {
            byte[] q = null;
            this.type = 1;
            this.key_alg_name = alg;
            int j = (K_S[i++] << 24 & 0xFF000000) | (K_S[i++] << 16 & 0xFF0000) | (K_S[i++] << 8 & 0xFF00) | (K_S[i++] & 0xFF);
            byte[] tmp2 = new byte[j];
            System.arraycopy(K_S, i, tmp2, 0, j);
            i += j;
            final byte[] p = tmp2;
            j = ((K_S[i++] << 24 & 0xFF000000) | (K_S[i++] << 16 & 0xFF0000) | (K_S[i++] << 8 & 0xFF00) | (K_S[i++] & 0xFF));
            tmp2 = new byte[j];
            System.arraycopy(K_S, i, tmp2, 0, j);
            i += j;
            q = tmp2;
            j = ((K_S[i++] << 24 & 0xFF000000) | (K_S[i++] << 16 & 0xFF0000) | (K_S[i++] << 8 & 0xFF00) | (K_S[i++] & 0xFF));
            tmp2 = new byte[j];
            System.arraycopy(K_S, i, tmp2, 0, j);
            i += j;
            final byte[] g = tmp2;
            j = ((K_S[i++] << 24 & 0xFF000000) | (K_S[i++] << 16 & 0xFF0000) | (K_S[i++] << 8 & 0xFF00) | (K_S[i++] & 0xFF));
            tmp2 = new byte[j];
            System.arraycopy(K_S, i, tmp2, 0, j);
            i += j;
            final byte[] f = tmp2;
            SignatureDSA sig2 = null;
            try {
                final Class c2 = Class.forName(this.session.getConfig("signature.dss"));
                sig2 = c2.newInstance();
                sig2.init();
            }
            catch (Exception e2) {
                System.err.println(e2);
            }
            sig2.setPubKey(f, p, q, g);
            sig2.update(this.H);
            result = sig2.verify(sig_of_H);
            if (JSch.getLogger().isEnabled(1)) {
                JSch.getLogger().log(1, "ssh_dss_verify: signature " + result);
            }
        }
        else if (alg.equals("ecdsa-sha2-nistp256") || alg.equals("ecdsa-sha2-nistp384") || alg.equals("ecdsa-sha2-nistp521")) {
            this.type = 2;
            this.key_alg_name = alg;
            int j = (K_S[i++] << 24 & 0xFF000000) | (K_S[i++] << 16 & 0xFF0000) | (K_S[i++] << 8 & 0xFF00) | (K_S[i++] & 0xFF);
            byte[] tmp = new byte[j];
            System.arraycopy(K_S, i, tmp, 0, j);
            i += j;
            j = ((K_S[i++] << 24 & 0xFF000000) | (K_S[i++] << 16 & 0xFF0000) | (K_S[i++] << 8 & 0xFF00) | (K_S[i++] & 0xFF));
            ++i;
            tmp = new byte[(j - 1) / 2];
            System.arraycopy(K_S, i, tmp, 0, tmp.length);
            i += (j - 1) / 2;
            final byte[] r = tmp;
            tmp = new byte[(j - 1) / 2];
            System.arraycopy(K_S, i, tmp, 0, tmp.length);
            i += (j - 1) / 2;
            final byte[] s = tmp;
            SignatureECDSA sig3 = null;
            try {
                final Class c = Class.forName(this.session.getConfig("signature.ecdsa"));
                sig3 = c.newInstance();
                sig3.init();
            }
            catch (Exception e) {
                System.err.println(e);
            }
            sig3.setPubKey(r, s);
            sig3.update(this.H);
            result = sig3.verify(sig_of_H);
        }
        else {
            System.err.println("unknown alg");
        }
        return result;
    }
    
    static {
        KeyExchange.kex = "diffie-hellman-group1-sha1";
        KeyExchange.server_host_key = "ssh-rsa,ssh-dss";
        KeyExchange.enc_c2s = "blowfish-cbc";
        KeyExchange.enc_s2c = "blowfish-cbc";
        KeyExchange.mac_c2s = "hmac-md5";
        KeyExchange.mac_s2c = "hmac-md5";
        KeyExchange.lang_c2s = "";
        KeyExchange.lang_s2c = "";
    }
}
