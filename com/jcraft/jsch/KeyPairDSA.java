// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.math.BigInteger;

public class KeyPairDSA extends KeyPair
{
    private byte[] P_array;
    private byte[] Q_array;
    private byte[] G_array;
    private byte[] pub_array;
    private byte[] prv_array;
    private int key_size;
    private static final byte[] begin;
    private static final byte[] end;
    private static final byte[] sshdss;
    
    public KeyPairDSA(final JSch jsch) {
        this(jsch, null, null, null, null, null);
    }
    
    public KeyPairDSA(final JSch jsch, final byte[] P_array, final byte[] Q_array, final byte[] G_array, final byte[] pub_array, final byte[] prv_array) {
        super(jsch);
        this.key_size = 1024;
        this.P_array = P_array;
        this.Q_array = Q_array;
        this.G_array = G_array;
        this.pub_array = pub_array;
        this.prv_array = prv_array;
        if (P_array != null) {
            this.key_size = new BigInteger(P_array).bitLength();
        }
    }
    
    @Override
    void generate(final int key_size) throws JSchException {
        this.key_size = key_size;
        try {
            final JSch jsch = this.jsch;
            final Class c = Class.forName(JSch.getConfig("keypairgen.dsa"));
            KeyPairGenDSA keypairgen = c.newInstance();
            keypairgen.init(key_size);
            this.P_array = keypairgen.getP();
            this.Q_array = keypairgen.getQ();
            this.G_array = keypairgen.getG();
            this.pub_array = keypairgen.getY();
            this.prv_array = keypairgen.getX();
            keypairgen = null;
        }
        catch (Exception e) {
            if (e instanceof Throwable) {
                throw new JSchException(e.toString(), e);
            }
            throw new JSchException(e.toString());
        }
    }
    
    @Override
    byte[] getBegin() {
        return KeyPairDSA.begin;
    }
    
    @Override
    byte[] getEnd() {
        return KeyPairDSA.end;
    }
    
    @Override
    byte[] getPrivateKey() {
        final int content = 1 + this.countLength(1) + 1 + 1 + this.countLength(this.P_array.length) + this.P_array.length + 1 + this.countLength(this.Q_array.length) + this.Q_array.length + 1 + this.countLength(this.G_array.length) + this.G_array.length + 1 + this.countLength(this.pub_array.length) + this.pub_array.length + 1 + this.countLength(this.prv_array.length) + this.prv_array.length;
        final int total = 1 + this.countLength(content) + content;
        final byte[] plain = new byte[total];
        int index = 0;
        index = this.writeSEQUENCE(plain, index, content);
        index = this.writeINTEGER(plain, index, new byte[1]);
        index = this.writeINTEGER(plain, index, this.P_array);
        index = this.writeINTEGER(plain, index, this.Q_array);
        index = this.writeINTEGER(plain, index, this.G_array);
        index = this.writeINTEGER(plain, index, this.pub_array);
        index = this.writeINTEGER(plain, index, this.prv_array);
        return plain;
    }
    
    @Override
    boolean parse(final byte[] plain) {
        try {
            if (this.vendor == 1) {
                if (plain[0] != 48) {
                    final Buffer buf = new Buffer(plain);
                    buf.getInt();
                    this.P_array = buf.getMPIntBits();
                    this.G_array = buf.getMPIntBits();
                    this.Q_array = buf.getMPIntBits();
                    this.pub_array = buf.getMPIntBits();
                    this.prv_array = buf.getMPIntBits();
                    if (this.P_array != null) {
                        this.key_size = new BigInteger(this.P_array).bitLength();
                    }
                    return true;
                }
                return false;
            }
            else {
                if (this.vendor == 2) {
                    final Buffer buf = new Buffer(plain);
                    buf.skip(plain.length);
                    try {
                        final byte[][] tmp = buf.getBytes(1, "");
                        this.prv_array = tmp[0];
                    }
                    catch (JSchException e) {
                        return false;
                    }
                    return true;
                }
                int index = 0;
                int length = 0;
                if (plain[index] != 48) {
                    return false;
                }
                ++index;
                length = (plain[index++] & 0xFF);
                if ((length & 0x80) != 0x0) {
                    int foo = length & 0x7F;
                    length = 0;
                    while (foo-- > 0) {
                        length = (length << 8) + (plain[index++] & 0xFF);
                    }
                }
                if (plain[index] != 2) {
                    return false;
                }
                ++index;
                length = (plain[index++] & 0xFF);
                if ((length & 0x80) != 0x0) {
                    int foo = length & 0x7F;
                    length = 0;
                    while (foo-- > 0) {
                        length = (length << 8) + (plain[index++] & 0xFF);
                    }
                }
                index += length;
                ++index;
                length = (plain[index++] & 0xFF);
                if ((length & 0x80) != 0x0) {
                    int foo = length & 0x7F;
                    length = 0;
                    while (foo-- > 0) {
                        length = (length << 8) + (plain[index++] & 0xFF);
                    }
                }
                System.arraycopy(plain, index, this.P_array = new byte[length], 0, length);
                index += length;
                ++index;
                length = (plain[index++] & 0xFF);
                if ((length & 0x80) != 0x0) {
                    int foo = length & 0x7F;
                    length = 0;
                    while (foo-- > 0) {
                        length = (length << 8) + (plain[index++] & 0xFF);
                    }
                }
                System.arraycopy(plain, index, this.Q_array = new byte[length], 0, length);
                index += length;
                ++index;
                length = (plain[index++] & 0xFF);
                if ((length & 0x80) != 0x0) {
                    int foo = length & 0x7F;
                    length = 0;
                    while (foo-- > 0) {
                        length = (length << 8) + (plain[index++] & 0xFF);
                    }
                }
                System.arraycopy(plain, index, this.G_array = new byte[length], 0, length);
                index += length;
                ++index;
                length = (plain[index++] & 0xFF);
                if ((length & 0x80) != 0x0) {
                    int foo = length & 0x7F;
                    length = 0;
                    while (foo-- > 0) {
                        length = (length << 8) + (plain[index++] & 0xFF);
                    }
                }
                System.arraycopy(plain, index, this.pub_array = new byte[length], 0, length);
                index += length;
                ++index;
                length = (plain[index++] & 0xFF);
                if ((length & 0x80) != 0x0) {
                    int foo = length & 0x7F;
                    length = 0;
                    while (foo-- > 0) {
                        length = (length << 8) + (plain[index++] & 0xFF);
                    }
                }
                System.arraycopy(plain, index, this.prv_array = new byte[length], 0, length);
                index += length;
                if (this.P_array != null) {
                    this.key_size = new BigInteger(this.P_array).bitLength();
                }
            }
        }
        catch (Exception e2) {
            return false;
        }
        return true;
    }
    
    @Override
    public byte[] getPublicKeyBlob() {
        final byte[] foo = super.getPublicKeyBlob();
        if (foo != null) {
            return foo;
        }
        if (this.P_array == null) {
            return null;
        }
        final byte[][] tmp = { KeyPairDSA.sshdss, this.P_array, this.Q_array, this.G_array, this.pub_array };
        return Buffer.fromBytes(tmp).buffer;
    }
    
    @Override
    byte[] getKeyTypeName() {
        return KeyPairDSA.sshdss;
    }
    
    @Override
    public int getKeyType() {
        return 1;
    }
    
    public int getKeySize() {
        return this.key_size;
    }
    
    @Override
    public byte[] getSignature(final byte[] data) {
        try {
            final JSch jsch = this.jsch;
            final Class c = Class.forName(JSch.getConfig("signature.dss"));
            final SignatureDSA dsa = c.newInstance();
            dsa.init();
            dsa.setPrvKey(this.prv_array, this.P_array, this.Q_array, this.G_array);
            dsa.update(data);
            final byte[] sig = dsa.sign();
            final byte[][] tmp = { KeyPairDSA.sshdss, sig };
            return Buffer.fromBytes(tmp).buffer;
        }
        catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public Signature getVerifier() {
        try {
            final JSch jsch = this.jsch;
            final Class c = Class.forName(JSch.getConfig("signature.dss"));
            final SignatureDSA dsa = c.newInstance();
            dsa.init();
            if (this.pub_array == null && this.P_array == null && this.getPublicKeyBlob() != null) {
                final Buffer buf = new Buffer(this.getPublicKeyBlob());
                buf.getString();
                this.P_array = buf.getString();
                this.Q_array = buf.getString();
                this.G_array = buf.getString();
                this.pub_array = buf.getString();
            }
            dsa.setPubKey(this.pub_array, this.P_array, this.Q_array, this.G_array);
            return dsa;
        }
        catch (Exception e) {
            return null;
        }
    }
    
    static KeyPair fromSSHAgent(final JSch jsch, final Buffer buf) throws JSchException {
        final byte[][] tmp = buf.getBytes(7, "invalid key format");
        final byte[] P_array = tmp[1];
        final byte[] Q_array = tmp[2];
        final byte[] G_array = tmp[3];
        final byte[] pub_array = tmp[4];
        final byte[] prv_array = tmp[5];
        final KeyPairDSA kpair = new KeyPairDSA(jsch, P_array, Q_array, G_array, pub_array, prv_array);
        kpair.publicKeyComment = new String(tmp[6]);
        kpair.vendor = 0;
        return kpair;
    }
    
    @Override
    public byte[] forSSHAgent() throws JSchException {
        if (this.isEncrypted()) {
            throw new JSchException("key is encrypted.");
        }
        final Buffer buf = new Buffer();
        buf.putString(KeyPairDSA.sshdss);
        buf.putString(this.P_array);
        buf.putString(this.Q_array);
        buf.putString(this.G_array);
        buf.putString(this.pub_array);
        buf.putString(this.prv_array);
        buf.putString(Util.str2byte(this.publicKeyComment));
        final byte[] result = new byte[buf.getLength()];
        buf.getByte(result, 0, result.length);
        return result;
    }
    
    @Override
    public void dispose() {
        super.dispose();
        Util.bzero(this.prv_array);
    }
    
    static {
        begin = Util.str2byte("-----BEGIN DSA PRIVATE KEY-----");
        end = Util.str2byte("-----END DSA PRIVATE KEY-----");
        sshdss = Util.str2byte("ssh-dss");
    }
}
