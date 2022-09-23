// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public class KeyPairECDSA extends KeyPair
{
    private static byte[][] oids;
    private static String[] names;
    private byte[] name;
    private byte[] r_array;
    private byte[] s_array;
    private byte[] prv_array;
    private int key_size;
    private static final byte[] begin;
    private static final byte[] end;
    
    public KeyPairECDSA(final JSch jsch) {
        this(jsch, null, null, null, null);
    }
    
    public KeyPairECDSA(final JSch jsch, final byte[] name, final byte[] r_array, final byte[] s_array, final byte[] prv_array) {
        super(jsch);
        this.name = Util.str2byte(KeyPairECDSA.names[0]);
        this.key_size = 256;
        if (name != null) {
            this.name = name;
        }
        this.r_array = r_array;
        this.s_array = s_array;
        if ((this.prv_array = prv_array) != null) {
            this.key_size = ((prv_array.length >= 64) ? 521 : ((prv_array.length >= 48) ? 384 : 256));
        }
    }
    
    @Override
    void generate(final int key_size) throws JSchException {
        this.key_size = key_size;
        try {
            final JSch jsch = this.jsch;
            final Class c = Class.forName(JSch.getConfig("keypairgen.ecdsa"));
            KeyPairGenECDSA keypairgen = c.newInstance();
            keypairgen.init(key_size);
            this.prv_array = keypairgen.getD();
            this.r_array = keypairgen.getR();
            this.s_array = keypairgen.getS();
            this.name = Util.str2byte(KeyPairECDSA.names[(this.prv_array.length >= 64) ? 2 : ((this.prv_array.length >= 48) ? 1 : 0)]);
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
        return KeyPairECDSA.begin;
    }
    
    @Override
    byte[] getEnd() {
        return KeyPairECDSA.end;
    }
    
    @Override
    byte[] getPrivateKey() {
        final byte[] tmp = { 1 };
        final byte[] oid = KeyPairECDSA.oids[(this.r_array.length >= 64) ? 2 : ((this.r_array.length >= 48) ? 1 : 0)];
        byte[] point = toPoint(this.r_array, this.s_array);
        final int bar = ((point.length + 1 & 0x80) == 0x0) ? 3 : 4;
        final byte[] foo = new byte[point.length + bar];
        System.arraycopy(point, 0, foo, bar, point.length);
        foo[0] = 3;
        if (bar == 3) {
            foo[1] = (byte)(point.length + 1);
        }
        else {
            foo[1] = -127;
            foo[2] = (byte)(point.length + 1);
        }
        point = foo;
        final int content = 1 + this.countLength(tmp.length) + tmp.length + 1 + this.countLength(this.prv_array.length) + this.prv_array.length + 1 + this.countLength(oid.length) + oid.length + 1 + this.countLength(point.length) + point.length;
        final int total = 1 + this.countLength(content) + content;
        final byte[] plain = new byte[total];
        int index = 0;
        index = this.writeSEQUENCE(plain, index, content);
        index = this.writeINTEGER(plain, index, tmp);
        index = this.writeOCTETSTRING(plain, index, this.prv_array);
        index = this.writeDATA(plain, (byte)(-96), index, oid);
        index = this.writeDATA(plain, (byte)(-95), index, point);
        return plain;
    }
    
    @Override
    boolean parse(final byte[] plain) {
        try {
            if (this.vendor == 1) {
                return false;
            }
            if (this.vendor == 2) {
                return false;
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
            final byte[] oid_array = new byte[length];
            System.arraycopy(plain, index, oid_array, 0, length);
            index += length;
            for (int i = 0; i < KeyPairECDSA.oids.length; ++i) {
                if (Util.array_equals(KeyPairECDSA.oids[i], oid_array)) {
                    this.name = Util.str2byte(KeyPairECDSA.names[i]);
                    break;
                }
            }
            ++index;
            length = (plain[index++] & 0xFF);
            if ((length & 0x80) != 0x0) {
                int foo2 = length & 0x7F;
                length = 0;
                while (foo2-- > 0) {
                    length = (length << 8) + (plain[index++] & 0xFF);
                }
            }
            final byte[] Q_array = new byte[length];
            System.arraycopy(plain, index, Q_array, 0, length);
            index += length;
            final byte[][] tmp = fromPoint(Q_array);
            this.r_array = tmp[0];
            this.s_array = tmp[1];
            if (this.prv_array != null) {
                this.key_size = ((this.prv_array.length >= 64) ? 521 : ((this.prv_array.length >= 48) ? 384 : 256));
            }
        }
        catch (Exception e) {
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
        if (this.r_array == null) {
            return null;
        }
        final byte[][] tmp = { Util.str2byte("ecdsa-sha2-" + new String(this.name)), this.name, new byte[1 + this.r_array.length + this.s_array.length] };
        tmp[2][0] = 4;
        System.arraycopy(this.r_array, 0, tmp[2], 1, this.r_array.length);
        System.arraycopy(this.s_array, 0, tmp[2], 1 + this.r_array.length, this.s_array.length);
        return Buffer.fromBytes(tmp).buffer;
    }
    
    @Override
    byte[] getKeyTypeName() {
        return Util.str2byte("ecdsa-sha2-" + new String(this.name));
    }
    
    @Override
    public int getKeyType() {
        return 3;
    }
    
    public int getKeySize() {
        return this.key_size;
    }
    
    @Override
    public byte[] getSignature(final byte[] data) {
        try {
            final JSch jsch = this.jsch;
            final Class c = Class.forName(JSch.getConfig("signature.ecdsa"));
            final SignatureECDSA ecdsa = c.newInstance();
            ecdsa.init();
            ecdsa.setPrvKey(this.prv_array);
            ecdsa.update(data);
            final byte[] sig = ecdsa.sign();
            final byte[][] tmp = { Util.str2byte("ecdsa-sha2-" + new String(this.name)), sig };
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
            final Class c = Class.forName(JSch.getConfig("signature.ecdsa"));
            final SignatureECDSA ecdsa = c.newInstance();
            ecdsa.init();
            if (this.r_array == null && this.s_array == null && this.getPublicKeyBlob() != null) {
                final Buffer buf = new Buffer(this.getPublicKeyBlob());
                buf.getString();
                buf.getString();
                final byte[][] tmp = fromPoint(buf.getString());
                this.r_array = tmp[0];
                this.s_array = tmp[1];
            }
            ecdsa.setPubKey(this.r_array, this.s_array);
            return ecdsa;
        }
        catch (Exception e) {
            return null;
        }
    }
    
    static KeyPair fromSSHAgent(final JSch jsch, final Buffer buf) throws JSchException {
        final byte[][] tmp = buf.getBytes(5, "invalid key format");
        final byte[] name = tmp[1];
        final byte[][] foo = fromPoint(tmp[2]);
        final byte[] r_array = foo[0];
        final byte[] s_array = foo[1];
        final byte[] prv_array = tmp[3];
        final KeyPairECDSA kpair = new KeyPairECDSA(jsch, name, r_array, s_array, prv_array);
        kpair.publicKeyComment = new String(tmp[4]);
        kpair.vendor = 0;
        return kpair;
    }
    
    @Override
    public byte[] forSSHAgent() throws JSchException {
        if (this.isEncrypted()) {
            throw new JSchException("key is encrypted.");
        }
        final Buffer buf = new Buffer();
        buf.putString(Util.str2byte("ecdsa-sha2-" + new String(this.name)));
        buf.putString(this.name);
        buf.putString(toPoint(this.r_array, this.s_array));
        buf.putString(this.prv_array);
        buf.putString(Util.str2byte(this.publicKeyComment));
        final byte[] result = new byte[buf.getLength()];
        buf.getByte(result, 0, result.length);
        return result;
    }
    
    static byte[] toPoint(final byte[] r_array, final byte[] s_array) {
        final byte[] tmp = new byte[1 + r_array.length + s_array.length];
        tmp[0] = 4;
        System.arraycopy(r_array, 0, tmp, 1, r_array.length);
        System.arraycopy(s_array, 0, tmp, 1 + r_array.length, s_array.length);
        return tmp;
    }
    
    static byte[][] fromPoint(final byte[] point) {
        int i;
        for (i = 0; point[i] != 4; ++i) {}
        ++i;
        final byte[][] tmp = new byte[2][];
        final byte[] r_array = new byte[(point.length - i) / 2];
        final byte[] s_array = new byte[(point.length - i) / 2];
        System.arraycopy(point, i, r_array, 0, r_array.length);
        System.arraycopy(point, i + r_array.length, s_array, 0, s_array.length);
        tmp[0] = r_array;
        tmp[1] = s_array;
        return tmp;
    }
    
    @Override
    public void dispose() {
        super.dispose();
        Util.bzero(this.prv_array);
    }
    
    static {
        KeyPairECDSA.oids = new byte[][] { { 6, 8, 42, -122, 72, -50, 61, 3, 1, 7 }, { 6, 5, 43, -127, 4, 0, 34 }, { 6, 5, 43, -127, 4, 0, 35 } };
        KeyPairECDSA.names = new String[] { "nistp256", "nistp384", "nistp521" };
        begin = Util.str2byte("-----BEGIN EC PRIVATE KEY-----");
        end = Util.str2byte("-----END EC PRIVATE KEY-----");
    }
}
