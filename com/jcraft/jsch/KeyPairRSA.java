// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.math.BigInteger;

public class KeyPairRSA extends KeyPair
{
    private byte[] n_array;
    private byte[] pub_array;
    private byte[] prv_array;
    private byte[] p_array;
    private byte[] q_array;
    private byte[] ep_array;
    private byte[] eq_array;
    private byte[] c_array;
    private int key_size;
    private static final byte[] begin;
    private static final byte[] end;
    private static final byte[] sshrsa;
    
    public KeyPairRSA(final JSch jsch) {
        this(jsch, null, null, null);
    }
    
    public KeyPairRSA(final JSch jsch, final byte[] n_array, final byte[] pub_array, final byte[] prv_array) {
        super(jsch);
        this.key_size = 1024;
        this.n_array = n_array;
        this.pub_array = pub_array;
        this.prv_array = prv_array;
        if (n_array != null) {
            this.key_size = new BigInteger(n_array).bitLength();
        }
    }
    
    @Override
    void generate(final int key_size) throws JSchException {
        this.key_size = key_size;
        try {
            final JSch jsch = this.jsch;
            final Class c = Class.forName(JSch.getConfig("keypairgen.rsa"));
            KeyPairGenRSA keypairgen = c.newInstance();
            keypairgen.init(key_size);
            this.pub_array = keypairgen.getE();
            this.prv_array = keypairgen.getD();
            this.n_array = keypairgen.getN();
            this.p_array = keypairgen.getP();
            this.q_array = keypairgen.getQ();
            this.ep_array = keypairgen.getEP();
            this.eq_array = keypairgen.getEQ();
            this.c_array = keypairgen.getC();
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
        return KeyPairRSA.begin;
    }
    
    @Override
    byte[] getEnd() {
        return KeyPairRSA.end;
    }
    
    @Override
    byte[] getPrivateKey() {
        final int content = 1 + this.countLength(1) + 1 + 1 + this.countLength(this.n_array.length) + this.n_array.length + 1 + this.countLength(this.pub_array.length) + this.pub_array.length + 1 + this.countLength(this.prv_array.length) + this.prv_array.length + 1 + this.countLength(this.p_array.length) + this.p_array.length + 1 + this.countLength(this.q_array.length) + this.q_array.length + 1 + this.countLength(this.ep_array.length) + this.ep_array.length + 1 + this.countLength(this.eq_array.length) + this.eq_array.length + 1 + this.countLength(this.c_array.length) + this.c_array.length;
        final int total = 1 + this.countLength(content) + content;
        final byte[] plain = new byte[total];
        int index = 0;
        index = this.writeSEQUENCE(plain, index, content);
        index = this.writeINTEGER(plain, index, new byte[1]);
        index = this.writeINTEGER(plain, index, this.n_array);
        index = this.writeINTEGER(plain, index, this.pub_array);
        index = this.writeINTEGER(plain, index, this.prv_array);
        index = this.writeINTEGER(plain, index, this.p_array);
        index = this.writeINTEGER(plain, index, this.q_array);
        index = this.writeINTEGER(plain, index, this.ep_array);
        index = this.writeINTEGER(plain, index, this.eq_array);
        index = this.writeINTEGER(plain, index, this.c_array);
        return plain;
    }
    
    @Override
    boolean parse(final byte[] plain) {
        try {
            int index = 0;
            int length = 0;
            if (this.vendor == 2) {
                final Buffer buf = new Buffer(plain);
                buf.skip(plain.length);
                try {
                    final byte[][] tmp = buf.getBytes(4, "");
                    this.prv_array = tmp[0];
                    this.p_array = tmp[1];
                    this.q_array = tmp[2];
                    this.c_array = tmp[3];
                }
                catch (JSchException e) {
                    return false;
                }
                this.getEPArray();
                this.getEQArray();
                return true;
            }
            if (this.vendor == 1) {
                if (plain[index] != 48) {
                    final Buffer buf = new Buffer(plain);
                    this.pub_array = buf.getMPIntBits();
                    this.prv_array = buf.getMPIntBits();
                    this.n_array = buf.getMPIntBits();
                    final byte[] u_array = buf.getMPIntBits();
                    this.p_array = buf.getMPIntBits();
                    this.q_array = buf.getMPIntBits();
                    if (this.n_array != null) {
                        this.key_size = new BigInteger(this.n_array).bitLength();
                    }
                    this.getEPArray();
                    this.getEQArray();
                    this.getCArray();
                    return true;
                }
                return false;
            }
            else {
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
                System.arraycopy(plain, index, this.n_array = new byte[length], 0, length);
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
                ++index;
                length = (plain[index++] & 0xFF);
                if ((length & 0x80) != 0x0) {
                    int foo = length & 0x7F;
                    length = 0;
                    while (foo-- > 0) {
                        length = (length << 8) + (plain[index++] & 0xFF);
                    }
                }
                System.arraycopy(plain, index, this.p_array = new byte[length], 0, length);
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
                System.arraycopy(plain, index, this.q_array = new byte[length], 0, length);
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
                System.arraycopy(plain, index, this.ep_array = new byte[length], 0, length);
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
                System.arraycopy(plain, index, this.eq_array = new byte[length], 0, length);
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
                System.arraycopy(plain, index, this.c_array = new byte[length], 0, length);
                index += length;
                if (this.n_array != null) {
                    this.key_size = new BigInteger(this.n_array).bitLength();
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
        if (this.pub_array == null) {
            return null;
        }
        final byte[][] tmp = { KeyPairRSA.sshrsa, this.pub_array, this.n_array };
        return Buffer.fromBytes(tmp).buffer;
    }
    
    @Override
    byte[] getKeyTypeName() {
        return KeyPairRSA.sshrsa;
    }
    
    @Override
    public int getKeyType() {
        return 2;
    }
    
    public int getKeySize() {
        return this.key_size;
    }
    
    @Override
    public byte[] getSignature(final byte[] data) {
        try {
            final JSch jsch = this.jsch;
            final Class c = Class.forName(JSch.getConfig("signature.rsa"));
            final SignatureRSA rsa = c.newInstance();
            rsa.init();
            rsa.setPrvKey(this.prv_array, this.n_array);
            rsa.update(data);
            final byte[] sig = rsa.sign();
            final byte[][] tmp = { KeyPairRSA.sshrsa, sig };
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
            final Class c = Class.forName(JSch.getConfig("signature.rsa"));
            final SignatureRSA rsa = c.newInstance();
            rsa.init();
            if (this.pub_array == null && this.n_array == null && this.getPublicKeyBlob() != null) {
                final Buffer buf = new Buffer(this.getPublicKeyBlob());
                buf.getString();
                this.pub_array = buf.getString();
                this.n_array = buf.getString();
            }
            rsa.setPubKey(this.pub_array, this.n_array);
            return rsa;
        }
        catch (Exception e) {
            return null;
        }
    }
    
    static KeyPair fromSSHAgent(final JSch jsch, final Buffer buf) throws JSchException {
        final byte[][] tmp = buf.getBytes(8, "invalid key format");
        final byte[] n_array = tmp[1];
        final byte[] pub_array = tmp[2];
        final byte[] prv_array = tmp[3];
        final KeyPairRSA kpair = new KeyPairRSA(jsch, n_array, pub_array, prv_array);
        kpair.c_array = tmp[4];
        kpair.p_array = tmp[5];
        kpair.q_array = tmp[6];
        kpair.publicKeyComment = new String(tmp[7]);
        kpair.vendor = 0;
        return kpair;
    }
    
    @Override
    public byte[] forSSHAgent() throws JSchException {
        if (this.isEncrypted()) {
            throw new JSchException("key is encrypted.");
        }
        final Buffer buf = new Buffer();
        buf.putString(KeyPairRSA.sshrsa);
        buf.putString(this.n_array);
        buf.putString(this.pub_array);
        buf.putString(this.prv_array);
        buf.putString(this.getCArray());
        buf.putString(this.p_array);
        buf.putString(this.q_array);
        buf.putString(Util.str2byte(this.publicKeyComment));
        final byte[] result = new byte[buf.getLength()];
        buf.getByte(result, 0, result.length);
        return result;
    }
    
    private byte[] getEPArray() {
        if (this.ep_array == null) {
            this.ep_array = new BigInteger(this.prv_array).mod(new BigInteger(this.p_array).subtract(BigInteger.ONE)).toByteArray();
        }
        return this.ep_array;
    }
    
    private byte[] getEQArray() {
        if (this.eq_array == null) {
            this.eq_array = new BigInteger(this.prv_array).mod(new BigInteger(this.q_array).subtract(BigInteger.ONE)).toByteArray();
        }
        return this.eq_array;
    }
    
    private byte[] getCArray() {
        if (this.c_array == null) {
            this.c_array = new BigInteger(this.q_array).modInverse(new BigInteger(this.p_array)).toByteArray();
        }
        return this.c_array;
    }
    
    @Override
    public void dispose() {
        super.dispose();
        Util.bzero(this.prv_array);
    }
    
    static {
        begin = Util.str2byte("-----BEGIN RSA PRIVATE KEY-----");
        end = Util.str2byte("-----END RSA PRIVATE KEY-----");
        sshrsa = Util.str2byte("ssh-rsa");
    }
}
